/*
 * Copyright 2017 Monterey Bay Aquarium Research Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mbari.vars.kbserver

import com.google.gson.{ FieldNamingPolicy, GsonBuilder }
import com.google.inject.Guice
import com.typesafe.config.{ Config, ConfigFactory }
import org.mbari.vars.kbserver.dao.DAOFactory
import org.mbari.vars.kbserver.dao.jdbc.generic.ImmutableConcept
import org.mbari.vars.kbserver.gson.{ ConceptNodeSerializer, ImmutableConceptSerializer, OptionSerializer, PhylogenyNodeSerializer }
import org.mbari.vars.kbserver.model.{ ConceptNode, DbParams, PhylogenyNode }

import scala.util.Try

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T13:28:00
 */
object Constants {

  lazy val CONFIG: Config = ConfigFactory.load()

  lazy val ENVIRONMENT: String =
    Try(CONFIG.getString("database.environment")).getOrElse("development")

  lazy val DB_PARAMS: DbParams = {
    val path = s"org.mbari.vars.knowledgebase.database.$ENVIRONMENT"
    val user = CONFIG.getString(s"${path}.user")
    val password = CONFIG.getString(s"${path}.password")
    val url = CONFIG.getString(s"${path}.url")
    val driver = CONFIG.getString(s"${path}.driver")
    val name = CONFIG.getString(s"${path}.name")
    val testQuery = Try(CONFIG.getString(s"${path}.hikari.test")).toOption
    DbParams(user, password, url, driver, name, testQuery)
  }

  lazy val GUICE_INJECTOR = Guice.createInjector(new InjectorModule)

  val GSON = new GsonBuilder()
    .setPrettyPrinting()
    .registerTypeAdapter(classOf[ImmutableConcept], new ImmutableConceptSerializer)
    .registerTypeAdapter(classOf[PhylogenyNode], new PhylogenyNodeSerializer)
    .registerTypeAdapter(classOf[ConceptNode], new ConceptNodeSerializer)
    .registerTypeAdapter(classOf[Option[_]], new OptionSerializer)
    .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    .create()

  lazy val DAO_FACTORY: DAOFactory = {
    val className = Try(CONFIG.getString("org.mbari.vars.kbserver.daofactory"))
      .getOrElse("org.mbari.vars.kbserver.dao.DefaultDAOFactory")
    //Class.forName(className).newInstance().asInstanceOf[DAOFactory]
    val clazz = Class.forName(className)
    GUICE_INJECTOR.getInstance(clazz).asInstanceOf[DAOFactory]
  }

}
