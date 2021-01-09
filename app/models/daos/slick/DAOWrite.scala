package models.daos.slick

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import javax.inject.Inject
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.StaticDatabaseConfig
import slick.jdbc.MySQLProfile
import utils.RelativeTime.timeStampNow

class DAOWrite @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, val config: Configuration)(implicit ec: ExecutionContext) {

  val dbUrl = config.get[String]("slick.dbs.default.db.url")
  val start = dbUrl.lastIndexOf("/")
  val end = if (dbUrl.lastIndexOf("?") > start) dbUrl.lastIndexOf("?") else dbUrl.length
  Tables.dbName = dbUrl.substring((start + 1), end)
  private val dbConfig = dbConfigProvider.get[MySQLProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  def from[T <: slick.lifted.AbstractTable[_]](table: TableQuery[T]) = db.run {
    table.result
  }

  def getDbConfig = dbConfig

  // insertExpression.forceInstertQuery(selectExpression)
  @StaticDatabaseConfig("file:conf/application.conf#slick.dbs.default")
  def incrementAnswerVotes(id: Long, amount: Int) = db.run(sqlu"UPDATE Answer SET voteCount = voteCount + $amount WHERE id = $id")

  @StaticDatabaseConfig("file:conf/application.conf#slick.dbs.default")
  def incrementQuestionViews(id: Long) = db.run(sqlu"UPDATE Question SET views = views + 1 WHERE id = $id")

  @StaticDatabaseConfig("file:conf/application.conf#slick.dbs.default")
  def incrementQuestionVotes(id: Long, amount: Int) = db.run(sqlu"UPDATE Question SET voteCount = voteCount + $amount WHERE id = $id")

  @StaticDatabaseConfig("file:conf/application.conf#slick.dbs.default")
  def incrementQuestionAnswers(questionId: Long) = db.run(sqlu"UPDATE Question SET answerCount = answerCount + 1 WHERE id = $questionId")

  @StaticDatabaseConfig("file:conf/application.conf#slick.dbs.default")
  def incrementCommentVotes(commentId: Long, amount: Int) = {
    db.run(sqlu"UPDATE Comment SET voteCount = voteCount + $amount WHERE id = $commentId")
  }

  def insertOrUpdateQuestioninformationTag(qit: Tables.QuestioninformationTagRow) = db.run(Tables.QuestioninformationTag.insertOrUpdate(qit))

  def insertAnswerinformationRow(air: Tables.AnswerinformationRow): Future[Long] = db.run(Tables.Answerinformation returning Tables.Answerinformation.map(_.id) += air)

  def insertAnswerRow(ar: Tables.AnswerRow): Future[Long] = db.run(Tables.Answer returning Tables.Answer.map(_.id) += ar)

  def insertAnswerVoteRow(aId: Long, v: Tables.VoteRow) = {
    db.run((Tables.Vote returning Tables.Vote.map(_.id) += v).flatMap(voteId =>
      Tables.AnswerVotes.insertOrUpdate(Create.answerVotesRow(aId, voteId))))
  }

  def insertAttachmentRow(ar: Tables.AttachmentRow) = db.run(Tables.Attachment returning Tables.Attachment.map(_.id) += ar)

  def insertQuestionAttachmentRow(questionId: Long, attachmentsId: Long) = db.run(Tables.QuestionAttachment += Tables.QuestionAttachmentRow(questionId, attachmentsId))

  def insertAnswerAttachmentRow(answerId: Long, attachmentsId: Long) = db.run(Tables.AnswerAttachment += Tables.AnswerAttachmentRow(answerId, attachmentsId))

  def updateAnswerRow(ar: Tables.AnswerRow) = db.run(Tables.Answer.filter(_.id === ar.id).map(a => (a.informationId, a.lasttouchedbyId, a.lastupdatedat)).update((ar.informationId, ar.lasttouchedbyId, ar.lastupdatedat)))

  @StaticDatabaseConfig("file:conf/application.conf#slick.dbs.default")
  def updateAnswerVoteRow(answerId: Long, userID: Long, voteType: String) = {
    print(s"UPDATE Vote v JOIN Answer_Votes av ON v.id = av.votes_id SET v.lastUpdatedAt = ${timeStampNow()}, v.type = ${voteType} WHERE av.Answer_id = ${answerId} AND v.author_id = ${userID};")
    db.run(sqlu"UPDATE Vote v JOIN Answer_Votes av ON v.id = av.votes_id SET v.lastUpdatedAt = ${timeStampNow()}, v.type = ${voteType} WHERE av.Answer_id = ${answerId} AND v.author_id = ${userID};")
  }

  def updateAnswerinformationRowWithAID(aiid: Long, aid: Long) = db.run(Tables.Answerinformation.filter(_.id === aiid).map(_.answerId).update(Some(aid)))

  @StaticDatabaseConfig("file:conf/application.conf#slick.dbs.default")
  def insertOrUpdateTagx(tr: Tables.TagxRow) = {
    db.run(sql"INSERT INTO Tag (id, createdAt, description, name, usageCount, author_id) VALUES (${tr.id}, ${tr.createdat}, ${tr.description}, ${tr.name}, 1, ${tr.authorId}) ON DUPLICATE KEY UPDATE Tag.usageCount = Tag.usageCount + 1".as[Long])
  }

  def insertQuestionRow(qr: Tables.QuestionRow): Future[Long] = db.run(Tables.Question returning Tables.Question.map(_.id) += qr)

  def updateQuestionRow(qr: Tables.QuestionRow) = db.run(Tables.Question.filter(_.id === qr.id).map(q => (q.informationId, q.lasttouchedbyId, q.lastupdatedat)).update((qr.informationId, qr.lasttouchedbyId, qr.lastupdatedat)))

  def updateQuestionRowSolutionId(qr: Tables.QuestionRow) = {
    db.run(Tables.Question.filter(_.id === qr.id).map(q => (q.solutionId, q.lasttouchedbyId, q.lastupdatedat)).update((qr.solutionId, qr.lasttouchedbyId, qr.lastupdatedat)))
  }

  def updateQuestioninformationRowWithQID(qiid: Long, qid: Long) = db.run(Tables.Questioninformation.filter(_.id === qiid).map(_.questionId).update(Some(qid)))

  def insertQuestioninformationRow(qir: Tables.QuestioninformationRow): Future[Long] = db.run(Tables.Questioninformation returning Tables.Questioninformation.map(_.id) += qir)

  def insertQuestionInteractionsRow(questionId: Long, userID: Long) = db.run(Tables.QuestionInteractions += Tables.QuestionInteractionsRow(questionId, userID))

  def insertCommentRow(c: Tables.CommentRow) = db.run((Tables.Comment returning Tables.Comment.map(_.id) += c))

  def insertCommentVoteRow(commentId: Long, v: Tables.VoteRow) = db.run((Tables.Vote returning Tables.Vote.map(_.id) += v).flatMap(vId =>
    Tables.CommentVotes.insertOrUpdate(Tables.CommentVotesRow(commentId, vId))))

  def updateCommentRow(c: Tables.CommentRow) = db.run(Tables.Comment.insertOrUpdate(c))

  def insertAnswerCommentRow(answerId: Long, commentsId: Long) = db.run(Tables.AnswerComments.insertOrUpdate(Tables.AnswerCommentsRow(answerId, commentsId)))

  def insertQuestionCommentRow(questionId: Long, commentsId: Long) = db.run(Tables.QuestionComments.insertOrUpdate(Tables.QuestionCommentsRow(questionId, commentsId)))

  @StaticDatabaseConfig("file:conf/application.conf#slick.dbs.default")
  def updateQuestionVoteRow(questionId: Long, userID: Long, voteType: String) = {
    print(s"UPDATE Vote v JOIN Question_Votes qv ON v.id = qv.votes_id SET v.lastUpdatedAt = ${timeStampNow()}, v.type = ${voteType} WHERE qv.Question_id = ${questionId} AND v.author_id = ${userID};")
    db.run(sqlu"UPDATE Vote v JOIN Question_Votes qv ON v.id = qv.votes_id SET v.lastUpdatedAt = ${timeStampNow()}, v.type = ${voteType} WHERE qv.Question_id = ${questionId} AND v.author_id = ${userID};")
  }

  def insertQuestionVoteRow(qId: Long, v: Tables.VoteRow) = {
    db.run((Tables.Vote returning Tables.Vote.map(_.id) += v).flatMap(voteId =>
      Tables.QuestionVotes.insertOrUpdate(Create.questionVotesRow(qId, voteId))))
  }

  @StaticDatabaseConfig("file:conf/application.conf#slick.dbs.default")
  def removeAnswerVoteRow(aId: Long, userID: Long) = {
    db.run(sql"DELETE av,v FROM Answer_Votes av JOIN Vote v ON votes_id = id Where Answer_id = ${aId} AND author_id = ${userID}".as[Long])
  }

  def removeQuestionAttachmentRow(attachmentsId: Long, userID: Long) = {
    db.run(sql"DELETE qat,at FROM Question_Attachment qat JOIN Attachment at ON attachments_id = id Where attachments_id = ${attachmentsId} AND owner_id = ${userID}".as[Long])
  }

  def removeAnswerAttachmentRow(attachmentsId: Long, userID: Long) = {
    db.run(sql"DELETE aat,at FROM Answer_Attachment aat JOIN Attachment at ON attachments_id = id Where attachments_id = ${attachmentsId} AND owner_id = ${userID}".as[Long])
  }

  @StaticDatabaseConfig("file:conf/application.conf#slick.dbs.default")
  def removeCommentVoteRow(cId: Long, userID: Long) = {
    db.run(sql"DELETE cv,v FROM Comment_Votes cv JOIN Vote v ON votes_id = id Where Comment_id = ${cId} AND author_id = ${userID}".as[Long])
  }

  @StaticDatabaseConfig("file:conf/application.conf#slick.dbs.default")
  def removeQuestionVoteRow(qId: Long, userID: Long) = {
    db.run(sql"DELETE qv,v FROM Question_Votes qv JOIN Vote v ON votes_id = id Where Question_id = ${qId} AND author_id = ${userID}".as[Long])
  }

  def insertOrUpdateQuestionWatcherRow(qw: Tables.QuestionWatchersRow) = db.run(Tables.QuestionWatchers.insertOrUpdate(qw))

  def insertReputationeventRow(re: Tables.ReputationeventRow) = {
    db.run((Tables.Reputationevent += re) andThen sqlu"UPDATE Users SET karma = karma + ${re.karmareward} WHERE id = ${re.userId}")
  }

  def removeReputationeventRow(re: Tables.ReputationeventRow) = {
    db.run((Tables.Reputationevent.filter(r => r.contextId === re.contextId && r.`type` === re.`type` && r.userId === re.userId).delete) andThen sqlu"UPDATE Users SET karma = karma - ${re.karmareward} WHERE id = ${re.userId}")
  }

  def updateReputationeventRow(qId: Long, karmaReward: Int, karmaChange: Int, oldType: String, newType: String, userID: Long) = {
    db.run((Tables.Reputationevent.filter(r => r.contextId === qId && r.`type` === oldType && r.userId === userID).map(re => (re.karmareward, re.`type`)).update((karmaReward, Some(newType))) andThen
      sqlu"UPDATE Users SET karma = karma + ${karmaChange} WHERE id = ${userID}"))
  }

  def insertCommentFlag(userID: Long, commentId: Long, flag: Tables.FlagRow) = {
    db.run((Tables.Flag returning Tables.Flag.map(_.id) += flag).flatMap(fId =>
      Tables.CommentFlags.insertOrUpdate(Tables.CommentFlagsRow(commentId, fId))))
  }

  def insertQuestionFlag(userID: Long, questionId: Long, flag: Tables.FlagRow) = {
    println("userID: " + userID + "\tquestionId: " + questionId)
    db.run((Tables.Flag returning Tables.Flag.map(_.id) += flag).flatMap(fId =>
      Tables.QuestionFlags.insertOrUpdate(Tables.QuestionFlagsRow(questionId, fId))))
  }

  private def insertWatcherRow(w: Tables.WatcherRow): Future[Long] = db.run(Tables.Watcher returning Tables.Watcher.map(_.id) += w)

  def inactivateWatcherRow(watcherId: Long) = {
    db.run(Tables.Watcher.filter(_.id === watcherId).map(w => w.active).update(false))
  }

  def insertWatcher(qId: Long, userID: Long) = insertWatcherRow(Create.watcherRow(userID)).map(id => insertOrUpdateQuestionWatcherRow(Create.questionWatchersRow(qId, id)))

  def toggleWatchesByQuestionUserIdQuery(questionId: Long, userID: Option[Long], value: Option[Boolean] = None) = {
    val active = value match {
      case None => "1 - w.active"
      case Some(x) if x == true => "1"
      case Some(x) if x == false => "0"
      case Some(_) => "1 - w.active"
    }
    db.run(sqlu"UPDATE Question_Watchers qw JOIN Watcher w ON qw.watchers_id = w.id AND qw.Question_id = ${questionId} AND w.watcher_id = ${userID.get} SET w.active = #${active};")
  }

}
  
