package utils;

import java.math.BigInteger

import org.apache.commons.codec.digest.DigestUtils;

object Digester {
  val SALT = "sadifhasdu34hqo9ihadfsoivuhaewuihfasiuasiufhifaew";
  
  def encrypt(toDigest: String): String = DigestUtils.sha256Hex(toDigest + SALT)

  def md5(content: String): String = DigestUtils.md5Hex(content);

  def hashFor(id: Long): String = encrypt(id.toString()) + new BigInteger("" + (id * 37 + 1)).toString(16);

  def idFor(hash: String): Long = {
    assert(hash.length() > 64);
    val obfuscatedId: String = hash.substring(64);
    val id: Long = (java.lang.Long.parseLong(obfuscatedId, 16) - 1) / 37;
    assert(hash.equals(hashFor(id)));
    id;
  }

}