package models.daos.slick

trait TextInputRow {
  val id: Long 
  val createdat: Option[java.sql.Timestamp]
  val lastupdatedat: Option[java.sql.Timestamp]
  val invisible: Boolean
  val deleted: Option[Boolean]
}