package org.mbari.vars.kbserver.dao.jpa

import javax.inject.Inject

import org.mbari.vars.kbserver.dao.ConceptNodeDAO
import org.mbari.vars.kbserver.model.ConceptNode
import vars.knowledgebase.{ KnowledgebaseDAOFactory }

import scala.collection.JavaConverters._
import scala.concurrent.{ ExecutionContext, Future }

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

  override def findAllNames()(implicit ec: ExecutionContext): Future[Seq[String]] =
    Future {
      val conceptNameDao = knowledgebaseDAOFactory.newConceptNameDAO()
      conceptNameDao.startTransaction()
      val names = conceptNameDao.findAll().asScala.map(_.getName).toSeq
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
