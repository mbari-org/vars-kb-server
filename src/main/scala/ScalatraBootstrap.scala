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

import org.mbari.kb.core.knowledgebase.KnowledgebaseDAOFactory

import java.util.concurrent.Executors
import javax.servlet.ServletContext
import org.mbari.vars.kbserver.Constants
import org.mbari.vars.kbserver.api._
import org.scalatra.LifeCycle
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-05-20T14:41:00
 */
class ScalatraBootstrap extends LifeCycle {

  private[this] val log = LoggerFactory.getLogger(getClass)

  override def init(context: ServletContext): Unit = {

    log.info("STARTING UP NOW")

    implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors()))

    val daoFactory = Constants.DAO_FACTORY

    val phylogenyApiV1 = new PhylogenyApiV1(daoFactory)
    val conceptApi = new ConceptApi(daoFactory)
    val linkApi = new LinkApi(daoFactory)
    val historyApi = new HistoryApi(daoFactory)
    val dsgApi = new DeepSeaGuideApi(daoFactory)

    val kbDaoFactory = Constants.GUICE_INJECTOR.getInstance(classOf[KnowledgebaseDAOFactory])
    val rawApi = new RawApi(kbDaoFactory)

    context.mount(phylogenyApiV1, "/v1/phylogeny")
    context.mount(conceptApi, "/v1/concept")
    context.mount(linkApi, "/v1/links")
    context.mount(rawApi, "/v1/raw")
    context.mount(historyApi, "/v1/history")
    context.mount(dsgApi, "/v1/dsg")

  }

}
