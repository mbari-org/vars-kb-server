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

import org.mbari.kb.core.{ILink, LinkBean}
import org.mbari.kb.core.knowledgebase.KnowledgebaseDAOFactory

import javax.inject.Inject
import org.mbari.vars.kbserver.dao.LinkNodeDAO
import org.mbari.vars.kbserver.model.LinkNode

import scala.jdk.CollectionConverters._
import scala.concurrent.{ExecutionContext, Future}

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-18T09:07:00
 */
class LinkNodeDAOImpl @Inject() (knowledgebaseDAOFactory: KnowledgebaseDAOFactory)
    extends LinkNodeDAO {
  override def findAllApplicableTo(conceptName: String)(
    implicit
    ec: ExecutionContext
  ): Future[Seq[LinkNode]] =
    Future {
      val conceptDAO = knowledgebaseDAOFactory.newConceptDAO()
      conceptDAO.startTransaction()
      val concept = Option(conceptDAO.findByName(conceptName))
      val links = concept match {
        case Some(c) =>
          val linkTemplateDAO =
            knowledgebaseDAOFactory.newLinkTemplateDAO(conceptDAO.getEntityManager)
          linkTemplateDAO.findAllApplicableToConcept(c).asScala.map(LinkNode(_)).toSeq
        case None => Nil
      }
      conceptDAO.endTransaction()
      conceptDAO.close()
      links
    }

  def findAll(implicit ec: ExecutionContext): Future[Seq[LinkNode]] = {
    Future {
      val dao = knowledgebaseDAOFactory.newLinkTemplateDAO()
      dao.startTransaction()
      val links = dao.findAll().asScala.map(LinkNode(_)).toSeq
      dao.endTransaction()
      dao.close()
      links
    }
  }

  def findAllLinkRealizationsByLinkName(linkName: String)(implicit ec: ExecutionContext): Future[Seq[ILink]] = {
    Future {
      val sql =
        """
          |SELECT
          |    cn.ConceptName,
          |    lr.LinkName,
          |    lr.ToConcept,
          |    lr.LinkValue
          |FROM
          |    LinkRealization lr RIGHT JOIN
          |    ConceptDelegate cd ON lr.ConceptDelegateID_FK = cd.id RIGHT JOIN
          |    Concept c ON cd.ConceptID_FK = c.id RIGHT JOIN
          |    ConceptName cn ON cn.ConceptID_FK = c.id
          |WHERE
          |    lr.LinkName = ? AND
          |    cn.NameType = 'Primary'
          |
          |""".stripMargin

      val dao = knowledgebaseDAOFactory.newLinkRealizationDAO()
      val entityManager = dao.getEntityManager
      dao.startTransaction()
      val query = entityManager.createNativeQuery(sql)
      query.setParameter(1, linkName)
      val links = query.getResultList
        .asScala
        .map(obj => {
          val xs = obj.asInstanceOf[Array[Object]]
          new LinkBean(xs(1).asInstanceOf[String],
            xs(2).asInstanceOf[String],
            xs(3).asInstanceOf[String],
            xs(0).asInstanceOf[String])
        })
        .sortBy(_.getFromConcept)
      dao.endTransaction()
      links.toSeq
    }
  }

}
