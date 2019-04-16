package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{ Clock, Credentials }
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import utils.auth.DefaultEnv

import forms.SignInForm
import models.services.UserService
import net.ceedubs.ficus.Ficus._
import org.webjars.play.WebJarsUtil
import play.api.mvc.{ AbstractController, ControllerComponents }
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages}
import play.api.Logging

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext 

/**
 * The credentials auth controller.
 *
 * @param env The Silhouette environment.
 * @param userService The user service implementation.
 * @param authInfoRepository The auth info repository implementation.
 * @param credentialsProvider The credentials provider.
 * @param socialProviderRegistry The social provider registry.
 * @param config The Play configuration.
 * @param clock The clock instance.
 */
class CredentialsAuthController @Inject() (
  cc: ControllerComponents,
  implicit val ec: ExecutionContext,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  credentialsProvider: CredentialsProvider,
  socialProviderRegistry: SocialProviderRegistry,
  config: Configuration,
  clock: Clock)(
  implicit
  webJarsUtil: WebJarsUtil) extends AbstractController(cc) with I18nSupport with Logging {

  val emailsignup = config.get[Boolean]("feature.emailsignup")

  /**
   * Authenticates a user against the credentials provider.
   *
   * @return The result to display.
   */
  def authenticate = Action.async { implicit request =>
    SignInForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.signIn(form, socialProviderRegistry))),
      data => {
        val credentials = Credentials(data.email, data.password)
        logger.info(credentials.identifier + "\tCAC credentials")
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          logger.info(loginInfo.providerKey + "\tCAC loginInfo " + loginInfo.providerID)
          val loginInfo2 = loginInfo.copy(providerID = "BRUTAL")
          //println("CAC authenticate request.uri: " + request.uri)
          //println("CAC authenticate data.originaluri: " + data.originaluri )
          val uri = data.originaluri match {
            case oldURI if oldURI.startsWith("/signIn") => "/"
            case _ => data.originaluri
          }
          val result = Redirect(uri)
          userService.retrieve(loginInfo2).flatMap {
            case Some(user) if (user.deleted) =>
              Future.failed(new IdentityNotFoundException("This user has already been deleted!"))
            case Some(user) if (user.account.id < 1 && emailsignup) =>
              Future.successful(Ok(views.html.activateAccount(user.email.getOrElse(""))))
            case Some(user) =>
              val c = config.underlying
              silhouette.SecuredRequestHandler.environment.authenticatorService.create(loginInfo2).map {
                case authenticator if data.rememberMe =>
                  authenticator.copy(
                    expirationDateTime = clock.now + c.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry"),
                    idleTimeout = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout"),
                    cookieMaxAge = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.cookieMaxAge"))
                case authenticator => authenticator
              }.flatMap { authenticator =>
                silhouette.SecuredRequestHandler.environment.eventBus.publish(LoginEvent(user, request))
                silhouette.SecuredRequestHandler.environment.authenticatorService.init(authenticator).flatMap { v =>
                  silhouette.SecuredRequestHandler.environment.authenticatorService.embed(v, result)
                }
              }
            case None =>
              Future.failed(new IdentityNotFoundException("Couldn't find user"))
          }
        }
      }.recover {
        case e: ProviderException =>
          logger.info(data.email + "\tLogin failed using " + data.password);
          Redirect(routes.SignInController.signIn("")).flashing("error" -> (Messages("invalid.credentials") + "  " + e.getMessage))
      })
  }
}