package models

import models.daos.slick.Tables.UsersRow
import models.daos.slick.TextInputRow
import utils.RelativeTime.format

trait WithAuthor {

  val row: TextInputRow

  val authorsRow: UsersRow // abstract

  def createdat = format(row.createdat)

  def lastupdatedat = format(row.lastupdatedat)

}