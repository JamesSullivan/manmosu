package models.daos.slick

import java.sql.Timestamp

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository

import javax.inject.Inject
import models.Account
import models.User
import models.daos.UserDAO
import play.api.Logging
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.MySQLProfile.api.booleanColumnType
import slick.jdbc.MySQLProfile.api.longColumnType
import slick.jdbc.MySQLProfile.api.stringColumnType

/**
 * Give access to the user object using Slick
 */
class UserDAOSlick @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, authInfoRepository: AuthInfoRepository, implicit val ec: ExecutionContext) extends UserDAO with DAOSlick with Logging {

  import slick.jdbc.MySQLProfile.api._

  val passWordInfoDAOSlick = new models.daos.slick.PasswordInfoDAOSlick(dbConfigProvider)

  private def userFromDBUser(user: DBUser, loginInfo: DBLoginInfo): User = userFromDBUser(user, LoginInfo(loginInfo.providerID, loginInfo.providerKey))

  private def userFromDBUser(user: DBUser, loginInfo: LoginInfo): User = {
    User(user.userID.getOrElse(0), loginInfo, user.about, user.birthDate.map(_.toLocalDateTime),
      Account(user.confirmedEmail), user.createdAt.map(_.toLocalDateTime), user.email,
      user.forgotPasswordToken, user.isBanned, user.isSubscribed, user.karma, user.location, user.markedAbout,
      user.moderator, user.name, user.nameLastTouchedAt.map(_.toLocalDateTime), user.photoUri, user.sluggedName, user.website,
      user.lastUpvote.map(_.toLocalDateTime), user.avatarImage_id, user.receiveAllUpdates, user.deleted)
  }

  private def dbuserFromUser(user: User): DBUser = {
    val userID = if (user.userID > 0) Some(user.userID) else None
    DBUser(userID, user.about, user.birthDate.map(x => Timestamp.valueOf(x)),
      user.account.id, user.createdAt.map(x => Timestamp.valueOf(x)), user.email,
      user.forgotPasswordToken, user.isBanned, user.isSubscribed, user.karma, user.location, user.markedAbout,
      user.moderator, user.name, user.nameLastTouchedAt.map(x => Timestamp.valueOf(x)), user.photoUri, user.sluggedName, user.website,
      user.lastUpvote.map(x => Timestamp.valueOf(x)), user.avatarImage_id, user.receiveAllUpdates, user.deleted)
  }

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo) = {
    logger.debug("UserDAOSlick.find by loginInfo: " + loginInfo.providerID + "  " + loginInfo.providerKey)
    val brutal = (loginInfo.providerID == "credentials")
    db.run(slickLoginInfos.filter(x => (x.providerID === loginInfo.providerID || brutal) && x.providerKey === loginInfo.providerKey).result.headOption).flatMap {
      case Some(info) =>
        db.run(slickUsers.filter(_.userID === info.user_id).result.headOption.map {
          case Some(dbuser) => Some(userFromDBUser(dbuser, loginInfo))
          case None => None
        })
      case None => Future(None: Option[User])
    }
  }

  /**
   * Finds a user by its email.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def findByEmail(email: String) = {
    db.run(slickUsers.filter(_.email === email).result.headOption).flatMap {
      case Some(dbuser) =>
        db.run(slickLoginInfos.filter(_.user_id === dbuser.userID.get).result.headOption.map {
          case Some(dbloginInfo) => Some(userFromDBUser(dbuser, dbloginInfo))
          case None => None
        })
      case None => Future(None: Option[User])
    }
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: Long): Future[Option[User]] = {
    db.run(slickUsers.filter(_.userID === userID).result.headOption).flatMap {
      case Some(user) =>
        db.run(slickLoginInfos.filter(_.user_id === userID).result.headOption.map {
          case Some(loginInfo) => Some(userFromDBUser(user, loginInfo))
          case None => None
        })
      case None => Future(None: Option[User])
    }
  }

  /**
   * Used to save a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User) = {
    logger.info("!!!!Saving userID: " + user.userID + "\tuser.name: " + user.name)
    db.run(slickUsers.filter(_.email === user.email).result.headOption).flatMap {
      case Some(dbuser) =>
	logger.info("\tupdating existing user")
        val updatedUser = user.copy(userID = dbuser.userID.get)
        val updatedDBUser = dbuserFromUser(updatedUser)
        db.run(slickLoginInfos.filter(_.providerKey === user.email.get).result.headOption.map {
          case Some(loginInfo) =>
            val updatedDBLoginInfo = DBLoginInfo(loginInfo.id, user.loginInfo.providerID, user.email.getOrElse(""), user.forgotPasswordToken.getOrElse(loginInfo.token),
              loginInfo.user_id)
            Await.result(passWordInfoDAOSlick.remove(user.loginInfo), 30.seconds)
            Await.result(db.run(slickUsers.insertOrUpdate(updatedDBUser)), 30.seconds)
            Await.result(db.run(slickLoginInfos.insertOrUpdate(updatedDBLoginInfo)), 30.seconds)
            userFromDBUser(updatedDBUser, updatedDBLoginInfo)
          case None => user
        })
      case None =>
        logger.info("\tinserting new user")
        Await.result(db.run(slickUsers +=
          dbuserFromUser(user)), 30.seconds)
        Await.result(db.run(slickUsers.filter(_.email === user.email).result.headOption.map {
          x =>
            x match {
              case Some(saveddbuser) =>
                val dbloginInfo = DBLoginInfo(None, user.loginInfo.providerID, saveddbuser.email.getOrElse(""), user.forgotPasswordToken.getOrElse(""),
                  saveddbuser.userID.get)
                Await.result(db.run(slickLoginInfos += dbloginInfo), 30.seconds)
                Future(userFromDBUser(saveddbuser, dbloginInfo))
              case None => Future(user);
            }
        }), 30.seconds)
    }
  }

  /**
   * Deletes a user.
   *
   * @param user The user to delete.
   * @return The deleted user.LoginInfo
   */
  def delete(user: User) = {
    logger.info("\r\n\r\n!!!!Deleting userID: " + user.userID + "\tuser.name: " + user.name)
    val dbUser = dbuserFromUser(user)
    logger.info("\tNumber of deleted loginfos: " + Await.result(passWordInfoDAOSlick.remove(user.loginInfo), 30.seconds))
    logger.info("\tUser removed from userID memory hashmap: " + models.daos.UserDAOImpl.users.remove(user.userID))
    logger.info("\tUser removed from email memory hashmap: " + models.daos.UserDAOImpl.usersByEmail.remove(user.email.getOrElse("")))
    db.run(slickUsers.filter(_.email === dbUser.email).delete)
    user
  }

  /**
   * Gets all Users
   *
   * @return All Users
   */
  def getUsers(): List[DBUser] = {
    val dbQueryResults = Await.result(db.run(slickUsers.result), 30.seconds)
    (for { u <- dbQueryResults } yield u).toList
  }

}
