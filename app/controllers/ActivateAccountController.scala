package controllers

import java.net.URLDecoder

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import javax.inject.Inject
import models.services.AuthTokenService
import models.services.UserService
import play.api.Configuration
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.i18n.Messages
import play.api.libs.mailer.MailerClient
import play.api.mvc.AbstractController
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import utils.MailService
import utils.auth.DefaultEnv

/**
 * The `Activate Account` controller.
 *
 * @param components       The Play controller components.
 * @param silhouette       The Silhouette stack.
 * @param userService      The user service implementation.
 * @param authTokenService The auth token service implementation.
 * @param mailerClient     The mailer client.
 * @param ex               The execution context.
 */
class ActivateAccountController @Inject() (
  components: ControllerComponents,
  config: Configuration,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  authTokenService: AuthTokenService,
  mailerClient: MailerClient,
  userDAOSlick: models.daos.slick.UserDAOSlick)(
  implicit
  ex: ExecutionContext) extends AbstractController(components) with I18nSupport with Logging{

  val mailService = new MailService(config)
  /**
   * Sends an account activation email to the user with the given email.
   *
   * @param email The email address of the user to send the activation mail to.
   * @return The result to display.
   */
  def send(email: String) = silhouette.UserAwareAction.async { implicit request: Request[AnyContent] =>
    val decodedEmail = URLDecoder.decode(email, "UTF-8")
    val loginInfo = LoginInfo(CredentialsProvider.ID, decodedEmail)
    val result = Redirect(routes.SignInController.signIn("")).flashing("info" -> Messages("activation.email.sent", decodedEmail))
    logger.info("send loginInfo: " + loginInfo.toString)
    userService.retrieve(loginInfo).flatMap {
      case Some(user) if user.account.id < 1 =>
        logger.info(user.email.getOrElse("") + "\t sending activation E-mail")
        authTokenService.create(user.userID).map { authToken =>
          val url = routes.ActivateAccountController.activate(authToken.id).absoluteURL()
          mailService.sendEmailAsync(decodedEmail)(Messages("email.activate.account.subject"), views.html.emails.activateAccount(user, url).body, views.txt.emails.activateAccount(user, url).body);
          result
        }
      case None =>
        logger.info(loginInfo.toString + "\t unable to send activation E-mail")
        Future.successful(result)
    }
  }

  /**
   * Activates an account.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def activate(token: Long) = silhouette.UserAwareAction.async { implicit request: Request[AnyContent] =>

    authTokenService.validate(token).flatMap {
      case Some(authToken) => userService.retrieve(authToken.userID).flatMap {
        case Some(user) if (user.loginInfo.providerID == "BRUTAL" || user.loginInfo.providerID == CredentialsProvider.ID) =>
          logger.info(user.email.getOrElse("") + "\tactivating account")
          userService.save(user.copy(account = models.Account.Free)).map { _ =>
            Redirect(routes.SignInController.signIn("")).flashing("success" -> Messages("account.activated"))
          }
        case Some(user) if user.loginInfo.providerID != CredentialsProvider.ID =>
          logger.info(user.email.getOrElse("") + "\tunable to activate account  providerID: " + user.loginInfo.providerID + " / " + CredentialsProvider.ID)
          Future.successful(Redirect(routes.SignInController.signIn("")).flashing("error" -> Messages("invalid.activation.link")))
        case _ => Future.successful(Redirect(routes.SignInController.signIn("")).flashing("error" -> Messages("invalid.activation.link")))
      }
      case None => Future.successful(Redirect(routes.SignInController.signIn("")).flashing("error" -> Messages("invalid.activation.link")))
    }
  }
}
