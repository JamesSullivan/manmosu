package models

import models.daos.slick.Tables

case class TagRanking(totalKarma: Int, userId: Long, user: Tables.UsersRow, reputationEventRows: Seq[Tables.ReputationeventRow]) {

}