package org.mbari.vars.kbserver.dao

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T13:45:00
 */
trait DAOFactory {

  def newPhylogenyDAO(): PhylogenyDAO

  def newConceptNodeDAO(): ConceptNodeDAO

  def newLinkNodeDAO(): LinkNodeDAO

}
