package models

import java.time.LocalDateTime

import com.mohiva.play.silhouette.api.Identity
import com.mohiva.play.silhouette.api.LoginInfo

/**
 * The user object. In addition to userRow it includes loginInfo.
 *
 */
case class User(
  userID: Long,
  loginInfo: LoginInfo,
  about: Option[String],
  birthDate: Option[LocalDateTime],
  account: Account.Value, // use confirmedEmail value
  createdAt: Option[LocalDateTime],
  email: Option[String],
  forgotPasswordToken: Option[String],
  isBanned: Boolean,
  isSubscribed: Boolean,
  karma: Long,
  location: Option[String],
  markedAbout: Option[String],
  moderator: Boolean,
  name: Option[String],
  nameLastTouchedAt: Option[LocalDateTime],
  photoUri: Option[String],
  sluggedName: Option[String],
  website: Option[String],
  lastUpvote: Option[LocalDateTime],
  avatarImage_id: Option[Long],
  receiveAllUpdates: Boolean,
  deleted: Boolean) extends Identity {

}

object User {
  def guest = User(
    0,
    null,
    None,
    None,
    Account.Gratis, // use confirmedEmail value
    Some(LocalDateTime.now()),
    Some("guest@guest.com"),
    Some(utils.Digester.encrypt("password")),
    false,
    true,
    0,
    None,
    None,
    false,
    Some("Unknown Guest"),
    Some(LocalDateTime.now()),
    None,
    Some("Unknown Guest".replaceAll("\\s", "-").toLowerCase()),
    None,
    None,
    None,
    false,
    false)
}