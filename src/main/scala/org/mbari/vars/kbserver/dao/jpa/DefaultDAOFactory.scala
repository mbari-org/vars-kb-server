package org.mbari.vars.kbserver.dao.jpa

import javax.persistence.{EntityManagerFactory, Persistence}

import com.typesafe.config.ConfigFactory
import org.mbari.vars.kbserver.Constants
import org.mbari.vars.kbserver.dao.{ConceptNodeDAO, DAOFactory, LinkNodeDAO, PhylogenyDAO}
import vars.knowledgebase.KnowledgebaseDAOFactory
import vars.knowledgebase.jpa.KnowledgebaseDAOFactoryImpl

import scala.collection.JavaConverters._

/**
  *
  *
  * @author Brian Schlining
  * @since 2016-11-18T11:36:00
  */
class DefaultDAOFactory extends DAOFactory {

  override def newPhylogenyDAO(): PhylogenyDAO =
    Constants.GUICE_INJECTOR.getInstance(classOf[PhylogenyDAO])

  override def newConceptNodeDAO(): ConceptNodeDAO =
    Constants.GUICE_INJECTOR.getInstance(classOf[ConceptNodeDAO])

  override def newLinkNodeDAO(): LinkNodeDAO =
    Constants.GUICE_INJECTOR.getInstance(classOf[LinkNodeDAO])
}
