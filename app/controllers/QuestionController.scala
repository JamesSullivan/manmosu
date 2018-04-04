package controllers

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry

import forms.AskForm
import javax.inject.Inject
import models.AnswerUser
import models.Ask
import models.CommentUser
import models.QuestionTag
import models.QuestionUser
import models.daos.slick.Create
import models.daos.slick.DAORead
import models.daos.slick.DAOWrite
import models.daos.slick.QA
import models.services.UserService
import play.api.Configuration
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.I18nSupport
import play.api.i18n.Messages
import play.api.libs.ws.WSClient
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents
import utils.auth.DefaultEnv

/**
 * The basic application controller.
 *
 * @param cc                     The Play controller components.
 * @param ec                     The Play execution context
 * @param socialProviderRegistry The social provider registry.
 * @param silhouette             The Silhouette stack.
 * @param webJarsUtil            The webjar util.
 */
class QuestionController @Inject() (cc: ControllerComponents, implicit val ec: ExecutionContext, ws: WSClient,
  config: Configuration, dbConfigProvider: DatabaseConfigProvider, userService: UserService, socialProviderRegistry: SocialProviderRegistry,
  silhouette: Silhouette[DefaultEnv]) extends AbstractController(cc) with I18nSupport {

  val daoRead = new DAORead(dbConfigProvider, config)
  val daoWrite = new DAOWrite(dbConfigProvider, config)
  val tagDAOImpl = new models.daos.TagDAOImpl
  tagDAOImpl.apply(Await.result(daoRead.tags, 30.seconds))
  val mailService = new utils.MailService(config)

  private val path = config.get[String]("attachments.root.fs.path")
  private val my_question_voted_down = config.get[Int]("karma.my_question_voted_down")
  private val my_question_voted_up = config.get[Int]("karma.my_question_voted_up")
  private val asked_question = config.get[Int]("karma.asked_question")
  private val downvoted_question_or_answer = config.get[Int]("karma.downvoted_question_or_answer")

  def ask(questionId: Long = 0) = silhouette.SecuredAction.async { implicit request =>
    val userID = request.identity.userID
    val askForm = AskForm
    val formData = if (questionId < 1) { // new question
      Ask(true, "", "", List[String](), "", "CREATED_QUESTION", 0)
    } else { // edit existing question
      val watch = Await.result(daoRead.watchesByQuestionUserId(questionId, Some(userID)), 30.seconds)
      val questionUser = Await.result(daoRead.questionUser(questionId), 30.seconds).map(qr => QuestionUser.tupled(qr)).get
      questionUser.tags = Await.result(daoRead.questionsTags(Seq(questionUser.informationRow.id)), 30.seconds).map(q => QuestionTag.tupled(q))
      Ask(watch, questionUser.informationRow.description, questionUser.informationRow.markeddescription.getOrElse(""), questionUser.tags.map(_.tagxRow.name), questionUser.informationRow.title, "", questionId)
    }
    val attachments = if (questionId < 1) Seq[models.daos.slick.Tables.AttachmentRow]() else Await.result(daoRead.attachmentsByQuestionId(questionId), 30.seconds)
    val askData = askForm.form.fill(formData)
    Future.successful(Ok(views.html.ask(askData, attachments, request.identity, config)))
  }

  def asked() = silhouette.SecuredAction.async { implicit request =>
    val userID = request.identity.userID

    AskForm.form.bindFromRequest.fold(
      formWithErrors => {
        val questionIdString: String = formWithErrors("questionId").value.getOrElse("0")
        val attachments = if (questionIdString == "0") Seq[models.daos.slick.Tables.AttachmentRow]() else Await.result(daoRead.attachmentsByQuestionId(questionIdString.toLong), 30.seconds)
        Future.successful(BadRequest(views.html.ask(formWithErrors, attachments, request.identity, config)))
      },
      {
        case (ask) =>
          val edit: Boolean = (ask.comment != "CREATED_QUESTION")
          for (tag <- ask.tags) { Await.result(daoWrite.insertOrUpdateTagx(Create.tagxRow(tag, userID)), 30.seconds) }
          val qinformationId = Await.result(daoWrite.insertQuestioninformationRow(Create.questioninformationRow(userID, ask, request.remoteAddress, ask.comment)), 30.seconds)
          val qId: Long = if (edit) {
            Await.result(daoWrite.updateQuestionRow(Create.questionRow(ask.questionId, userID, qinformationId)), 30.seconds);
            ask.questionId
          } else {
            Await.result(daoWrite.insertQuestionRow(Create.questionRow(0L, userID, qinformationId)), 30.seconds)
          }
          if (!edit) Await.result(daoWrite.updateQuestioninformationRowWithQID(qinformationId, qId), 30.seconds)
          for ((tag, count) <- ask.tags.zipWithIndex) {
            val tagId = Await.result(daoRead.tagNo(tag), 30.seconds)
            tagId.map(t => Await.result(daoWrite.insertOrUpdateQuestioninformationTag(Create.questioninformationTagRow(qinformationId, t, count)), 30.seconds))
          }
          AttachmentController.saveFile(daoWrite, path, QA.QUESTION, qId, request.remoteAddress, userID)
          if (!edit) {
            if (ask.watching) daoWrite.insertWatcher(qId, userID)
            for {
              qi <- daoWrite.insertQuestionInteractionsRow(qId, userID)
              re <- daoWrite.insertReputationeventRow(Create.reputationeventRow(qId, asked_question, ask.comment, Some(userID)))
            } yield qi
            // send notification E-mails
            val messages: Messages = request.messages
            val questionResult = Await.result(daoRead.questionUser(qId), 30.seconds)
            val questionUser = models.QuestionUser.tupled(questionResult.get)
            val watchers = Await.result(daoRead.watchUsersAllQuestions(), 30.seconds).filterNot(_.id == userID)
            val title: String = messages("new_question_notification", messages("site.name"))
            for (w <- watchers) {
              val emailHTML = views.html.emails.questionNotification(w, questionUser)
              mailService.sendEmailAsync(w.email.get)(title, emailHTML.toString, "")
            }
            Future.successful(Redirect(routes.ApplicationController.index("")))
          } else {
            Future.successful(Redirect(routes.QuestionController.question(ask.questionId, utils.Sanitizer.slugify(ask.title))))
          }
      })
  }

  def questionSuggestion(query: String, limit: Long) = silhouette.UserAwareAction.async { implicit request =>
    // SELECT *, MATCH(title,description) AGAINST ('A question about questions') AS score FROM `QuestionInformation` HAVING score > 0 Limit 3;
    val qiqs = Await.result(daoRead.relevantMatch(query, limit), 30.seconds)
    val questions = qiqs.map { qiq => QuestionUser(qiq._2, qiq._1, null) }
    Future.successful(Ok(views.html.snippets.questionlist(None, questions, true)))
  }

  // huge opportunties for refactoring with answerVote
  def questionVote(number: Long, direction: String) = silhouette.SecuredAction.async { implicit request =>
    val userID = request.identity.userID
    val up = (direction.toLowerCase() != "down")
    val voteDirection: Int = if (up) 1 else -1
    val karmaAmount = if (up) my_question_voted_up else my_question_voted_down
    val karmaType = if (up) "QUESTION_UPVOTE" else "QUESTION_DOWNVOTE"
    val q = Await.result(daoRead.questionUser(number), 30.seconds).map(q => QuestionUser.tupled(q))
    Logger.info("userID: " + userID + " " + karmaType + " for " +userID)
    if (q.get.authorsRow.id == userID) {
      Future.successful(Conflict("vote.ownquestion"))
    } else {
      val vote = Await.result(daoRead.voteByQuestionVoter(number, userID), 30.seconds) // Option[(questionVotesRow, voteRow)]
      val oldVoteType = if (vote.isEmpty) "" else vote.get._2.`type`.getOrElse("")
      if (vote.isEmpty) {
        Logger.debug("\tvote was empty")
        Await.result(daoWrite.insertQuestionVoteRow(number, Create.voteRow(direction.toUpperCase(), userID)), 30.seconds)
        Await.result(daoWrite.incrementQuestionVotes(q.get.row.id, voteDirection), 30.seconds)
        Await.result(daoWrite.insertReputationeventRow(Create.reputationeventRow(q.get.row.id, karmaAmount, karmaType, q.get.row.authorId)), 30.seconds)
        if (!up) {
          Await.result(daoWrite.insertReputationeventRow(Create.reputationeventRow(q.get.row.id, downvoted_question_or_answer, "QUESTION_DOWNVOTING", Some(userID))), 30.seconds)
        } // add reputation events
        Future.successful(Ok((q.get.row.votecount + voteDirection).toString))
      } else if (oldVoteType == direction.toUpperCase()) { // same again so remove
        Logger.debug("\tsame again so remove")
        Await.result(daoWrite.removeQuestionVoteRow(number, userID), 30.seconds)
        Await.result(daoWrite.incrementQuestionVotes(q.get.row.id, (voteDirection * -1)), 30.seconds)
        Await.result(daoWrite.removeReputationeventRow(Create.reputationeventRow(q.get.row.id, karmaAmount, karmaType, q.get.row.authorId)), 30.seconds)
        if (!up) {
          Await.result(daoWrite.removeReputationeventRow(Create.reputationeventRow(q.get.row.id, downvoted_question_or_answer, "QUESTION_DOWNVOTING", Some(userID))), 30.seconds)
        }
        Future.successful(Ok((q.get.row.votecount + (voteDirection * -1)).toString))
      } else if ((voteDirection == 1 && oldVoteType.equals("DOWN")) || (voteDirection == -1 && oldVoteType.equals("UP"))) {
        Logger.debug("\tchange vote to opposite")
        val voteChange = voteDirection * 2
        val karmaChange = (my_question_voted_up - my_question_voted_down) * voteDirection
        val oldType = if (!up) "QUESTION_UPVOTE" else "QUESTION_DOWNVOTE"
        Await.result(daoWrite.updateQuestionVoteRow(q.get.row.id, userID, direction.toUpperCase()), 30.seconds)
        Await.result(daoWrite.incrementQuestionVotes(q.get.row.id, voteChange), 30.seconds)
        Await.result(daoWrite.updateReputationeventRow(q.get.row.id, karmaAmount, karmaChange, oldType, karmaType, q.get.row.authorId.get), 30.seconds)
        if (up) {
          Await.result(daoWrite.removeReputationeventRow(Create.reputationeventRow(q.get.row.id, downvoted_question_or_answer, "QUESTION_DOWNVOTING", Some(userID))), 30.seconds)
        } else {
          Await.result(daoWrite.insertReputationeventRow(Create.reputationeventRow(q.get.row.id, downvoted_question_or_answer, "QUESTION_DOWNVOTING", Some(userID))), 30.seconds)
        }
        Future.successful(Ok((q.get.row.votecount + voteChange).toString))
      } else {
        Future.successful(Ok((q.get.row.votecount).toString))
      }
    }
  }

  def question(number: Long, title: String) = silhouette.UserAwareAction.async { implicit request =>
    val userID: Option[Long] = if (request.identity.isDefined) Some(request.identity.get.userID) else None
    daoWrite.incrementQuestionViews(number)
    val questionResult = Await.result(daoRead.questionUser(number), 30.seconds)
    if (questionResult.isEmpty) {
      Future.successful(Ok("Question does not exist"))
    } else {
      val question = QuestionUser.tupled(questionResult.get)
      question.comments = Await.result(daoRead.questionComments(number), 30.seconds).map(q => CommentUser.tupled(q))
      question.attachments = Await.result(daoRead.attachmentsByQuestionId(number), 30.seconds)
      val qcv = if (userID.isDefined) Await.result(daoRead.questionCommentsVotes(userID.get, number), 30.seconds) else Seq()
      if (userID.isDefined) question.voteComments(qcv.map(_._2))
      question.tags = Await.result(daoRead.questionsTags(Seq(question.informationRow.id)), 30.seconds).map(q => QuestionTag.tupled(q))
      val answers = Await.result(daoRead.answers(question.row.id), 30.seconds).map(a => AnswerUser.tupled(a))
      val answersComments = Await.result(daoRead.answersComments(answers.map(_.informationRow.id)), 30.seconds).map(a => CommentUser.tupled(a))
      for (a <- answers) {
        a.comments = answersComments.filter(_.id == a.row.id)
        a.attachments = Await.result(daoRead.attachmentsByAnswerId(a.row.id), 30.seconds)
        a.voted = Await.result(daoRead.voteByAnswerUserId(a.row.id, userID.getOrElse(0)), 30.seconds)
        val acv = if (userID.isDefined) Await.result(daoRead.answerCommentsVotes(userID.get, a.row.id), 30.seconds) else Seq()
        if (userID.isDefined) a.voteComments(acv.map(_._2))
      }
      val recentTags: Seq[models.daos.slick.Tables.TagxRow] = Await.result(daoRead.recentTags(), 30.seconds)
      val qiqs = Await.result(daoRead.relevantMatch(question.informationRow.title, 5), 30.seconds)
      val questions = qiqs.map { qiq => QuestionUser(qiq._2, qiq._1, null) }
      val relevant = views.html.snippets.questionlist(None, questions, false).toString
      // (vote is 1 or -1, watch is true or false)
      val voteWatching = Await.result(daoRead.voteWatchesByQuestionUserId(number, userID.getOrElse(0)), 30.seconds)
      question.voted = voteWatching._1
      question.watching = voteWatching._2
      question match {
        case question: QuestionUser => Future.successful(Ok(views.html.question(request.identity, question, answers, recentTags, relevant, config)))
        case _ => Future.successful(Ok("Question does not exist"))
      }
    }
  }

}
