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
