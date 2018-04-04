package forms

import play.api.data.Form
import play.api.data.Forms.email

/**
 * The `Forgot Password` form.
 */
object ForgotPasswordForm {

  /**
   * A play framework form.
   */
  val form = Form(
    "email" -> email)
}
