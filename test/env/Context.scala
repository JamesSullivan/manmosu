package env

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import utils.auth.DefaultEnv
import models.User
import play.api.inject.guice.GuiceApplicationBuilder
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.test._
import javax.inject._

import play.api.inject.guice.GuiceableModule.fromGuiceModule
import scala.collection.Seq
import scala.concurrent.ExecutionContext 
import scala.reflect.ManifestFactory.classType
import scala.reflect.api.materializeTypeTag


  trait Context {

    /**
     * A fake Guice module.
     */
    class FakeModule @Inject()(implicit ec: ExecutionContext) extends AbstractModule with ScalaModule {
      override def configure() = {
        bind[Environment[DefaultEnv]].toInstance(env)
      }
    }

    /**
     * An identity.
     */
    val identity = User(
      userID = 1002L,
      loginInfo = LoginInfo("BRUTAL", "1002"),
      about = None,
      birthDate = None,
      account = models.Account.Professional,   // use confirmedEmail value
      createdAt = None,
      email = Some("test@test.com"),
      forgotPasswordToken = None,
      isBanned = false,
      isSubscribed = true,
      karma = 2,
      location = None,
      markedAbout = None,
      moderator = false,
      name = Some("test name"),
      nameLastTouchedAt = None,
      photoUri = None,
      sluggedName = Some("test-name"),
      website = None,
      lastUpvote = None,
      avatarImage_id = None,
      receiveAllUpdates = true,
      deleted = false
    )
    
    /**
     * A Silhouette fake environmenNone.
     */
    implicit val env: Environment[DefaultEnv] = new FakeEnvironment[DefaultEnv](Seq(identity.loginInfo -> identity))

    /**
     * The application.
     */
    lazy val application = new GuiceApplicationBuilder()
      .overrides(new FakeModule)
      .build()
}