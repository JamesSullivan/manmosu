package utils.auth

import javax.inject.Inject
import javax.inject.Provider
import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import play.api.http.DefaultHttpErrorHandler
import controllers.routes
import play.api.Environment
import play.api.Logging
import play.api.routing.Router
import play.api.mvc.Results._
import play.api.mvc.RequestHeader
import play.api.routing.Router
import play.api.{ OptionalSourceMapper, Configuration }
import scala.concurrent.Future

/**
 * A secured error handler.
 */
class CustomSecuredErrorHandler @Inject() (
  env: Environment,
  config: Configuration,
  sourceMapper: OptionalSourceMapper,
  router: Provider[Router])
  extends DefaultHttpErrorHandler(env, config, sourceMapper, router)
  with SecuredErrorHandler with Logging {

  /**
   * Called when a user is not authenticated.
   *
   * As defined by RFC 2616, the status code of the response should be 401 Unauthorized.
   *
   * @param request The request header.
   * @param messages The messages for the current language.
   * @return The result to send to the client.
   */
  override def onNotAuthenticated(implicit request: RequestHeader) = {
    logger.warn("CustomSecuredErrorHandler onNotAuthenticated uri: " + request.uri)
    Future.successful(Redirect(routes.SignInController.signIn("").toString + request.uri))
  }

  /**
   * Called when a user is authenticated but not authorized.
   *
   * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
   *
   * @param request The request header.
   * @param messages The messages for the current language.
   * @return The result to send to the client.
   */
  override def onNotAuthorized(implicit request: RequestHeader) = {
    logger.warn("CustomSecuredErrorHandler onNotAuthorized uri: " + request.uri)
    Future.successful(Forbidden)
  }
}
