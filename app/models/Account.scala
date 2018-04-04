package models

object Account extends Enumeration {
  val Gratis = Value(0)
  val Free = Value(1)
  val Trial = Value(3)
  val Professional = Value(10)
  val Admin = Value(20)
}