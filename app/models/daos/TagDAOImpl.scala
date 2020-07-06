package models.daos

import scala.collection.mutable

import models.daos.TagDAOImpl.sortedTags
import models.daos.TagDAOImpl.tagxRows
import models.daos.slick.Tables
import models.daos.slick.Tables.TagxRow

/**
 * Give access to the user object.
 */
class TagDAOImpl {

  def apply(newTagxRows: Seq[Tables.TagxRow]) = {
    tagxRows.clear()
    tagxRows ++= (newTagxRows.map { tr: Tables.TagxRow => (tr.name, tr) })
    sortedTags.clear()
    sortedTags ++= (newTagxRows.map { tr: Tables.TagxRow => tr.name })
  }

  /**
   * Finds a Tagx
   *
   * @param tag The tag as string to find.
   * @return TagxRow or None if no TagxRow for the given tag string could be found.
   */
  def find(tag: String) = tagxRows.get(tag)

  /**
   * Checks if tag exists.
   *
   * @param tag The tag as a String
   * @return true or false
   */
  def contains(tag: String) = sortedTags.contains(tag)

  /**
   * Saves a Tagx.
   *
   * @param tagxRow The TagxRow to save.
   * @return The saved tagxRow.
   */
  def save(tagxRow: TagxRow) = {
    tagxRows.+=((tagxRow.name, tagxRow))
    sortedTags += tagxRow.name
  }

  def startsWith(tagStart: String) = sortedTags.filter(_.startsWith(tagStart))
}

/**
 * The companion object.
 */
object TagDAOImpl {

  /**
   * The lists of tags.
   */
  val tagxRows: mutable.HashMap[String, TagxRow] = mutable.HashMap()
  val sortedTags: mutable.SortedSet[String] = mutable.SortedSet[String]()
}