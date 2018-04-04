package controllers

import java.time.LocalDateTime

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry

import forms.AnswerForm
import javax.inject.Inject
import models.AnswerUser
import models.CommentUser
import models.QuestionTag
import models.QuestionUser
import models.TagRanking
import models.daos.slick.Create
import models.daos.slick.DAORead
import models.daos.slick.DAOWrite
import models.daos.slick.QA
import models.services.UserService
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.I18nSupport
import play.api.i18n.Messages
import play.api.libs.ws.WSClient
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents
import utils.RelativeTime.timeStampNow
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
class ApplicationController @Inject() (cc: ControllerComponents, implicit val ec: ExecutionContext, ws: WSClient,
  config: Configuration, dbConfigProvider: DatabaseConfigProvider, userService: UserService, socialProviderRegistry: SocialProviderRegistry,
  silhouette: Silhouette[DefaultEnv]) extends AbstractController(cc) with I18nSupport {

  val daoRead = new DAORead(dbConfigProvider, config)
  val daoWrite = new DAOWrite(dbConfigProvider, config)
  val tagDAOImpl = new models.daos.TagDAOImpl
  tagDAOImpl.apply(Await.result(daoRead.tags, 30.seconds))
  val mailService = new utils.MailService(config)

  private val path = config.get[String]("attachments.root.fs.path")
  private val solved_question_author = config.get[Int]("karma.solved_question_author")
  private val solution_author = config.get[Int]("karma.solution_author")
  private val my_answer_voted_up = config.get[Int]("karma.my_answer_voted_up")
  private val my_answer_voted_down = config.get[Int]("karma.my_answer_voted_down")
  private val approved_information = config.get[Int]("karma.approved_information")
  private val comment_voted_up = config.get[Int]("karma.comment_voted_up")
  private val answered_question = config.get[Int]("karma.answered_question")
  private val downvoted_question_or_answer = config.get[Int]("karma.downvoted_question_or_answer")

  def about() = silhouette.UserAwareAction.async { implicit request => Future.successful(Ok(views.html.about(request.identity, config))) }

  // id is questionId for answer and answerId for edit
  def answer(id: Long = 0) = silhouette.SecuredAction.async { implicit request =>
    val userID = request.identity.userID
    val edit: Boolean = request.uri.contains("/edit/")
    val answerForm = AnswerForm
    val answerId: Long = if (edit) id else 0
    val formData: AnswerForm.Answer = if (!edit) { // new question
      AnswerForm.blank(id)
    } else { // edit existing question
      val questionId: Long = if (edit) Await.result(daoRead.questionIdByAnswerId(id), 30.seconds).get else id
      val watch = Await.result(daoRead.watchesByQuestionUserId(questionId, Some(userID)), 30.seconds)
      val answerUser = Await.result(daoRead.answerUser(answerId), 30.seconds).map(ar => AnswerUser.tupled(ar)).get
      AnswerForm.Answer(watch, answerUser.informationRow.description, "", id, questionId)
    }
    val attachments = if (answerId < 1) Seq[models.daos.slick.Tables.AttachmentRow]() else Await.result(daoRead.attachmentsByAnswerId(answerId), 30.seconds)
    val answerData = answerForm.form.fill(formData)
    Future.successful(Ok(views.html.answer(id, answerData, attachments, request.identity, config)))
  }

  // id is questionId for answer and answerId for edit
  def answered(id: Long) = silhouette.SecuredAction.async { implicit request =>
    val userID = request.identity.userID
    val edit: Boolean = request.uri.contains("/edit/")
    AnswerForm.form.bindFromRequest.fold(
      formWithErrors => {
        val answerIdString: String = formWithErrors("answerId").value.getOrElse("0")
        val attachments = if (answerIdString == "0") Seq[models.daos.slick.Tables.AttachmentRow]() else Await.result(daoRead.attachmentsByAnswerId(answerIdString.toLong), 30.seconds)
        Future.successful(BadRequest(views.html.answer(id, formWithErrors, attachments, request.identity, config)))
      },
      {
        case (answer) =>
          println("answer: " + answer)
          val questionId: Long = if (edit) Await.result(daoRead.questionIdByAnswerId(id), 30.seconds).get else id
          println("questionId: " + questionId)
          val air = Create.answerinformationRow(userID, answer, request.remoteAddress, answer.comment)
          println("air: " + air)
          val ainformationId = Await.result(daoWrite.insertAnswerinformationRow(air), 30.seconds)
          val ar = if (edit) {
            val oldAnswerRow = Create.answerRow(answer.answerId, userID, ainformationId, questionId)
            Await.result(daoWrite.updateAnswerRow(oldAnswerRow), 30.seconds);
            oldAnswerRow
          } else {
            val templateAnswerRow = Create.answerRow(0L, userID, ainformationId, questionId)
            val arId = Await.result(daoWrite.insertAnswerRow(templateAnswerRow), 30.seconds)
            templateAnswerRow.copy(id = arId)
          }
          println("ainformationId, ar.id: " + ainformationId + "," + ar.id)
          if (!edit) Await.result(daoWrite.updateAnswerinformationRowWithAID(ainformationId, ar.id), 30.seconds)
          if (!edit) {
            println("userId: " + userID)
            if (answer.watching) daoWrite.insertWatcher(questionId, userID)
            for {
              q <- daoWrite.incrementQuestionAnswers(questionId)
              qi <- daoWrite.insertQuestionInteractionsRow(questionId, userID)
              re <- daoWrite.insertReputationeventRow(Create.reputationeventRow(questionId, answered_question, "CREATED_ANSWER", Some(userID)))
            } yield Redirect(routes.ApplicationController.index(""))
          }
          // handle attachments
          AttachmentController.saveFile(daoWrite, path, QA.ANSWER, ar.id, request.remoteAddress, userID)
          // send notification E-mails
          val aur: models.daos.slick.Tables.UsersRow = Await.result(daoRead.user(userID), 30.seconds).getOrElse(null)
          val questionResult = Await.result(daoRead.questionUser(questionId), 30.seconds)
          val questionUser = models.QuestionUser.tupled(questionResult.get)
          val watchers = Await.result(daoRead.watchUsersByQuestionId(questionUser.row.id), 30.seconds).filterNot(_._1.watcherId.getOrElse(0L) == aur.id)
          val emailHTML = views.html.emails.answerNotification(aur, questionUser, models.AnswerUser(ar, air, aur))
          for (w <- watchers) {
            mailService.sendEmailAsync(w._2.email.get)("Answer", emailHTML.toString, "")
            daoWrite.inactivateWatcherRow(w._1.id)
          }
          Future.successful(Redirect(routes.QuestionController.question(questionId, questionUser.informationRow.sluggedtitle)));
      })
  }


  def solution(answerId: Long) = silhouette.SecuredAction.async { implicit request =>
    val userID = request.identity.userID
    val questionId: Long = Await.result(daoRead.questionIdByAnswerId(answerId), 30.seconds).get
    val qu = Await.result(daoRead.questionUser(questionId), 30.seconds).map(qu => QuestionUser.tupled(qu)).get
    val ar = Await.result(daoRead.answerRow(answerId), 30.seconds).get
    val solutionId = if (qu.row.solutionId == None) Some(answerId) else None
    println(if (solutionId == None) "Unmarking solution" else "Marking solution")
    val qr = qu.row.copy(lastupdatedat = Some(timeStampNow), lasttouchedbyId = Some(userID), solutionId = solutionId)
    println("qr: " + qr)
    if (qu.row.authorId.get == userID) {
      Await.result(daoWrite.updateQuestionRowSolutionId(qr), 30.seconds)
      val markedRepEvent = Create.reputationeventRow(questionId, solved_question_author, "MARKED_SOLUTION", Some(userID))
      val solvedRepEvent = Create.reputationeventRow(questionId, solution_author, "SOLVED_QUESTION", ar.authorId)
      if (solutionId != None) {
        daoWrite.insertReputationeventRow(markedRepEvent)
        daoWrite.insertReputationeventRow(solvedRepEvent)
      } else {
        daoWrite.removeReputationeventRow(markedRepEvent)
        daoWrite.removeReputationeventRow(solvedRepEvent)
      }
    }
    Future.successful(Ok("1"))
  }

  // id is overloaded can be questionId, answerId or commentId
  def comment(id: Long, about: String) = silhouette.SecuredAction.async { implicit request =>
    println("request.body: " + request.body)
    val edit: Boolean = request.uri.contains("/edit/")
    val userID = request.identity.userID
    val formBody: Option[Map[String, Seq[String]]] = request.body.asFormUrlEncoded
    val commentsId = if (edit) id else 0L
    println("comment " + about + "id: " + id)
    if (formBody.isDefined) {
      val form = formBody.get
      val comment: String = form("comment").head
      val watching: Boolean = (form("watching").head.toLowerCase == "true")
      val cr = Create.commentRow(commentsId, comment, userID)
      println("cr: " + cr)
      val user = Await.result(daoRead.user(userID), 30.seconds)
      val questionId: Long = form("questionId").head.toLong
      println("questionId: " + questionId)
      Await.result(daoWrite.toggleWatchesByQuestionUserIdQuery(questionId, Some(userID), Some(watching)), 30.seconds)
      val commentsIdConfirmed = if (edit) {
        Await.result(daoWrite.updateCommentRow(cr), 30.seconds);
        id
      } else {
        Await.result(daoWrite.insertCommentRow(cr), 30.seconds)
      }
      println("commentsIdConfirmed: " + commentsIdConfirmed)
      if (!edit) {
        if (about == "question") Await.result(daoWrite.insertQuestionCommentRow(id, commentsIdConfirmed), 30.seconds)
        if (about == "answer") Await.result(daoWrite.insertAnswerCommentRow(id, commentsIdConfirmed), 30.seconds)
      }
      val cm = CommentUser(id, commentsIdConfirmed, cr.copy(id = commentsIdConfirmed), user.get)
      // send notification E-mails
      val questionResult = Await.result(daoRead.questionUser(questionId), 30.seconds)
      val questionUser = models.QuestionUser.tupled(questionResult.get)
      val watchers = Await.result(daoRead.watchUsersByQuestionId(questionUser.row.id), 30.seconds).filterNot(_._1.watcherId.getOrElse(0L) == user.get.id)
      val emailHTML = views.html.emails.commentNotification(user.get, questionUser, cm)
      val messages: Messages = request.messages
      for (w <- watchers) {
        val title: String = messages("comment_notification_mail", user.get.name, messages("site.name"))
        mailService.sendEmailAsync(w._2.email.get)(title, emailHTML.toString, "")
        daoWrite.inactivateWatcherRow(w._1.id)
      }
      Future.successful(Ok(views.html.snippets.comment(userID, questionId, cm)))
    } else {
      Future.successful(Ok(s"Missing form body for $about comment"))
    }
  }

  def answerVote(number: Long, direction: String) = silhouette.SecuredAction.async { implicit request =>
    val userID = request.identity.userID
    val up = (direction.toLowerCase() != "down")
    val voteDirection: Int = if (up) 1 else -1
    val karmaAmount = if (up) my_answer_voted_up else my_answer_voted_down
    val karmaType = if (up) "ANSWER_UPVOTE" else "ANSWER_DOWNVOTE"
    val q = Await.result(daoRead.answerUser(number), 30.seconds).map(q => AnswerUser.tupled(q))
    println("userID: " + userID)
    println("q.get.authorsRow: " + q.get.authorsRow)
    if (q.get.authorsRow.id == userID) {
      Future.successful(Conflict("vote.ownanswer"))
    } else {
      val vote = Await.result(daoRead.voteByAnswerVoter(number, userID), 30.seconds) // Option[(answerVotesRow, voteRow)]
      val oldVoteType = if (vote.isEmpty) "" else vote.get._2.`type`.getOrElse("")
      println("vote: " + vote)
      if (vote.isEmpty) {
        println("Vote was empty")
        Await.result(daoWrite.insertAnswerVoteRow(number, Create.voteRow(direction.toUpperCase(), userID)), 30.seconds)
        Await.result(daoWrite.incrementAnswerVotes(q.get.row.id, voteDirection), 30.seconds)
        Await.result(daoWrite.insertReputationeventRow(Create.reputationeventRow(q.get.row.id, karmaAmount, karmaType, q.get.row.authorId)), 30.seconds)
        if (!up) {
          Await.result(daoWrite.insertReputationeventRow(Create.reputationeventRow(q.get.row.id, downvoted_question_or_answer, "ANSWER_DOWNVOTING", Some(userID))), 30.seconds)
        } // add reputation events
        Future.successful(Ok((q.get.row.votecount + voteDirection).toString))
      } else if (oldVoteType == direction.toUpperCase()) { // same again so remove
        println("same again so remove")
        Await.result(daoWrite.removeAnswerVoteRow(number, userID), 30.seconds)
        Await.result(daoWrite.incrementAnswerVotes(q.get.row.id, (voteDirection * -1)), 30.seconds)
        Await.result(daoWrite.removeReputationeventRow(Create.reputationeventRow(q.get.row.id, karmaAmount, karmaType, q.get.row.authorId)), 30.seconds)
        if (!up) {
          Await.result(daoWrite.removeReputationeventRow(Create.reputationeventRow(q.get.row.id, downvoted_question_or_answer, "ANSWER_DOWNVOTING", Some(userID))), 30.seconds)
        }
        Future.successful(Ok((q.get.row.votecount + (voteDirection * -1)).toString))
      } else if ((voteDirection == 1 && oldVoteType.equals("DOWN")) || (voteDirection == -1 && oldVoteType.equals("UP"))) {
        println("change vote to opposite")
        val voteChange = voteDirection * 2
        val karmaChange = (my_answer_voted_up - my_answer_voted_down) * voteDirection
        val oldType = if (!up) "ANSWER_UPVOTE" else "ANSWER_DOWNVOTE"
        Await.result(daoWrite.updateAnswerVoteRow(q.get.row.id, userID, direction.toUpperCase()), 30.seconds)
        Await.result(daoWrite.incrementAnswerVotes(q.get.row.id, voteChange), 30.seconds)
        Await.result(daoWrite.updateReputationeventRow(q.get.row.id, karmaAmount, karmaChange, oldType, karmaType, q.get.row.authorId.get), 30.seconds)
        if (up) {
          Await.result(daoWrite.removeReputationeventRow(Create.reputationeventRow(q.get.row.id, downvoted_question_or_answer, "ANSWER_DOWNVOTING", Some(userID))), 30.seconds)
        } else {
          Await.result(daoWrite.insertReputationeventRow(Create.reputationeventRow(q.get.row.id, downvoted_question_or_answer, "ANSWER_DOWNVOTING", Some(userID))), 30.seconds)
        }
        Future.successful(Ok((q.get.row.votecount + voteChange).toString))
      } else {
        Future.successful(Ok((q.get.row.votecount).toString))
      }
    }
  }

  def commentVote(commentId: Long, direction: String) = silhouette.SecuredAction.async { implicit request =>
    val userID = request.identity.userID
    val karmaAmount = comment_voted_up
    val karmaType = "COMMENT_UPVOTE"
    val (cv, v) = Await.result(daoRead.commentVotesByCommentIdUserID(commentId, userID), 30.seconds).
      getOrElse((Create.commentVotesRow(commentId, 0L), Create.voteRow("UP", userID)))
    val comment = Await.result(daoRead.comment(commentId), 30.seconds).get
    println("userID: " + userID)
    println("vote: " + v)
    val voteDirection = if (v.id < 1) 1 else -1
    if (comment.authorId == userID) {
      println("vote.owncomment")
      Future.successful(Conflict("vote.owncomment"))
    } else {
      if (v.id < 1) {
        println("Comment Vote was empty")
        Await.result(daoWrite.insertCommentVoteRow(commentId, v), 30.seconds)
        Await.result(daoWrite.incrementCommentVotes(commentId, 1), 30.seconds)
        Await.result(daoWrite.insertReputationeventRow(Create.reputationeventRow(comment.id, karmaAmount, karmaType, Some(comment.authorId))), 30.seconds)
      } else { // same again so remove
        println("same comment vote again so remove")
        Await.result(daoWrite.removeCommentVoteRow(commentId, userID), 30.seconds)
        Await.result(daoWrite.incrementCommentVotes(commentId, -1), 30.seconds)
        Await.result(daoWrite.removeReputationeventRow(Create.reputationeventRow(comment.id, karmaAmount, karmaType, Some(comment.authorId))), 30.seconds)
      }
      Future.successful(Ok((comment.votecount + voteDirection).toString))
    }
  }

  def history(questionId: Long) = silhouette.UserAwareAction.async { implicit request =>
    val questionResult = Await.result(daoRead.questionUser(questionId), 30.seconds)
    if (questionResult.isEmpty) {
      Future.successful(Ok("Question does not exist"))
    } else {
      val question = QuestionUser.tupled(questionResult.get)
      val qiu = Await.result(daoRead.questioninformationUsersSortedById(questionId), 30.seconds)
      question.informationUserTagRows = qiu.map {
        case (qi, u) =>
          (qi, u, Await.result(daoRead.questionsTags(Seq(qi.id)), 30.seconds).map(q => QuestionTag.tupled(q).tagxRow))
      }
      Future.successful(Ok(views.html.history(request.identity, question, config)))
    }
  }

  def mark(id: Long) = silhouette.SecuredAction.async { implicit request =>
    val userID = request.identity.userID
    val question: Boolean = request.uri.contains("/question/")
    val formBody: Option[Map[String, Seq[String]]] = request.body.asFormUrlEncoded
    formBody.map { form =>
      val reason: Option[String] = if (form("reason").head.length > 0) Some(form("reason").head) else None
      if (question) {
        val qFlagIds = Await.result(daoRead.questionFlagsIds(userID, id), 30.seconds)
        if (qFlagIds.length < 1) Await.result(daoWrite.insertQuestionFlag(userID, id, Create.flagRow(reason, Some(form("flagType").head), userID)), 30.seconds)
      } else {
        val cFlagIds = Await.result(daoRead.commentFlagsIds(userID, id), 30.seconds)
        if (cFlagIds.length < 1) Await.result(daoWrite.insertCommentFlag(userID, id, Create.flagRow(reason, Some(form("flagType").head), userID)), 30.seconds)
      }
    }.getOrElse {
      println("Missing form body for mark flag")
    }
    Future.successful(Ok("1"))
  }

  def watched(number: Long) = silhouette.SecuredAction.async { implicit request =>
    val updated = Await.result(daoWrite.toggleWatchesByQuestionUserIdQuery(number, Some(request.identity.userID)), 30.seconds)
    if (updated < 1) Await.result(daoWrite.insertWatcher(number, request.identity.userID), 30.seconds)
    Future.successful(Ok(updated.toString))
  }

  // Future.successful(Ok(s"""{"item":{"name":"${tag}tag1"}}""")
  def searchTags(tag: String) = silhouette.UserAwareAction.async { implicit request =>
    val tags = tagDAOImpl.startsWith(tag)
    val applicableTags = for (t <- tags) yield s"""{"id":"${t}","text":"${t}"}"""
    val applicableTagsJSON = "[" + applicableTags.mkString(", ") + "]"
    Future.successful(Ok(applicableTagsJSON))
  }

  def usageTerms() = silhouette.UserAwareAction.async { implicit request => Future.successful(Ok(views.html.usageTerms(request.identity, config))) }
  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def index(sort: String, q: String, p: Long) = silhouette.UserAwareAction.async { implicit request =>
    val page: Long = if (p < 1L) 1L else p
    val numberOfQuestions = sort match {
      case "unanswered" => Await.result(daoRead.numberOfUnansweredQuestions, 30.seconds)
      case "unsolved" => Await.result(daoRead.numberOfUnsolvedQuestions, 30.seconds)
      case "search" => Await.result(daoRead.numberOfQuestionsByMatch(q), 30.seconds).head
      case _ => Await.result(daoRead.numberOfQuestions, 30.seconds)
    }
    val futureQuestions = sort match {
      case "voted" => daoRead.questionsSortedByVoted(page)
      case "answered" => daoRead.questionsSortedByAnswered(page)
      case "viewed" => daoRead.questionsSortedByVisited(page)
      case "unanswered" => daoRead.unansweredQuestionsSortedByLastUpdate(page)
      case "unsolved" => daoRead.unsolvedQuestionsSortedByLastUpdate(page)
      case "search" => daoRead.questionsByMatch(q, page)
      case _ => daoRead.questionsSortedByLastUpdate(page)
    }
    val questions: Seq[QuestionUser] = Await.result(futureQuestions, 30.seconds).map(q => QuestionUser.tupled(q))
    val questionsTags: Seq[QuestionTag] = Await.result(daoRead.questionsTags(questions.map(_.informationRow.id)), 30.seconds).map(q => QuestionTag.tupled(q))
    questions.foreach(q => q.tags = questionsTags.filter(_.questioninformationId == q.row.informationId))
    val recentTags: Seq[models.daos.slick.Tables.TagxRow] = Await.result(daoRead.recentTags(), 30.seconds)
    request.identity match {
      case Some(user) =>
        user.account match {
          //case models.Account.Gratis if request.flash.isEmpty =>
          // Future.successful(Ok(views.html.activateAccount(user.email.getOrElse(""))))
          case _ =>
            Future.successful(Ok(views.html.questions(request.identity, numberOfQuestions, questions, recentTags, config)))
        }
      case _ => Future.successful(Ok(views.html.questions(None, numberOfQuestions, questions, recentTags, config)))
    }
  }

  def ranking(ranking: String, p: Long) = silhouette.UserAwareAction.async { implicit request =>
    val numberOfUsers = Await.result(daoRead.numberOfUsers, 30.seconds)
    val authorsRow: Seq[models.daos.slick.Tables.UsersRow] = Await.result(daoRead.usersSortedByKarma(p), 30.seconds)
    Future.successful(Ok(views.html.ranking(request.identity, numberOfUsers, authorsRow, config)))
  }

  def rankingTag(tag: String) = silhouette.UserAwareAction.async { implicit request =>
    import models.daos.slick.QA
    val tagNo: Long = Await.result(daoRead.tagNo(tag), 30.seconds).getOrElse(0)
    val oneMonthAgo = LocalDateTime.now().plusMonths(-1)
    val answersRanking = Await.result(daoRead.tagRanking(tagNo, QA.ANSWER), 30.seconds).map(tr => TagRanking.tupled(tr))
    val answers30DayRanking = answersRanking.map { tr =>
      val rew = tr.reputationEventRows.filter(_.date.isDefined).filter(rew => rew.date.get.toLocalDateTime().compareTo(oneMonthAgo) > 0);
      TagRanking(rew.map(_.karmareward).sum, tr.userId, tr.user, rew)
    }
    val questionsRanking = Await.result(daoRead.tagRanking(tagNo, QA.QUESTION), 30.seconds).map(tr => TagRanking.tupled(tr))
    val questions30DayRanking = questionsRanking.map { tr =>
      val rew = tr.reputationEventRows.filter(_.date.isDefined).filter(rew => rew.date.get.toLocalDateTime().compareTo(oneMonthAgo) > 0);
      TagRanking(rew.map(_.karmareward).sum, tr.userId, tr.user, rew)
    }
    println("questionsRanking: " + questionsRanking.mkString("\r\n"))
    Future.successful(Ok(views.html.rankingtag(request.identity, tag, answers30DayRanking, answersRanking, questions30DayRanking, questionsRanking, config)))
  }

  def search(query: String) = silhouette.UserAwareAction.async { implicit request =>

    Future.successful(Ok(Await.result(daoRead.questionsByMatch("tagging", 5), 30.seconds).toString))
  }

  def tag(tag: String, p: Long) = silhouette.UserAwareAction.async { implicit request =>
    val tagNo: Long = Await.result(daoRead.tagNo(tag), 30.seconds).getOrElse(0)
    val questions = Await.result(daoRead.questionsByTagSorted(tagNo, p), 30.seconds).map(q => QuestionUser.tupled(q))
    val recentTags: Seq[models.daos.slick.Tables.TagxRow] = Await.result(daoRead.recentTags(), 30.seconds)
    Future.successful(Ok(views.html.tag(request.identity, tag, Math.max(1, questions.length), questions, recentTags, config)))
  }

}
