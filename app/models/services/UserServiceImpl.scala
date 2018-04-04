package models.services

import java.util.UUID
import javax.inject.Inject
import java.time.LocalDate
import java.time.LocalDateTime

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.User
import models.Account._
import models.daos.UserDAO
import utils.Sanitizer.safeText
import utils.Sanitizer.slugify

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Handles actions to users.
 *
 * @param userDAO The user DAO implementation.
 * @param ex      The execution context.
 */
class UserServiceImpl @Inject() (userDAO: UserDAO)(implicit ex: ExecutionContext) extends UserService {

  
  /**
   * Retrieves a user that matches the specified ID.
   *
   * @param id The ID to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given ID.
   */
def retrieve(id: Long) = userDAO.find(id)

  /**
   * Retrieves a user that matches the specified login info.
   *
   * @param loginInfo The login info to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given login info.
   */
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDAO.find(loginInfo)

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User) = userDAO.save(user)

  /**
   * Saves the social profile for a user.
   *
   * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
   *
   * @param profile The social profile to save.
   * @return The user for whom the profile was saved.
   */
  def save(profile: CommonSocialProfile) = {
    userDAO.findByEmail(profile.email.getOrElse("")).flatMap {
      case Some(user) => // Update user with profile
       userDAO.save(user.copy(
          name = profile.fullName,
          email = profile.email,
          photoUri = profile.avatarURL,
          sluggedName = profile.fullName.map(n => slugify(safeText(n))),
          deleted = false
        ))
      case None => // Insert a new user
        userDAO.save(User(1,
              profile.loginInfo,
              None,
              None,
              Free, // use confirmedEmail value
              Some(LocalDateTime.now),
              profile.email,
              None,
              false,
              true,
              0,
              None,
              None,
              false,
              profile.fullName.map(safeText(_)),
              Some(LocalDateTime.now),
              profile.avatarURL,
              profile.fullName.map(n => utils.Sanitizer.slugify(safeText(n))),
              None,
              Some(LocalDateTime.now),
              None,
              false,
              false))
    }
  }
}
