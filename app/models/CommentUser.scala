package models

import models.daos.slick.Tables.CommentRow
import models.daos.slick.Tables.UsersRow

// authorsRow is the author of the comment
// voted is whether the current viewer voted for this comment (viewer may be different from the author)
case class CommentUser(id: Long, commentsId: Long, row: CommentRow, authorsRow: UsersRow) extends WithAuthor {
  var voted: Boolean = false;
}
