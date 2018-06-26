package controllers

import java.time.LocalDateTime

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry

import forms.SignUpForm
import javax.inject.Inject
import models.Account
import models.Account.Admin
import models.User
import models.services.AuthTokenService
import models.services.UserService
import play.api.Configuration
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.i18n.Messages
import play.api.libs.ws.WSClient
import play.api.libs.ws.WSResponse
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext 
import scala.concurrent.Future
import utils.MailService
import utils.Sanitizer.safeText
import utils.Sanitizer.slugify
import utils.auth.DefaultEnv

/**
 * The sign up controller.
 *
 * @param messagesApi The Play messages API.
 * @param env The Silhouette environment.
 * @param userService The user service implementation.
 * @param authInfoRepository The auth info repository implementation.
 * @param avatarService The avatar service implementation.
 * @param passwordHasher The password hasher implementation.
 */
class SignUpController @Inject() (
  components: ControllerComponents,
  ws: WSClient,
  socialProviderRegistry: SocialProviderRegistry,
  config: Configuration,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  authTokenService: AuthTokenService,
  avatarService: AvatarService,
  passwordHasherRegistry: PasswordHasherRegistry,
  passwordHasher: PasswordHasher,
  userDAOSlick: models.daos.slick.UserDAOSlick)(
  implicit
  ex: ExecutionContext)
  extends AbstractController(components) with I18nSupport {

  val google_datasitekey = config.get[String]("google.datasitekey")
  val google_secret = config.get[String]("google.secret")
  val emailsignup = config.get[Boolean]("feature.emailsignup")
  val mailService = new MailService(config)

  /**
   * Views the Sign Up page.
   *
   * @return The result to display.
   */
  def view = silhouette.UserAwareAction.async { implicit request =>
    Future.successful(Ok(views.html.signUp(SignUpForm.form, request.identity.getOrElse(null), socialProviderRegistry, google_datasitekey)))
  }

  /**
   * Registers a new user.
   *
   * @return The result to display.
   */
  def submit = silhouette.UserAwareAction.async { implicit request =>
    Logger.info("attempt to add new user from " + request.remoteAddress)
    SignUpForm.form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.signUp(formWithErrors, request.identity.getOrElse(null), socialProviderRegistry, google_datasitekey))),
      data => {
        val googleResponse: String = if(google_datasitekey.length < 1 || google_secret.length < 1) {
          "true,"
        } else {
          Await.result(ws.url("https://www.google.com/recaptcha/api/siteverify")
          .post(Map("secret" -> Seq(google_secret),"response" -> Seq(data.grecaptcharesponse),"remoteip" -> Seq(request.remoteAddress))), 30.seconds).body.toString
        }
        if(!googleResponse.contains("true,")){
          Logger.warn("Possible robo signup attempt " + googleResponse)
          Future.successful(Redirect(routes.SignUpController.view()).flashing("error" -> Messages("You could be a robot. Please select not a Robot." )))
        } else {
        val loginInfo = LoginInfo("BRUTAL", data.email)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) =>
            Logger.warn(data.email + "\ttrying to add already existing account")
            val url = routes.SignInController.signIn("").absoluteURL()
            mailService.sendEmailAsync(data.email)(Messages("email.already.signed.up.subject"), views.html.emails.alreadySignedUp(user, url).body, views.txt.emails.alreadySignedUp(user, url).body);
            if(user.deleted){
              Future.successful(Redirect(routes.SignInController.signIn("")).flashing("info" -> Messages("email.already.deleted.txt.text", data.email)))
            }else {
              Future.successful(Redirect(routes.SignInController.signIn("")).flashing("info" -> Messages("email.already.signed.up.txt.text", data.email)))
            }
          case None =>
            val hashedAuthInfo = passwordHasher.hash(data.password)
            Logger.info(data.email + "\tStarting to add new account")
            val name = safeText(data.name)
            val user = User(
              0,
              loginInfo,
              None,
              None,
              Account.Gratis, // use confirmedEmail value
              Some(LocalDateTime.now()),
              Some(data.email),
              Some(utils.Digester.encrypt(data.password)),
              false,
              true,
              0,
              None,
              None,
              false,
              Some(name),
              Some(LocalDateTime.now()),
              None,
              Some(slugify(name)),
              None,
              None,
              None,
              false,
              false)
            for {
              avatar <- avatarService.retrieveURL(data.email)
              theUser <- userService.save(user.copy(photoUri = avatar))
              authInfo <- authInfoRepository.add(loginInfo, hashedAuthInfo)
              authToken <- authTokenService.create(user.userID)
              authenticator <- silhouette.SecuredRequestHandler.environment.authenticatorService.create(loginInfo)
              value <- silhouette.SecuredRequestHandler.environment.authenticatorService.init(authenticator)
              //result <- silhouette.SecuredRequestHandler.environment.authenticatorService.embed(value, Redirect(routes.ApplicationController.index("")))
            } yield {
              if (emailsignup) {
                Redirect(routes.ActivateAccountController.send(data.email))
              } else {
                Redirect(routes.SignInController.signIn("")).flashing("info" -> Messages("account.activated", data.email))
              }
            }
        }
      }
      })
  }

}
