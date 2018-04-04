package models.daos.slick


import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.OAuth1Info

import models.Account
import models.daos.OAuth1InfoDAO
import models.daos.UserDAO
import models.User

import org.scalatest._
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.inject.ConfigurationProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.test._
import play.api.test.Helpers._
import play.Logger

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.jdbc.MySQLProfile.api._


/**
 * @author sullija
 */
class UserDAOSlickSpec @Inject()(userDAOSlick: models.daos.slick.UserDAOSlick) extends PlaySpec with GuiceOneServerPerSuite with env.Context {

  "A slickLoginInfos join slickUserLoginInfos" should {
    "return some ids" in {
      Await.result(userDAOSlick.find(new LoginInfo("credentials", "brianm24@outlook.com")), Duration.Inf).get.name mustBe Some("Brian McManus")
    }
  }

}

