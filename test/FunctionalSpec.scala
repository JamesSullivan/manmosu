import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.libs.typedmap.TypedMap
import java.time.ZonedDateTime
import java.time.ZoneId
import javax.inject.Inject

import utils.auth.DefaultEnv
import utils.MailService
import com.mohiva.play.silhouette.api._

import play.api.test.CSRFTokenHelper._

import scala.concurrent.ExecutionContext.Implicits.global

import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.test._
import env.Context



  /**
   * The context.
   */

/**
 * Functional tests start a Play application internally, available
 * as `app`.
 */
class FunctionalSpec extends PlaySpec with GuiceOneAppPerSuite with env.Context {

  "Routes" should {

    "send 404 on a bad request /boum" in  {
      route(app, FakeRequest(GET, "/boum")).map(status(_)) mustBe Some(NOT_FOUND)
    }

    "send 200 on a good request /" in  {
      route(app, FakeRequest(GET, "/")).map(status(_)) mustBe Some(OK)
    }

  }

  "ApplicationController" should {
    "render the /signIn page" in {
      val home = route(application, FakeRequest(GET, "/signIn").withAuthenticator[DefaultEnv](LoginInfo("invalid", "invalid"))).get
      status(home) mustBe Status.OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Sign in")
    }
  }
  


}