package utils

import java.io.UnsupportedEncodingException

import models.daos.slick.Tables.UsersRow

object Photo {

  def getPhoto(usersRow: UsersRow, width: Int, height: Int, gravatarUrl: String = "https://www.gravatar.com/avatar/"): String =
    getPhotoString(usersRow.avatarimageId, usersRow.email, usersRow.photouri, width, height, gravatarUrl)

  def getPhotoFromUser(user: models.User, width: Int, height: Int, gravatarUrl: String = "https://www.gravatar.com/avatar/"): String =
    getPhotoString(user.avatarImage_id.map(x => x.toLong), user.email, user.photoUri, width, height, gravatarUrl)

  def getPhotoString(avatarimageId: Option[Long], email: Option[String], photouri: Option[String], width: Int, height: Int, gravatarUrl: String = "https://www.gravatar.com/avatar/"): String = {
    if (avatarimageId.isDefined) return linkedAvatarPhoto(avatarimageId, width, height);
    val size: String = s"${width}x${height}";
    if (photouri.isEmpty) {
      val digest: String = utils.Digester.md5(email.getOrElse(""));
      val identiURL: String = gravatarUrl + digest +"?s=" + width + "&d=identicon&r=PG"
      val roboURL: String = gravatarUrl + digest + ".png?r=PG&size=" + size + "&d=robohash"
      identiURL
    } else if (photouri.get.contains("googleusercontent")) {
      photouri.get.replaceAll("sz=(\\d+)", "sz=" + width);
    } else if (photouri.get.contains("facebook.com/")) {
      photouri.get
    } else {
      val marker =  if(photouri.get.contains("?")) "&" else "?"
      photouri.get + marker + "width=" + width + "&height=" + height;
    }
  }

  private def linkedAvatarPhoto(avatarImage_id: Option[Long], width: Int, height: Int): String = s"""${avatarImage_id.get}?w=${width}&h=${height}"""

}