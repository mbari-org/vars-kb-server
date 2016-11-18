package org.mbari.vars.kbserver.dao

import org.mbari.vars.kbserver.model.PhylogenyNode

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T13:45:00
 */
trait PhylogenyDAO {

  def findUp(name: String): Option[PhylogenyNode]

  def findDown(name: String): Option[PhylogenyNode]

}
