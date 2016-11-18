package org.mbari.vars.kbserver.model

import vars.ILink

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T16:23:00
 */
case class LinkNode(linkName: String, toConcept: String, linkValue: String)

object LinkNode {
  def apply(link: ILink): LinkNode =
    LinkNode(link.getLinkName, link.getToConcept, link.getLinkValue)
}
