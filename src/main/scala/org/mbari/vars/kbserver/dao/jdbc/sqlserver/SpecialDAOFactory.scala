package org.mbari.vars.kbserver.dao.jdbc.sqlserver

import com.google.inject.Inject
import org.mbari.vars.kbserver.dao.jpa.DefaultDAOFactory
import org.mbari.vars.kbserver.dao.PhylogenyDAO
import vars.knowledgebase.KnowledgebaseDAOFactory

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-21T13:47:00
 */
class SpecialDAOFactory @Inject() (knowledgebaseDAOFactory: KnowledgebaseDAOFactory)
    extends DefaultDAOFactory(knowledgebaseDAOFactory) {

  override def newPhylogenyDAO(): PhylogenyDAO = new PhylogenyDAOImpl(newConceptNodeDAO())

}
