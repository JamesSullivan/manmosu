package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginEvent
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfileBuilder
import com.mohiva.play.silhouette.impl.providers.SocialProvider
import com.mohiva.play.silhouette.impl.providers.OAuth2Provider
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry

import javax.inject.Inject
import models.services.UserService
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.i18n.Messages
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents
import utils.auth.DefaultEnv

/**
 * The social auth controller.
 *
 * @param ec The ExecutionCOntext.
 * @param userService The user service implementation.
 * @param authInfoRepository The auth info service implementation.
 * @param socialProviderRegistry The social provider registry.
 */
class SocialAuthController @Inject() (
  cc: ControllerComponents,
  implicit val ec: ExecutionContext,
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  socialProviderRegistry: SocialProviderRegistry,
  silhouette: Silhouette[DefaultEnv]) extends AbstractController(cc) with I18nSupport with Logging {

  /**
   * Authenticates a user against a social provider.
   *
   * @param provider The ID of the provider to authenticate against.
   * @return The result to display.
   */
  def authenticate(provider: String) = Action.async { implicit request =>
    val referer = request.headers.get("referer")
    val uri = referer.getOrElse("")
    val realProvider = if (provider == "BRUTAL") "credential" else provider
    logger.info("49 SocialAuthController " + realProvider)
    (socialProviderRegistry.get[SocialProvider](realProvider) match {
      case Some(p: OAuth2Provider with CommonSocialProfileBuilder) =>
      logger.info("Social authenticate request.uri: " + request.uri)
        p.authenticate().flatMap {
          case Left(result) => Future.successful(result)
          case Right(authInfo) => for {
            profile <- p.retrieveProfile(authInfo)
            user <- userService.save(profile)
            authInfo <- authInfoRepository.save(user.loginInfo, authInfo)
            authenticator <- silhouette.SecuredRequestHandler.environment.authenticatorService.create(user.loginInfo)
            value <- silhouette.SecuredRequestHandler.environment.authenticatorService.init(authenticator)
            result <- silhouette.SecuredRequestHandler.environment.authenticatorService.embed(value, Redirect(uri))
          } yield {
            logger.debug("profile: " + profile.email + "\t" + profile.fullName)
            logger.debug("user: " + user)
            logger.debug("authenticator: " + authenticator.id + "\t" + authenticator.loginInfo)
            logger.info("SocialAuth " + user.loginInfo.providerID + "\t" + user.loginInfo.providerKey)
            silhouette.env.eventBus.publish(LoginEvent(user.copy(deleted = false), request))
            result
          }
        }
      case _ => Future.failed(new ProviderException(s"Cannot authenticate with unexpected social provider $realProvider"))
    }).recover {
      case e: ProviderException =>
        logger.error("Unexpected provider error", e)
        Redirect(routes.SignInController.signIn("")).flashing("error" -> Messages("could.not.authenticate"))
    }
  }

}
