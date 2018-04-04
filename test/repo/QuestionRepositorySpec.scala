package repo


import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.test.WithApplication
import org.scalatest._
import org.scalatestplus.play._
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import java.util.Date
import java.sql.Timestamp
import java.util.Calendar
import scala.concurrent.Future
import utils.auth.DefaultEnv
import play.api.libs.mailer.{ Email, MailerClient }

import scala.concurrent.ExecutionContext.Implicits.global

class QuestionRepositorySpec extends PlaySpec with GuiceOneAppPerSuite with env.Context {
  
  import models._
  
  val ts = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());

  
  def await[T](v: Future[T]): T = Await.result(v, Duration.Inf)
  
  "QuestionRepository" should {
    "send an E-mail" in new WithApplication() {
            mailerClient.send(Email(
              subject = Messages("email.already.signed.up.subject"),
              from = Messages("email.from"),
              to = Seq(data.email),
              bodyText = Some(views.txt.emails.alreadySignedUp(user, url).body),
              bodyHtml = Some(views.html.emails.alreadySignedUp(user, url).body)
            ))
    }
    
   // def quesRepo(implicit app: Application) = Application.instanceCache[QuestionRepository].apply(app)
    /**
    "read the table" in new WithApplication() {
      println("sql: " + quesRepo.createSQL)
      val questions = await(quesRepo.list())
      //println(questions.mkString("\r\n"))
      questions.length > 1;
    }
  
    "insert a row" in new WithApplication() {  
    val question = await(quesRepo.create(1, ts, ts, true, 1, 2, 1001, 1,1001,None,true))
    println("question: " + question)
     question.author_id === 1001
    }

    */
  }
}