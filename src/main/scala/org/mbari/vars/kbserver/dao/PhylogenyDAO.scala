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
    val nodes = new mutable.ArrayBuffer[Node]
    for (row <- rows) {
      val parent = nodes
        .find(n => n.name == row.parentName)
        .getOrElse({
          val node = Node(row.parentName, Option(row.parentRank), None)
          nodes += node
          node
        })

      val child = Node(row.childName, Option(row.childRank), Option(parent))
      parent.children += child
      nodes += child
    }

    nodes.find(_.parent.isEmpty).map(_.asConceptNode)
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

case class Node(
    name: String,
    rank: Option[String] = None,
    parent: Option[Node] = None,
    children: mutable.HashSet[Node] = new mutable.HashSet[Node]
) {

  def asConceptNode: PhylogenyNode =
    PhylogenyNode(name, rank, parent.map(_.asConceptNode), children.map(_.asConceptNode).toSet)

}
