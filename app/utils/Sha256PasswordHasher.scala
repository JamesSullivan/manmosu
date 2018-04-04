package utils

import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.api.util.PasswordInfo

import utils.Sha256PasswordHasher.ID

/**
 * Implementation of the password hasher based on BCrypt.
 *
 * @param logRounds The log2 of the number of rounds of hashing to apply.
 * @see [[http://www.mindrot.org/files/jBCrypt/jBCrypt-0.2-doc/BCrypt.html#gensalt(int) gensalt]]
 */
class Sha256PasswordHasher(logRounds: Int = 10) extends PasswordHasher {

  /**
   * Gets the ID of the hasher.
   *
   * @return The ID of the hasher.
   */
  override def id = ID

  /**
   * Hashes a password.
   *
   * This implementation does not return the salt separately because it is embedded in the hashed password.
   * Other implementations might need to return it so it gets saved in the backing store.
   *
   * @param plainPassword The password to hash.
   * @return A PasswordInfo containing the hashed password.
   */
  override def hash(plainPassword: String) = PasswordInfo(id, Digester.encrypt(plainPassword))

  /**
   * Checks if a password matches the hashed version.
   *
   * @param passwordInfo The password retrieved from the backing store.
   * @param suppliedPassword The password supplied by the user trying to log in.
   * @return True if the password matches, false otherwise.
   */
  override def matches(passwordInfo: PasswordInfo, suppliedPassword: String) = {
    val m = Digester.encrypt(suppliedPassword).equals(passwordInfo.password)
    // println("Sha256PasswordHasher.matches: " + m)
    m
  }

  /**
   * Indicates if a password info hashed with this hasher is deprecated.
   *
   * A password can be deprecated if some internal state of a hasher has changed.
   *
   * @param passwordInfo The password info to check the deprecation status for.
   * @return True if the given password info is deprecated, false otherwise. If a hasher isn't
   *         suitable for the given password, this method should return None.
   */
  override def isDeprecated(passwordInfo: PasswordInfo): Option[Boolean] = Some(false)
}

/**
 * The companion object.
 */
object Sha256PasswordHasher {

  /**
   * The ID of the hasher.
   */
  val ID = "BRUTAL"
}
