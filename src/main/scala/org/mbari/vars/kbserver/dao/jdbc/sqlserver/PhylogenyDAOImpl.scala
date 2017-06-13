package org.mbari.vars.kbserver.dao.jdbc.sqlserver

import java.sql.ResultSet

import org.mbari.vars.kbserver.dao.{ PhylogenyDAO, PhylogenyRow }
import org.mbari.vars.kbserver.model.PhylogenyNode
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.control.NonFatal

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T13:46:00
 */
object PhylogenyDAOImpl extends BaseDAO with PhylogenyDAO {

  val UP_SQL = BaseDAO.readSQL(getClass.getResource("/sql/sqlserver/up.sql"))
  val DOWN_SQL = BaseDAO.readSQL(getClass.getResource("/sql/sqlserver/down.sql"))

  private[this] val log = LoggerFactory.getLogger(getClass)


  override def findUp(name: String)(implicit ec: ExecutionContext): Future[Option[PhylogenyNode]] =
    Future(rowsToConceptNode(executeQuery(PhylogenyDAOImpl.UP_SQL, name)))

  override def findDown(name: String)(implicit ec: ExecutionContext): Future[Option[PhylogenyNode]] =
    Future(rowsToConceptNode(executeQuery(PhylogenyDAOImpl.DOWN_SQL, name)))

  private def executeQuery(sql: String, name: String): Seq[PhylogenyRow] =
    try {
      // TODO look up primary name in case name is alternate
      val connection = dataSource.getConnection()
      val preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
      preparedStatement.setString(1, name)
      val resultSet = preparedStatement.executeQuery()
      val rows = new mutable.ArrayBuffer[PhylogenyRow]()
      while (resultSet.next()) {
        val parentId = resultSet.getLong(1)
        val parentName = resultSet.getString(2)
        val parentRank = resultSet.getString(3)
        val childId = resultSet.getLong(4)
        val childName = resultSet.getString(5)
        val childRank = resultSet.getString(6)
        val r = PhylogenyRow(parentId, parentName, parentRank, childId, childName, childRank)
        rows += r
      }
      connection.close()
      rows
    } catch {
      case NonFatal(e) =>
        log.error("Failed to execute SQL", e)
        Nil
    }



}
