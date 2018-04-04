package forms

import play.api.data.Form
import play.api.data.Forms.boolean
import play.api.data.Forms.email
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.text

/**
 * The form which handles the submission of the credentials.
 */
object SignInForm {

  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText,
      "originaluri" -> text,
      "rememberMe" -> boolean)(Data.apply)(Data.unapply))

  /**
   * The form data.
   *
   * @param email The email of the user.
   * @param password The password of the user.
   * @param originaluri The target uri before being forced to sign in
   * @param rememberMe Indicates if the user should stay logged in on the next visit.
   */
  case class Data(
    email: String,
    password: String,
    originaluri: String,
    rememberMe: Boolean)
}
