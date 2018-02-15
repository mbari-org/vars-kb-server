package org.mbari.vars.kbserver.dao.jdbc.generic

import java.sql.ResultSet
import java.time.Instant

import org.mbari.vars.kbserver.dao.jdbc.BaseDAO
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.control.NonFatal

/**
  * @author Brian Schlining
  * @since 2018-02-11T11:19:00
  */
class FastPhylogenyDAO(connectionTestQuery: Option[String])
  extends BaseDAO(connectionTestQuery) {

  private[this] val log = LoggerFactory.getLogger(getClass)

  private[this] var lastUpdate = Instant.EPOCH
  private[this] var rootNode: Option[MutableConcept] = None
  private[this] var allNodes: Seq[MutableConcept] = Nil

  def findUp(name: String)(implicit ec: ExecutionContext): Future[Option[ImmutableConcept]] = {
    Future {
      load()
      // Hide the children
      //  do branch walk
      findMutableNode(name)
        .map(_.copyUp())
        .map(_.root())
        .map(_.toImmutable())
    }
  }

  def findDown(name: String)(implicit ec: ExecutionContext): Future[Option[ImmutableConcept]] = {
    Future {
      load()
      // Parent is't seen so we can walk down from this node
      val mc = findMutableNode(name)
      mc.map(_.toImmutable())
    }
  }

  private def findMutableNode(name: String): Option[MutableConcept] =
    allNodes.find(_.names.map(_.name).contains(name))

  private def load(): Unit = {
    val lastUpdateInDb = executeLastUpdateQuery()
    if (lastUpdateInDb.isAfter(lastUpdate)) {
      log.debug("Loading cache ...")
      val cache = executeQuery()
      if (cache.nonEmpty) {
        val lu = cache.maxBy(_.lastUpdate.toEpochMilli)
        lastUpdate = lu.lastUpdate
      }

      val r = MutableConcept.toTree(cache)
      rootNode = r._1
      allNodes = r._2

    }
  }



  private def executeLastUpdateQuery(): Instant =
    try {
      val connection = dataSource.getConnection()
      val preparedStatement = connection.prepareStatement(FastPhylogenyDAO.LAST_UPDATE_SQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
      val resultSet = preparedStatement.executeQuery()
      val lastUpdate = if (resultSet.next()) resultSet.getTimestamp(1).toInstant
          else Instant.now()
      connection.close()
      lastUpdate
    } catch {
      case NonFatal(e) =>
        log.error(s"Failed to execute SQL: ${FastPhylogenyDAO.LAST_UPDATE_SQL}", e)
        Instant.now()
    }

  private def executeQuery(): Seq[ConceptRow] =
    try {
      val connection = dataSource.getConnection()
      val preparedStatement = connection.prepareStatement(FastPhylogenyDAO.SQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
      val resultSet = preparedStatement.executeQuery()
      val rows = new mutable.ArrayBuffer[ConceptRow]()
      while (resultSet.next()) {
        val id = resultSet.getLong(1)
        val pid = resultSet.getLong(2)
        val parentId = if (pid <= 0) None else Some(pid)
        val name = resultSet.getString(3)
        val rankLevel = Option(resultSet.getString(4))
        val rankName = Option(resultSet.getString(5))
        val nameType = resultSet.getString(6)
        val conceptTimestamp = Try(resultSet.getTimestamp(7).toInstant).getOrElse(Instant.EPOCH)
        val conceptNameTimestamp = Try(resultSet.getTimestamp(8).toInstant).getOrElse(Instant.EPOCH)
        val r = ConceptRow(id, parentId, name, rankLevel, rankName, nameType, conceptTimestamp, conceptNameTimestamp)
        rows += r
      }
      connection.close()
      rows
    } catch {
      case NonFatal(e) =>
        log.error(s"Failed to execute SQL: ${FastPhylogenyDAO.SQL}", e)
        Nil
    }
}

object FastPhylogenyDAO {
  val SQL: String =
    """
      |SELECT
      |  c.ID,
      |  c.PARENTCONCEPTID_FK,
      |  cn.CONCEPTNAME,
      |  c.RANKLEVEL,
      |  c.RANKNAME,
      |  cn.NAMETYPE,
      |  c.LAST_UPDATED_TIME AS concept_timestamp,
      |  cn.LAST_UPDATED_TIME AS conceptname_timestamp
      |FROM
      |  CONCEPT C LEFT JOIN
      |  ConceptName cn ON cn.CONCEPTID_FK = C.ID
      |WHERE
      | cn.CONCEPTNAME IS NOT NULL
    """.stripMargin

  val LAST_UPDATE_SQL: String =
    """
      |SELECT
      |  MAX(t.mytime)
      |FROM
      |(SELECT
      |  MAX(LAST_UPDATED_TIME) AS mytime
      |FROM
      |  Concept
      |UNION
      |SELECT
      |  MAX(LAST_UPDATED_TIME) AS mytime
      |FROM
      |  ConceptName) t
    """.stripMargin
}
