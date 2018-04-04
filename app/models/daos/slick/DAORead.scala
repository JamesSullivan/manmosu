package models.daos.slick

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import QA.QA
import javax.inject.Inject
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.StaticDatabaseConfig
import slick.jdbc.MySQLProfile

class DAORead @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, val config: Configuration)(implicit ec: ExecutionContext) {

  val dbUrl = config.get[String]("slick.dbs.default.db.url")
  val start = dbUrl.lastIndexOf("/")
  val end = if (dbUrl.lastIndexOf("?") > start) dbUrl.lastIndexOf("?") else dbUrl.length
  Tables.dbName = dbUrl.substring((start + 1), end)
  private val max_recent_tags = config.get[Int]("max_recent_tags")
  private val noPerPage = config.get[Int]("elements_per_page")
  private val dbConfig = dbConfigProvider.get[MySQLProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  def from[T <: slick.lifted.AbstractTable[_]](table: TableQuery[T]) = db.run {
    table.result
  }

  def getDbConfig = dbConfig

  def numberOfQuestionsByMatch(q: String) = db.run((sql"SELECT count(Question.id) FROM `QuestionInformation` JOIN Question ON QuestionInformation.id = Question.information_id where MATCH(title,description) AGAINST (${q}) > 0").as[Int].head)

  def numberOfQuestions: Future[Int] = db.run(Tables.Question.filter(_.deleted === false).size.result)

  def numberOfUnansweredQuestions: Future[Int] = db.run(Tables.Question.filter(q => q.deleted === false && q.answercount < 1L).size.result)

  def numberOfUnsolvedQuestions: Future[Int] = db.run(Tables.Question.filter(q => q.deleted === false && q.solutionId.isEmpty).size.result)

  def numberOfUsers: Future[Int] = db.run(Tables.Users.filter(_.deleted === false).size.result)

  def numberOfQuestionsByTagNo(tagNo: Long): Future[Int] = db.run(Tables.Tagx.filter(_.id === tagNo).
    join(Tables.Question.filter(_.deleted === false)).on(_.id === _.informationId).size.result)

  def numberofVotesAnswersByAuthorId(authorId: Long): Future[Int] = db.run(votesAnswersByAuthorIdQuery(authorId).size.result)

  def numberofVotesQuestionsByAuthorId(authorId: Long): Future[Int] = db.run(votesQuestionsByAuthorIdQuery(authorId).size.result)

  def numberofVotesWatchesByAuthorId(authorId: Long): Future[Int] = db.run(votesWatchesByAuthorIdQuery(authorId).size.result)

  def answersSortedByLastUpdate = db.run(answersQuery().sortBy(_._1.lastupdatedat.desc).take(100).result)

  def attachmentsByAttachmentId(attachmentsId: Long) = db.run(Tables.Attachment.filter(_.id === attachmentsId).result.headOption)

  def attachmentsByAnswerId(answerId: Long) = db.run(Tables.AnswerAttachment.filter(_.answerId === answerId).
    join(Tables.Attachment).on(_.attachmentsId === _.id).
    map { case (aa, a) => a }.result)

  def attachmentsByQuestionId(questionId: Long) = db.run(Tables.QuestionAttachment.filter(_.questionId === questionId).
    join(Tables.Attachment).on(_.attachmentsId === _.id).
    map { case (qa, a) => a }.result)

  def comment(commentId: Long) = db.run(Tables.Comment.filter(_.id === commentId).result.headOption)

  def commentFlagsIds(userID: Long, commentId: Long) = db.run(Tables.CommentFlags.filter(_.commentId === commentId).
    join(Tables.Flag.filter(_.authorId === userID)).on(_.flagsId === _.id).map(_._2.id).result)

  def answerCommentsVotes(userID: Long, answerId: Long) = db.run(Tables.AnswerComments.filter(a => a.answerId === answerId).
    join(Tables.CommentVotes).on(_.commentsId === _.commentId).
    join(Tables.Vote.filter(v => v.authorId === userID)).on { case ((ac, cv), v) => cv.votesId === v.id }.
    map { case ((ac, cv), v) => (ac, cv) }.result)

  def questionCommentsVotes(userID: Long, questionId: Long) = db.run(Tables.QuestionComments.filter(q => q.questionId === questionId).
    join(Tables.CommentVotes).on(_.commentsId === _.commentId).
    join(Tables.Vote.filter(v => v.authorId === userID)).on { case ((qc, cv), v) => cv.votesId === v.id }.
    map { case ((qc, cv), v) => (qc, cv) }.result)

  def commentVotesByCommentIdUserID(commentsId: Long, userID: Long) = db.run(Tables.CommentVotes.filter(cv => cv.commentId === commentsId).
    join(Tables.Vote.filter(v => v.authorId === userID)).on(_.votesId === _.id).
    map { case (cv, c) => (cv, c) }.result.headOption)

  def questionFlagsIds(userID: Long, questionId: Long) = db.run(Tables.QuestionFlags.filter(_.questionId === questionId).
    join(Tables.Flag.filter(_.authorId === userID)).on(_.flagsId === _.id).map(_._2.id).result)

  def questionIdFromAnswerByCommentId(commentId: Long) = db.run(Tables.AnswerComments.filter(_.commentsId === commentId).
    join(Tables.Answer).on(_.answerId === _.id).
    map { case (ac, a) => a.questionId }.result.head)

  def questionIdFromQuestionByCommentId(commentId: Long) = db.run(Tables.QuestionComments.filter(_.commentsId === commentId).map(_.questionId).result.headOption)

  def questionIdByAnswerId(answerId: Long) = db.run(Tables.Answer.filter(_.id === answerId).map(_.questionId).result.head)

  def questioninformationUsersSortedById(questionId: Long) = db.run(
    Tables.Questioninformation.filter(_.questionId === questionId).
      join(Tables.Users).on(_.authorId === _.id).
      sortBy(_._1.id.asc).result)

  def questionsSortedByLastUpdate(page: Long) = db.run(questionsUsersQuery().sortBy(_._1.lastupdatedat.desc).drop((page - 1) * noPerPage).take(noPerPage).result)

  def questionsSortedByVoted(page: Long) = db.run(questionsUsersQuery().sortBy(_._1.votecount.desc).drop((page - 1) * noPerPage).take(noPerPage).result)

  def questionsSortedByAnswered(page: Long) = db.run(questionsUsersQuery().sortBy(_._1.answercount.desc).drop((page - 1) * noPerPage).take(noPerPage).result)

  def questionsSortedByVisited(page: Long) = db.run(questionsUsersQuery().sortBy(_._1.views.desc).drop((page - 1) * noPerPage).take(noPerPage).result)

  def questionsByTagSorted(tagNo: Long, page: Long) = db.run(tagQuestionsUsersQuery(tagNo).sortBy(_._1.views.desc).drop((page - 1) * noPerPage).take(noPerPage).result)

  def unansweredQuestionsSortedByLastUpdate(page: Long) = db.run(questionsUsersQuery().filter(_._1.answercount < 1L).sortBy(_._1.lastupdatedat.desc).drop((page - 1) * noPerPage).take(noPerPage).result)

  def unsolvedQuestionsSortedByLastUpdate(page: Long) = db.run(questionsUsersQuery().filter(_._1.solutionId.isEmpty).sortBy(_._1.lastupdatedat.desc).drop((page - 1) * noPerPage).take(noPerPage).result)

  def usersSortedByKarma(page: Long) = db.run(Tables.Users.filter(_.deleted === false).sortBy(_.karma.desc).drop((page - 1) * noPerPage).take(noPerPage).result)

  def tags() = db.run(Tables.Tagx.result)

  def tagsByAuthorIdSortByUsageCount(authorId: Long) = db.run(Tables.Tagx.filter(_.authorId === authorId).sortBy(_.usagecount).result)

  def voteByAnswerVoter(answerId: Long, voterId: Long) = db.run(voteByAnswerVoterQuery(answerId, voterId).result.headOption)

  def votesAnswersByAuthorIdSortByVoteCount(authorId: Long, page: Long) = db.run(votesAnswersByAuthorIdQuery(authorId).sortBy(_._1.votecount.desc).
    map { case (answerRow, questionInformationRow) => (answerRow.id, answerRow.lastupdatedat, answerRow.votecount, questionInformationRow.title, questionInformationRow.sluggedtitle) }.
    drop((page - 1) * noPerPage).take(noPerPage).result)

  def votesAnswersByAuthorIdSortByLastUpdateAt(authorId: Long, page: Long) = db.run(votesAnswersByAuthorIdQuery(authorId).sortBy(_._1.lastupdatedat.desc).
    map { case (answerRow, questionInformationRow) => (answerRow.id, answerRow.lastupdatedat, answerRow.votecount, questionInformationRow.title, questionInformationRow.sluggedtitle) }.
    drop((page - 1) * noPerPage).take(noPerPage).result)

  def votesQuestionsByAuthorIdSortByVoteCount(authorId: Long, page: Long) = db.run(votesQuestionsByAuthorIdQuery(authorId).sortBy(_._1.votecount.desc).
    map { case (questionRow, questionInformationRow) => (questionRow.id, questionRow.lastupdatedat, questionRow.votecount, questionInformationRow.title, questionInformationRow.sluggedtitle) }.
    drop((page - 1) * noPerPage).take(noPerPage).result)

  def voteByQuestionVoter(questionId: Long, voterId: Long) = db.run(voteByQuestionVoterQuery(questionId, voterId).result.headOption)

  def votesQuestionsByAuthorIdSortByLastUpdateAt(authorId: Long, page: Long) = db.run(votesQuestionsByAuthorIdQuery(authorId).sortBy(_._1.lastupdatedat.desc).
    map { case (questionRow, questionInformationRow) => (questionRow.id, questionRow.lastupdatedat, questionRow.votecount, questionInformationRow.title, questionInformationRow.sluggedtitle) }.
    drop((page - 1) * noPerPage).take(noPerPage).result)

  def votesReputationEventByAuthorIdSortByLastUpdateAt(authorId: Long, amountOfEvents: Long) = db.run(votesReputationEventByAuthorIdQuery(authorId).sortBy(_._1.date.desc).
    map { case (reputationevent, questionRow, questionInformationRow) => (questionRow.id, reputationevent.date, reputationevent.karmareward, questionInformationRow.title, questionInformationRow.sluggedtitle) }.
    take(amountOfEvents).result)

  def votesWatchesByAuthorIdSortByVoteCount(authorId: Long, page: Long) = db.run(votesWatchesByAuthorIdQuery(authorId).sortBy(_._1.votecount.desc).
    map { case (questionRow, questionInformationRow) => (questionRow.id, questionRow.lastupdatedat, questionRow.votecount, questionInformationRow.title, questionInformationRow.sluggedtitle) }.
    drop((page - 1) * noPerPage).take(noPerPage).result)

  def watchesByQuestionUserId(questionId: Long, userID: Option[Long]) = db.run(watchesByQuestionUserIdQuery(questionId, userID).exists.result)

  private def voteToInt(vote: Option[(Any, Tables.VoteRow)]) = {
    vote match {
      case Some(v) if (v._2.`type` == Some("UP")) => 1
      case Some(v) if (v._2.`type` == Some("DOWN")) => -1
      case _ => 0
    }
  }

  def voteByAnswerUserId(answerId: Long, userID: Long) = voteByAnswerVoter(answerId, userID).map(voteToInt(_))

  def voteWatchesByQuestionUserId(questionId: Long, userID: Long) = {
    for {
      v <- voteByQuestionVoter(questionId, userID)
      w <- watchesByQuestionUserId(questionId, Some(userID))
    } yield (voteToInt(v), w)
  }

  @StaticDatabaseConfig("file:conf/application.conf#slick.dbs.default")
  def relevantMatch(query: String, limit: Long) = {
    db.run(sql"""SELECT *, MATCH(title,description) AGAINST (${query}) AS score FROM `QuestionInformation` JOIN Question ON question_id = Question.id HAVING score > 0 limit #$limit""".as[(Tables.QuestioninformationRow, Tables.QuestionRow)])
  }

  @StaticDatabaseConfig("file:conf/application.conf#slick.dbs.default")
  def questionsByMatch(query: String, page: Long): Future[Seq[(Tables.QuestionRow, Tables.QuestioninformationRow, Tables.UsersRow)]] = {
    db.run(sql"""SELECT * FROM `QuestionInformation` JOIN Question ON QuestionInformation.id = Question.information_id JOIN Users ON Question.author_id = Users.id where MATCH(title,description) AGAINST (${query}) > 0 limit #${noPerPage} offset #${((page - 1) * noPerPage)}""".
      as[((Tables.QuestioninformationRow, Tables.QuestionRow), Tables.UsersRow)]).
      map { case a: Vector[((Tables.QuestioninformationRow, Tables.QuestionRow), Tables.UsersRow)] => a.map(b => (b._1._2, b._1._1, b._2)) }
  }

  def answerRow(answerId: Long) = db.run(Tables.Answer.filter(_.id === answerId).result.headOption)

  def answerUser(number: Long) = db.run(answerUserQuery(number).result.headOption)

  def answerUserQuery(number: Long) = for {
    ((answerRow, answerInformationRow), usersRow) <- Tables.Answer.filter(_.id === number) join
      Tables.Answerinformation on { case (a, ai) => a.informationId === ai.id } join
      Tables.Users on { case (a, u) => a._1.authorId === u.id }
  } yield (answerRow, answerInformationRow, usersRow)

  def questionUser(number: Long) = db.run(questionUserQuery(number).result.headOption)

  def questionUserQuery(number: Long) = for {
    ((questionRow, questionInformationRow), usersRow) <- Tables.Question.filter(_.id === number) join
      Tables.Questioninformation on { case (q, qi) => q.informationId === qi.id } join
      Tables.Users on { case (q, u) => q._1.authorId === u.id }
  } yield (questionRow, questionInformationRow, usersRow)

  def answersQuery() = for {
    ((answerRow, answerInformationRow), usersRow) <- Tables.Answer join
      Tables.Answerinformation on { case (a, ai) => a.informationId === ai.id } join
      Tables.Users on { case (a, u) => a._1.authorId === u.id }
  } yield (answerRow, answerInformationRow, usersRow)

  def voteByAnswerVoterQuery(answerId: Long, voterId: Long) = for {
    (voteRow, answerVotesRow) <- Tables.Vote.filter(_.authorId === voterId) join
      (Tables.AnswerVotes.filter(_.answerId === answerId)) on { case (v, av) => v.id === av.votesId }
  } yield (answerVotesRow, voteRow)

  def votesAnswersByAuthorIdQuery(authorID: Long) = for {
    (answerRow, questionInformationRow) <- Tables.Answer.filter(a => a.deleted === false && a.authorId === authorID) join
      Tables.Questioninformation on { case (a, qi) => a.questionId === qi.id }
  } yield (answerRow, questionInformationRow)

  def votesQuestionsByAuthorIdQuery(authorID: Long) = for {
    (questionRow, questionInformationRow) <- Tables.Question.filter(q => q.deleted === false && q.authorId === authorID) join
      Tables.Questioninformation on { case (q, qi) => q.informationId === qi.id }
  } yield (questionRow, questionInformationRow)

  def voteByQuestionVoterQuery(questionId: Long, voterId: Long) = for {
    (voteRow, questionVotesRow) <- Tables.Vote.filter(_.authorId === voterId) join
      (Tables.QuestionVotes.filter(_.questionId === questionId)) on { case (v, qv) => v.id === qv.votesId }
  } yield (questionVotesRow, voteRow)

  def votesReputationEventByAuthorIdQuery(authorID: Long) = for {
    ((reputationeventRow, questionRow), questionInformationRow) <- Tables.Reputationevent.filter(_.userId === authorID).
      join(Tables.Question.filter(_.deleted === false)).on(_.contextId === _.id).
      join(Tables.Questioninformation).on(_._2.informationId === _.id)
  } yield (reputationeventRow, questionRow, questionInformationRow)

  def votesWatchesByAuthorIdQuery(authorID: Long) = for {
    (((watcherRow, questionWatchersRow), questionRow), questionInformationRow) <- Tables.Watcher.filter(w => w.active === true && w.watcherId === authorID).
      join(Tables.QuestionWatchers).on(_.id === _.watchersId).
      join(Tables.Question.filter(_.deleted === false)).on(_._2.questionId === _.id).
      join(Tables.Questioninformation).on(_._2.informationId === _.id)
  } yield (questionRow, questionInformationRow)

  def watchUsersByQuestionId(questionId: Long) = db.run(Tables.QuestionWatchers.filter(qw => qw.questionId === questionId).
    join(Tables.Watcher).on { case (qw, w) => qw.watchersId === w.id && w.active === true }.
    join(Tables.Users).on { case ((qw, w), u) => w.watcherId === u.id }.
    map { case ((qw, w), u) => (w, u) }.result)

  def watchesByQuestionUserIdQuery(questionId: Long, userID: Option[Long]) = for {
    (questionWatchersRow, watcherRow) <- Tables.QuestionWatchers.filter(qw => qw.questionId === questionId).
      join(Tables.Watcher).on { case (qw, w) => qw.watchersId === w.id && w.active === true }.
      filter(_._2.watcherId === userID)
  } yield (questionWatchersRow, watcherRow)

  def watchUsersAllQuestions() = db.run(Tables.Users.filter(_.receiveallupdates.getOrElse(false) === true).result)

  def questionsUsersQuery() = for {
    ((questionRow, questionInformationRow), usersRow) <- Tables.Question.filter(_.deleted === false) join
      Tables.Questioninformation on { case (q, qi) => q.informationId === qi.id } join
      Tables.Users on { case (q, u) => q._1.authorId === u.id }
  } yield (questionRow, questionInformationRow, usersRow)

  def questionsTags(qids: Seq[Long]) = {
    val q = for {
      (questioninformationTagRow, tagxRow) <- Tables.QuestioninformationTag.filter(q => q.questioninformationId inSetBind qids).sortBy(_.tagOrder) join
        Tables.Tagx on { case (qit, t) => qit.tagsId === t.id }
    } yield (questioninformationTagRow.questioninformationId, tagxRow)
    db.run(q.result)
  }

  def answers(questionId: Long) = {
    val q = Tables.Answer.filter(a => a.questionId === questionId).join(Tables.Answerinformation).on(_.informationId === _.id).
      join(Tables.Users).on(_._2.authorId === _.id).
      map { case ((answer, answerInformation), users) => (answer, answerInformation, users) }
    db.run(q.result)
  }

  def answersComments(aIds: Seq[Long]) = {
    val q = for {
      ((answerComments, comment), users) <- Tables.AnswerComments.filter(a => a.answerId inSetBind aIds) join
        Tables.Comment on { case (ac, c) => ac.commentsId === c.id } join
        Tables.Users on { case (acc, u) => acc._2.authorId === u.id }
    } yield (answerComments.answerId, answerComments.commentsId, comment, users)
    db.run(q.result)
  }

  def questionComments(questionId: Long) = {
    val q = Tables.QuestionComments.filter(q => q.questionId === questionId).join(Tables.Comment).on(_.commentsId === _.id).
      join(Tables.Users).on(_._2.authorId === _.id).
      map { case ((questionComments, comment), users) => (questionComments.questionId, questionComments.commentsId, comment, users) }
    db.run(q.result)
  }

  def recentTags() = {
    val q = for {
      (tagxRow) <- Tables.Tagx.sortBy(_.createdat).take(max_recent_tags)
    } yield (tagxRow)
    db.run(q.result)
  }

  /**
   * Finds karma, userId, userRow, Reputationevent for a given tag for either answers or questions
   *
   *  @param tagNo the tag.id
   *  @param qaType ANSWER or QUESTION
   */
  def tagRanking(tagNo: Long, qaType: QA) = {
    val qa = "CREATED_" + qaType.toString
    db.run(
      Tables.QuestioninformationTag.filter(t => t.tagsId === tagNo).join(Tables.Questioninformation).on(_.questioninformationId === _.id).
        join(Tables.Reputationevent.filter(_.`type` === qa)).on(_._2.questionId === _.contextId).join(Tables.Users).on(_._2.userId === _.id).
        map { case (((qit, qi), repevent), users) => (users, repevent) }.result).map {
        case seq: Seq[(Tables.UsersRow, Tables.ReputationeventRow)] => {
          val groupedByUser = seq.groupBy(_._2.userId)
          val unsorted = groupedByUser.map { case (userId: Option[Long], data: Seq[(Tables.UsersRow, Tables.ReputationeventRow)]) => (data.map(r => r._2.karmareward).sum, userId.getOrElse(0L), data.head._1, data.map(_._2)) }
          unsorted.toSeq.sortWith(_._1 > _._1)
        }
      }
  }

  def tagNo(tag: String) = db.run(Tables.Tagx.filter(t => t.name === tag).map(_.id).result.headOption)

  def tagQuestionsUsersQuery(number: Long) = {
    Tables.QuestioninformationTag.filter(t => t.tagsId === number).join(Tables.Questioninformation).on(_.questioninformationId === _.id).
      join(Tables.Question.filter(_.deleted === false)).on(_._2.id === _.informationId).
      join(Tables.Users).on(_._2.authorId === _.id).
      map { case (((qit, qi), q), users) => (q, qi, users) }
  }

  def user(number: Long) = db.run(Tables.Users.filter(u => u.deleted === false && u.id === number).result.headOption)

}


  
