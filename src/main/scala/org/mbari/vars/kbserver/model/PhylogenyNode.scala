package org.mbari.vars.kbserver.model

import scala.collection.mutable

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T13:41:00
 */
case class PhylogenyNode(
    name: String,
    rank: Option[String] = None,
    parent: Option[PhylogenyNode],
    children: mutable.HashSet[PhylogenyNode] = new mutable.HashSet[PhylogenyNode]
) {

  override def equals(obj: Any): Boolean = name == obj

  override def hashCode(): Int = name.hashCode

  lazy val root: PhylogenyNode = parent match {
    case None => this
    case Some(p) => p.root
  }

  /**
   * Extracts a node from a tree of nodes with a matching name. The ndoe is trimmed so that it's parent
   * is null
   * @param name The name to find
   * @return A matching node
   */
  def subnode(name: String): Option[PhylogenyNode] = if (name.equalsIgnoreCase(name)) Some(this)
  else children.toStream.map(n => n.subnode("name").map(_.copy(parent = None))).head

}

