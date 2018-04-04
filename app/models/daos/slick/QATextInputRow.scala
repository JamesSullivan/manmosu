package models.daos.slick

trait QATextInputRow extends TextInputRow {
  val votecount: Long
  val authorId: Option[Long]
  val informationId: Long
  val lasttouchedbyId: Option[Long]
}