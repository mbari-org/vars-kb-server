package org.mbari.vars.kbserver.dao.jdbc.generic

import java.sql.ResultSet
import java.time.Instant

import org.mbari.vars.kbserver.dao.jdbc.BaseDAO
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

/**
  * @author Brian Schlining
  * @since 2018-02-11T11:19:00
  */
class PhylogenyDAO2(connectionTestQuery: Option[String])
  extends BaseDAO(connectionTestQuery) {

  private[this] val log = LoggerFactory.getLogger(getClass)

  private[this] var lastUpdate = Instant.EPOCH
  private[this] var root: Option[ImmutableConcept] = None
  private[this] var mutableRoot: Option[MutableConcept] = None
  private[this] var allNames: Seq[String] = Nil

  def findUp(name: String)(implicit ec: ExecutionContext): Future[Option[ImmutableConcept]] = {
    Future {
      load()
      // Hide the children
      //  do branch walk
      if (allNames.contains(name)) {
        findMutableNode(name).map(_.copyUp())
          .map(_.root())
          .map(_.toImmutable())
      }
      else None
    }
  }

  def findDown(name: String)(implicit ec: ExecutionContext): Future[Option[ImmutableConcept]] = {
    Future {
      load()
      // Parent is't seen so we can walk down from this node
      if (allNames.contains(name)) {
        findNode(name)
      }
      else None
    }
  }

//  private def findBranch(name: String): Option[ImmutableConcept] = {
//    def isInPath(node:ImmutableConcept): Boolean =
//      node.containsName(name) || node.children.exists(isInPath)
//
//    def buildPath(node: ImmutableConcept, accum: Seq[ImmutableConcept] = Nil): Seq[ImmutableConcept] = {
//      if (isInPath(node)) {
//        val childInPath = node.children.filter(isInPath)
//        val newNode = node.copy(children = childInPath)
//        accum :+ newNode
//      }
//      else accum
//    }
//
//
//
//    root.map(r => buildPath(r))
//      .map(_.head)
//
//  }

  private def findNode(name: String,
                       nodes: Seq[ImmutableConcept] = root.map(Seq(_)).getOrElse(Nil)): Option[ImmutableConcept] = {

    nodes.find(_.containsName(name)) match {
      case Some(nn) => Some(nn)
      case None =>
        val children = nodes.flatMap(_.children)
        findNode(name, children)
    }

  }

  private def findMutableNode(name: String,
                       nodes: Seq[MutableConcept] = mutableRoot.map(Seq(_)).getOrElse(Nil)): Option[MutableConcept] = {
    val n = nodes.filter(_.names.map(_.name).contains(name)) match {
      case x :: xs => Some(x)
      case Nil =>
        val children = nodes.flatMap(_.children)
        findMutableNode(name, children)
    }
    n
  }

  private def load(): Unit = {
    val lastUpdateInDb = executeLastUpdateQuery()
    if (lastUpdateInDb.isAfter(lastUpdate)) {
      log.debug("Loading cache ...")
      val cache = executeQuery()
      if (cache.nonEmpty) {
        val lu = cache.maxBy(_.lastUpdate.toEpochMilli)
        lastUpdate = lu.lastUpdate
      }

      mutableRoot = MutableConcept.toTree(cache)

      root = mutableRoot.map(_.toImmutable())
      allNames = cache.map(_.name)
    }
  }



  private def executeLastUpdateQuery(): Instant =
    try {
      val connection = dataSource.getConnection()
      val preparedStatement = connection.prepareStatement(PhylogenyDAO2.LAST_UPDATE_SQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
      val resultSet = preparedStatement.executeQuery()
      val lastUpdate = if (resultSet.next()) resultSet.getTimestamp(1).toInstant
          else Instant.now()
      connection.close()
      lastUpdate
    } catch {
      case NonFatal(e) =>
        log.error("Failed to execute SQL", e)
        Instant.now()
    }

  private def executeQuery(): Seq[ConceptRow] =
    try {
      val connection = dataSource.getConnection()
      val preparedStatement = connection.prepareStatement(PhylogenyDAO2.SQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
      val resultSet = preparedStatement.executeQuery()
      val rows = new mutable.ArrayBuffer[ConceptRow]()
      while (resultSet.next()) {
        val id = resultSet.getLong(1)
        val pid = resultSet.getLong(2)
        val parentId = if (pid <= 0) None else Some(pid)
        //val parentId = Option(resultSet.getLong(2))
        val name = resultSet.getString(3)
        val rankLevel = Option(resultSet.getString(4))
        val rankName = Option(resultSet.getString(5))
        val nameType = resultSet.getString(6)
        val conceptTimestamp = resultSet.getTimestamp(7).toInstant
        val conceptNameTimestamp = resultSet.getTimestamp(8).toInstant
        val r = ConceptRow(id, parentId, name, rankLevel, rankName, nameType, conceptTimestamp, conceptNameTimestamp)
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

object PhylogenyDAO2 {
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
      |  CONCEPT AS C LEFT JOIN
      |  ConceptName AS cn ON cn.CONCEPTID_FK = C.ID
    """.stripMargin

  val LAST_UPDATE_SQL: String =
    """
      |SELECT
      |  MAX(c.mytime)
      |FROM
      |(SELECT
      |  MAX(LAST_UPDATED_TIME) AS mytime
      |FROM
      |  Concept
      |UNION
      |SELECT
      |  MAX(LAST_UPDATED_TIME) AS mytime
      |FROM
      |  ConceptName) as c
    """.stripMargin
}
