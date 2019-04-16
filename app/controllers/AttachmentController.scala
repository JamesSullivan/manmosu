package controllers

import java.io.File
import java.net.URLDecoder
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

import com.mohiva.play.silhouette.api.Silhouette

import javax.inject.Inject
import models.daos.slick.Create
import models.daos.slick.DAORead
import models.daos.slick.DAOWrite
import models.daos.slick.QA
import models.services.UserService
import play.api.Configuration
import play.api.Logging
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.I18nSupport
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents
import utils.auth.DefaultEnv

/**
 * The basic application controller.
 *
 * @param cc                     The Play controller components.
 * @param ec                     The Play execution context
 * @param silhouette             The Silhouette stack.
 */
class AttachmentController @Inject() (cc: ControllerComponents, implicit val ec: ExecutionContext,
  config: Configuration, dbConfigProvider: DatabaseConfigProvider, userService: UserService,
  silhouette: Silhouette[DefaultEnv]) extends AbstractController(cc) with I18nSupport with Logging {

  val daoRead = new DAORead(dbConfigProvider, config)
  val daoWrite = new DAOWrite(dbConfigProvider, config)

  private val path = config.get[String]("attachments.root.fs.path")

  /**
   * Displays an individual file attachment
   * @param attachmentId
   * @return Result to display or download
   */
  def displayFile(attachmentsId: Long) = silhouette.SecuredAction.async { implicit request =>
    val entirePath = path + attachmentsId
    val ar = Await.result(daoRead.attachmentsByAttachmentId(attachmentsId), 30.seconds).get
    val name = ar.name.getOrElse("")
    val file = new File(entirePath)
    val etag = file.length.toString + file.lastModified.toString
    val oldEtag = request.headers.get("If-None-Match").getOrElse("")
    val theDate: ZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified), java.time.ZoneId.of("Z"));
    val lastModified = theDate.format(RFC_1123_DATE_TIME)
    def okSendFile(contentType: String, contentDisposition: String = "") = {
      val path: java.nio.file.Path = file.toPath
      val source: akka.stream.scaladsl.Source[akka.util.ByteString, _] = akka.stream.scaladsl.FileIO.fromPath(path)
      val contentLength = Some(file.length())
      play.api.mvc.Result(
        header = play.api.mvc.ResponseHeader(200, Map.empty),
        body = play.api.http.HttpEntity.Streamed(source, contentLength, Some(contentType))).withHeaders(
          ETAG -> etag,
          LAST_MODIFIED -> lastModified,
          CONTENT_LENGTH -> file.length.toString,
          CONTENT_DISPOSITION -> contentDisposition)
      // CONTENT_TYPE -> contentType)
    }
    if (oldEtag.equals(etag)) {
      println("304 - Not Modified")
      Future.successful(Status(304)(""))
    } else if (name.toLowerCase.matches(""".*\.(gif|jpg|jpeg|png|bmp)""")) {
      val ext = if (name.toLowerCase.endsWith(".jpeg")) "jpg" else name.takeRight(3)
      Future.successful(okSendFile("image/" + ext))
    } else if (name.endsWith(".pdf")) {
      Future.successful(Ok.sendFile(file, fileName = _ => name))
    } else {
      Future.successful(okSendFile(ar.mime.getOrElse("application/x-download"), s"""attachment; filename=$name"""))
    }
  }

  def removeFile(questionId: Long, attachmentsId: Long) = silhouette.SecuredAction.async { implicit request =>
    val userID = request.identity.userID
    Await.result(daoWrite.removeQuestionAttachmentRow(attachmentsId, userID), 30.seconds)
    Await.result(daoWrite.removeAnswerAttachmentRow(attachmentsId, userID), 30.seconds)
    val f = new java.io.File(path + attachmentsId)
    f.delete
    Future.successful(Redirect(routes.QuestionController.ask(questionId)))
  }

  import akka.stream.ActorMaterializer
  val system = akka.actor.ActorSystem()
  implicit val materializer = ActorMaterializer()(system)

  def upload = silhouette.SecuredAction.async(this.parse.maxLength(30L * 1024L * 1024L, parse.multipartFormData)) { implicit request =>
    def show = Future.successful(request.body match {
      case Left(maxSizeExceeded) => Ok("The size of your file(s) is too large, we accept just " + maxSizeExceeded.length + " bytes at a time!")
      case Right(multipartForm) => {
        multipartForm.files.map { file =>
          val fp = Paths.get(path + request.identity.userID)
          if (!Files.exists(fp)) Files.createDirectory(fp)
          val p = Paths.get(fp + java.io.File.separator + URLDecoder.decode(file.filename, "UTF8"))
          logger.info("p.toAbsolutePath(): " + p.toAbsolutePath())
          val f = p.toFile()
          if (f.exists && !f.isDirectory()) f.delete()
          file.ref.moveFileTo(f)
        }
        Ok("File uploaded")
      }
    })
    show
  }
}

object AttachmentController extends Logging {

  def saveFile(daoWrite: DAOWrite, path: String, table: QA.Value, id: Long, ip: String, userID: Long) = {
    val d = new java.io.File(path + userID)
    try {
      if (d.exists && d.isDirectory) {
        val files = d.listFiles.filter(_.isFile).toList.take(10)
        for (f <- files) {
          val mime = Option(java.nio.file.Files.probeContentType(f.toPath())).getOrElse("unknown")
          val attachmentRow = Create.attachmentRow(ip, mime, f.getName, userID)
          val attachmentsId = Await.result(daoWrite.insertAttachmentRow(attachmentRow), 30.seconds)
          table match {
            case QA.QUESTION => Await.result(daoWrite.insertQuestionAttachmentRow(id, attachmentsId), 30.seconds)
            case QA.ANSWER => Await.result(daoWrite.insertAnswerAttachmentRow(id, attachmentsId), 30.seconds)
          }
          val targetPath = Paths.get(path + attachmentsId)
          println("targetPath: " + targetPath)
          java.nio.file.Files.move(f.toPath(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
        }
        logger.info("file(s) saved: " + files.length)
      } else {
        logger.info("file(s) saved: 0")
      }
    } catch {
      case e: java.io.IOException => logger.error(e.getCause + "\r\n" + e.getStackTrace);
    } finally {
      if (d != null && d.isDirectory()) {
        for (f <- d.listFiles()) {
          f.delete()
        }
        d.delete()
        ()
      }
    }
  }
}
