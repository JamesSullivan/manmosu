package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

import com.mohiva.play.silhouette.api.Authenticator.Implicits.RichDateTime
import com.mohiva.play.silhouette.api.LoginEvent
import com.mohiva.play.silhouette.api.LogoutEvent
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry

import forms.SignInForm
import javax.inject.Inject
import models.services.UserService
import net.ceedubs.ficus.Ficus._
import play.api.Configuration
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.i18n.Messages
import play.api.mvc.AbstractController
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import utils.auth.DefaultEnv



/**
 * The `Sign In` controller.
 *
 * @param components             The Play controller components.
 * @param silhouette             The Silhouette stack.
 * @param userService            The user service implementation.
 * @param credentialsProvider    The credentials provider.
 * @param socialProviderRegistry The social provider registry.
 * @param configuration          The Play configuration.
 * @param clock                  The clock instance.
 * @param webJarsUtil            The webjar util.
 */
class SignInController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  credentialsProvider: CredentialsProvider,
  socialProviderRegistry: SocialProviderRegistry,
  configuration: Configuration,
  clock: Clock
)(
  implicit
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport with Logging{


  
  /**
   * Views the `Sign In` page.
   *
   * @return The result to display.
   */
  def view = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(views.html.signIn(SignInForm.form, socialProviderRegistry)))
  }
  
  /**	
   * Handles the Sign In action.
   *
   * @return The result to display.
   */
  def signIn(path: String) = silhouette.UserAwareAction.async { implicit request =>
    val referer = request.headers.get("referer")
    println("signIn referer: " + referer.getOrElse("None"))
    request.identity match {
     // case Some(user) if(user.account.id > 0) =>
      case Some(user) =>
        logger.info(user.email.getOrElse("") + "\t" + "logged in from " + request.remoteAddress + "\t" + request.headers.get("User-Agent").getOrElse("No User-Agent"))
        Future.successful(Redirect(routes.ApplicationController.index("")))
    //  case Some(user) if(user.account.id < 1 && request.flash.isEmpty) =>
    //    logger.info(user.email.getOrElse("") + "\t" + "unactivated user from " + request.remoteAddress + "\t" + request.headers.get("User-Agent").getOrElse("No User-Agent"))
    //    Future.successful(Ok(views.html.activateAccount(user.email.getOrElse(""))))
      case _ =>
        logger.info("      " + "\t" + "login attempt from " + request.remoteAddress + "\t" + request.headers.get("User-Agent").getOrElse("No User-Agent"))
        Future.successful(Ok(views.html.signIn(SignInForm.form, socialProviderRegistry)))
    }
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = silhouette.SecuredAction.async { implicit request =>
    val result = Redirect(routes.ApplicationController.index(""))
    logger.info(request.identity.email.getOrElse("Unidentified") + "\tsigning out\t")
    silhouette.SecuredRequestHandler.environment.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.SecuredRequestHandler.environment.authenticatorService.discard(request.authenticator, result)
  }


}
