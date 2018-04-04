package models

import models.daos.slick.Tables.TagxRow

case class QuestionTag(questioninformationId: Long, tagxRow: TagxRow)