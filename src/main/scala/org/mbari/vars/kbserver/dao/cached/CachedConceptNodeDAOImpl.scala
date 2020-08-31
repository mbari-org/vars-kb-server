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

package org.mbari.vars.kbserver.dao.cached

import java.util.concurrent.TimeUnit

import com.github.benmanes.caffeine.cache.{Cache, Caffeine}
import org.mbari.vars.kbserver.dao.ConceptNodeDAO
import org.mbari.vars.kbserver.model.ConceptNode

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Brian Schlining
  * @since 2017-10-23T09:41:00
  */
class CachedConceptNodeDAOImpl(val proxied: ConceptNodeDAO) extends ConceptNodeDAO {

  import CachedConceptNodeDAOImpl._

  val nameCache: Cache[String, ConceptNode] = Caffeine
    .newBuilder()
    .expireAfterWrite(15, TimeUnit.MINUTES)
    .build[String, ConceptNode]()

  val allNamesCache = Caffeine
    .newBuilder()
    .expireAfterWrite(15, TimeUnit.MINUTES)
    .build[String, Seq[String]]()

  override def findByName(
      name: String
  )(implicit ec: ExecutionContext): Future[Option[ConceptNode]] = {
    Option(nameCache.getIfPresent(name)) match {
      case Some(node) => Future(Some(node))
      case None =>
        val future = proxied.findByName(name)
        future.foreach({
          case Some(conceptNode) => nameCache.put(name, conceptNode)
          case None              => // Do nothing
        })
        future
    }
  }

  override def findAllNames()(implicit ec: ExecutionContext): Future[Seq[String]] = {
    val allNames = Option(allNamesCache.getIfPresent(AllNamesCacheKey))
    if (allNames.isDefined && allNames.get.nonEmpty) {
      Future(allNames.get)
    }
    else {
      val future = proxied.findAllNames()
      future.foreach(names => allNamesCache.put(AllNamesCacheKey, names))
      future
    }
  }

  override def findRoot()(implicit ec: ExecutionContext): Future[Option[ConceptNode]] = {
    proxied.findRoot()
  }

  override def findParent(name: String)(
      implicit ec: ExecutionContext
  ): Future[Option[ConceptNode]] = proxied.findParent(name)

  def findChildren(name: String)(implicit ec: ExecutionContext): Future[Seq[ConceptNode]] =
    proxied.findChildren(name)
}

object CachedConceptNodeDAOImpl {
  val AllNamesCacheKey = "all-names"
}
