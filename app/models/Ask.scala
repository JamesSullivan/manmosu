package models

case class Ask(watching: Boolean, description: String, markedDescription: String, tags: Seq[String], title: String, comment: String, questionId: Long) {

}