package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import org.webjars.play.WebJarsUtil

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.util.PasswordInfo

import forms.ResetPasswordForm
import javax.inject.Inject
import models.daos.slick.PasswordInfoDAOSlick
import models.services.AuthTokenService
import models.services.UserService
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.i18n.Messages
import play.api.mvc.AbstractController
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import utils.auth.DefaultEnv

/**
 * The `Reset Password` controller.
 *
 * @param components             The Play controller components.
 * @param silhouette             The Silhouette stack.
 * @param userService            The user service implementation.
 * @param authInfoRepository     The auth info repository.
 * @param passwordHasherRegistry The password hasher registry.
 * @param passwordHasher				 The password hasher.
 * @param authTokenService       The auth token service implementation.
 * @param passwordInfoDAOSlick   Database access implementation
 * @param webJarsUtil            The webjar util.
 * @param ex                     The execution context.
 */
class ResetPasswordController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  passwordHasherRegistry: PasswordHasherRegistry,
  passwordHasher: PasswordHasher,
  authTokenService: AuthTokenService,
  passwordInfoDAOSlick: PasswordInfoDAOSlick)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext) extends AbstractController(components) with I18nSupport with Logging {

  /**
   * Views the `Reset Password` page.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def view(token: Long) = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    println("reset password view")
    authTokenService.validate(token).map {
      case Some(_) => Ok(views.html.resetPassword(ResetPasswordForm.form, token))
      case None => Redirect(routes.SignInController.signIn("")).flashing("error" -> Messages("invalid.reset.link"))
    }
  }

  /**
   * Resets the password.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def submit(token: Long) = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    logger.info("starting to reset password for " + token)
    authTokenService.validate(token).flatMap {
      case Some(authToken) =>
        ResetPasswordForm.form.bindFromRequest.fold(
          form => Future.successful(BadRequest(views.html.resetPassword(form, token))),
          password => userService.retrieve(authToken.userID).flatMap {
            case Some(user) =>
              logger.info(user.email.getOrElse("") + "\t resetting password" + token)
              val passwordInfo = passwordHasher.hash(password)
              println("passwordInfo: " + passwordInfo)
              val loginInfo = LoginInfo("BRUTAL", user.email.getOrElse(""))
              println("loginInfo: " + loginInfo)
              authInfoRepository.update[PasswordInfo](loginInfo, passwordInfo).map { _ =>
                Redirect(routes.SignInController.signIn("")).flashing("success" -> Messages("password.reset"))
              }
              Future.successful(Redirect(routes.SignInController.signIn("")).flashing("success" -> Messages("password.reset")))
            case _ =>
              logger.info("Not matching for reset " + token)
              Future.successful(Redirect(routes.SignInController.signIn("")).flashing("error" -> Messages("invalid.reset.link")))
          })
      case None => Future.successful(Redirect(routes.SignInController.signIn("")).flashing("error" -> Messages("invalid.reset.link")))
    }
  }
}
