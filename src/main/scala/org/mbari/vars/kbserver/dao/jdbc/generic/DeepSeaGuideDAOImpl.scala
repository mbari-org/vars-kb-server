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

package org.mbari.vars.kbserver.dao.jdbc.generic

import org.mbari.vars.kbserver.dao.{DAOFactory, DeepSeaGuideDAO}
import org.mbari.vars.kbserver.dao.jdbc.BaseDAO
import org.mbari.vars.kbserver.model.{BasicConceptNode, MediaNode, NamedMedia}
import org.slf4j.LoggerFactory

import java.net.URL
import java.sql.ResultSet
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
import scala.util.control.NonFatal

class DeepSeaGuideDAOImpl(daoFactory: DAOFactory, connectionTestQuery: Option[String])
  extends BaseDAO(connectionTestQuery) with DeepSeaGuideDAO {

  private[this] val log = LoggerFactory.getLogger(getClass)

  override def findRepresentativeImages(concept: String, count: Int)(implicit ec: ExecutionContext): Future[Seq[NamedMedia]] = {
    val conceptNodeDao = daoFactory.newConceptNodeDAO()
    val future = conceptNodeDao.findByName(concept).map {
      case None => Future(Nil)
      case Some(cn) =>
        if (cn.media.nonEmpty) Future(cn.media.map(m => NamedMedia(concept, m)))
        else {
          daoFactory.newFastPhylogenyDAO()
            .findDown(concept)
            .map {
              case None => Nil
              case Some(tree) =>
                val descendants = BasicConceptNode.taxa(tree).map(_.name)
                val subsample = Random.shuffle(descendants).take(1000)
                val allImages = findAllDescendantImages(subsample)
                selectRandomImages(allImages, count)
            }
        }
    }
    future.flatten

  }

  private def selectRandomImages(images: Seq[NamedMedia], count: Int): Seq[NamedMedia] = {
    if (images.isEmpty) Nil
    else if (images.length <= count) images
    else Random.shuffle(images).take(count)
  }

  def findAllDescendantImages(concepts: Seq[String]): Seq[NamedMedia] = {
    val inClause = concepts
      .map(s => s"'$s'")
      .mkString("(", ",", ")")
    val sql = DeepSeaGuideDAOImpl.RepresentativeImageSql.replace("???", inClause)
    log.debug(s"Using SQL: $sql")

    try {
      val connection = dataSource.getConnection()
      val statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
      val resultSet = statement.executeQuery(sql)
      val media = new mutable.ArrayBuffer[NamedMedia]()
      while (resultSet.next()) {
        val name = resultSet.getString(1)
        val url = new URL(resultSet.getString(2))
        val caption = Option(resultSet.getString(3)).getOrElse("")
        val credit = Option(resultSet.getString(4)).getOrElse("")
        val mediaType = MediaNode.resolveMimeType(resultSet.getString(5), url.toExternalForm)
        val m = NamedMedia(name, url, caption, credit, mediaType)
        media += m
      }
      connection.close()
      media.toSeq
    }
    catch {
      case NonFatal(e) =>
        log.error(s"Failed to execute SQL: $sql", e)
        Nil
    }
  }
}


object DeepSeaGuideDAOImpl {
  val RepresentativeImageSql: String =
    """SELECT
      |    cn.ConceptName,
      |    m.Url,
      |    m.Caption,
      |    m.Credit,
      |    m.MediaType
      |FROM
      |    ConceptName cn RIGHT JOIN
      |    Concept c ON cn.ConceptID_FK = c.id LEFT JOIN
      |    ConceptDelegate cd ON cd.ConceptID_FK = c.id LEFT JOIN
      |    Media m ON m.ConceptDelegateID_FK = cd.id
      |WHERE
      |    cn.ConceptName IN ??? AND
      |    m.Url IS NOT NULL AND
      |    m.MediaType = 'Image'
      |""".stripMargin
}