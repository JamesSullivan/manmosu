package models.daos.slick

import java.sql.Timestamp

import slick.jdbc.MySQLProfile.api.ForeignKeyAction
import slick.jdbc.MySQLProfile.api.Table
import slick.jdbc.MySQLProfile.api.TableQuery
import slick.jdbc.MySQLProfile.api.Tag
import slick.jdbc.MySQLProfile.api.anyToShapedValue
import slick.jdbc.MySQLProfile.api.booleanColumnType
import slick.jdbc.MySQLProfile.api.columnExtensionMethods
import slick.jdbc.MySQLProfile.api.intColumnType
import slick.jdbc.MySQLProfile.api.longColumnType
import slick.jdbc.MySQLProfile.api.stringColumnType
import slick.jdbc.MySQLProfile.api.timestampColumnType

trait DBTableDefinitions {

  case class DBUser(
    userID: Option[Long],
    about: Option[String],
    birthDate: Option[Timestamp],
    confirmedEmail: Int, // use confirmedEmail value
    createdAt: Option[Timestamp],
    email: Option[String],
    forgotPasswordToken: Option[String],
    isBanned: Boolean,
    isSubscribed: Boolean,
    karma: Long,
    location: Option[String],
    markedAbout: Option[String],
    moderator: Boolean,
    name: Option[String],
    nameLastTouchedAt: Option[Timestamp],
    photoUri: Option[String],
    sluggedName: Option[String],
    website: Option[String],
    lastUpvote: Option[Timestamp],
    avatarImage_id: Option[Long],
    receiveAllUpdates: Boolean,
    deleted: Boolean)

  class Users(tag: Tag) extends Table[DBUser](tag, "Users") {
    def userID = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def about = column[Option[String]]("about")
    def birthDate = column[Option[Timestamp]]("birthDate")
    def confirmedEmail = column[Int]("confirmedEmail")
    def createdAt = column[Option[Timestamp]]("createdAt")
    def email = column[Option[String]]("email")
    def forgotPasswordToken = column[Option[String]]("forgotPasswordToken")
    def isBanned = column[Boolean]("isBanned")
    def isSubscribed = column[Boolean]("isSubscribed")
    def karma = column[Long]("karma")
    def location = column[Option[String]]("location")
    def markedAbout = column[Option[String]]("markedAbout")
    def moderator = column[Boolean]("moderator")
    def name = column[Option[String]]("name")
    def nameLastTouchedAt = column[Option[Timestamp]]("nameLastTouchedAt")
    def photoUri = column[Option[String]]("photoUri")
    def sluggedName = column[Option[String]]("sluggedName")
    def website = column[Option[String]]("website")
    def lastUpvote = column[Option[Timestamp]]("lastUpvote")
    def avatarImage = column[Option[Long]]("avatarImage_id")
    def receiveAllUpdates = column[Boolean]("receiveAllUpdates")
    def deleted = column[Boolean]("deleted")
    def * = (userID.?, about, birthDate, confirmedEmail, createdAt, email, forgotPasswordToken, isBanned,
      isSubscribed, karma, location, markedAbout, moderator, name, nameLastTouchedAt, photoUri, sluggedName,
      website, lastUpvote, avatarImage, receiveAllUpdates, deleted) <> (DBUser.tupled, DBUser.unapply)
  }

  case class DBLoginInfo(
    id: Option[Long],
    providerID: String, // type
    providerKey: String, // serviceEmail
    token: String,
    user_id: Long)

  class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, "LoginMethod") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def providerID = column[String]("type")
    def providerKey = column[String]("serviceEmail")
    def token = column[String]("token")
    def user_id = column[Long]("user_id")
    def users = foreignKey("USER_ID_FK", user_id, slickUsers)(_.userID, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.Cascade)
    def * = (id.?, providerID, providerKey, token, user_id) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  case class DBUserLoginInfo(
    userID: Long,
    loginInfoId: Long)

  class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, "LoginMethod") {
    def userID = column[Long]("user_id")
    def loginInfoId = column[Long]("id")
    def * = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  case class DBPasswordInfo(
    hasher: String,
    password: String,
    salt: Option[String],
    loginInfoId: Long)

  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "LoginMethod") {
    def hasher = column[String]("type")
    def password = column[String]("token")
    def salt = column[Option[String]]("serviceEmail")
    def loginInfoId = column[Long]("id")
    def * = (hasher, password, salt, loginInfoId) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  case class DBOAuth1Info(
    id: Option[Long],
    token: String,
    secret: String,
    loginInfoId: Long)

  class OAuth1Infos(tag: Tag) extends Table[DBOAuth1Info](tag, "oauth1info") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def token = column[String]("token")
    def secret = column[String]("secret")
    def loginInfoId = column[Long]("loginInfoId")
    def * = (id.?, token, secret, loginInfoId) <> (DBOAuth1Info.tupled, DBOAuth1Info.unapply)
  }

  case class DBOAuth2Info(
    id: Option[Long],
    accessToken: String,
    tokenType: Option[String],
    expiresIn: Option[Int],
    refreshToken: Option[String],
    loginInfoId: Long)

  class OAuth2Infos(tag: Tag) extends Table[DBOAuth2Info](tag, "oauth2info") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def accessToken = column[String]("accesstoken")
    def tokenType = column[Option[String]]("tokentype")
    def expiresIn = column[Option[Int]]("expiresin")
    def refreshToken = column[Option[String]]("refreshtoken")
    def loginInfoId = column[Long]("logininfoid")
    def * = (id.?, accessToken, tokenType, expiresIn, refreshToken, loginInfoId) <> (DBOAuth2Info.tupled, DBOAuth2Info.unapply)
  }

  val slickUsers = TableQuery[Users]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickUserLoginInfos = TableQuery[UserLoginInfos]
  val slickPasswordInfos = TableQuery[PasswordInfos]
  val slickOAuth1Infos = TableQuery[OAuth1Infos]
  val slickOAuth2Infos = TableQuery[OAuth2Infos]

}
