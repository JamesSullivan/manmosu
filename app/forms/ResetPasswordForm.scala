package forms

import play.api.data.Form
import play.api.data.Forms.nonEmptyText

/**
 * The `Reset Password` form.
 */
object ResetPasswordForm {

  /**
   * A play framework form.
   */
  val form = Form(
    "password" -> nonEmptyText)
}
