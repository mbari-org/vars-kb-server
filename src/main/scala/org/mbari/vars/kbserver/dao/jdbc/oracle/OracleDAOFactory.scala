package org.mbari.vars.kbserver.dao.jdbc.oracle

import com.google.inject.Inject
import org.mbari.vars.kbserver.dao.PhylogenyDAO
import org.mbari.vars.kbserver.dao.jpa.DefaultDAOFactory
import vars.knowledgebase.KnowledgebaseDAOFactory

/**
  * @author Brian Schlining
  * @since 2018-02-09T13:02:00
  */
class OracleDAOFactory  @Inject() (knowledgebaseDAOFactory: KnowledgebaseDAOFactory)
  extends DefaultDAOFactory(knowledgebaseDAOFactory) {

  override def newPhylogenyDAO(): PhylogenyDAO = new PhylogenyDAOImpl(newConceptNodeDAO())

}