package org.mbari.vars.kbserver.dao.jpa

import javax.inject.Inject

import org.mbari.vars.kbserver.dao.LinkNodeDAO
import org.mbari.vars.kbserver.model.LinkNode

import scala.collection.JavaConverters._
import vars.knowledgebase.{ ConceptDAO, KnowledgebaseDAOFactory, LinkTemplateDAO }

import scala.concurrent.{ ExecutionContext, Future }

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

}
