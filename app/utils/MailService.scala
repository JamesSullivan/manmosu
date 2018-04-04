package utils

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.HtmlEmail

import javax.inject.Inject
import play.Logger
import play.api.Configuration

class MailService @Inject() (config: Configuration)(implicit val ec: ExecutionContext) {

  def conf(s: String): String = config.get[String]("play.mailer." + s)
  val system = akka.actor.ActorSystem()

  def sendEmailAsync(recipients: String*)(subject: String, bodyHtml: String, bodyText: String = "") = {
    val cancellable = system.scheduler.scheduleOnce(100 milliseconds) {
      sendEmail(recipients: _*)(subject, bodyHtml, bodyText)
    }
  }

  def sendEmail(recipients: String*)(subject: String, bodyHtml: String, bodyText: String = "") = {
    val email: HtmlEmail = new HtmlEmail();
    email.setHostName(conf("host"));
    email.setCharset("UTF-8")
    email.setSmtpPort(config.get[Int]("play.mailer.port"))
    email.setAuthenticator(new DefaultAuthenticator(conf("user"), conf("password")))
    email.setSSLOnConnect(config.get[Boolean]("play.mailer.ssl"));
    email.setStartTLSEnabled(config.get[Boolean]("play.mailer.tls"))
    val j = recipients.map(a => new javax.mail.internet.InternetAddress(a))
    email.setTo(scala.collection.JavaConverters.asJavaCollection(j))
    email.setFrom(conf("from"), conf("from"))
    email.setSubject(subject);
    // set the html message
    email.setHtmlMsg(bodyHtml);
    // set the alternative message
    email.setTextMsg("Your email client does not support HTML messages");
    Logger.warn("Sending Email: " + email.send() + "\tto: " + recipients);
    Logger.info("host: " + conf("host"))
    Logger.info("user " + conf("user"))
  }
}







