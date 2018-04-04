package models

import models.daos.slick.QATextInputRow
import models.daos.slick.Tables.AttachmentRow
import models.daos.slick.Tables.CommentVotesRow

trait QorA extends WithAuthor {

  val isQuestion: Boolean
  val text: String
  val row: QATextInputRow
  var attachments: Seq[AttachmentRow] = Seq[AttachmentRow]()
  var comments: Seq[CommentUser] = Seq[CommentUser]()
  var voted: Int = 0 //current user (not necessarily answer author) -1 voted down, 0 no vote, 1 voted up

  /**
   * Used to initialize comments with if the user has already voted for them
   *
   */
  def voteComments(commentsVotes: Seq[CommentVotesRow]) =
    comments.foreach { c => c.voted = commentsVotes.exists(cv => cv.commentId == c.commentsId) }
}