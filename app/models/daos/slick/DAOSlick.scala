package models.daos.slick

import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.MySQLProfile

/**
 * Trait that contains generic slick db handling code to be mixed in with DAOs
 */
trait DAOSlick extends DBTableDefinitions with HasDatabaseConfigProvider[MySQLProfile]