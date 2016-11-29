package org.mbari.vars.kbserver

import com.google.gson.{ FieldNamingPolicy, GsonBuilder }
import com.google.inject.Guice
import com.typesafe.config.{ Config, ConfigFactory }
import org.mbari.vars.kbserver.dao.DAOFactory
import org.mbari.vars.kbserver.gson.PhylogenyNodeSerializer
import org.mbari.vars.kbserver.model.{ DbParams, PhylogenyNode }

import scala.util.Try

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T13:28:00
 */
object Constants {

  val CONFIG: Config = ConfigFactory.load()

  val ENVIRONMENT: String =
    Try(CONFIG.getString("database.environment")).getOrElse("development")

  val DB_PARAMS: DbParams = {
    val path = s"org.mbari.vars.knowledgebase.database.${ENVIRONMENT}"
    val user = CONFIG.getString(s"${path}.user")
    val password = CONFIG.getString(s"${path}.password")
    val url = CONFIG.getString(s"${path}.url")
    val driver = CONFIG.getString(s"${path}.driver")
    val name = CONFIG.getString(s"${path}.name")
    DbParams(user, password, url, driver, name)
  }

  val GUICE_INJECTOR = Guice.createInjector(new InjectorModule)

  val GSON = new GsonBuilder()
    .setPrettyPrinting()
    .registerTypeAdapter(classOf[PhylogenyNode], new PhylogenyNodeSerializer)
    .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    .create()

  //  val DAO_FACTORY: DAOFactory = {
  //    val className = Try(CONFIG.getString("org.mbari.vars.kbserver.daofactory"))
  //      .getOrElse("org.mbari.vars.kbserver.dao.DefaultDAOFactory")
  //    Class.forName(className).newInstance().asInstanceOf[DAOFactory]
  //  }

}
