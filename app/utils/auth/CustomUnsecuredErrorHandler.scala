package utils.auth

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.UnsecuredErrorHandler

import play.api.Logging
import play.api.mvc.RequestHeader
import play.api.mvc.Results.Redirect

/**
 * Custom unsecured error handler.
 */
class CustomUnsecuredErrorHandler extends UnsecuredErrorHandler with Logging {

  /**
   * Called when a user is authenticated but not authorized.
   *
   * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
   *
   * @param request The request header.
   * @return The result to send to the client.
   */
  override def onNotAuthorized(implicit request: RequestHeader) = {
    logger.warn("CustomUnSecuredErrorHandler onNotAuthorized uri: " + request.uri)
    Future.successful(Redirect(controllers.routes.ApplicationController.index("")))
  }
}