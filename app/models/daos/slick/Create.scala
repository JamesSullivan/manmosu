package models.daos.slick

import com.github.rjeschke.txtmark

import forms.AnswerForm.Answer
import models.Ask
import models.daos.slick.Tables.AnswerRow
import models.daos.slick.Tables.AnswerVotesRow
import models.daos.slick.Tables.AnswerinformationRow
import models.daos.slick.Tables.AttachmentRow
import models.daos.slick.Tables.CommentRow
import models.daos.slick.Tables.CommentVotesRow
import models.daos.slick.Tables.FlagRow
import models.daos.slick.Tables.QuestionRow
import models.daos.slick.Tables.QuestionVotesRow
import models.daos.slick.Tables.QuestionWatchersRow
import models.daos.slick.Tables.QuestioninformationRow
import models.daos.slick.Tables.QuestioninformationTagRow
import models.daos.slick.Tables.ReputationeventRow
import models.daos.slick.Tables.TagxRow
import models.daos.slick.Tables.VoteRow
import models.daos.slick.Tables.WatcherRow
import utils.RelativeTime.timeStampNow
import utils.Sanitizer.safeHTML
import utils.Sanitizer.safeText
import utils.Sanitizer.slugify

/**
 * Object to help create databaseTableRows
 *
 */
object Create {

  def answerVotesRow(answerId: Long, votesId: Long) = AnswerVotesRow(answerId, votesId)

  def answerRow(id: Long, userID: Long, informationId: Long, questionId: Long) = {
    val now = Some(timeStampNow())
    AnswerRow(id, now, now, false, 0L, Some(userID), informationId, Some(userID), Some(questionId), Some(false))
  }

  def answerinformationRow(userID: Long, answer: Answer, ip: String, comment: String) = {
    val now = Some(timeStampNow())
    val htmlcomment = txtmark.Processor.process(answer.description)
    val answerId: Option[Long] = if (answer.answerId < 1) None else Some(answer.answerId)
    AnswerinformationRow(0, comment, now, safeText(answer.description), Some(ip), Some(safeHTML(htmlcomment)),
      None, Some("NO_NEED_TO_APPROVE"), answerId, userID, None)
  }

  def attachmentRow(ip: String, mime: String, name: String, userID: Long) = {
    val now = Some(timeStampNow())
    AttachmentRow(0L, now, Some(ip), Some(mime), Some(name), Some(userID))
  }

  def commentRow(commentsId: Long, comment: String, authorId: Long) = {
    val now = Some(timeStampNow())
    val htmlcomment = txtmark.Processor.process(comment)
    CommentRow(commentsId, comment: String, now, htmlcomment, now, false, 0L, authorId: Long, Some(false))
  }

  def commentVotesRow(commentId: Long, votesId: Long) = CommentVotesRow(commentId, votesId)

  def flagRow(reason: Option[String], flagType: Option[String], authorId: Long) = FlagRow(0L, reason, flagType, Some(authorId), Some(false))

  def questioninformationTagRow(qinformationId: Long, tagId: Long, count: Int) = Tables.QuestioninformationTagRow(qinformationId, tagId, count)

  def questionRow(id: Long, userID: Long, informationId: Long) = {
    val now = Some(timeStampNow())
    QuestionRow(id, 0L, now, now, false, 0L, 0L, Some(userID), informationId, Some(userID), None, Some(false))
  }

  def questioninformationRow(userID: Long, ask: Ask, ip: String, comment: String) = {
    val now = Some(timeStampNow())
    val questionId: Option[Long] = if (ask.questionId < 1) None else Some(ask.questionId)
    QuestioninformationRow(0, comment, now, safeText(ask.description), Some(ip), Some(safeHTML(ask.markedDescription)),
      None, slugify(ask.title), Some("NO_NEED_TO_APPROVE"), ask.title, userID, None, questionId)
  }

  def questioninformationTagRow(questioninformationId: Long, tagsIds: List[Long]) = {
    tagsIds.zipWithIndex.map {
      case (tagsId, tagOrder) =>
        QuestioninformationTagRow(questioninformationId, tagsId, tagOrder)
    }
  }

  def questionVotesRow(questionId: Long, votesId: Long) = QuestionVotesRow(questionId, votesId)

  def questionWatchersRow(questionId: Long, watchersID: Long) = {
    QuestionWatchersRow(questionId, watchersID)
  }

  def reputationeventRow(questionId: Long, karmareward: Int, karmatype: String, userID: Option[Long]) = {
    ReputationeventRow(0, Some("QUESTION"), Some(questionId), Some(timeStampNow()), karmareward, Some(karmatype), userID, Some(false))
  }

  def tagxRow(name: String, authorId: Long) = {
    val now = Some(timeStampNow())
    TagxRow(0L, now, None, slugify(safeText(name)), Some(1L), Some(authorId))
  }

  def voteRow(voteType: String, voterId: Long) = {
    val now = Some(timeStampNow())
    VoteRow(0, now, now, Some(voteType), Some(voterId))
  }

  def watcherRow(watcherId: Long) = WatcherRow(0, true, Some(timeStampNow()), Some(watcherId))

}