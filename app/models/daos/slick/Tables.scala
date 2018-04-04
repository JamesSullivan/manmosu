package models.daos.slick

import slick.jdbc.MySQLProfile


// AUTO-GENERATED Slick data model
// Tag changed to Tagx because of naming collision
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
  var dbName = "";
} with Tables {
}


/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: MySQLProfile
  var dbName: String
  import profile.api._
  import scala.language.implicitConversions 
  
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(Answer.schema, AnswerAttachment.schema, AnswerComments.schema, AnswerFlags.schema, Answerinformation.schema, AnswerVotes.schema, Attachment.schema, Blockedip.schema, Brutalmigration.schema, Comment.schema, CommentFlags.schema, CommentVotes.schema, Flag.schema, Loginmethod.schema, News.schema, NewsComments.schema, NewsFlags.schema, Newsinformation.schema, Newslettersentlog.schema, NewsVotes.schema, NewsWatchers.schema, Question.schema, QuestionAttachment.schema, QuestionComments.schema, QuestionFlags.schema, Questioninformation.schema, QuestioninformationTag.schema, QuestionInteractions.schema, QuestionVotes.schema, QuestionWatchers.schema, Reputationevent.schema, Tagx.schema, Tagpage.schema, Users.schema, Usersession.schema, Vote.schema, Watcher.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

 
  /** Entity class storing rows of table Answer
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param createdat Database column createdAt SqlType(DATETIME), Default(None)
   *  @param lastupdatedat Database column lastUpdatedAt SqlType(DATETIME), Default(None)
   *  @param invisible Database column invisible SqlType(BIT)
   *  @param votecount Database column voteCount SqlType(BIGINT)
   *  @param authorId Database column author_id SqlType(BIGINT), Default(None)
   *  @param informationId Database column information_id SqlType(BIGINT)
   *  @param lasttouchedbyId Database column lastTouchedBy_id SqlType(BIGINT), Default(None)
   *  @param questionId Database column question_id SqlType(BIGINT), Default(None)
   *  @param deleted Database column deleted SqlType(BIT), Default(Some(false)) */
  case class AnswerRow(id: Long, createdat: Option[java.sql.Timestamp] = None, lastupdatedat: Option[java.sql.Timestamp] = None, invisible: Boolean, votecount: Long, authorId: Option[Long] = None, informationId: Long, lasttouchedbyId: Option[Long] = None, questionId: Option[Long] = None, deleted: Option[Boolean] = Some(false)) extends QATextInputRow
  /** GetResult implicit for fetching AnswerRow objects using plain SQL queries */
  implicit def GetResultAnswerRow(implicit e0: GR[Long], e1: GR[Option[java.sql.Timestamp]], e2: GR[Boolean], e3: GR[Option[Long]], e4: GR[Option[Boolean]]): GR[AnswerRow] = GR{
    prs => import prs._

    AnswerRow.tupled((<<[Long], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<[Boolean], <<[Long], <<?[Long], <<[Long], <<?[Long], <<?[Long], <<?[Boolean]))
  }
  /** Table description of table Answer. Objects of this class serve as prototypes for rows in queries. */
  class Answer(_tableTag: Tag) extends profile.api.Table[AnswerRow](_tableTag, Some(dbName), "Answer") {
    def * = (id, createdat, lastupdatedat, invisible, votecount, authorId, informationId, lasttouchedbyId, questionId, deleted) <> (AnswerRow.tupled, AnswerRow.unapply )
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), createdat, lastupdatedat, Rep.Some(invisible), Rep.Some(votecount), authorId, Rep.Some(informationId), lasttouchedbyId, questionId, deleted).shaped.<>({r=>import r._; _1.map(_=> AnswerRow.tupled((_1.get, _2, _3, _4.get, _5.get, _6, _7.get, _8, _9, _10)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column createdAt SqlType(DATETIME), Default(None) */
    val createdat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt", O.Default(None))
    /** Database column lastUpdatedAt SqlType(DATETIME), Default(None) */
    val lastupdatedat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("lastUpdatedAt", O.Default(None))
    /** Database column invisible SqlType(BIT) */
    val invisible: Rep[Boolean] = column[Boolean]("invisible")
    /** Database column voteCount SqlType(BIGINT) */
    val votecount: Rep[Long] = column[Long]("voteCount")
    /** Database column author_id SqlType(BIGINT), Default(None) */
    val authorId: Rep[Option[Long]] = column[Option[Long]]("author_id", O.Default(None))
    /** Database column information_id SqlType(BIGINT) */
    val informationId: Rep[Long] = column[Long]("information_id")
    /** Database column lastTouchedBy_id SqlType(BIGINT), Default(None) */
    val lasttouchedbyId: Rep[Option[Long]] = column[Option[Long]]("lastTouchedBy_id", O.Default(None))
    /** Database column question_id SqlType(BIGINT), Default(None) */
    val questionId: Rep[Option[Long]] = column[Option[Long]]("question_id", O.Default(None))
    /** Database column deleted SqlType(BIT), Default(Some(false)) */
    val deleted: Rep[Option[Boolean]] = column[Option[Boolean]]("deleted", O.Default(Some(false)))

    /** Foreign key referencing Answerinformation (database name FK_dshhxt02iww0fxkl2li8l3ao2) */
    lazy val answerinformationFk = foreignKey("FK_dshhxt02iww0fxkl2li8l3ao2", informationId, Answerinformation)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Question (database name FK_10g8xii7lw9kq0kcsobgmtw72) */
    lazy val questionFk = foreignKey("FK_10g8xii7lw9kq0kcsobgmtw72", questionId, Question)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name FK_3q5s4b88xp78n3c49dtxfs97e) */
    lazy val usersFk3 = foreignKey("FK_3q5s4b88xp78n3c49dtxfs97e", authorId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name FK_drifk8pp2s7wsh57nvwna9m1g) */
    lazy val usersFk4 = foreignKey("FK_drifk8pp2s7wsh57nvwna9m1g", lasttouchedbyId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Answer */
  lazy val Answer = new TableQuery(tag => new Answer(tag))

  /** Entity class storing rows of table AnswerAttachment
   *  @param answerId Database column Answer_id SqlType(BIGINT)
   *  @param attachmentsId Database column attachments_id SqlType(BIGINT) */
  case class AnswerAttachmentRow(answerId: Long, attachmentsId: Long)
  /** GetResult implicit for fetching AnswerAttachmentRow objects using plain SQL queries */
  implicit def GetResultAnswerAttachmentRow(implicit e0: GR[Long]): GR[AnswerAttachmentRow] = GR{
    prs => import prs._
    AnswerAttachmentRow.tupled((<<[Long], <<[Long]))
  }
  /** Table description of table Answer_Attachment. Objects of this class serve as prototypes for rows in queries. */
  class AnswerAttachment(_tableTag: Tag) extends profile.api.Table[AnswerAttachmentRow](_tableTag, Some(dbName), "Answer_Attachment") {
    def * = (answerId, attachmentsId) <> (AnswerAttachmentRow.tupled, AnswerAttachmentRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(answerId), Rep.Some(attachmentsId)).shaped.<>({r=>import r._; _1.map(_=> AnswerAttachmentRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Answer_id SqlType(BIGINT) */
    val answerId: Rep[Long] = column[Long]("Answer_id")
    /** Database column attachments_id SqlType(BIGINT) */
    val attachmentsId: Rep[Long] = column[Long]("attachments_id")

    /** Primary key of AnswerAttachment (database name Answer_Attachment_PK) */
    val pk = primaryKey("Answer_Attachment_PK", (answerId, attachmentsId))

    /** Foreign key referencing Answer (database name FK_2r3h5i8jc5w2kqqhnhhlvenht) */
    lazy val answerFk = foreignKey("FK_2r3h5i8jc5w2kqqhnhhlvenht", answerId, Answer)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Attachment (database name FK_m8lisjgd2lw0uy50bxngkvi5o) */
    lazy val attachmentFk = foreignKey("FK_m8lisjgd2lw0uy50bxngkvi5o", attachmentsId, Attachment)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (attachmentsId) (database name UK_m8lisjgd2lw0uy50bxngkvi5o) */
    val index1 = index("UK_m8lisjgd2lw0uy50bxngkvi5o", attachmentsId, unique=true)
  }
  /** Collection-like TableQuery object for table AnswerAttachment */
  lazy val AnswerAttachment = new TableQuery(tag => new AnswerAttachment(tag))

  /** Entity class storing rows of table AnswerComments
   *  @param answerId Database column Answer_id SqlType(BIGINT)
   *  @param commentsId Database column comments_id SqlType(BIGINT) */
  case class AnswerCommentsRow(answerId: Long, commentsId: Long)
  /** GetResult implicit for fetching AnswerCommentsRow objects using plain SQL queries */
  implicit def GetResultAnswerCommentsRow(implicit e0: GR[Long]): GR[AnswerCommentsRow] = GR{
    prs => import prs._
    AnswerCommentsRow.tupled((<<[Long], <<[Long]))
  }
  /** Table description of table Answer_Comments. Objects of this class serve as prototypes for rows in queries. */
  class AnswerComments(_tableTag: Tag) extends profile.api.Table[AnswerCommentsRow](_tableTag, Some(dbName), "Answer_Comments") {
    def * = (answerId, commentsId) <> (AnswerCommentsRow.tupled, AnswerCommentsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(answerId), Rep.Some(commentsId)).shaped.<>({r=>import r._; _1.map(_=> AnswerCommentsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Answer_id SqlType(BIGINT) */
    val answerId: Rep[Long] = column[Long]("Answer_id")
    /** Database column comments_id SqlType(BIGINT) */
    val commentsId: Rep[Long] = column[Long]("comments_id")

    /** Foreign key referencing Answer (database name FK_5c40gkw8p92hpuy5nnothdhw5) */
    lazy val answerFk = foreignKey("FK_5c40gkw8p92hpuy5nnothdhw5", answerId, Answer)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Comment (database name FK_731ugn0r28nit0o73yytcw5oh) */
    lazy val commentFk = foreignKey("FK_731ugn0r28nit0o73yytcw5oh", commentsId, Comment)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (commentsId) (database name UK_731ugn0r28nit0o73yytcw5oh) */
    val index1 = index("UK_731ugn0r28nit0o73yytcw5oh", commentsId, unique=true)
  }
  /** Collection-like TableQuery object for table AnswerComments */
  lazy val AnswerComments = new TableQuery(tag => new AnswerComments(tag))

  /** Entity class storing rows of table AnswerFlags
   *  @param answerId Database column Answer_id SqlType(BIGINT)
   *  @param flagsId Database column flags_id SqlType(BIGINT) */
  case class AnswerFlagsRow(answerId: Long, flagsId: Long)
  /** GetResult implicit for fetching AnswerFlagsRow objects using plain SQL queries */
  implicit def GetResultAnswerFlagsRow(implicit e0: GR[Long]): GR[AnswerFlagsRow] = GR{
    prs => import prs._
    AnswerFlagsRow.tupled((<<[Long], <<[Long]))
  }
  /** Table description of table Answer_Flags. Objects of this class serve as prototypes for rows in queries. */
  class AnswerFlags(_tableTag: Tag) extends profile.api.Table[AnswerFlagsRow](_tableTag, Some(dbName), "Answer_Flags") {
    def * = (answerId, flagsId) <> (AnswerFlagsRow.tupled, AnswerFlagsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(answerId), Rep.Some(flagsId)).shaped.<>({r=>import r._; _1.map(_=> AnswerFlagsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Answer_id SqlType(BIGINT) */
    val answerId: Rep[Long] = column[Long]("Answer_id")
    /** Database column flags_id SqlType(BIGINT) */
    val flagsId: Rep[Long] = column[Long]("flags_id")

    /** Foreign key referencing Answer (database name FK_6lq2gt46y9974igmh0jxo666) */
    lazy val answerFk = foreignKey("FK_6lq2gt46y9974igmh0jxo666", answerId, Answer)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Flag (database name FK_9y4wu81bq6tdthk881o8twros) */
    lazy val flagFk = foreignKey("FK_9y4wu81bq6tdthk881o8twros", flagsId, Flag)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (flagsId) (database name UK_9y4wu81bq6tdthk881o8twros) */
    val index1 = index("UK_9y4wu81bq6tdthk881o8twros", flagsId, unique=true)
  }
  /** Collection-like TableQuery object for table AnswerFlags */
  lazy val AnswerFlags = new TableQuery(tag => new AnswerFlags(tag))

  /** Entity class storing rows of table Answerinformation
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param comment Database column comment SqlType(LONGTEXT), Length(2147483647,true)
   *  @param createdat Database column createdAt SqlType(DATETIME), Default(None)
   *  @param description Database column description SqlType(LONGTEXT), Length(2147483647,true)
   *  @param ip Database column ip SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param markeddescription Database column markedDescription SqlType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param moderatedat Database column moderatedAt SqlType(DATETIME), Default(None)
   *  @param status Database column status SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param answerId Database column answer_id SqlType(BIGINT), Default(None)
   *  @param authorId Database column author_id SqlType(BIGINT)
   *  @param moderatedbyId Database column moderatedBy_id SqlType(BIGINT), Default(None) */
  case class AnswerinformationRow(id: Long, comment: String, createdat: Option[java.sql.Timestamp] = None, description: String, ip: Option[String] = None, markeddescription: Option[String] = None, moderatedat: Option[java.sql.Timestamp] = None, status: Option[String] = None, answerId: Option[Long] = None, authorId: Long, moderatedbyId: Option[Long] = None)
  /** GetResult implicit for fetching AnswerinformationRow objects using plain SQL queries */
  implicit def GetResultAnswerinformationRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Option[java.sql.Timestamp]], e3: GR[Option[String]], e4: GR[Option[Long]]): GR[AnswerinformationRow] = GR{
    prs => import prs._
    AnswerinformationRow.tupled((<<[Long], <<[String], <<?[java.sql.Timestamp], <<[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<?[String], <<?[Long], <<[Long], <<?[Long]))
  }
  /** Table description of table AnswerInformation. Objects of this class serve as prototypes for rows in queries. */
  class Answerinformation(_tableTag: Tag) extends profile.api.Table[AnswerinformationRow](_tableTag, Some(dbName), "AnswerInformation") {
    def * = (id, comment, createdat, description, ip, markeddescription, moderatedat, status, answerId, authorId, moderatedbyId) <> (AnswerinformationRow.tupled, AnswerinformationRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(comment), createdat, Rep.Some(description), ip, markeddescription, moderatedat, status, answerId, Rep.Some(authorId), moderatedbyId).shaped.<>({r=>import r._; _1.map(_=> AnswerinformationRow.tupled((_1.get, _2.get, _3, _4.get, _5, _6, _7, _8, _9, _10.get, _11)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column comment SqlType(LONGTEXT), Length(2147483647,true) */
    val comment: Rep[String] = column[String]("comment", O.Length(2147483647,varying=true))
    /** Database column createdAt SqlType(DATETIME), Default(None) */
    val createdat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt", O.Default(None))
    /** Database column description SqlType(LONGTEXT), Length(2147483647,true) */
    val description: Rep[String] = column[String]("description", O.Length(2147483647,varying=true))
    /** Database column ip SqlType(VARCHAR), Length(255,true), Default(None) */
    val ip: Rep[Option[String]] = column[Option[String]]("ip", O.Length(255,varying=true), O.Default(None))
    /** Database column markedDescription SqlType(LONGTEXT), Length(2147483647,true), Default(None) */
    val markeddescription: Rep[Option[String]] = column[Option[String]]("markedDescription", O.Length(2147483647,varying=true), O.Default(None))
    /** Database column moderatedAt SqlType(DATETIME), Default(None) */
    val moderatedat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("moderatedAt", O.Default(None))
    /** Database column status SqlType(VARCHAR), Length(255,true), Default(None) */
    val status: Rep[Option[String]] = column[Option[String]]("status", O.Length(255,varying=true), O.Default(None))
    /** Database column answer_id SqlType(BIGINT), Default(None) */
    val answerId: Rep[Option[Long]] = column[Option[Long]]("answer_id", O.Default(None))
    /** Database column author_id SqlType(BIGINT) */
    val authorId: Rep[Long] = column[Long]("author_id")
    /** Database column moderatedBy_id SqlType(BIGINT), Default(None) */
    val moderatedbyId: Rep[Option[Long]] = column[Option[Long]]("moderatedBy_id", O.Default(None))

    /** Foreign key referencing Answer (database name FK_lecgqmqj00d06wb8nwaq60rpr) */
    lazy val answerFk = foreignKey("FK_lecgqmqj00d06wb8nwaq60rpr", answerId, Answer)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name FK_237rcoro0n05xyxjga1ip7pd8) */
    lazy val usersFk2 = foreignKey("FK_237rcoro0n05xyxjga1ip7pd8", authorId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name FK_dbuximcggdn5k2j9svvpwtxrm) */
    lazy val usersFk3 = foreignKey("FK_dbuximcggdn5k2j9svvpwtxrm", moderatedbyId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Answerinformation */
  lazy val Answerinformation = new TableQuery(tag => new Answerinformation(tag))

  /** Entity class storing rows of table AnswerVotes
   *  @param answerId Database column Answer_id SqlType(BIGINT)
   *  @param votesId Database column votes_id SqlType(BIGINT) */
  case class AnswerVotesRow(answerId: Long, votesId: Long)
  /** GetResult implicit for fetching AnswerVotesRow objects using plain SQL queries */
  implicit def GetResultAnswerVotesRow(implicit e0: GR[Long]): GR[AnswerVotesRow] = GR{
    prs => import prs._
    AnswerVotesRow.tupled((<<[Long], <<[Long]))
  }
  /** Table description of table Answer_Votes. Objects of this class serve as prototypes for rows in queries. */
  class AnswerVotes(_tableTag: Tag) extends profile.api.Table[AnswerVotesRow](_tableTag, Some(dbName), "Answer_Votes") {
    def * = (answerId, votesId) <> (AnswerVotesRow.tupled, AnswerVotesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(answerId), Rep.Some(votesId)).shaped.<>({r=>import r._; _1.map(_=> AnswerVotesRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Answer_id SqlType(BIGINT) */
    val answerId: Rep[Long] = column[Long]("Answer_id")
    /** Database column votes_id SqlType(BIGINT) */
    val votesId: Rep[Long] = column[Long]("votes_id")

    /** Foreign key referencing Answer (database name FK_qpawvfihxtc49opw1q5le336l) */
    lazy val answerFk = foreignKey("FK_qpawvfihxtc49opw1q5le336l", answerId, Answer)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Vote (database name FK_5ya5d072g3h38tvb9brj7bs8o) */
    lazy val voteFk = foreignKey("FK_5ya5d072g3h38tvb9brj7bs8o", votesId, Vote)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (votesId) (database name UK_5ya5d072g3h38tvb9brj7bs8o) */
    val index1 = index("UK_5ya5d072g3h38tvb9brj7bs8o", votesId, unique=true)
  }
  /** Collection-like TableQuery object for table AnswerVotes */
  lazy val AnswerVotes = new TableQuery(tag => new AnswerVotes(tag))

  /** Entity class storing rows of table Attachment
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param createdat Database column createdAt SqlType(DATETIME), Default(None)
   *  @param ip Database column ip SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param mime Database column mime SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param name Database column name SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param ownerId Database column owner_id SqlType(BIGINT), Default(None) */
  case class AttachmentRow(id: Long, createdat: Option[java.sql.Timestamp] = None, ip: Option[String] = None, mime: Option[String] = None, name: Option[String] = None, ownerId: Option[Long] = None)
  /** GetResult implicit for fetching AttachmentRow objects using plain SQL queries */
  implicit def GetResultAttachmentRow(implicit e0: GR[Long], e1: GR[Option[java.sql.Timestamp]], e2: GR[Option[String]], e3: GR[Option[Long]]): GR[AttachmentRow] = GR{
    prs => import prs._
    AttachmentRow.tupled((<<[Long], <<?[java.sql.Timestamp], <<?[String], <<?[String], <<?[String], <<?[Long]))
  }
  /** Table description of table Attachment. Objects of this class serve as prototypes for rows in queries. */
  class Attachment(_tableTag: Tag) extends profile.api.Table[AttachmentRow](_tableTag, Some(dbName), "Attachment") {
    def * = (id, createdat, ip, mime, name, ownerId) <> (AttachmentRow.tupled, AttachmentRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), createdat, ip, mime, name, ownerId).shaped.<>({r=>import r._; _1.map(_=> AttachmentRow.tupled((_1.get, _2, _3, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column createdAt SqlType(DATETIME), Default(None) */
    val createdat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt", O.Default(None))
    /** Database column ip SqlType(VARCHAR), Length(255,true), Default(None) */
    val ip: Rep[Option[String]] = column[Option[String]]("ip", O.Length(255,varying=true), O.Default(None))
    /** Database column mime SqlType(VARCHAR), Length(255,true), Default(None) */
    val mime: Rep[Option[String]] = column[Option[String]]("mime", O.Length(255,varying=true), O.Default(None))
    /** Database column name SqlType(VARCHAR), Length(255,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(255,varying=true), O.Default(None))
    /** Database column owner_id SqlType(BIGINT), Default(None) */
    val ownerId: Rep[Option[Long]] = column[Option[Long]]("owner_id", O.Default(None))

    /** Foreign key referencing Users (database name FK_enrbut32jkvqv2ttop49nkcb4) */
    lazy val usersFk = foreignKey("FK_enrbut32jkvqv2ttop49nkcb4", ownerId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Attachment */
  lazy val Attachment = new TableQuery(tag => new Attachment(tag))

  /** Entity class storing rows of table Blockedip
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param createdat Database column createdAt SqlType(DATETIME), Default(None)
   *  @param ip Database column ip SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param authorId Database column author_id SqlType(BIGINT), Default(None) */
  case class BlockedipRow(id: Long, createdat: Option[java.sql.Timestamp] = None, ip: Option[String] = None, authorId: Option[Long] = None)
  /** GetResult implicit for fetching BlockedipRow objects using plain SQL queries */
  implicit def GetResultBlockedipRow(implicit e0: GR[Long], e1: GR[Option[java.sql.Timestamp]], e2: GR[Option[String]], e3: GR[Option[Long]]): GR[BlockedipRow] = GR{
    prs => import prs._
    BlockedipRow.tupled((<<[Long], <<?[java.sql.Timestamp], <<?[String], <<?[Long]))
  }
  /** Table description of table BlockedIp. Objects of this class serve as prototypes for rows in queries. */
  class Blockedip(_tableTag: Tag) extends profile.api.Table[BlockedipRow](_tableTag, Some(dbName), "BlockedIp") {
    def * = (id, createdat, ip, authorId) <> (BlockedipRow.tupled, BlockedipRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), createdat, ip, authorId).shaped.<>({r=>import r._; _1.map(_=> BlockedipRow.tupled((_1.get, _2, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column createdAt SqlType(DATETIME), Default(None) */
    val createdat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt", O.Default(None))
    /** Database column ip SqlType(VARCHAR), Length(255,true), Default(None) */
    val ip: Rep[Option[String]] = column[Option[String]]("ip", O.Length(255,varying=true), O.Default(None))
    /** Database column author_id SqlType(BIGINT), Default(None) */
    val authorId: Rep[Option[Long]] = column[Option[Long]]("author_id", O.Default(None))

    /** Foreign key referencing Users (database name FK_jx0qq5i8p02d6geh0cp4yl2lh) */
    lazy val usersFk = foreignKey("FK_jx0qq5i8p02d6geh0cp4yl2lh", authorId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Blockedip */
  lazy val Blockedip = new TableQuery(tag => new Blockedip(tag))

  /** Entity class storing rows of table Brutalmigration
   *  @param number Database column number SqlType(INT), Default(None) */
  case class BrutalmigrationRow(number: Option[Int] = None)
  /** GetResult implicit for fetching BrutalmigrationRow objects using plain SQL queries */
  implicit def GetResultBrutalmigrationRow(implicit e0: GR[Option[Int]]): GR[BrutalmigrationRow] = GR{
    prs => import prs._
    BrutalmigrationRow(<<?[Int])
  }
  /** Table description of table brutalmigration. Objects of this class serve as prototypes for rows in queries. */
  class Brutalmigration(_tableTag: Tag) extends profile.api.Table[BrutalmigrationRow](_tableTag, Some(dbName), "brutalmigration") {
    def * = number <> (BrutalmigrationRow, BrutalmigrationRow.unapply)

    /** Database column number SqlType(INT), Default(None) */
    val number: Rep[Option[Int]] = column[Option[Int]]("number", O.Default(None))
  }
  /** Collection-like TableQuery object for table Brutalmigration */
  lazy val Brutalmigration = new TableQuery(tag => new Brutalmigration(tag))

  /** Entity class storing rows of table Comment
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param comment Database column comment SqlType(LONGTEXT), Length(2147483647,true)
   *  @param createdat Database column createdAt SqlType(DATETIME), Default(None)
   *  @param htmlcomment Database column htmlComment SqlType(LONGTEXT), Length(2147483647,true)
   *  @param lastupdatedat Database column lastUpdatedAt SqlType(DATETIME), Default(None)
   *  @param invisible Database column invisible SqlType(BIT)
   *  @param votecount Database column voteCount SqlType(BIGINT)
   *  @param authorId Database column author_id SqlType(BIGINT)
   *  @param deleted Database column deleted SqlType(BIT), Default(Some(false)) */
  case class CommentRow(id: Long, comment: String, createdat: Option[java.sql.Timestamp] = None, htmlcomment: String, lastupdatedat: Option[java.sql.Timestamp] = None, invisible: Boolean, votecount: Long, authorId: Long, deleted: Option[Boolean] = Some(false)) extends TextInputRow
  /** GetResult implicit for fetching CommentRow objects using plain SQL queries */
  implicit def GetResultCommentRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Option[java.sql.Timestamp]], e3: GR[Boolean], e4: GR[Option[Boolean]]): GR[CommentRow] = GR{
    prs => import prs._
    CommentRow.tupled((<<[Long], <<[String], <<?[java.sql.Timestamp], <<[String], <<?[java.sql.Timestamp], <<[Boolean], <<[Long], <<[Long], <<?[Boolean]))
  }
  /** Table description of table Comment. Objects of this class serve as prototypes for rows in queries. */
  class Comment(_tableTag: Tag) extends profile.api.Table[CommentRow](_tableTag, Some(dbName), "Comment") {
    def * = (id, comment, createdat, htmlcomment, lastupdatedat, invisible, votecount, authorId, deleted) <> (CommentRow.tupled, CommentRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(comment), createdat, Rep.Some(htmlcomment), lastupdatedat, Rep.Some(invisible), Rep.Some(votecount), Rep.Some(authorId), deleted).shaped.<>({r=>import r._; _1.map(_=> CommentRow.tupled((_1.get, _2.get, _3, _4.get, _5, _6.get, _7.get, _8.get, _9)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column comment SqlType(LONGTEXT), Length(2147483647,true) */
    val comment: Rep[String] = column[String]("comment", O.Length(2147483647,varying=true))
    /** Database column createdAt SqlType(DATETIME), Default(None) */
    val createdat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt", O.Default(None))
    /** Database column htmlComment SqlType(LONGTEXT), Length(2147483647,true) */
    val htmlcomment: Rep[String] = column[String]("htmlComment", O.Length(2147483647,varying=true))
    /** Database column lastUpdatedAt SqlType(DATETIME), Default(None) */
    val lastupdatedat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("lastUpdatedAt", O.Default(None))
    /** Database column invisible SqlType(BIT) */
    val invisible: Rep[Boolean] = column[Boolean]("invisible")
    /** Database column voteCount SqlType(BIGINT) */
    val votecount: Rep[Long] = column[Long]("voteCount")
    /** Database column author_id SqlType(BIGINT) */
    val authorId: Rep[Long] = column[Long]("author_id")
    /** Database column deleted SqlType(BIT), Default(Some(false)) */
    val deleted: Rep[Option[Boolean]] = column[Option[Boolean]]("deleted", O.Default(Some(false)))

    /** Foreign key referencing Users (database name FK_j94pith5sd971k29j6ysxuk7) */
    lazy val usersFk = foreignKey("FK_j94pith5sd971k29j6ysxuk7", authorId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Comment */
  lazy val Comment = new TableQuery(tag => new Comment(tag))

  /** Entity class storing rows of table CommentFlags
   *  @param commentId Database column Comment_id SqlType(BIGINT)
   *  @param flagsId Database column flags_id SqlType(BIGINT) */
  case class CommentFlagsRow(commentId: Long, flagsId: Long)
  /** GetResult implicit for fetching CommentFlagsRow objects using plain SQL queries */
  implicit def GetResultCommentFlagsRow(implicit e0: GR[Long]): GR[CommentFlagsRow] = GR{
    prs => import prs._
    CommentFlagsRow.tupled((<<[Long], <<[Long]))
  }
  /** Table description of table Comment_Flags. Objects of this class serve as prototypes for rows in queries. */
  class CommentFlags(_tableTag: Tag) extends profile.api.Table[CommentFlagsRow](_tableTag, Some(dbName), "Comment_Flags") {
    def * = (commentId, flagsId) <> (CommentFlagsRow.tupled, CommentFlagsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(commentId), Rep.Some(flagsId)).shaped.<>({r=>import r._; _1.map(_=> CommentFlagsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Comment_id SqlType(BIGINT) */
    val commentId: Rep[Long] = column[Long]("Comment_id")
    /** Database column flags_id SqlType(BIGINT) */
    val flagsId: Rep[Long] = column[Long]("flags_id")

    /** Foreign key referencing Comment (database name FK_g45y0rm9o8k7uyoih84rrccra) */
    lazy val commentFk = foreignKey("FK_g45y0rm9o8k7uyoih84rrccra", commentId, Comment)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Flag (database name FK_gkehc29f3h04bua96bjurv4vd) */
    lazy val flagFk = foreignKey("FK_gkehc29f3h04bua96bjurv4vd", flagsId, Flag)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (flagsId) (database name UK_gkehc29f3h04bua96bjurv4vd) */
    val index1 = index("UK_gkehc29f3h04bua96bjurv4vd", flagsId, unique=true)
  }
  /** Collection-like TableQuery object for table CommentFlags */
  lazy val CommentFlags = new TableQuery(tag => new CommentFlags(tag))

  /** Entity class storing rows of table CommentVotes
   *  @param commentId Database column Comment_id SqlType(BIGINT)
   *  @param votesId Database column votes_id SqlType(BIGINT) */
  case class CommentVotesRow(commentId: Long, votesId: Long)
  /** GetResult implicit for fetching CommentVotesRow objects using plain SQL queries */
  implicit def GetResultCommentVotesRow(implicit e0: GR[Long]): GR[CommentVotesRow] = GR{
    prs => import prs._
    CommentVotesRow.tupled((<<[Long], <<[Long]))
  }
  /** Table description of table Comment_Votes. Objects of this class serve as prototypes for rows in queries. */
  class CommentVotes(_tableTag: Tag) extends profile.api.Table[CommentVotesRow](_tableTag, Some(dbName), "Comment_Votes") {
    def * = (commentId, votesId) <> (CommentVotesRow.tupled, CommentVotesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(commentId), Rep.Some(votesId)).shaped.<>({r=>import r._; _1.map(_=> CommentVotesRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Comment_id SqlType(BIGINT) */
    val commentId: Rep[Long] = column[Long]("Comment_id")
    /** Database column votes_id SqlType(BIGINT) */
    val votesId: Rep[Long] = column[Long]("votes_id")

    /** Foreign key referencing Comment (database name FK_obxdv4j0ph2swt8r81dx8h0yw) */
    lazy val commentFk = foreignKey("FK_obxdv4j0ph2swt8r81dx8h0yw", commentId, Comment)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Vote (database name FK_dtgxtqffciorpdsjdshnkbpw6) */
    lazy val voteFk = foreignKey("FK_dtgxtqffciorpdsjdshnkbpw6", votesId, Vote)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (votesId) (database name UK_dtgxtqffciorpdsjdshnkbpw6) */
    val index1 = index("UK_dtgxtqffciorpdsjdshnkbpw6", votesId, unique=true)
  }
  /** Collection-like TableQuery object for table CommentVotes */
  lazy val CommentVotes = new TableQuery(tag => new CommentVotes(tag))

  /** Entity class storing rows of table Flag
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param reason Database column reason SqlType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param `type` Database column type SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param authorId Database column author_id SqlType(BIGINT), Default(None)
   *  @param deleted Database column deleted SqlType(BIT), Default(Some(false)) */
  case class FlagRow(id: Long, reason: Option[String] = None, `type`: Option[String] = None, authorId: Option[Long] = None, deleted: Option[Boolean] = Some(false))
  /** GetResult implicit for fetching FlagRow objects using plain SQL queries */
  implicit def GetResultFlagRow(implicit e0: GR[Long], e1: GR[Option[String]], e2: GR[Option[Long]], e3: GR[Option[Boolean]]): GR[FlagRow] = GR{
    prs => import prs._
    FlagRow.tupled((<<[Long], <<?[String], <<?[String], <<?[Long], <<?[Boolean]))
  }
  /** Table description of table Flag. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class Flag(_tableTag: Tag) extends profile.api.Table[FlagRow](_tableTag, Some(dbName), "Flag") {
    def * = (id, reason, `type`, authorId, deleted) <> (FlagRow.tupled, FlagRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), reason, `type`, authorId, deleted).shaped.<>({r=>import r._; _1.map(_=> FlagRow.tupled((_1.get, _2, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column reason SqlType(LONGTEXT), Length(2147483647,true), Default(None) */
    val reason: Rep[Option[String]] = column[Option[String]]("reason", O.Length(2147483647,varying=true), O.Default(None))
    /** Database column type SqlType(VARCHAR), Length(255,true), Default(None)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Rep[Option[String]] = column[Option[String]]("type", O.Length(255,varying=true), O.Default(None))
    /** Database column author_id SqlType(BIGINT), Default(None) */
    val authorId: Rep[Option[Long]] = column[Option[Long]]("author_id", O.Default(None))
    /** Database column deleted SqlType(BIT), Default(Some(false)) */
    val deleted: Rep[Option[Boolean]] = column[Option[Boolean]]("deleted", O.Default(Some(false)))

    /** Foreign key referencing Users (database name FK_lumgu8dnorkynxw1l039ovm8q) */
    lazy val usersFk = foreignKey("FK_lumgu8dnorkynxw1l039ovm8q", authorId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Flag */
  lazy val Flag = new TableQuery(tag => new Flag(tag))

  /** Entity class storing rows of table Loginmethod
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param serviceemail Database column serviceEmail SqlType(VARCHAR), Length(100,true), Default(None)
   *  @param token Database column token SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param `type` Database column type SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param userId Database column user_id SqlType(BIGINT), Default(None) */
  case class LoginmethodRow(id: Long, serviceemail: Option[String] = None, token: Option[String] = None, `type`: Option[String] = None, userId: Option[Long] = None)
  /** GetResult implicit for fetching LoginmethodRow objects using plain SQL queries */
  implicit def GetResultLoginmethodRow(implicit e0: GR[Long], e1: GR[Option[String]], e2: GR[Option[Long]]): GR[LoginmethodRow] = GR{
    prs => import prs._
    LoginmethodRow.tupled((<<[Long], <<?[String], <<?[String], <<?[String], <<?[Long]))
  }
  /** Table description of table LoginMethod. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class Loginmethod(_tableTag: Tag) extends profile.api.Table[LoginmethodRow](_tableTag, Some(dbName), "LoginMethod") {
    def * = (id, serviceemail, token, `type`, userId) <> (LoginmethodRow.tupled, LoginmethodRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), serviceemail, token, `type`, userId).shaped.<>({r=>import r._; _1.map(_=> LoginmethodRow.tupled((_1.get, _2, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column serviceEmail SqlType(VARCHAR), Length(100,true), Default(None) */
    val serviceemail: Rep[Option[String]] = column[Option[String]]("serviceEmail", O.Length(100,varying=true), O.Default(None))
    /** Database column token SqlType(VARCHAR), Length(255,true), Default(None) */
    val token: Rep[Option[String]] = column[Option[String]]("token", O.Length(255,varying=true), O.Default(None))
    /** Database column type SqlType(VARCHAR), Length(255,true), Default(None)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Rep[Option[String]] = column[Option[String]]("type", O.Length(255,varying=true), O.Default(None))
    /** Database column user_id SqlType(BIGINT), Default(None) */
    val userId: Rep[Option[Long]] = column[Option[Long]]("user_id", O.Default(None))

    /** Foreign key referencing Users (database name FK_l0vhdtw8ymw1rrxq9usrqjh4x) */
    lazy val usersFk = foreignKey("FK_l0vhdtw8ymw1rrxq9usrqjh4x", userId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Loginmethod */
  lazy val Loginmethod = new TableQuery(tag => new Loginmethod(tag))

  /** Entity class storing rows of table News
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param approved Database column approved SqlType(BIT)
   *  @param createdat Database column createdAt SqlType(DATETIME), Default(None)
   *  @param lastupdatedat Database column lastUpdatedAt SqlType(DATETIME), Default(None)
   *  @param invisible Database column invisible SqlType(BIT)
   *  @param views Database column views SqlType(BIGINT)
   *  @param votecount Database column voteCount SqlType(BIGINT)
   *  @param authorId Database column author_id SqlType(BIGINT), Default(None)
   *  @param informationId Database column information_id SqlType(BIGINT)
   *  @param lasttouchedbyId Database column lastTouchedBy_id SqlType(BIGINT), Default(None) */
  case class NewsRow(id: Long, approved: Boolean, createdat: Option[java.sql.Timestamp] = None, lastupdatedat: Option[java.sql.Timestamp] = None, invisible: Boolean, views: Long, votecount: Long, authorId: Option[Long] = None, informationId: Long, lasttouchedbyId: Option[Long] = None)
  /** GetResult implicit for fetching NewsRow objects using plain SQL queries */
  implicit def GetResultNewsRow(implicit e0: GR[Long], e1: GR[Boolean], e2: GR[Option[java.sql.Timestamp]], e3: GR[Option[Long]]): GR[NewsRow] = GR{
    prs => import prs._
    NewsRow.tupled((<<[Long], <<[Boolean], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<[Boolean], <<[Long], <<[Long], <<?[Long], <<[Long], <<?[Long]))
  }
  /** Table description of table News. Objects of this class serve as prototypes for rows in queries. */
  class News(_tableTag: Tag) extends profile.api.Table[NewsRow](_tableTag, Some(dbName), "News") {
    def * = (id, approved, createdat, lastupdatedat, invisible, views, votecount, authorId, informationId, lasttouchedbyId) <> (NewsRow.tupled, NewsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(approved), createdat, lastupdatedat, Rep.Some(invisible), Rep.Some(views), Rep.Some(votecount), authorId, Rep.Some(informationId), lasttouchedbyId).shaped.<>({r=>import r._; _1.map(_=> NewsRow.tupled((_1.get, _2.get, _3, _4, _5.get, _6.get, _7.get, _8, _9.get, _10)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column approved SqlType(BIT) */
    val approved: Rep[Boolean] = column[Boolean]("approved")
    /** Database column createdAt SqlType(DATETIME), Default(None) */
    val createdat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt", O.Default(None))
    /** Database column lastUpdatedAt SqlType(DATETIME), Default(None) */
    val lastupdatedat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("lastUpdatedAt", O.Default(None))
    /** Database column invisible SqlType(BIT) */
    val invisible: Rep[Boolean] = column[Boolean]("invisible")
    /** Database column views SqlType(BIGINT) */
    val views: Rep[Long] = column[Long]("views")
    /** Database column voteCount SqlType(BIGINT) */
    val votecount: Rep[Long] = column[Long]("voteCount")
    /** Database column author_id SqlType(BIGINT), Default(None) */
    val authorId: Rep[Option[Long]] = column[Option[Long]]("author_id", O.Default(None))
    /** Database column information_id SqlType(BIGINT) */
    val informationId: Rep[Long] = column[Long]("information_id")
    /** Database column lastTouchedBy_id SqlType(BIGINT), Default(None) */
    val lasttouchedbyId: Rep[Option[Long]] = column[Option[Long]]("lastTouchedBy_id", O.Default(None))

    /** Foreign key referencing Newsinformation (database name FK_5qrrq79ar6t4p4vf4djvqf63i) */
    lazy val newsinformationFk = foreignKey("FK_5qrrq79ar6t4p4vf4djvqf63i", informationId, Newsinformation)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name FK_e3k3kapw96m39ma7uus1r6f7m) */
    lazy val usersFk2 = foreignKey("FK_e3k3kapw96m39ma7uus1r6f7m", authorId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name FK_glms254gw9a4kv5qh3ptijiqd) */
    lazy val usersFk3 = foreignKey("FK_glms254gw9a4kv5qh3ptijiqd", lasttouchedbyId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table News */
  lazy val News = new TableQuery(tag => new News(tag))

  /** Entity class storing rows of table NewsComments
   *  @param newsId Database column News_id SqlType(BIGINT)
   *  @param commentsId Database column comments_id SqlType(BIGINT) */
  case class NewsCommentsRow(newsId: Long, commentsId: Long)
  /** GetResult implicit for fetching NewsCommentsRow objects using plain SQL queries */
  implicit def GetResultNewsCommentsRow(implicit e0: GR[Long]): GR[NewsCommentsRow] = GR{
    prs => import prs._
    NewsCommentsRow.tupled((<<[Long], <<[Long]))
  }
  /** Table description of table News_Comments. Objects of this class serve as prototypes for rows in queries. */
  class NewsComments(_tableTag: Tag) extends profile.api.Table[NewsCommentsRow](_tableTag, Some(dbName), "News_Comments") {
    def * = (newsId, commentsId) <> (NewsCommentsRow.tupled, NewsCommentsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(newsId), Rep.Some(commentsId)).shaped.<>({r=>import r._; _1.map(_=> NewsCommentsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column News_id SqlType(BIGINT) */
    val newsId: Rep[Long] = column[Long]("News_id")
    /** Database column comments_id SqlType(BIGINT) */
    val commentsId: Rep[Long] = column[Long]("comments_id")

    /** Foreign key referencing Comment (database name FK_5rrh2dvs1cy19pliwyvb8w8u7) */
    lazy val commentFk = foreignKey("FK_5rrh2dvs1cy19pliwyvb8w8u7", commentsId, Comment)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing News (database name FK_dcofc609g7loixe8pcm3myemd) */
    lazy val newsFk = foreignKey("FK_dcofc609g7loixe8pcm3myemd", newsId, News)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (commentsId) (database name UK_5rrh2dvs1cy19pliwyvb8w8u7) */
    val index1 = index("UK_5rrh2dvs1cy19pliwyvb8w8u7", commentsId, unique=true)
  }
  /** Collection-like TableQuery object for table NewsComments */
  lazy val NewsComments = new TableQuery(tag => new NewsComments(tag))

  /** Entity class storing rows of table NewsFlags
   *  @param newsId Database column News_id SqlType(BIGINT)
   *  @param flagsId Database column flags_id SqlType(BIGINT) */
  case class NewsFlagsRow(newsId: Long, flagsId: Long)
  /** GetResult implicit for fetching NewsFlagsRow objects using plain SQL queries */
  implicit def GetResultNewsFlagsRow(implicit e0: GR[Long]): GR[NewsFlagsRow] = GR{
    prs => import prs._
    NewsFlagsRow.tupled((<<[Long], <<[Long]))
  }
  /** Table description of table News_Flags. Objects of this class serve as prototypes for rows in queries. */
  class NewsFlags(_tableTag: Tag) extends profile.api.Table[NewsFlagsRow](_tableTag, Some(dbName), "News_Flags") {
    def * = (newsId, flagsId) <> (NewsFlagsRow.tupled, NewsFlagsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(newsId), Rep.Some(flagsId)).shaped.<>({r=>import r._; _1.map(_=> NewsFlagsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column News_id SqlType(BIGINT) */
    val newsId: Rep[Long] = column[Long]("News_id")
    /** Database column flags_id SqlType(BIGINT) */
    val flagsId: Rep[Long] = column[Long]("flags_id")

    /** Foreign key referencing Flag (database name FK_s0ugdfyknbgui197dcioqqovi) */
    lazy val flagFk = foreignKey("FK_s0ugdfyknbgui197dcioqqovi", flagsId, Flag)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing News (database name FK_fr0qqkoqj2rai6g9epq0iw4wu) */
    lazy val newsFk = foreignKey("FK_fr0qqkoqj2rai6g9epq0iw4wu", newsId, News)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (flagsId) (database name UK_s0ugdfyknbgui197dcioqqovi) */
    val index1 = index("UK_s0ugdfyknbgui197dcioqqovi", flagsId, unique=true)
  }
  /** Collection-like TableQuery object for table NewsFlags */
  lazy val NewsFlags = new TableQuery(tag => new NewsFlags(tag))

  /** Entity class storing rows of table Newsinformation
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param comment Database column comment SqlType(LONGTEXT), Length(2147483647,true)
   *  @param createdat Database column createdAt SqlType(DATETIME), Default(None)
   *  @param description Database column description SqlType(LONGTEXT), Length(2147483647,true)
   *  @param ip Database column ip SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param markeddescription Database column markedDescription SqlType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param moderatedat Database column moderatedAt SqlType(DATETIME), Default(None)
   *  @param sluggedtitle Database column sluggedTitle SqlType(LONGTEXT), Length(2147483647,true)
   *  @param status Database column status SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param title Database column title SqlType(LONGTEXT), Length(2147483647,true)
   *  @param authorId Database column author_id SqlType(BIGINT)
   *  @param moderatedbyId Database column moderatedBy_id SqlType(BIGINT), Default(None)
   *  @param newsId Database column news_id SqlType(BIGINT), Default(None) */
  case class NewsinformationRow(id: Long, comment: String, createdat: Option[java.sql.Timestamp] = None, description: String, ip: Option[String] = None, markeddescription: Option[String] = None, moderatedat: Option[java.sql.Timestamp] = None, sluggedtitle: String, status: Option[String] = None, title: String, authorId: Long, moderatedbyId: Option[Long] = None, newsId: Option[Long] = None)
  /** GetResult implicit for fetching NewsinformationRow objects using plain SQL queries */
  implicit def GetResultNewsinformationRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Option[java.sql.Timestamp]], e3: GR[Option[String]], e4: GR[Option[Long]]): GR[NewsinformationRow] = GR{
    prs => import prs._
    NewsinformationRow.tupled((<<[Long], <<[String], <<?[java.sql.Timestamp], <<[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<[String], <<?[String], <<[String], <<[Long], <<?[Long], <<?[Long]))
  }
  /** Table description of table NewsInformation. Objects of this class serve as prototypes for rows in queries. */
  class Newsinformation(_tableTag: Tag) extends profile.api.Table[NewsinformationRow](_tableTag, Some(dbName), "NewsInformation") {
    def * = (id, comment, createdat, description, ip, markeddescription, moderatedat, sluggedtitle, status, title, authorId, moderatedbyId, newsId) <> (NewsinformationRow.tupled, NewsinformationRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(comment), createdat, Rep.Some(description), ip, markeddescription, moderatedat, Rep.Some(sluggedtitle), status, Rep.Some(title), Rep.Some(authorId), moderatedbyId, newsId).shaped.<>({r=>import r._; _1.map(_=> NewsinformationRow.tupled((_1.get, _2.get, _3, _4.get, _5, _6, _7, _8.get, _9, _10.get, _11.get, _12, _13)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column comment SqlType(LONGTEXT), Length(2147483647,true) */
    val comment: Rep[String] = column[String]("comment", O.Length(2147483647,varying=true))
    /** Database column createdAt SqlType(DATETIME), Default(None) */
    val createdat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt", O.Default(None))
    /** Database column description SqlType(LONGTEXT), Length(2147483647,true) */
    val description: Rep[String] = column[String]("description", O.Length(2147483647,varying=true))
    /** Database column ip SqlType(VARCHAR), Length(255,true), Default(None) */
    val ip: Rep[Option[String]] = column[Option[String]]("ip", O.Length(255,varying=true), O.Default(None))
    /** Database column markedDescription SqlType(LONGTEXT), Length(2147483647,true), Default(None) */
    val markeddescription: Rep[Option[String]] = column[Option[String]]("markedDescription", O.Length(2147483647,varying=true), O.Default(None))
    /** Database column moderatedAt SqlType(DATETIME), Default(None) */
    val moderatedat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("moderatedAt", O.Default(None))
    /** Database column sluggedTitle SqlType(LONGTEXT), Length(2147483647,true) */
    val sluggedtitle: Rep[String] = column[String]("sluggedTitle", O.Length(2147483647,varying=true))
    /** Database column status SqlType(VARCHAR), Length(255,true), Default(None) */
    val status: Rep[Option[String]] = column[Option[String]]("status", O.Length(255,varying=true), O.Default(None))
    /** Database column title SqlType(LONGTEXT), Length(2147483647,true) */
    val title: Rep[String] = column[String]("title", O.Length(2147483647,varying=true))
    /** Database column author_id SqlType(BIGINT) */
    val authorId: Rep[Long] = column[Long]("author_id")
    /** Database column moderatedBy_id SqlType(BIGINT), Default(None) */
    val moderatedbyId: Rep[Option[Long]] = column[Option[Long]]("moderatedBy_id", O.Default(None))
    /** Database column news_id SqlType(BIGINT), Default(None) */
    val newsId: Rep[Option[Long]] = column[Option[Long]]("news_id", O.Default(None))

    /** Foreign key referencing News (database name FK_b942i5pshr99wwqdk03d98ofg) */
    lazy val newsFk = foreignKey("FK_b942i5pshr99wwqdk03d98ofg", newsId, News)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name FK_fppkwwg6svwefnnni9ygaefg2) */
    lazy val usersFk2 = foreignKey("FK_fppkwwg6svwefnnni9ygaefg2", moderatedbyId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name FK_n3hxv49li2jcopaj6dio6b8b6) */
    lazy val usersFk3 = foreignKey("FK_n3hxv49li2jcopaj6dio6b8b6", authorId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Newsinformation */
  lazy val Newsinformation = new TableQuery(tag => new Newsinformation(tag))

  /** Entity class storing rows of table Newslettersentlog
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param createdat Database column createdAt SqlType(DATE), Default(None) */
  case class NewslettersentlogRow(id: Long, createdat: Option[java.sql.Date] = None)
  /** GetResult implicit for fetching NewslettersentlogRow objects using plain SQL queries */
  implicit def GetResultNewslettersentlogRow(implicit e0: GR[Long], e1: GR[Option[java.sql.Date]]): GR[NewslettersentlogRow] = GR{
    prs => import prs._
    NewslettersentlogRow.tupled((<<[Long], <<?[java.sql.Date]))
  }
  /** Table description of table NewsletterSentLog. Objects of this class serve as prototypes for rows in queries. */
  class Newslettersentlog(_tableTag: Tag) extends profile.api.Table[NewslettersentlogRow](_tableTag, Some(dbName), "NewsletterSentLog") {
    def * = (id, createdat) <> (NewslettersentlogRow.tupled, NewslettersentlogRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), createdat).shaped.<>({r=>import r._; _1.map(_=> NewslettersentlogRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column createdAt SqlType(DATE), Default(None) */
    val createdat: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("createdAt", O.Default(None))
  }
  /** Collection-like TableQuery object for table Newslettersentlog */
  lazy val Newslettersentlog = new TableQuery(tag => new Newslettersentlog(tag))

  /** Entity class storing rows of table NewsVotes
   *  @param newsId Database column News_id SqlType(BIGINT)
   *  @param votesId Database column votes_id SqlType(BIGINT) */
  case class NewsVotesRow(newsId: Long, votesId: Long)
  /** GetResult implicit for fetching NewsVotesRow objects using plain SQL queries */
  implicit def GetResultNewsVotesRow(implicit e0: GR[Long]): GR[NewsVotesRow] = GR{
    prs => import prs._
    NewsVotesRow.tupled((<<[Long], <<[Long]))
  }
  /** Table description of table News_Votes. Objects of this class serve as prototypes for rows in queries. */
  class NewsVotes(_tableTag: Tag) extends profile.api.Table[NewsVotesRow](_tableTag, Some(dbName), "News_Votes") {
    def * = (newsId, votesId) <> (NewsVotesRow.tupled, NewsVotesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(newsId), Rep.Some(votesId)).shaped.<>({r=>import r._; _1.map(_=> NewsVotesRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column News_id SqlType(BIGINT) */
    val newsId: Rep[Long] = column[Long]("News_id")
    /** Database column votes_id SqlType(BIGINT) */
    val votesId: Rep[Long] = column[Long]("votes_id")

    /** Foreign key referencing News (database name FK_rqhl3hox4wntf7oc9y4af5cgv) */
    lazy val newsFk = foreignKey("FK_rqhl3hox4wntf7oc9y4af5cgv", newsId, News)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Vote (database name FK_mecjpg8cg90p1ry4sg4rckb09) */
    lazy val voteFk = foreignKey("FK_mecjpg8cg90p1ry4sg4rckb09", votesId, Vote)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (votesId) (database name UK_mecjpg8cg90p1ry4sg4rckb09) */
    val index1 = index("UK_mecjpg8cg90p1ry4sg4rckb09", votesId, unique=true)
  }
  /** Collection-like TableQuery object for table NewsVotes */
  lazy val NewsVotes = new TableQuery(tag => new NewsVotes(tag))

  /** Entity class storing rows of table NewsWatchers
   *  @param newsId Database column News_id SqlType(BIGINT)
   *  @param watchersId Database column watchers_id SqlType(BIGINT) */
  case class NewsWatchersRow(newsId: Long, watchersId: Long)
  /** GetResult implicit for fetching NewsWatchersRow objects using plain SQL queries */
  implicit def GetResultNewsWatchersRow(implicit e0: GR[Long]): GR[NewsWatchersRow] = GR{
    prs => import prs._
    NewsWatchersRow.tupled((<<[Long], <<[Long]))
  }
  /** Table description of table News_Watchers. Objects of this class serve as prototypes for rows in queries. */
  class NewsWatchers(_tableTag: Tag) extends profile.api.Table[NewsWatchersRow](_tableTag, Some(dbName), "News_Watchers") {
    def * = (newsId, watchersId) <> (NewsWatchersRow.tupled, NewsWatchersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(newsId), Rep.Some(watchersId)).shaped.<>({r=>import r._; _1.map(_=> NewsWatchersRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column News_id SqlType(BIGINT) */
    val newsId: Rep[Long] = column[Long]("News_id")
    /** Database column watchers_id SqlType(BIGINT) */
    val watchersId: Rep[Long] = column[Long]("watchers_id")

    /** Foreign key referencing News (database name FK_bywcu6iha3jaici2oiljml8ho) */
    lazy val newsFk = foreignKey("FK_bywcu6iha3jaici2oiljml8ho", newsId, News)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Watcher (database name FK_m5koj8lugxy5pp42xk656vqln) */
    lazy val watcherFk = foreignKey("FK_m5koj8lugxy5pp42xk656vqln", watchersId, Watcher)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (watchersId) (database name UK_m5koj8lugxy5pp42xk656vqln) */
    val index1 = index("UK_m5koj8lugxy5pp42xk656vqln", watchersId, unique=true)
  }
  /** Collection-like TableQuery object for table NewsWatchers */
  lazy val NewsWatchers = new TableQuery(tag => new NewsWatchers(tag))

  /** Entity class storing rows of table Question
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param answercount Database column answerCount SqlType(BIGINT)
   *  @param createdat Database column createdAt SqlType(DATETIME), Default(None)
   *  @param lastupdatedat Database column lastUpdatedAt SqlType(DATETIME), Default(None)
   *  @param invisible Database column invisible SqlType(BIT)
   *  @param views Database column views SqlType(BIGINT)
   *  @param votecount Database column voteCount SqlType(BIGINT)
   *  @param authorId Database column author_id SqlType(BIGINT), Default(None)
   *  @param informationId Database column information_id SqlType(BIGINT)
   *  @param lasttouchedbyId Database column lastTouchedBy_id SqlType(BIGINT), Default(None)
   *  @param solutionId Database column solution_id SqlType(BIGINT), Default(None)
   *  @param deleted Database column deleted SqlType(BIT), Default(Some(false)) */
  case class QuestionRow(id: Long, answercount: Long, createdat: Option[java.sql.Timestamp] = None, lastupdatedat: Option[java.sql.Timestamp] = None, invisible: Boolean, views: Long, votecount: Long, authorId: Option[Long] = None, informationId: Long, lasttouchedbyId: Option[Long] = None, solutionId: Option[Long] = None, deleted: Option[Boolean] = Some(false)) extends QATextInputRow
  /** GetResult implicit for fetching QuestionRow objects using plain SQL queries */
  implicit def GetResultQuestionRow(implicit e0: GR[Long], e1: GR[Option[java.sql.Timestamp]], e2: GR[Boolean], e3: GR[Option[Long]], e4: GR[Option[Boolean]]): GR[QuestionRow] = GR{
    prs => import prs._
    QuestionRow.tupled((<<[Long], <<[Long], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<[Boolean], <<[Long], <<[Long], <<?[Long], <<[Long], <<?[Long], <<?[Long], <<?[Boolean]))
  }
  /** Table description of table Question. Objects of this class serve as prototypes for rows in queries. */
  class Question(_tableTag: Tag) extends profile.api.Table[QuestionRow](_tableTag, Some(dbName), "Question") {
    def * = (id, answercount, createdat, lastupdatedat, invisible, views, votecount, authorId, informationId, lasttouchedbyId, solutionId, deleted) <> (QuestionRow.tupled, QuestionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(answercount), createdat, lastupdatedat, Rep.Some(invisible), Rep.Some(views), Rep.Some(votecount), authorId, Rep.Some(informationId), lasttouchedbyId, solutionId, deleted).shaped.<>({r=>import r._; _1.map(_=> QuestionRow.tupled((_1.get, _2.get, _3, _4, _5.get, _6.get, _7.get, _8, _9.get, _10, _11, _12)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column answerCount SqlType(BIGINT) */
    val answercount: Rep[Long] = column[Long]("answerCount")
    /** Database column createdAt SqlType(DATETIME), Default(None) */
    val createdat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt", O.Default(None))
    /** Database column lastUpdatedAt SqlType(DATETIME), Default(None) */
    val lastupdatedat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("lastUpdatedAt", O.Default(None))
    /** Database column invisible SqlType(BIT) */
    val invisible: Rep[Boolean] = column[Boolean]("invisible")
    /** Database column views SqlType(BIGINT) */
    val views: Rep[Long] = column[Long]("views")
    /** Database column voteCount SqlType(BIGINT) */
    val votecount: Rep[Long] = column[Long]("voteCount")
    /** Database column author_id SqlType(BIGINT), Default(None) */
    val authorId: Rep[Option[Long]] = column[Option[Long]]("author_id", O.Default(None))
    /** Database column information_id SqlType(BIGINT) */
    val informationId: Rep[Long] = column[Long]("information_id")
    /** Database column lastTouchedBy_id SqlType(BIGINT), Default(None) */
    val lasttouchedbyId: Rep[Option[Long]] = column[Option[Long]]("lastTouchedBy_id", O.Default(None))
    /** Database column solution_id SqlType(BIGINT), Default(None) */
    val solutionId: Rep[Option[Long]] = column[Option[Long]]("solution_id", O.Default(None))
    /** Database column deleted SqlType(BIT), Default(Some(false)) */
    val deleted: Rep[Option[Boolean]] = column[Option[Boolean]]("deleted", O.Default(Some(false)))

    /** Foreign key referencing Answer (database name FK_liw3djybv5je7ra806bsipg68) */
    lazy val answerFk = foreignKey("FK_liw3djybv5je7ra806bsipg68", solutionId, Answer)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Questioninformation (database name FK_i2xt9jcwfauudnswun94neqyg) */
    lazy val questioninformationFk = foreignKey("FK_i2xt9jcwfauudnswun94neqyg", informationId, Questioninformation)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name FK_9d3cyy648wfruj9t7556wqgjr) */
    lazy val usersFk3 = foreignKey("FK_9d3cyy648wfruj9t7556wqgjr", lasttouchedbyId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name FK_a3dib35x299yvhfk7pau0kw5w) */
    lazy val usersFk4 = foreignKey("FK_a3dib35x299yvhfk7pau0kw5w", authorId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Question */
  lazy val Question = new TableQuery(tag => new Question(tag))

  /** Entity class storing rows of table QuestionAttachment
   *  @param questionId Database column Question_id SqlType(BIGINT)
   *  @param attachmentsId Database column attachments_id SqlType(BIGINT) */
  case class QuestionAttachmentRow(questionId: Long, attachmentsId: Long)
  /** GetResult implicit for fetching QuestionAttachmentRow objects using plain SQL queries */
  implicit def GetResultQuestionAttachmentRow(implicit e0: GR[Long]): GR[QuestionAttachmentRow] = GR{
    prs => import prs._
    QuestionAttachmentRow.tupled((<<[Long], <<[Long]))
  }
  /** Table description of table Question_Attachment. Objects of this class serve as prototypes for rows in queries. */
  class QuestionAttachment(_tableTag: Tag) extends profile.api.Table[QuestionAttachmentRow](_tableTag, Some(dbName), "Question_Attachment") {
    def * = (questionId, attachmentsId) <> (QuestionAttachmentRow.tupled, QuestionAttachmentRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(questionId), Rep.Some(attachmentsId)).shaped.<>({r=>import r._; _1.map(_=> QuestionAttachmentRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Question_id SqlType(BIGINT) */
    val questionId: Rep[Long] = column[Long]("Question_id")
    /** Database column attachments_id SqlType(BIGINT) */
    val attachmentsId: Rep[Long] = column[Long]("attachments_id")

    /** Foreign key referencing Attachment (database name FK_7y9vgsl3nbmms94toj87g69lu) */
    lazy val attachmentFk = foreignKey("FK_7y9vgsl3nbmms94toj87g69lu", attachmentsId, Attachment)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Question (database name FK_ib0aqkj0a4a4l9ku9oai3lw9w) */
    lazy val questionFk = foreignKey("FK_ib0aqkj0a4a4l9ku9oai3lw9w", questionId, Question)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (attachmentsId) (database name UK_7y9vgsl3nbmms94toj87g69lu) */
    val index1 = index("UK_7y9vgsl3nbmms94toj87g69lu", attachmentsId, unique=true)
  }
  /** Collection-like TableQuery object for table QuestionAttachment */
  lazy val QuestionAttachment = new TableQuery(tag => new QuestionAttachment(tag))

  /** Entity class storing rows of table QuestionComments
   *  @param questionId Database column Question_id SqlType(BIGINT)
   *  @param commentsId Database column comments_id SqlType(BIGINT) */
  case class QuestionCommentsRow(questionId: Long, commentsId: Long)
  /** GetResult implicit for fetching QuestionCommentsRow objects using plain SQL queries */
  implicit def GetResultQuestionCommentsRow(implicit e0: GR[Long]): GR[QuestionCommentsRow] = GR{
    prs => import prs._
    QuestionCommentsRow.tupled((<<[Long], <<[Long]))
  }
  /** Table description of table Question_Comments. Objects of this class serve as prototypes for rows in queries. */
  class QuestionComments(_tableTag: Tag) extends profile.api.Table[QuestionCommentsRow](_tableTag, Some(dbName), "Question_Comments") {
    def * = (questionId, commentsId) <> (QuestionCommentsRow.tupled, QuestionCommentsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(questionId), Rep.Some(commentsId)).shaped.<>({r=>import r._; _1.map(_=> QuestionCommentsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Question_id SqlType(BIGINT) */
    val questionId: Rep[Long] = column[Long]("Question_id")
    /** Database column comments_id SqlType(BIGINT) */
    val commentsId: Rep[Long] = column[Long]("comments_id")

    /** Foreign key referencing Comment (database name FK_6jsfvsef241a3ldcck6pid4vi) */
    lazy val commentFk = foreignKey("FK_6jsfvsef241a3ldcck6pid4vi", commentsId, Comment)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Question (database name FK_fm57yvrnidsyeuvls0he5c9pk) */
    lazy val questionFk = foreignKey("FK_fm57yvrnidsyeuvls0he5c9pk", questionId, Question)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (commentsId) (database name UK_6jsfvsef241a3ldcck6pid4vi) */
    val index1 = index("UK_6jsfvsef241a3ldcck6pid4vi", commentsId, unique=true)
  }
  /** Collection-like TableQuery object for table QuestionComments */
  lazy val QuestionComments = new TableQuery(tag => new QuestionComments(tag))

  /** Entity class storing rows of table QuestionFlags
   *  @param questionId Database column Question_id SqlType(BIGINT)
   *  @param flagsId Database column flags_id SqlType(BIGINT) */
  case class QuestionFlagsRow(questionId: Long, flagsId: Long)
  /** GetResult implicit for fetching QuestionFlagsRow objects using plain SQL queries */
  implicit def GetResultQuestionFlagsRow(implicit e0: GR[Long]): GR[QuestionFlagsRow] = GR{
    prs => import prs._
    QuestionFlagsRow.tupled((<<[Long], <<[Long]))
  }
  /** Table description of table Question_Flags. Objects of this class serve as prototypes for rows in queries. */
  class QuestionFlags(_tableTag: Tag) extends profile.api.Table[QuestionFlagsRow](_tableTag, Some(dbName), "Question_Flags") {
    def * = (questionId, flagsId) <> (QuestionFlagsRow.tupled, QuestionFlagsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(questionId), Rep.Some(flagsId)).shaped.<>({r=>import r._; _1.map(_=> QuestionFlagsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Question_id SqlType(BIGINT) */
    val questionId: Rep[Long] = column[Long]("Question_id")
    /** Database column flags_id SqlType(BIGINT) */
    val flagsId: Rep[Long] = column[Long]("flags_id")

    /** Foreign key referencing Flag (database name FK_a8brcb8bpevccipiyplquqqjv) */
    lazy val flagFk = foreignKey("FK_a8brcb8bpevccipiyplquqqjv", flagsId, Flag)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Question (database name FK_ftntexa7hxbaqo2i2yg4i0yr3) */
    lazy val questionFk = foreignKey("FK_ftntexa7hxbaqo2i2yg4i0yr3", questionId, Question)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (flagsId) (database name UK_a8brcb8bpevccipiyplquqqjv) */
    val index1 = index("UK_a8brcb8bpevccipiyplquqqjv", flagsId, unique=true)
  }
  /** Collection-like TableQuery object for table QuestionFlags */
  lazy val QuestionFlags = new TableQuery(tag => new QuestionFlags(tag))

  /** Entity class storing rows of table Questioninformation
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param comment Database column comment SqlType(LONGTEXT), Length(2147483647,true)
   *  @param createdat Database column createdAt SqlType(DATETIME), Default(None)
   *  @param description Database column description SqlType(LONGTEXT), Length(2147483647,true)
   *  @param ip Database column ip SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param markeddescription Database column markedDescription SqlType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param moderatedat Database column moderatedAt SqlType(DATETIME), Default(None)
   *  @param sluggedtitle Database column sluggedTitle SqlType(LONGTEXT), Length(2147483647,true)
   *  @param status Database column status SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param title Database column title SqlType(LONGTEXT), Length(2147483647,true)
   *  @param authorId Database column author_id SqlType(BIGINT)
   *  @param moderatedbyId Database column moderatedBy_id SqlType(BIGINT), Default(None)
   *  @param questionId Database column question_id SqlType(BIGINT), Default(None) */
  case class QuestioninformationRow(id: Long, comment: String, createdat: Option[java.sql.Timestamp] = None, description: String, ip: Option[String] = None, markeddescription: Option[String] = None, moderatedat: Option[java.sql.Timestamp] = None, sluggedtitle: String, status: Option[String] = None, title: String, authorId: Long, moderatedbyId: Option[Long] = None, questionId: Option[Long] = None)
  /** GetResult implicit for fetching QuestioninformationRow objects using plain SQL queries */
  implicit def GetResultQuestioninformationRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Option[java.sql.Timestamp]], e3: GR[Option[String]], e4: GR[Option[Long]]): GR[QuestioninformationRow] = GR{
    prs => import prs._
    QuestioninformationRow.tupled((<<[Long], <<[String], <<?[java.sql.Timestamp], <<[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<[String], <<?[String], <<[String], <<[Long], <<?[Long], <<?[Long]))
  }
  /** Table description of table QuestionInformation. Objects of this class serve as prototypes for rows in queries. */
  class Questioninformation(_tableTag: Tag) extends profile.api.Table[QuestioninformationRow](_tableTag, Some(dbName), "QuestionInformation") {
    def * = (id, comment, createdat, description, ip, markeddescription, moderatedat, sluggedtitle, status, title, authorId, moderatedbyId, questionId) <> (QuestioninformationRow.tupled, QuestioninformationRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(comment), createdat, Rep.Some(description), ip, markeddescription, moderatedat, Rep.Some(sluggedtitle), status, Rep.Some(title), Rep.Some(authorId), moderatedbyId, questionId).shaped.<>({r=>import r._; _1.map(_=> QuestioninformationRow.tupled((_1.get, _2.get, _3, _4.get, _5, _6, _7, _8.get, _9, _10.get, _11.get, _12, _13)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column comment SqlType(LONGTEXT), Length(2147483647,true) */
    val comment: Rep[String] = column[String]("comment", O.Length(2147483647,varying=true))
    /** Database column createdAt SqlType(DATETIME), Default(None) */
    val createdat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt", O.Default(None))
    /** Database column description SqlType(LONGTEXT), Length(2147483647,true) */
    val description: Rep[String] = column[String]("description", O.Length(2147483647,varying=true))
    /** Database column ip SqlType(VARCHAR), Length(255,true), Default(None) */
    val ip: Rep[Option[String]] = column[Option[String]]("ip", O.Length(255,varying=true), O.Default(None))
    /** Database column markedDescription SqlType(LONGTEXT), Length(2147483647,true), Default(None) */
    val markeddescription: Rep[Option[String]] = column[Option[String]]("markedDescription", O.Length(2147483647,varying=true), O.Default(None))
    /** Database column moderatedAt SqlType(DATETIME), Default(None) */
    val moderatedat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("moderatedAt", O.Default(None))
    /** Database column sluggedTitle SqlType(LONGTEXT), Length(2147483647,true) */
    val sluggedtitle: Rep[String] = column[String]("sluggedTitle", O.Length(2147483647,varying=true))
    /** Database column status SqlType(VARCHAR), Length(255,true), Default(None) */
    val status: Rep[Option[String]] = column[Option[String]]("status", O.Length(255,varying=true), O.Default(None))
    /** Database column title SqlType(LONGTEXT), Length(2147483647,true) */
    val title: Rep[String] = column[String]("title", O.Length(2147483647,varying=true))
    /** Database column author_id SqlType(BIGINT) */
    val authorId: Rep[Long] = column[Long]("author_id")
    /** Database column moderatedBy_id SqlType(BIGINT), Default(None) */
    val moderatedbyId: Rep[Option[Long]] = column[Option[Long]]("moderatedBy_id", O.Default(None))
    /** Database column question_id SqlType(BIGINT), Default(None) */
    val questionId: Rep[Option[Long]] = column[Option[Long]]("question_id", O.Default(None))

    /** Foreign key referencing Question (database name FK_pl1drgbxhfd4hbmd3smwa3svl) */
    lazy val questionFk = foreignKey("FK_pl1drgbxhfd4hbmd3smwa3svl", questionId, Question)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name FK_9nfk2kchvyn69e7gdh798gf) */
    lazy val usersFk2 = foreignKey("FK_9nfk2kchvyn69e7gdh798gf", authorId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name FK_t1oox0xh74vlikcvhxd5k2kq1) */
    lazy val usersFk3 = foreignKey("FK_t1oox0xh74vlikcvhxd5k2kq1", moderatedbyId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Questioninformation */
  lazy val Questioninformation = new TableQuery(tag => new Questioninformation(tag))

  /** Entity class storing rows of table QuestioninformationTag
   *  @param questioninformationId Database column QuestionInformation_id SqlType(BIGINT)
   *  @param tagsId Database column tags_id SqlType(BIGINT)
   *  @param tagOrder Database column tag_order SqlType(INT) */
  case class QuestioninformationTagRow(questioninformationId: Long, tagsId: Long, tagOrder: Int)
  /** GetResult implicit for fetching QuestioninformationTagRow objects using plain SQL queries */
  implicit def GetResultQuestioninformationTagRow(implicit e0: GR[Long], e1: GR[Int]): GR[QuestioninformationTagRow] = GR{
    prs => import prs._
    QuestioninformationTagRow.tupled((<<[Long], <<[Long], <<[Int]))
  }
  /** Table description of table QuestionInformation_Tag. Objects of this class serve as prototypes for rows in queries. */
  class QuestioninformationTag(_tableTag: Tag) extends profile.api.Table[QuestioninformationTagRow](_tableTag, Some(dbName), "QuestionInformation_Tag") {
    def * = (questioninformationId, tagsId, tagOrder) <> (QuestioninformationTagRow.tupled, QuestioninformationTagRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(questioninformationId), Rep.Some(tagsId), Rep.Some(tagOrder)).shaped.<>({r=>import r._; _1.map(_=> QuestioninformationTagRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column QuestionInformation_id SqlType(BIGINT) */
    val questioninformationId: Rep[Long] = column[Long]("QuestionInformation_id")
    /** Database column tags_id SqlType(BIGINT) */
    val tagsId: Rep[Long] = column[Long]("tags_id")
    /** Database column tag_order SqlType(INT) */
    val tagOrder: Rep[Int] = column[Int]("tag_order")

    /** Primary key of QuestioninformationTag (database name QuestionInformation_Tag_PK) */
    val pk = primaryKey("QuestionInformation_Tag_PK", (questioninformationId, tagOrder))

    /** Foreign key referencing Questioninformation (database name FK_7u4x47xa5gdhmt650curhu3kx) */
    lazy val questioninformationFk = foreignKey("FK_7u4x47xa5gdhmt650curhu3kx", questioninformationId, Questioninformation)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Tag (database name FK_nv1tmcost5jqejnlb6u0wrypo) */
    lazy val tagFk = foreignKey("FK_nv1tmcost5jqejnlb6u0wrypo", tagsId, Tagx)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table QuestioninformationTag */
  lazy val QuestioninformationTag = new TableQuery(tag => new QuestioninformationTag(tag))

  /** Entity class storing rows of table QuestionInteractions
   *  @param questionId Database column Question_id SqlType(BIGINT)
   *  @param userinteractionsId Database column userInteractions_id SqlType(BIGINT) */
  case class QuestionInteractionsRow(questionId: Long, userinteractionsId: Long)
  /** GetResult implicit for fetching QuestionInteractionsRow objects using plain SQL queries */
  implicit def GetResultQuestionInteractionsRow(implicit e0: GR[Long]): GR[QuestionInteractionsRow] = GR{
    prs => import prs._
    QuestionInteractionsRow.tupled((<<[Long], <<[Long]))
  }
  /** Table description of table Question_Interactions. Objects of this class serve as prototypes for rows in queries. */
  class QuestionInteractions(_tableTag: Tag) extends profile.api.Table[QuestionInteractionsRow](_tableTag, Some(dbName), "Question_Interactions") {
    def * = (questionId, userinteractionsId) <> (QuestionInteractionsRow.tupled, QuestionInteractionsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(questionId), Rep.Some(userinteractionsId)).shaped.<>({r=>import r._; _1.map(_=> QuestionInteractionsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Question_id SqlType(BIGINT) */
    val questionId: Rep[Long] = column[Long]("Question_id")
    /** Database column userInteractions_id SqlType(BIGINT) */
    val userinteractionsId: Rep[Long] = column[Long]("userInteractions_id")

    /** Primary key of QuestionInteractions (database name Question_Interactions_PK) */
    val pk = primaryKey("Question_Interactions_PK", (questionId, userinteractionsId))

    /** Foreign key referencing Question (database name FK_plnjd89r1mncrtf1vfj65pspt) */
    lazy val questionFk = foreignKey("FK_plnjd89r1mncrtf1vfj65pspt", questionId, Question)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name FK_nydo4x8ey7gnhhwg1gqmikwo6) */
    lazy val usersFk = foreignKey("FK_nydo4x8ey7gnhhwg1gqmikwo6", userinteractionsId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table QuestionInteractions */
  lazy val QuestionInteractions = new TableQuery(tag => new QuestionInteractions(tag))

  /** Entity class storing rows of table QuestionVotes
   *  @param questionId Database column Question_id SqlType(BIGINT)
   *  @param votesId Database column votes_id SqlType(BIGINT) */
  case class QuestionVotesRow(questionId: Long, votesId: Long)
  /** GetResult implicit for fetching QuestionVotesRow objects using plain SQL queries */
  implicit def GetResultQuestionVotesRow(implicit e0: GR[Long]): GR[QuestionVotesRow] = GR{
    prs => import prs._
    QuestionVotesRow.tupled((<<[Long], <<[Long]))
  }
  /** Table description of table Question_Votes. Objects of this class serve as prototypes for rows in queries. */
  class QuestionVotes(_tableTag: Tag) extends profile.api.Table[QuestionVotesRow](_tableTag, Some(dbName), "Question_Votes") {
    def * = (questionId, votesId) <> (QuestionVotesRow.tupled, QuestionVotesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(questionId), Rep.Some(votesId)).shaped.<>({r=>import r._; _1.map(_=> QuestionVotesRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Question_id SqlType(BIGINT) */
    val questionId: Rep[Long] = column[Long]("Question_id")
    /** Database column votes_id SqlType(BIGINT) */
    val votesId: Rep[Long] = column[Long]("votes_id")

    /** Foreign key referencing Question (database name FK_24u6uwfjr8s8pfk7oyfw8u4o4) */
    lazy val questionFk = foreignKey("FK_24u6uwfjr8s8pfk7oyfw8u4o4", questionId, Question)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Vote (database name FK_p5sgssf0gw0br66mvu9cctlmq) */
    lazy val voteFk = foreignKey("FK_p5sgssf0gw0br66mvu9cctlmq", votesId, Vote)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (votesId) (database name UK_p5sgssf0gw0br66mvu9cctlmq) */
    val index1 = index("UK_p5sgssf0gw0br66mvu9cctlmq", votesId, unique=true)
  }
  /** Collection-like TableQuery object for table QuestionVotes */
  lazy val QuestionVotes = new TableQuery(tag => new QuestionVotes(tag))

  /** Entity class storing rows of table QuestionWatchers
   *  @param questionId Database column Question_id SqlType(BIGINT)
   *  @param watchersId Database column watchers_id SqlType(BIGINT) */
  case class QuestionWatchersRow(questionId: Long, watchersId: Long)
  /** GetResult implicit for fetching QuestionWatchersRow objects using plain SQL queries */
  implicit def GetResultQuestionWatchersRow(implicit e0: GR[Long]): GR[QuestionWatchersRow] = GR{
    prs => import prs._
    QuestionWatchersRow.tupled((<<[Long], <<[Long]))
  }
  /** Table description of table Question_Watchers. Objects of this class serve as prototypes for rows in queries. */
  class QuestionWatchers(_tableTag: Tag) extends profile.api.Table[QuestionWatchersRow](_tableTag, Some(dbName), "Question_Watchers") {
    def * = (questionId, watchersId) <> (QuestionWatchersRow.tupled, QuestionWatchersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(questionId), Rep.Some(watchersId)).shaped.<>({r=>import r._; _1.map(_=> QuestionWatchersRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Question_id SqlType(BIGINT) */
    val questionId: Rep[Long] = column[Long]("Question_id")
    /** Database column watchers_id SqlType(BIGINT) */
    val watchersId: Rep[Long] = column[Long]("watchers_id")

    /** Foreign key referencing Question (database name FK_pu72rhjonka0flev96adthdp0) */
    lazy val questionFk = foreignKey("FK_pu72rhjonka0flev96adthdp0", questionId, Question)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Watcher (database name FK_5twinfe7e6g09gaowkeah0498) */
    lazy val watcherFk = foreignKey("FK_5twinfe7e6g09gaowkeah0498", watchersId, Watcher)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (watchersId) (database name UK_5twinfe7e6g09gaowkeah0498) */
    val index1 = index("UK_5twinfe7e6g09gaowkeah0498", watchersId, unique=true)
  }
  /** Collection-like TableQuery object for table QuestionWatchers */
  lazy val QuestionWatchers = new TableQuery(tag => new QuestionWatchers(tag))

  /** Entity class storing rows of table Reputationevent
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param contextType Database column context_type SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param contextId Database column context_id SqlType(BIGINT), Default(None)
   *  @param date Database column date SqlType(DATETIME), Default(None)
   *  @param karmareward Database column karmaReward SqlType(INT)
   *  @param `type` Database column type SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param userId Database column user_id SqlType(BIGINT), Default(None)
   *  @param deleted Database column deleted SqlType(BIT), Default(Some(false)) */
  case class ReputationeventRow(id: Long, contextType: Option[String] = None, contextId: Option[Long] = None, date: Option[java.sql.Timestamp] = None, karmareward: Int, `type`: Option[String] = None, userId: Option[Long] = None, deleted: Option[Boolean] = Some(false))
  /** GetResult implicit for fetching ReputationeventRow objects using plain SQL queries */
  implicit def GetResultReputationeventRow(implicit e0: GR[Long], e1: GR[Option[String]], e2: GR[Option[Long]], e3: GR[Option[java.sql.Timestamp]], e4: GR[Int], e5: GR[Option[Boolean]]): GR[ReputationeventRow] = GR{
    prs => import prs._
    ReputationeventRow.tupled((<<[Long], <<?[String], <<?[Long], <<?[java.sql.Timestamp], <<[Int], <<?[String], <<?[Long], <<?[Boolean]))
  }
  /** Table description of table ReputationEvent. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class Reputationevent(_tableTag: Tag) extends profile.api.Table[ReputationeventRow](_tableTag, Some(dbName), "ReputationEvent") {
    def * = (id, contextType, contextId, date, karmareward, `type`, userId, deleted) <> (ReputationeventRow.tupled, ReputationeventRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), contextType, contextId, date, Rep.Some(karmareward), `type`, userId, deleted).shaped.<>({r=>import r._; _1.map(_=> ReputationeventRow.tupled((_1.get, _2, _3, _4, _5.get, _6, _7, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column context_type SqlType(VARCHAR), Length(255,true), Default(None) */
    val contextType: Rep[Option[String]] = column[Option[String]]("context_type", O.Length(255,varying=true), O.Default(None))
    /** Database column context_id SqlType(BIGINT), Default(None) */
    val contextId: Rep[Option[Long]] = column[Option[Long]]("context_id", O.Default(None))
    /** Database column date SqlType(DATETIME), Default(None) */
    val date: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("date", O.Default(None))
    /** Database column karmaReward SqlType(INT) */
    val karmareward: Rep[Int] = column[Int]("karmaReward")
    /** Database column type SqlType(VARCHAR), Length(255,true), Default(None)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Rep[Option[String]] = column[Option[String]]("type", O.Length(255,varying=true), O.Default(None))
    /** Database column user_id SqlType(BIGINT), Default(None) */
    val userId: Rep[Option[Long]] = column[Option[Long]]("user_id", O.Default(None))
    /** Database column deleted SqlType(BIT), Default(Some(false)) */
    val deleted: Rep[Option[Boolean]] = column[Option[Boolean]]("deleted", O.Default(Some(false)))

    /** Foreign key referencing Users (database name FK_gbu6jo147pal18b3q3blpr0of) */
    lazy val usersFk = foreignKey("FK_gbu6jo147pal18b3q3blpr0of", userId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Reputationevent */
  lazy val Reputationevent = new TableQuery(tag => new Reputationevent(tag))

  /** Entity class storing rows of table Tag
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param createdat Database column createdAt SqlType(DATETIME), Default(None)
   *  @param description Database column description SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param name Database column name SqlType(VARCHAR), Length(255,true)
   *  @param usagecount Database column usageCount SqlType(BIGINT), Default(None)
   *  @param authorId Database column author_id SqlType(BIGINT), Default(None) */
  case class TagxRow(id: Long, createdat: Option[java.sql.Timestamp] = None, description: Option[String] = None, name: String, usagecount: Option[Long] = None, authorId: Option[Long] = None)
  /** GetResult implicit for fetching TagxRow objects using plain SQL queries */
  implicit def GetResultTagxRow(implicit e0: GR[Long], e1: GR[Option[java.sql.Timestamp]], e2: GR[Option[String]], e3: GR[String], e4: GR[Option[Long]]): GR[TagxRow] = GR{
    prs => import prs._
    TagxRow.tupled((<<[Long], <<?[java.sql.Timestamp], <<?[String], <<[String], <<?[Long], <<?[Long]))
  }
  /** Table description of table Tag. Objects of this class serve as prototypes for rows in queries. */
  class Tagx(_tableTag: Tag) extends profile.api.Table[TagxRow](_tableTag, Some(dbName), "Tag") {
    def * = (id, createdat, description, name, usagecount, authorId) <> (TagxRow.tupled, TagxRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), createdat, description, Rep.Some(name), usagecount, authorId).shaped.<>({r=>import r._; _1.map(_=> TagxRow.tupled((_1.get, _2, _3, _4.get, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column createdAt SqlType(DATETIME), Default(None) */
    val createdat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt", O.Default(None))
    /** Database column description SqlType(VARCHAR), Length(255,true), Default(None) */
    val description: Rep[Option[String]] = column[Option[String]]("description", O.Length(255,varying=true), O.Default(None))
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column sluggedName SqlType(LONGTEXT), Length(2147483647,true) */
    /**val sluggedname: Rep[String] = column[String]("sluggedName", O.Length(2147483647,varying=true)) */
    /** Database column usageCount SqlType(BIGINT), Default(None) */
    val usagecount: Rep[Option[Long]] = column[Option[Long]]("usageCount", O.Default(None))
    /** Database column author_id SqlType(BIGINT), Default(None) */
    val authorId: Rep[Option[Long]] = column[Option[Long]]("author_id", O.Default(None))

    /** Foreign key referencing Users (database name FK_9a9b8a968n0ejs6yikpgo563r) */
    lazy val usersFk = foreignKey("FK_9a9b8a968n0ejs6yikpgo563r", authorId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (name) (database name UK_24642shpebunaq3ggshotv9hk) */
    val index1 = index("UK_24642shpebunaq3ggshotv9hk", name, unique=true)
  }
  /** Collection-like TableQuery object for table Tag */
  lazy val Tagx = new TableQuery(tagx => new Tagx(tagx))

  /** Entity class storing rows of table Tagpage
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param about Database column about SqlType(LONGTEXT), Length(2147483647,true)
   *  @param markedabout Database column markedAbout SqlType(LONGTEXT), Length(2147483647,true)
   *  @param tagId Database column tag_id SqlType(BIGINT), Default(None) */
  case class TagpageRow(id: Long, about: String, markedabout: String, tagId: Option[Long] = None)
  /** GetResult implicit for fetching TagpageRow objects using plain SQL queries */
  implicit def GetResultTagpageRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Option[Long]]): GR[TagpageRow] = GR{
    prs => import prs._
    TagpageRow.tupled((<<[Long], <<[String], <<[String], <<?[Long]))
  }
  /** Table description of table TagPage. Objects of this class serve as prototypes for rows in queries. */
  class Tagpage(_tableTag: Tag) extends profile.api.Table[TagpageRow](_tableTag, Some(dbName), "TagPage") {
    def * = (id, about, markedabout, tagId) <> (TagpageRow.tupled, TagpageRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(about), Rep.Some(markedabout), tagId).shaped.<>({r=>import r._; _1.map(_=> TagpageRow.tupled((_1.get, _2.get, _3.get, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column about SqlType(LONGTEXT), Length(2147483647,true) */
    val about: Rep[String] = column[String]("about", O.Length(2147483647,varying=true))
    /** Database column markedAbout SqlType(LONGTEXT), Length(2147483647,true) */
    val markedabout: Rep[String] = column[String]("markedAbout", O.Length(2147483647,varying=true))
    /** Database column tag_id SqlType(BIGINT), Default(None) */
    val tagId: Rep[Option[Long]] = column[Option[Long]]("tag_id", O.Default(None))

    /** Foreign key referencing Tag (database name FK_jcmikqbikgwump3qo3fnqbext) */
    lazy val tagFk = foreignKey("FK_jcmikqbikgwump3qo3fnqbext", tagId, Tagx)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Tagpage */
  lazy val Tagpage = new TableQuery(tag => new Tagpage(tag))

  /** Entity class storing rows of table Users
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param about Database column about SqlType(VARCHAR), Length(500,true), Default(None)
   *  @param birthdate Database column birthDate SqlType(DATETIME), Default(None)
   *  @param confirmedemail Database column confirmedEmail SqlType(BIT)
   *  @param createdat Database column createdAt SqlType(DATETIME), Default(None)
   *  @param email Database column email SqlType(VARCHAR), Length(100,true), Default(None)
   *  @param forgotpasswordtoken Database column forgotPasswordToken SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param isbanned Database column isBanned SqlType(BIT)
   *  @param issubscribed Database column isSubscribed SqlType(BIT)
   *  @param karma Database column karma SqlType(BIGINT)
   *  @param location Database column location SqlType(VARCHAR), Length(100,true), Default(None)
   *  @param markedabout Database column markedAbout SqlType(VARCHAR), Length(600,true), Default(None)
   *  @param moderator Database column moderator SqlType(BIT)
   *  @param name Database column name SqlType(VARCHAR), Length(100,true)
   *  @param namelasttouchedat Database column nameLastTouchedAt SqlType(DATETIME), Default(None)
   *  @param photouri Database column photoUri SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param sluggedname Database column sluggedName SqlType(LONGTEXT), Length(2147483647,true)
   *  @param website Database column website SqlType(VARCHAR), Length(200,true), Default(None)
   *  @param lastupvote Database column lastUpvote SqlType(DATETIME), Default(None)
   *  @param avatarimageId Database column avatarImage_id SqlType(BIGINT), Default(None)
   *  @param receiveallupdates Database column receiveAllUpdates SqlType(BIT), Default(Some(false))
   *  @param deleted Database column deleted SqlType(BIT), Default(Some(false)) */
  case class UsersRow(id: Long, about: Option[String] = None, birthdate: Option[java.sql.Timestamp] = None, confirmedemail: Boolean, createdat: Option[java.sql.Timestamp] = None, email: Option[String] = None, forgotpasswordtoken: Option[String] = None, isbanned: Boolean, issubscribed: Boolean, karma: Long, location: Option[String] = None, markedabout: Option[String] = None, moderator: Boolean, name: String, namelasttouchedat: Option[java.sql.Timestamp] = None, photouri: Option[String] = None, sluggedname: String, website: Option[String] = None, lastupvote: Option[java.sql.Timestamp] = None, avatarimageId: Option[Long] = None, receiveallupdates: Option[Boolean] = Some(false), deleted: Option[Boolean] = Some(false))
  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[Long], e1: GR[Option[String]], e2: GR[Option[java.sql.Timestamp]], e3: GR[Boolean], e4: GR[String], e5: GR[Option[Long]], e6: GR[Option[Boolean]]): GR[UsersRow] = GR{
    prs => import prs._
    UsersRow.tupled((<<[Long], <<?[String], <<?[java.sql.Timestamp], <<[Boolean], <<?[java.sql.Timestamp], <<?[String], <<?[String], <<[Boolean], <<[Boolean], <<[Long], <<?[String], <<?[String], <<[Boolean], <<[String], <<?[java.sql.Timestamp], <<?[String], <<[String], <<?[String], <<?[java.sql.Timestamp], <<?[Long], <<?[Boolean], <<?[Boolean]))
  }
  /** Table description of table Users. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends profile.api.Table[UsersRow](_tableTag, Some(dbName), "Users") {
    def * = (id, about, birthdate, confirmedemail, createdat, email, forgotpasswordtoken, isbanned, issubscribed, karma, location, markedabout, moderator, name, namelasttouchedat, photouri, sluggedname, website, lastupvote, avatarimageId, receiveallupdates, deleted) <> (UsersRow.tupled, UsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), about, birthdate, Rep.Some(confirmedemail), createdat, email, forgotpasswordtoken, Rep.Some(isbanned), Rep.Some(issubscribed), Rep.Some(karma), location, markedabout, Rep.Some(moderator), Rep.Some(name), namelasttouchedat, photouri, Rep.Some(sluggedname), website, lastupvote, avatarimageId, receiveallupdates, deleted).shaped.<>({r=>import r._; _1.map(_=> UsersRow.tupled((_1.get, _2, _3, _4.get, _5, _6, _7, _8.get, _9.get, _10.get, _11, _12, _13.get, _14.get, _15, _16, _17.get, _18, _19, _20, _21, _22)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column about SqlType(VARCHAR), Length(500,true), Default(None) */
    val about: Rep[Option[String]] = column[Option[String]]("about", O.Length(500,varying=true), O.Default(None))
    /** Database column birthDate SqlType(DATETIME), Default(None) */
    val birthdate: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("birthDate", O.Default(None))
    /** Database column confirmedEmail SqlType(BIT) */
    val confirmedemail: Rep[Boolean] = column[Boolean]("confirmedEmail")
    /** Database column createdAt SqlType(DATETIME), Default(None) */
    val createdat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt", O.Default(None))
    /** Database column email SqlType(VARCHAR), Length(100,true), Default(None) */
    val email: Rep[Option[String]] = column[Option[String]]("email", O.Length(100,varying=true), O.Default(None))
    /** Database column forgotPasswordToken SqlType(VARCHAR), Length(255,true), Default(None) */
    val forgotpasswordtoken: Rep[Option[String]] = column[Option[String]]("forgotPasswordToken", O.Length(255,varying=true), O.Default(None))
    /** Database column isBanned SqlType(BIT) */
    val isbanned: Rep[Boolean] = column[Boolean]("isBanned")
    /** Database column isSubscribed SqlType(BIT) */
    val issubscribed: Rep[Boolean] = column[Boolean]("isSubscribed")
    /** Database column karma SqlType(BIGINT) */
    val karma: Rep[Long] = column[Long]("karma")
    /** Database column location SqlType(VARCHAR), Length(100,true), Default(None) */
    val location: Rep[Option[String]] = column[Option[String]]("location", O.Length(100,varying=true), O.Default(None))
    /** Database column markedAbout SqlType(VARCHAR), Length(600,true), Default(None) */
    val markedabout: Rep[Option[String]] = column[Option[String]]("markedAbout", O.Length(600,varying=true), O.Default(None))
    /** Database column moderator SqlType(BIT) */
    val moderator: Rep[Boolean] = column[Boolean]("moderator")
    /** Database column name SqlType(VARCHAR), Length(100,true) */
    val name: Rep[String] = column[String]("name", O.Length(100,varying=true))
    /** Database column nameLastTouchedAt SqlType(DATETIME), Default(None) */
    val namelasttouchedat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("nameLastTouchedAt", O.Default(None))
    /** Database column photoUri SqlType(VARCHAR), Length(255,true), Default(None) */
    val photouri: Rep[Option[String]] = column[Option[String]]("photoUri", O.Length(255,varying=true), O.Default(None))
    /** Database column sluggedName SqlType(LONGTEXT), Length(2147483647,true) */
    val sluggedname: Rep[String] = column[String]("sluggedName", O.Length(2147483647,varying=true))
    /** Database column website SqlType(VARCHAR), Length(200,true), Default(None) */
    val website: Rep[Option[String]] = column[Option[String]]("website", O.Length(200,varying=true), O.Default(None))
    /** Database column lastUpvote SqlType(DATETIME), Default(None) */
    val lastupvote: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("lastUpvote", O.Default(None))
    /** Database column avatarImage_id SqlType(BIGINT), Default(None) */
    val avatarimageId: Rep[Option[Long]] = column[Option[Long]]("avatarImage_id", O.Default(None))
    /** Database column receiveAllUpdates SqlType(BIT), Default(Some(false)) */
    val receiveallupdates: Rep[Option[Boolean]] = column[Option[Boolean]]("receiveAllUpdates", O.Default(Some(false)))
    /** Database column deleted SqlType(BIT), Default(Some(false)) */
    val deleted: Rep[Option[Boolean]] = column[Option[Boolean]]("deleted", O.Default(Some(false)))

    /** Foreign key referencing Attachment (database name FK_gvmhe2prumyg00npgdawfu7la) */
    lazy val attachmentFk = foreignKey("FK_gvmhe2prumyg00npgdawfu7la", avatarimageId, Attachment)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (email) (database name email) */
    val index1 = index("email", email, unique=true)
    /** Index over (email) (database name email_2) */
    val index2 = index("email_2", email)
  }
  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))

  /** Entity class storing rows of table Usersession
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param createdat Database column createdAt SqlType(DATETIME), Default(None)
   *  @param sessionkey Database column sessionKey SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param userId Database column user_id SqlType(BIGINT), Default(None) */
  case class UsersessionRow(id: Long, createdat: Option[java.sql.Timestamp] = None, sessionkey: Option[String] = None, userId: Option[Long] = None)
  /** GetResult implicit for fetching UsersessionRow objects using plain SQL queries */
  implicit def GetResultUsersessionRow(implicit e0: GR[Long], e1: GR[Option[java.sql.Timestamp]], e2: GR[Option[String]], e3: GR[Option[Long]]): GR[UsersessionRow] = GR{
    prs => import prs._
    UsersessionRow.tupled((<<[Long], <<?[java.sql.Timestamp], <<?[String], <<?[Long]))
  }
  /** Table description of table UserSession. Objects of this class serve as prototypes for rows in queries. */
  class Usersession(_tableTag: Tag) extends profile.api.Table[UsersessionRow](_tableTag, Some(dbName), "UserSession") {
    def * = (id, createdat, sessionkey, userId) <> (UsersessionRow.tupled, UsersessionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), createdat, sessionkey, userId).shaped.<>({r=>import r._; _1.map(_=> UsersessionRow.tupled((_1.get, _2, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column createdAt SqlType(DATETIME), Default(None) */
    val createdat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt", O.Default(None))
    /** Database column sessionKey SqlType(VARCHAR), Length(255,true), Default(None) */
    val sessionkey: Rep[Option[String]] = column[Option[String]]("sessionKey", O.Length(255,varying=true), O.Default(None))
    /** Database column user_id SqlType(BIGINT), Default(None) */
    val userId: Rep[Option[Long]] = column[Option[Long]]("user_id", O.Default(None))

    /** Foreign key referencing Users (database name FK_g1vcu7yf9bjb3kj31y3ghw0jg) */
    lazy val usersFk = foreignKey("FK_g1vcu7yf9bjb3kj31y3ghw0jg", userId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (sessionkey) (database name UK_jhpxm4m9w5ujlygolg3nj08m9) */
    val index1 = index("UK_jhpxm4m9w5ujlygolg3nj08m9", sessionkey, unique=true)
    /** Index over (sessionkey) (database name session_key) */
    val index2 = index("session_key", sessionkey)
  }
  /** Collection-like TableQuery object for table Usersession */
  lazy val Usersession = new TableQuery(tag => new Usersession(tag))

  /** Entity class storing rows of table Vote
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param createdat Database column createdAt SqlType(DATETIME), Default(None)
   *  @param lastupdatedat Database column lastUpdatedAt SqlType(DATETIME), Default(None)
   *  @param `type` Database column type SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param authorId Database column author_id SqlType(BIGINT), Default(None) */
  case class VoteRow(id: Long, createdat: Option[java.sql.Timestamp] = None, lastupdatedat: Option[java.sql.Timestamp] = None, `type`: Option[String] = None, authorId: Option[Long] = None)
  /** GetResult implicit for fetching VoteRow objects using plain SQL queries */
  implicit def GetResultVoteRow(implicit e0: GR[Long], e1: GR[Option[java.sql.Timestamp]], e2: GR[Option[String]], e3: GR[Option[Long]]): GR[VoteRow] = GR{
    prs => import prs._
    VoteRow.tupled((<<[Long], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[String], <<?[Long]))
  }
  /** Table description of table Vote. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class Vote(_tableTag: Tag) extends profile.api.Table[VoteRow](_tableTag, Some(dbName), "Vote") {
    def * = (id, createdat, lastupdatedat, `type`, authorId) <> (VoteRow.tupled, VoteRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), createdat, lastupdatedat, `type`, authorId).shaped.<>({r=>import r._; _1.map(_=> VoteRow.tupled((_1.get, _2, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column createdAt SqlType(DATETIME), Default(None) */
    val createdat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt", O.Default(None))
    /** Database column lastUpdatedAt SqlType(DATETIME), Default(None) */
    val lastupdatedat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("lastUpdatedAt", O.Default(None))
    /** Database column type SqlType(VARCHAR), Length(255,true), Default(None)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Rep[Option[String]] = column[Option[String]]("type", O.Length(255,varying=true), O.Default(None))
    /** Database column author_id SqlType(BIGINT), Default(None) */
    val authorId: Rep[Option[Long]] = column[Option[Long]]("author_id", O.Default(None))

    /** Foreign key referencing Users (database name FK_6nch3y92lphrbsh0o5c7o0jov) */
    lazy val usersFk = foreignKey("FK_6nch3y92lphrbsh0o5c7o0jov", authorId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Vote */
  lazy val Vote = new TableQuery(tag => new Vote(tag))

  /** Entity class storing rows of table Watcher
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param active Database column active SqlType(BIT)
   *  @param createdat Database column createdAt SqlType(DATETIME), Default(None)
   *  @param watcherId Database column watcher_id SqlType(BIGINT), Default(None) */
  case class WatcherRow(id: Long, active: Boolean, createdat: Option[java.sql.Timestamp] = None, watcherId: Option[Long] = None)
  /** GetResult implicit for fetching WatcherRow objects using plain SQL queries */
  implicit def GetResultWatcherRow(implicit e0: GR[Long], e1: GR[Boolean], e2: GR[Option[java.sql.Timestamp]], e3: GR[Option[Long]]): GR[WatcherRow] = GR{
    prs => import prs._
    WatcherRow.tupled((<<[Long], <<[Boolean], <<?[java.sql.Timestamp], <<?[Long]))
  }
  /** Table description of table Watcher. Objects of this class serve as prototypes for rows in queries. */
  class Watcher(_tableTag: Tag) extends profile.api.Table[WatcherRow](_tableTag, Some(dbName), "Watcher") {
    def * = (id, active, createdat, watcherId) <> (WatcherRow.tupled, WatcherRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(active), createdat, watcherId).shaped.<>({r=>import r._; _1.map(_=> WatcherRow.tupled((_1.get, _2.get, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column active SqlType(BIT) */
    val active: Rep[Boolean] = column[Boolean]("active")
    /** Database column createdAt SqlType(DATETIME), Default(None) */
    val createdat: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("createdAt", O.Default(None))
    /** Database column watcher_id SqlType(BIGINT), Default(None) */
    val watcherId: Rep[Option[Long]] = column[Option[Long]]("watcher_id", O.Default(None))

    /** Foreign key referencing Users (database name FK_hobtys3mefri57vry8w6o8xyq) */
    lazy val usersFk = foreignKey("FK_hobtys3mefri57vry8w6o8xyq", watcherId, Users)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Watcher */
  lazy val Watcher = new TableQuery(tag => new Watcher(tag))
}
