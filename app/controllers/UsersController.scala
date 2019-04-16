package controllers

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

import com.mohiva.play.silhouette.api.LoginEvent
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry

import forms.UserEditForm
import javax.inject.Inject
import models.VoteTitleModel
import models.daos.slick.DAORead
import models.services.UserService
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.I18nSupport
import play.api.i18n.Messages
import play.api.libs.ws.WSClient
import play.api.Logging
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents
import utils.Sanitizer.safeHTML
import utils.Sanitizer.safeText
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

class UsersController @Inject() (cc: ControllerComponents, implicit val ec: ExecutionContext, ws: WSClient,
  config: Configuration, dbConfigProvider: DatabaseConfigProvider, userService: UserService, socialProviderRegistry: SocialProviderRegistry,
  silhouette: Silhouette[DefaultEnv]) extends AbstractController(cc) with I18nSupport with Logging {

  val dao = new DAORead(dbConfigProvider, config)

  val tagDAOImpl = new models.daos.TagDAOImpl
  tagDAOImpl.apply(Await.result(dao.tags, 30.seconds))

  def users(number: Long, name: String) = silhouette.SecuredAction.async { implicit request =>
    logger.info(request.identity.email.getOrElse("Unidentified") + " displayed user number " + number)
    val displayUser = Await.result(dao.user(number), 30.seconds)
    displayUser match {
      case Some(displayUser) => 
        val futureNoQuestions = dao.numberofVotesQuestionsByAuthorId(number)
        val futureQuestions = dao.votesQuestionsByAuthorIdSortByVoteCount(number, 1)
        val futureNoAnswers = dao.numberofVotesAnswersByAuthorId(number)
        val futureAnswers = dao.votesAnswersByAuthorIdSortByVoteCount(number, 1)
        val futureNoWatches = dao.numberofVotesWatchesByAuthorId(number)
        val futureWatches = dao.votesWatchesByAuthorIdSortByVoteCount(number, 1)
        val futureReputationEvents = dao.votesReputationEventByAuthorIdSortByLastUpdateAt(number, config.get[Long]("elements_per_page"))
        val futureTags = dao.tagsByAuthorIdSortByUsageCount(number)
        for {
          noQuestions <- futureNoQuestions
          questions <- futureQuestions
          noAnswers <- futureNoAnswers 
          answers <- futureAnswers
          noWatches <- futureNoWatches
          watches <- futureWatches
          reputationEvents <- futureReputationEvents
          tags <- futureTags
        } yield Ok(views.html.users(Some(request.identity), displayUser, noQuestions, questions.map(q => VoteTitleModel.tupled(q)), noAnswers, answers.map(a => VoteTitleModel.tupled(a)), 
            noWatches, watches.map(a => VoteTitleModel.tupled(a)), reputationEvents.map(a => VoteTitleModel(a._1, a._2, a._3.toLong, a._4, a._5)), tags, config))
      case _ => Future.successful(Ok("User does not exist"))
    }
  }

  def usersEdit(user: Long) = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.usersEdit(UserEditForm.userToFilledForm(request.identity), request.identity, config)))
  }
  
  def usersDelete(user: Long) = silhouette.SecuredAction.async { implicit request =>
    if(request.identity.userID == user){
      logger.info(request.identity.email.getOrElse("Unidentified") + " deleted their own user number " + user)
      val updatedUser = request.identity.copy(name = Some("*****"), website = Some(""), karma = 0, photoUri = Some("https://secure.gravatar.com/avatar/"),
          sluggedName = Some("*****"), birthDate = None, location = None, markedAbout = None, about = None, isSubscribed = false, receiveAllUpdates = false, deleted = true)
        userService.save(updatedUser).map {
          savedUser => silhouette.env.eventBus.publish(LoginEvent(updatedUser, request)); Redirect(routes.SignInController.signOut)
        } recover {
          case t => Redirect(routes.SignInController.signOut).flashing("error" -> Messages("Unable to update user data: " + t))
        }
    } else {
      logger.info("      " + "\t" + "Delete attempt from " + request.remoteAddress + "\t" + request.headers.get("User-Agent").getOrElse("No User-Agent"))
      logger.info(request.identity.email.getOrElse("Unidentified") + " tried to delete user number " + user)
      Future.successful(Redirect(routes.SignInController.signOut))
    }
  }

  def usersEdited(user: Long) = silhouette.SecuredAction.async { implicit request =>
    UserEditForm.form.bindFromRequest.fold(
      formWithErrors => { Future.successful(BadRequest(views.html.usersEdit(formWithErrors, request.identity, config))) },
      uForm => {
        val updatedUser = request.identity.copy(name = Some(safeText(uForm.name)), website = Some(safeText(uForm.website)), birthDate = uForm.birthDate.map(_.atStartOfDay),
          location = Some(safeText(uForm.location)), markedAbout = Some(safeHTML(uForm.description)), about = Some(safeText(uForm.description)), isSubscribed = uForm.isSubscribed,
          receiveAllUpdates = uForm.receiveAllUpdates)
        userService.save(updatedUser).map {
          savedUser => silhouette.env.eventBus.publish(LoginEvent(updatedUser, request)); Redirect(routes.UsersController.users(updatedUser.userID, updatedUser.sluggedName.getOrElse("")))
        } recover {
          case t => BadRequest(views.html.usersEdit(UserEditForm.form.bindFromRequest, request.identity, config)).flashing("error" -> Messages("Unable to update user data: " + t))
        }
      })
  }

  def usersPostsByType(number: Long, field: String, p: Long, order: String) = silhouette.UserAwareAction.async { implicit request =>
    val votesTitles = (field, order) match {
      case ("questions", "ByVotes") => Await.result(dao.votesQuestionsByAuthorIdSortByVoteCount(number, p), 30.seconds).map(q => VoteTitleModel.tupled(q))
      case ("questions", "ByDate") => Await.result(dao.votesQuestionsByAuthorIdSortByLastUpdateAt(number, p), 30.seconds).map(q => VoteTitleModel.tupled(q))
      case ("answers", "ByVotes") => Await.result(dao.votesAnswersByAuthorIdSortByVoteCount(number, p), 30.seconds).map(vt => VoteTitleModel.tupled(vt))
      case ("answers", "ByDate") => Await.result(dao.votesAnswersByAuthorIdSortByLastUpdateAt(number, p), 30.seconds).map(vt => VoteTitleModel.tupled(vt))
      case ("watched", _) => Await.result(dao.votesWatchesByAuthorIdSortByVoteCount(number, p), 30.seconds).map(vt => VoteTitleModel.tupled(vt))
      case (_, _) => throw new IllegalArgumentException("unexpected arguments to ApplicationController.usersPostsByType")
    }
    val html = for (vt <- votesTitles) yield s"""	    <li class="ellipsis advanced-data-line">
	    	<span class="counter">${vt.voteCount}</span> 
			<a href="/${vt.id}-${vt.sluggedTitle}">${vt.title}</a>
		</li>"""
    Future.successful(Ok(html.mkString("\r\n")))
  }

  def usersReputation(number: Long, name: String) = silhouette.UserAwareAction.async { implicit request =>
    val displayUser = Await.result(dao.user(number), 30.seconds)
    val reputationEvents = Await.result(dao.votesReputationEventByAuthorIdSortByLastUpdateAt(number, 200), 30.seconds).map(a => VoteTitleModel(a._1, a._2, a._3.toLong, a._4, a._5))
    displayUser match {
      case Some(displayUser) => Future.successful(Ok(views.html.usersReputation(request.identity, displayUser, reputationEvents, config)))
      case _ => Future.successful(Ok("User does not exist"))
    }
  }

}


