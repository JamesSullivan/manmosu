package models

import models.daos.slick.Tables.AnswerRow
import models.daos.slick.Tables.AnswerinformationRow
import models.daos.slick.Tables.UsersRow

case class AnswerUser(row: AnswerRow, informationRow: AnswerinformationRow, authorsRow: UsersRow) extends QorA {
  val isQuestion: Boolean = false
  val text: String = "answer"
}