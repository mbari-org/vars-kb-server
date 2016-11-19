package org.mbari.vars.kbserver.dao.jpa

import com.typesafe.config.ConfigFactory
import org.mbari.vars.kbserver.Constants

/**
  *
  *
  * @author Brian Schlining
  * @since 2016-11-18T11:36:00
  */
class DefaultDAOFactory {

  val config = ConfigFactory.load()
  val params = Constants.DB_PARAMS
  private[this] val props = Map(
    "eclipselink.connection-pool.default.initial" -> "2",
    "eclipselink.connection-pool.default.max" -> "16",
    "eclipselink.connection-pool.default.min" -> "2",
    "eclipselink.logging.level" -> "INFO",
    "eclipselink.logging.session" -> "false",
    "eclipselink.logging.thread" -> "false",
    "eclipselink.logging.timestamp" -> "false",
    "eclipselink.target-database" -> params.name,
    "javax.persistence.database-product-name" -> params.name,
    "javax.persistence.schema-generation.database.action" -> "create"
  )





}
