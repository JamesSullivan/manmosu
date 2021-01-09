package models.daos.slick

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.Success
import scala.reflect.ClassTag

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

import javax.inject.Inject
import play.api.Logging
import play.api.db.slick.DatabaseConfigProvider

/**
 * The DAO to store the password information.
 */
class PasswordInfoDAOSlick @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext)
    extends DelegableAuthInfoDAO[PasswordInfo] with DAOSlick with Logging {
  import slick.jdbc.MySQLProfile.api._
  
  val classTag: scala.reflect.ClassTag[com.mohiva.play.silhouette.api.util.PasswordInfo] = scala.reflect.classTag[PasswordInfo]
  
  /**
   * Saves the LoginInfo.
   *
   * @param loginInfo The login info for which the auth info should be saved.
   * @param authInfo The password info to save.
   * @return The saved password info or None if the password info couldn't be saved.
   */

  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    logger.info("PasswordInfoDAOSlick saving: ")
    logger.info("  authInfo: " + authInfo)
    logger.info("  loginInfo: " + loginInfo)
    val query = slickLoginInfos.filter(x => x.providerID === loginInfo.providerID && x.providerKey === loginInfo.providerKey)
    val old: Future[Option[DBLoginInfo]] = db.run(query.result.headOption)
    old.andThen { 
      case Success(oldDbLoginInfo: Option[DBLoginInfo]) if(oldDbLoginInfo.isDefined) => 
        logger.info("oldDbLoginfo: " + oldDbLoginInfo)
        db.run(query.update(DBLoginInfo(oldDbLoginInfo.get.id, loginInfo.providerID, loginInfo.providerKey, authInfo.password, oldDbLoginInfo.get.user_id)))
      case _  =>
        logger.info("newDbLoginfo: ")
        db.run(slickUsers.filter(_.email === loginInfo.providerKey).result.headOption) onComplete {
          case Success(Some(dbuser)) if(dbuser.userID.isDefined) => db.run(query.insertOrUpdate(DBLoginInfo(None, loginInfo.providerID, loginInfo.providerKey, authInfo.password, dbuser.userID.get)))
          case _ => logger.warn("models.daos.slick.PasswordINfoDAOSlick.save: Unable to find email === loginInfo.providerKey")
        }
    } map { x => authInfo}
      
  }

  /**
   * Updates the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be updated.
   * @param authInfo The auth info to update.
   * @return The updated auth info.
   */
  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    save(loginInfo, authInfo)
  }

  /**
   * Updates the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be updated.
   * @param authInfo The auth info to update.
   * @return The updated auth info.
   */
  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    save(loginInfo, authInfo)
  }

  /**
   * Finds the password info which is linked with the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The retrieved password info or None if no password info could be retrieved for the given login info.
   */
  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    logger.info("PasswordInfoDAOSlick.find: " + loginInfo.providerID + "  " + loginInfo.providerKey)
    val providerID = if(loginInfo.providerID.equalsIgnoreCase("credentials")) "BRUTAL" else loginInfo.providerID
    db.run(slickLoginInfos.filter(info => info.providerID === providerID && info.providerKey === loginInfo.providerKey).result.headOption.map {
      _ match {
          case Some(info) =>
            logger.info("\tinfo.id: " + info.id)
            val passwordInfo = Await.result(db.run(slickPasswordInfos.filter(_.loginInfoId === info.id).result.head), Duration.Inf)
            logger.info("\tpasswordInfo.loginInfoId: " + passwordInfo.loginInfoId + "\t" + passwordInfo.hasher)
            Some(PasswordInfo(passwordInfo.hasher, passwordInfo.password, passwordInfo.salt))
          case None => None
        }
    })
  }


  /**
   * remove the password info.
   *
   * @param loginInfo The login info for which the auth info should be deleted.
   * @return int
   */
  def remove(loginInfo: LoginInfo): Future[Unit] = {
    db.run(slickLoginInfos.filter(info => info.providerID === loginInfo.providerID && info.providerKey === loginInfo.providerKey).result.headOption).map {
      _ match {
        case Some(info) =>
          db.run(slickPasswordInfos.filter(_.loginInfoId === info.id).delete); () 
        case None => ()
      }
    }
  }

}
