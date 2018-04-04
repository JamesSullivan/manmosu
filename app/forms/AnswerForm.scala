package forms

import play.api.data.Forms._
import play.api.data.Form

object AnswerForm {

  val form = Form(
    mapping(
      "watching" -> boolean,
      "description" -> nonEmptyText,
      "comment" -> text,
      "answerId" -> longNumber,
      "questionId" -> longNumber)(Answer.apply)(Answer.unapply))

  case class Answer(watching: Boolean, description: String, comment: String, answerId: Long, questionId: Long)

  def blank(informationId: Long) = Answer(true, "", "", 0L, informationId)

  def blankForm(informationId: Long) = form.fill(blank(informationId))

}