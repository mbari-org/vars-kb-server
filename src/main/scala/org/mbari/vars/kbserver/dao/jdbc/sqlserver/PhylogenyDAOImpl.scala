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
class PhylogenyDAOImpl extends BaseDAO with PhylogenyDAO {

  private[this] val log = LoggerFactory.getLogger(getClass)

  private[this] val upSql = readSQL(getClass.getResource("/sql/sqlserver/up.sql"))
  private[this] val downSql = readSQL(getClass.getResource("/sql/sqlserver/down.sql"))

  override def findUp(name: String)(implicit ec: ExecutionContext): Future[Option[PhylogenyNode]] =
    Future(rowsToConceptNode(executeQuery(upSql, name)))

  override def findDown(name: String)(
    implicit
    ec: ExecutionContext
  ): Future[Option[PhylogenyNode]] =
    Future(rowsToConceptNode(executeQuery(downSql, name)))

  private def executeQuery(sql: String, name: String): Seq[PhylogenyRow] =
    try {
      val connection = dataSource.getConnection()
      val preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY)
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
      rows
    } catch {
      case NonFatal(e) =>
        log.error("Failed to execute SQL", e)
        Nil
    }

}
