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

import org.mbari.vars.kbserver.dao.ConceptNodeDAO
import org.mbari.vars.kbserver.model.ConceptNode
import vars.knowledgebase.{KnowledgebaseDAOFactory}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

/**
  *
  *
  * @author Brian Schlining
  * @since 2016-11-18T08:59:00
  */
class ConceptNodeDAOImpl @Inject() (knowledgebaseDAOFactory: KnowledgebaseDAOFactory)
    extends ConceptNodeDAO {

  override def findByName(name: String)(
      implicit
      ec: ExecutionContext
  ): Future[Option[ConceptNode]] =
    Future {
      val conceptDao = knowledgebaseDAOFactory.newConceptDAO()
      conceptDao.startTransaction()
      val node = Option(conceptDao.findByName(name)).map(ConceptNode(_))
      conceptDao.endTransaction()
      conceptDao.close()
      node
    }

  override def findParent(
      name: String
  )(implicit ec: ExecutionContext): Future[Option[ConceptNode]] = {
    Future {
      val conceptDao = knowledgebaseDAOFactory.newConceptDAO()
      conceptDao.startTransaction()
      val node = Option(conceptDao.findByName(name))
        .flatMap(c => Option(c.getParentConcept))
        .map(ConceptNode(_))
      conceptDao.endTransaction()
      conceptDao.close()
      node
    }
  }

  override def findChildren(
      name: String
  )(implicit ec: ExecutionContext): Future[Seq[ConceptNode]] = {
    Future {
      val dao = knowledgebaseDAOFactory.newConceptDAO()
      dao.startTransaction()
      val nodes = Option(dao.findByName(name))
        .map(c => c.getChildConcepts().asScala.map(ConceptNode(_)))
        .getOrElse(Nil)
        .toSeq
      dao.endTransaction()
      dao.close()
      nodes
    }
  }

  override def findAllNames()(implicit ec: ExecutionContext): Future[Seq[String]] =
    Future {
      val conceptNameDao = knowledgebaseDAOFactory.newConceptNameDAO()
      conceptNameDao.startTransaction()
      val names = conceptNameDao.findAllNamesAsStrings().asScala
      conceptNameDao.endTransaction()
      conceptNameDao.close()
      names
    }

  override def findRoot()(implicit ec: ExecutionContext): Future[Option[ConceptNode]] =
    Future {
      val conceptDao = knowledgebaseDAOFactory.newConceptDAO()
      conceptDao.startTransaction()
      val root = Option(conceptDao.findRoot()).map(ConceptNode(_))
      conceptDao.endTransaction()
      conceptDao.close()
      root
    }
}
