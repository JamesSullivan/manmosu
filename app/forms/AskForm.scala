package forms

import models.Ask
import play.api.data.Form
import play.api.data.Forms.boolean
import play.api.data.Forms.longNumber
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.seq
import play.api.data.Forms.text

object AskForm {
  val form = Form(
    mapping(
      "watching" -> boolean,
      "description" -> nonEmptyText,
      "markedDescription" -> text,
      "tags" -> seq(text),
      "title" -> nonEmptyText,
      "comment" -> text,
      "questionId" -> longNumber)(models.Ask.apply)(Ask.unapply))
}