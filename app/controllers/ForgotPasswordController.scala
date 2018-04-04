package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import org.webjars.play.WebJarsUtil

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import forms.ForgotPasswordForm
import javax.inject.Inject
import models.services.AuthTokenService
import models.services.UserService
import play.api.Configuration
import play.api.Logger
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
 * The `Forgot Password` controller.
 *
 * @param components       The Play controller components.
 * @param silhouette       The Silhouette stack.
 * @param userService      The user service implementation.
 * @param authTokenService The auth token service implementation.
 * @param mailerClient     The mailer client.
 * @param webJarsUtil      The webjar util.
 * @param ex               The execution context.
 */
class ForgotPasswordController @Inject() (
  components: ControllerComponents,
  config: Configuration,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  authTokenService: AuthTokenService,
  mailerClient: MailerClient)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext) extends AbstractController(components) with I18nSupport {

  val mailService = new MailService(config)

  /**
   * Views the `Forgot Password` page.
   *
   * @return The result to display.
   */
  def view = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(views.html.forgotPassword(ForgotPasswordForm.form)))
  }

  /**
   * Sends an email with password reset instructions.
   *
   * It sends an email to the given address if it exists in the database. Otherwise we do not show the user
   * a notice for not existing email addresses to prevent the leak of existing email addresses.
   *
   * @return The result to display.
   */
  def submit = silhouette.UserAwareAction.async { implicit request: Request[AnyContent] =>
    Logger.info("Starting forgot password")
    ForgotPasswordForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.forgotPassword(form))),
      email => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, email)
        Logger.info("loginInfo: " + loginInfo)
        val result = Redirect(routes.SignInController.signIn("")).flashing("info" -> Messages("forgot_password.sent_mail", email))
        userService.retrieve(loginInfo).flatMap {
          case Some(user) if user.email.isDefined =>
            Logger.info(user.email.getOrElse("") + "\tabout to E-mail password")
            authTokenService.create(user.userID).map { authToken =>
              val url = routes.ResetPasswordController.view(authToken.id).absoluteURL()
              Logger.info(user.email.getOrElse("") + "\tE-mailing password")
              mailService.sendEmailAsync(email)(
                Messages("email.reset.password.subject"),
                views.html.emails.resetPassword(user, url).body,
                views.txt.emails.resetPassword(user, url).body);
              result
            }
          case None =>
            Logger.info("Forgot password - problem E-mailing password to email")
            Future.successful(Redirect(routes.SignInController.signIn("")).flashing("error" -> (Messages("forgot_password.invalid_email") + "  " + email)))
        }
      })
  }
}
