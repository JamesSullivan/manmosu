package repo

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.concurrent.Future
import scala.concurrent.duration.Duration

import org.scalatest._
import org.scalatestplus.play._
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.jdbc.MySQLProfile

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.test.WithApplication

import java.sql.Timestamp
import javax.inject.Inject
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider
import utils.auth.DefaultEnv


class DAOSpec extends PlaySpec with GuiceOneAppPerSuite with env.Context {
   
    def getDAO(implicit app: Application) = Application.instanceCache[DAO].apply(app)
    val dao = getDAO
    val dbConfig = dao.getDbConfig
    import dbConfig._
    import profile.api._

    def getTest(number: Long) = {
      val q = Tables.QuestionComments.filter(q => q.questionId === number).join(Tables.Comment).on(_.commentsId === _.id)
      db.run(q.result)
  }
 
   "DAO." should {
    

        
      /**"return a result for dao.getQuestion(207L)" in  {
        val questionResult = Await.result(dao.getQuestion(207L), 30.seconds)
        questionResult.size > 1
     }*/
     
    "return a result for dao.getComment(207L)" in  {
        val questionComments = Await.result(dao.getQuestionComments(207L), 30.seconds)
        println("questionCommentsResult: " + questionComments)
        questionComments.size > 1
     }
     
      /**"return a result for getTest(207L)" in  {
        val testResults = Await.result(getTest(207L), 30.seconds)
        println("testResults: " + testResults)
        testResults.size > 1
     }*/
    }
  
  
  
  
}