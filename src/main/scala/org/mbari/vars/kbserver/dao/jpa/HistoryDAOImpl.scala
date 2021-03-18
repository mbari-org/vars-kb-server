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

import javax.inject.Inject
import org.mbari.vars.kbserver.dao.HistoryDAO
import org.mbari.vars.kbserver.model.History
import vars.knowledgebase.KnowledgebaseDAOFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import scala.util.Try

/**
 * @author Brian Schlining
 * @since 2019-11-19T13:46:00
 */
class HistoryDAOImpl @Inject() (knowledgebaseDAOFactory: KnowledgebaseDAOFactory)
  extends HistoryDAO {
  override def findPendingHistories()(implicit ec: ExecutionContext): Future[Seq[History]] = Future {
    val dao = knowledgebaseDAOFactory.newHistoryDAO()
    dao.startTransaction()
    val histories = dao.findPendingHistories()
      .asScala
      .toSeq
      .map(h => History(h.getConceptMetadata.getConcept.getPrimaryConceptName.getName, h))
      .sortBy(_.creationTimestamp)
    dao.endTransaction()
    dao.close()
    histories
  }

  override def findApprovedHistories()(implicit ec: ExecutionContext): Future[Seq[History]] = Future {
    val dao = knowledgebaseDAOFactory.newHistoryDAO()
    dao.startTransaction()
    val histories = dao.findApprovedHistories()
      .asScala
      .toSeq
      .flatMap(h => Try(History(h.getConceptMetadata.getConcept.getPrimaryConceptName.getName, h)).toOption)
      .sortBy(_.processedTimestamp)
    dao.endTransaction()
    dao.close()
    histories
  }
}
