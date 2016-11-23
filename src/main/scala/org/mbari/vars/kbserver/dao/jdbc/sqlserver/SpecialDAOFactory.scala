package org.mbari.vars.kbserver.dao.jdbc.sqlserver

import org.mbari.vars.kbserver.dao.jpa.DefaultDAOFactory
import org.mbari.vars.kbserver.dao.{PhylogenyDAO}

/**
  *
  *
  * @author Brian Schlining
  * @since 2016-11-21T13:47:00
  */
class SpecialDAOFactory extends DefaultDAOFactory {

  override def newPhylogenyDAO(): PhylogenyDAO = new PhylogenyDAOImpl

}
