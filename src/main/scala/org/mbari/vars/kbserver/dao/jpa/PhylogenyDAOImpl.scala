package org.mbari.vars.kbserver.dao.jpa

import javax.inject.Inject

import org.mbari.vars.kbserver.dao.{ PhylogenyDAO, PhylogenyRow }
import org.mbari.vars.kbserver.model.{ ConceptNode, PhylogenyNode }
import vars.knowledgebase.{ Concept, KnowledgebaseDAOFactory }

import scala.collection.JavaConverters._
import scala.concurrent.{ ExecutionContext, Future }

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-18T09:38:00
 */
class PhylogenyDAOImpl @Inject() (knowledgebaseDAOFactory: KnowledgebaseDAOFactory)
    extends PhylogenyDAO {

  override def findUp(name: String)(implicit ec: ExecutionContext): Future[Option[PhylogenyNode]] =
    find(name, (c: Concept) => rowsToConceptNode(toUpRows(c)))

  override def findDown(name: String)(
    implicit
    ec: ExecutionContext
  ): Future[Option[PhylogenyNode]] =
    find(name, (c: Concept) => rowsToConceptNode(toDownRows(c)))

  private def find(
    name: String,
    fn: Concept => Option[PhylogenyNode]
  )(implicit ec: ExecutionContext): Future[Option[PhylogenyNode]] =
    Future {
      val conceptDao = knowledgebaseDAOFactory.newConceptDAO()
      conceptDao.startTransaction()
      val node = Option(conceptDao.findByName(name)).flatMap(c => fn(c))
      conceptDao.endTransaction()
      conceptDao.close()
      node
    }

  private def toUpRows(concept: Concept, accum: List[PhylogenyRow] = Nil): Seq[PhylogenyRow] = {
    val rows = toPhylogenyRow(concept) :: accum
    Option(concept.getParentConcept).map(toUpRows(_, rows)).getOrElse(accum)
  }

  private def toDownRows(concept: Concept): Seq[PhylogenyRow] = {
    val childRows = for (child <- concept.getChildConcepts.asScala) yield {
      toDownRows(child)
    }
    toPhylogenyRow(concept) :: childRows.toList.flatten
  }

  private def toPhylogenyRow(concept: Concept): PhylogenyRow = {
    val conceptNode = ConceptNode(concept)
    Option(concept.getParentConcept) match {
      case None =>
        PhylogenyRow(
          0,
          null,
          null,
          concept.getPrimaryKey.asInstanceOf[Long],
          conceptNode.name,
          conceptNode.rank.getOrElse("")
        )
      case Some(p) =>
        val parentNode = ConceptNode(p)
        PhylogenyRow(
          p.getPrimaryKey.asInstanceOf[Long],
          parentNode.name,
          parentNode.rank.getOrElse(""),
          concept.getPrimaryKey.asInstanceOf[Long],
          conceptNode.name,
          conceptNode.rank.getOrElse("")
        )
    }
  }
}
