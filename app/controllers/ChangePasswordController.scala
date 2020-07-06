package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import org.webjars.play.WebJarsUtil

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import forms.ChangePasswordForm
import javax.inject.Inject
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.i18n.Messages
import play.api.mvc.AbstractController
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import utils.auth.DefaultEnv
import utils.auth.WithProvider

/**
 * The `Change Password` controller.
 *
 * @param components             The Play controller components.
 * @param silhouette             The Silhouette stack.
 * @param credentialsProvider    The credentials provider.
 * @param authInfoRepository     The auth info repository.
 * @param passwordHasherRegistry The password hasher registry.
 * @param webJarsUtil            The webjar util.
 * @param ex                     The execution context.
 */
class ChangePasswordController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  credentialsProvider: CredentialsProvider,
  authInfoRepository: AuthInfoRepository,
  passwordHasherRegistry: PasswordHasherRegistry,
  passwordHasher: PasswordHasher)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext) extends AbstractController(components) with I18nSupport with Logging {

  /**
   * Views the `Change Password` page.
   *
   * @return The result to display.
   */
  def view = silhouette.SecuredAction.async {
    implicit request =>
      Future(Ok(views.html.changePassword(ChangePasswordForm.form, request.identity)))
  }

  /**
   * Changes the password.
   *
   * @return The result to display.
   */
  def submit = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID)).async {
    implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
      logger.info(request.identity.email.getOrElse("") + "\t changing password")
      ChangePasswordForm.form.bindFromRequest().fold(
        form => Future.successful(BadRequest(views.html.changePassword(form, request.identity))),
        password => {
          val (currentPassword, newPassword) = password
          val credentials = Credentials(request.identity.email.getOrElse(""), currentPassword)
          credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
            // val passwordInfo = passwordHasherRegistry.current.hash(newPassword)
            val passwordInfo = passwordHasher.hash(newPassword)
            val loginInfo = LoginInfo("BRUTAL", request.identity.email.getOrElse(""))
            println("passwordInfo: " + passwordInfo)
            authInfoRepository.update[PasswordInfo](loginInfo, passwordInfo).map { _ =>
              Redirect(routes.ChangePasswordController.view()).flashing("success" -> Messages("password.changed"))
            }
          }.recover {
            case _: ProviderException =>
              Redirect(routes.ChangePasswordController.view()).flashing("error" -> Messages("current.password.invalid"))
          }
        })
  }
}
