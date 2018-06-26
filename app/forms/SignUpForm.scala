package forms

import play.api.data.Form
import play.api.data.Forms.email
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.text

/**
 * The form which handles the sign up process.
 */
object SignUpForm {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText,
      "g-recaptcha-response" -> text)(Data.apply)(Data.unapply))

  /**
   * The form data.
   *
   * @param name The full name of a user.
   * @param email The email of the user.
   * @param password The password of the user.
   */
  case class Data(
    name: String,
    email: String,
    password: String,
    grecaptcharesponse: String)
}
