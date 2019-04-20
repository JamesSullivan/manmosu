package forms

import java.time.LocalDate

import models.User
import play.api.data.Form
import play.api.data.Forms.boolean
import play.api.data.Forms.localDate
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.optional
import play.api.data.Forms.text;

object UserEditForm {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "email" -> text,
      "name" -> nonEmptyText,
      "photoUri" -> text,
      "website" -> text,
      "birthDate" -> optional(localDate("yyyy-MM-dd")),
      "location" -> text,
      "description" -> text,
      "isSubscribed" -> boolean,
      "receiveAllUpdates" -> boolean)(Data.apply)(Data.unapply))

  /**
   * The form data.
   *
   */
  case class Data(
    email: String,
    name: String,
    photoUri: String,
    website: String,
    birthDate: Option[LocalDate],
    location: String,
    description: String,
    isSubscribed: Boolean,
    receiveAllUpdates: Boolean)

  def userToFilledForm(user: User): Form[UserEditForm.Data] = {
    val userForm = UserEditForm
    userForm.form.fill(Data(user.email.getOrElse(""), user.name.getOrElse(""), user.photoUri.getOrElse(""), user.website.getOrElse(""), user.birthDate.map(_.toLocalDate),
      user.location.getOrElse(""), user.about.getOrElse(""), user.isSubscribed, user.receiveAllUpdates))
  }

}
