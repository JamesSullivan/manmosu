package models

import models.daos.slick.Tables.QuestionRow
import models.daos.slick.Tables.QuestioninformationRow
import models.daos.slick.Tables.TagxRow
import models.daos.slick.Tables.UsersRow

/**
 * All of the question and associated information such as author (usersRow)
 *
 */
case class QuestionUser(row: QuestionRow, informationRow: QuestioninformationRow, authorsRow: UsersRow) extends QorA {
  val isQuestion: Boolean = true
  val text: String = "question"
  var tags: Seq[QuestionTag] = Seq[QuestionTag]()
  var watching: Boolean = false //current user (not necessarily question author)
  var informationUserTagRows: Seq[(QuestioninformationRow, UsersRow, Seq[TagxRow])] = Seq[(QuestioninformationRow, UsersRow, Seq[TagxRow])]()

  def url = row.id + "-" + informationRow.sluggedtitle

}
