package org.mbari.vars.kbserver.dao.jpa


import com.google.inject.Inject
import org.mbari.vars.kbserver.dao.{ ConceptNodeDAO, DAOFactory, LinkNodeDAO, PhylogenyDAO }
import vars.knowledgebase.KnowledgebaseDAOFactory

import scala.collection.JavaConverters._

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-18T11:36:00
 */
class DefaultDAOFactory @Inject() (knowledgebaseDAOFactory: KnowledgebaseDAOFactory)
    extends DAOFactory {

  override def newPhylogenyDAO(): PhylogenyDAO =
    new PhylogenyDAOImpl(knowledgebaseDAOFactory)

  override def newConceptNodeDAO(): ConceptNodeDAO =
    new ConceptNodeDAOImpl(knowledgebaseDAOFactory)

  override def newLinkNodeDAO(): LinkNodeDAO =
    new LinkNodeDAOImpl(knowledgebaseDAOFactory)
}
