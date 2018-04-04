package models

import java.sql.Timestamp

case class VoteTitleModel(id: Long, date: Option[Timestamp], voteCount: Long, title: String, sluggedTitle: String) {

}
