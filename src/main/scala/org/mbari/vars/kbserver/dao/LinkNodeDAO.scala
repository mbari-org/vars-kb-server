package org.mbari.vars.kbserver.dao

import org.mbari.vars.kbserver.model.LinkNode

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T16:40:00
 */
trait LinkNodeDAO {

  def findLinkTemplatesFor(conceptName: String): Seq[LinkNode]

}
