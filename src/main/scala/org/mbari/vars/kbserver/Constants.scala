package org.mbari.vars.kbserver

import com.typesafe.config.{ Config, ConfigFactory }
import org.mbari.vars.kbserver.model.DbParams

import scala.util.Try

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T13:28:00
 */
object Constants {

  @volatile lazy val CONFIG: Config = ConfigFactory.load()

  @volatile lazy val ENVIRONMENT: String =
    Try(CONFIG.getString("database.environment")).getOrElse("development")

  @volatile lazy val DB_PARAMS: DbParams = {
    val path = s"org.mbari.vars.kbserver.database.${ENVIRONMENT}"
    val user = CONFIG.getString(s"${path}.user")
    val password = CONFIG.getString(s"${path}.password")
    val url = CONFIG.getString(s"${path}.url")
    val driver = CONFIG.getString(s"${path}.driver")
    val name = CONFIG.getString(s"${path}.name")
    DbParams(user, password, url, driver, name)
  }

}
