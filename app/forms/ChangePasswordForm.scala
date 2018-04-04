package forms

import play.api.data.Form
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.tuple

/**
 * The `Change Password` form.
 */
object ChangePasswordForm {

  /**
   * A play framework form.
   */
  val form = Form(tuple(
    "current-password" -> nonEmptyText,
    "new-password" -> nonEmptyText))
}
