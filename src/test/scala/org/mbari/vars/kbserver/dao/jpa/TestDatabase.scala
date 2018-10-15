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

package org.mbari.vars.kbserver.dao.jpa

import java.util.concurrent.TimeUnit

import com.google.inject.Guice
import org.mbari.net.URLUtilities
import org.slf4j.LoggerFactory
import vars.gson.InitializeKnowledgebaseApp
import vars.jpa.InjectorModule
import vars.knowledgebase.KnowledgebaseDAOFactory

import scala.concurrent.duration.Duration
import scala.util.control.NonFatal

/**
 * Reads a zipped up JSON dump of MBARI's KB and loads it into the database.
 *
 * @author Brian Schlining
 * @since 2016-11-22T15:28:00
 */
object TestDatabase {

  private[this] val log = LoggerFactory.getLogger(getClass)
  private[this] val injector = Guice.createInjector(new InjectorModule)

  val TIMEOUT = Duration(10, TimeUnit.SECONDS)

  @volatile
  lazy val init: Boolean = {
    log.info("Begin database initialization")
    try {
      val url = getClass.getResource("/kb/kb-dump.json.zip")
      val file = URLUtilities.toFile(url)
      InitializeKnowledgebaseApp.run(file, KB_DAO_FACTORY)
      true
    } catch {
      case NonFatal(e) => false
    }
  }

  val KB_DAO_FACTORY = injector.getInstance(classOf[KnowledgebaseDAOFactory])

}
