package models.daos

import scala.collection.mutable
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo

import models.User
import models.daos.UserDAOImpl.users
import models.daos.UserDAOImpl.usersByEmail

/**
 * Give access to the user object.
 */
class UserDAOImpl extends UserDAO {

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo) = {
    Future.successful(
      users.find { case (id, user) => user.loginInfo == loginInfo }.map(_._2))
  }
  /**
   * Finds a user by its Email.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def findByEmail(email: String) = Future.successful(usersByEmail.get(email))

  /**
   * Finds a user by its user ID.
   *
   * @param userID The long ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: Long) = Future.successful(users.get(userID))

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User) = {
    users += (user.userID -> user)
    usersByEmail += (user.email.get -> user)
    Future.successful(user)
  }
}

/**
 * The companion object.
 */
object UserDAOImpl {

  /**
   * The list of users.
   */
  val users: mutable.HashMap[Long, User] = mutable.HashMap()
  val usersByEmail: mutable.HashMap[String, User] = mutable.HashMap()
}
