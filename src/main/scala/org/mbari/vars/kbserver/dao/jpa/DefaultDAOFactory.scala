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

import org.mbari.kb.core.knowledgebase.KnowledgebaseDAOFactory
import org.mbari.vars.kbserver.Constants
import org.mbari.vars.kbserver.dao.cached.CachedConceptNodeDAOImpl
import org.mbari.vars.kbserver.dao.jdbc.generic.FastPhylogenyDAO
import org.mbari.vars.kbserver.dao.{ConceptNodeDAO, DAOFactory, HistoryDAO, LinkNodeDAO, PhylogenyDAO}

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-18T11:36:00
 */
class DefaultDAOFactory @Inject() (knowledgebaseDAOFactory: KnowledgebaseDAOFactory)
    extends DAOFactory {

  override def newPhylogenyDAO(): PhylogenyDAO =
    new PhylogenyDAOImpl(knowledgebaseDAOFactory)

  override def newConceptNodeDAO(): ConceptNodeDAO =
    new CachedConceptNodeDAOImpl(new ConceptNodeDAOImpl(knowledgebaseDAOFactory))

  override def newLinkNodeDAO(): LinkNodeDAO =
    new LinkNodeDAOImpl(knowledgebaseDAOFactory)

  override def newFastPhylogenyDAO(): FastPhylogenyDAO =
    DefaultDAOFactory.PhylogenyDAO2

  override def newHistoryDAO(): HistoryDAO =
    new HistoryDAOImpl(knowledgebaseDAOFactory)
}

object DefaultDAOFactory {
  lazy val PhylogenyDAO2: FastPhylogenyDAO = new FastPhylogenyDAO(Constants.DB_PARAMS.testQuery)
}
