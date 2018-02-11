package org.mbari.vars.kbserver.dao.jpa


import com.google.inject.Inject
import org.mbari.vars.kbserver.Constants
import org.mbari.vars.kbserver.dao.cached.CachedConceptNodeDAOImpl
import org.mbari.vars.kbserver.dao.jdbc.generic.PhylogenyDAO2
import org.mbari.vars.kbserver.dao.{ConceptNodeDAO, DAOFactory, LinkNodeDAO, PhylogenyDAO}
import vars.knowledgebase.KnowledgebaseDAOFactory

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
    new CachedConceptNodeDAOImpl(new ConceptNodeDAOImpl(knowledgebaseDAOFactory))

  override def newLinkNodeDAO(): LinkNodeDAO =
    new LinkNodeDAOImpl(knowledgebaseDAOFactory)

  override def newPhylogenyDAO2(): PhylogenyDAO2 =
    new PhylogenyDAO2(Constants.DB_PARAMS.testQuery)
}
