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

import org.mbari.vars.kbserver.dao.PhylogenyDAO
import org.mbari.vars.kbserver.model.PhylogenyNode
import vars.knowledgebase.KnowledgebaseDAOFactory

import scala.concurrent.{ ExecutionContext, Future }

/**
 * @author Brian Schlining
 * @since 2018-02-09T08:05:00
 */
class PhylogenyDAOSpecialImpl @Inject() (knowledgebaseDAOFactory: KnowledgebaseDAOFactory)
    extends PhylogenyDAO {

  override def findUp(name: String)(implicit ec: ExecutionContext): Future[Option[PhylogenyNode]] = {
    val conceptDao = knowledgebaseDAOFactory.newConceptDAO()
    conceptDao.startTransaction()
    Option(conceptDao.findByName(name)).map(concept => {

    })
    conceptDao.endTransaction()
    Future(None)
  }

  override def findDown(name: String)(implicit ec: ExecutionContext): Future[Option[PhylogenyNode]] = ???
}
