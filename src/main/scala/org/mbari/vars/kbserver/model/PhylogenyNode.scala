package org.mbari.vars.kbserver.model

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
    children: Set[PhylogenyNode]
) {

  override def equals(obj: Any): Boolean = name == obj

  override def hashCode(): Int = name.hashCode
}
