/*
 * Copyright 2017 Monterey Bay Aquarium Research Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mbari.vars.kbserver.dao.jdbc.oracle

import java.sql.ResultSet
import javax.inject.Inject

import org.mbari.vars.kbserver.dao.jdbc.BaseDAO
import org.mbari.vars.kbserver.dao.{ ConceptNodeDAO, PhylogenyDAO, PhylogenyRow }
import org.mbari.vars.kbserver.model.PhylogenyNode
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.control.NonFatal

/**
 * If needed test query is "SELECT 1 FROM DUAL"
 * @author Brian Schlining
 * @since 2018-02-09T13:03:00
 */
class PhylogenyDAOImpl @Inject() (conceptNodeDAO: ConceptNodeDAO)
    extends BaseDAO(None) with PhylogenyDAO {

  private[this] val log = LoggerFactory.getLogger(getClass)

  private[this] val upSql: String = readSQL(getClass.getResource("/sql/oracle/up.sql"))
  private[this] val downSql: String = readSQL(getClass.getResource("/sql/oracle/down.sql"))
  private[this] val singleSql: String = readSQL(getClass.getResource("/sql/oracle/single.sql"))

  // TODO Can abstarct out common founctions betwen oracle and sqlserver
  override def findUp(name: String)(implicit ec: ExecutionContext): Future[Option[PhylogenyNode]] =
    conceptNodeDAO.findByName(name) // We have to look up the primary name first
      .map({
        case None => None
        case Some(conceptNode) => safeExecuteQuery(upSql, conceptNode.name)
      })

  override def findDown(name: String)(implicit ec: ExecutionContext): Future[Option[PhylogenyNode]] =
    conceptNodeDAO.findByName(name) // We have to look up the primary name first
      .map({
        case None => None
        case Some(conceptNode) => safeExecuteQuery(downSql, conceptNode.name)
      })

  private def executeQuery(sql: String, name: String): Seq[PhylogenyRow] =
    try {

      //val psql = sql.replace("?", s"'$name'")

      val connection = dataSource.getConnection()
      //      val statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
      //      val resultSet = statement.executeQuery(psql)
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
      rows.toSeq
    } catch {
      case NonFatal(e) =>
        log.error("Failed to execute SQL", e)
        Nil
    }

  /**
   * Our fancy SQL will not return anything if you're at the top or bottom of
   * the hierarchy. As a workaround, if we look for a single matching node if
   * no matches are returned by the intial query
   * @param name
   * @return
   */
  private def findSingleNode(name: String): Option[PhylogenyNode] = {
    try {
      val connection = dataSource.getConnection()
      val preparedStatement = connection.prepareStatement(singleSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
      preparedStatement.setString(1, name)
      val resultSet = preparedStatement.executeQuery()
      val node = if (resultSet.next()) {
        val name = resultSet.getString(1)
        val rank = resultSet.getString(2)
        Option(PhylogenyNode(name, Option(rank)))
      } else None
      connection.close()
      node
    } catch {
      case NonFatal(e) =>
        log.error("Failed to execute SQL", e)
        None
    }

  }

  private def safeExecuteQuery(sql: String, name: String): Option[PhylogenyNode] = {
    val nodes = rowsToConceptNode(executeQuery(sql, name))
    if (nodes.isEmpty) findSingleNode(name)
    else nodes
  }

}

