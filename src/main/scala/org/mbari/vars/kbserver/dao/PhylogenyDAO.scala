package org.mbari.vars.kbserver.dao

import org.mbari.vars.kbserver.model.PhylogenyNode

import scala.collection.mutable
import scala.concurrent.{ ExecutionContext, Future }

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T13:45:00
 */
trait PhylogenyDAO {

  def findUp(name: String)(implicit ec: ExecutionContext): Future[Option[PhylogenyNode]]

  def findDown(name: String)(implicit ec: ExecutionContext): Future[Option[PhylogenyNode]]

  protected def rowsToConceptNode(rows: Seq[PhylogenyRow]): Option[PhylogenyNode] = {
    val nodes = new mutable.ArrayBuffer[PhylogenyNode]
    for (row <- rows) {
      val parent = nodes
        .find(n => n.name == row.parentName)
        .getOrElse({
          val node = PhylogenyNode(row.parentName, Option(row.parentRank), None)
          nodes += node
          node
        })

      val child = PhylogenyNode(row.childName, Option(row.childRank), Option(parent))
      parent.children += child
      nodes += child
    }

    nodes.find(_.parent.isEmpty)
  }

}

case class PhylogenyRow(
  parentId: Long,
  parentName: String,
  parentRank: String,
  childId: Long,
  childName: String,
  childRank: String
)

