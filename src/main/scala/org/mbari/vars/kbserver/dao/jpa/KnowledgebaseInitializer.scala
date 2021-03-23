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

package org.mbari.vars.kbserver.dao.jpa

import java.io.{IOException, InputStream}
import java.util
import java.util.Scanner
import com.google.gson.{FieldNamingPolicy, GsonBuilder}
import org.mbari.vars.kbserver.Constants
import org.slf4j.LoggerFactory

import scala.jdk.CollectionConverters._
import org.mbari.kb.core.knowledgebase.{Concept, KnowledgebaseDAOFactory}
import org.mbari.kb.jpa.gson.{AnnotatedFieldExclusionStrategy, ConceptSerializer, UnderscoreFieldExclusionStrategy}
import org.mbari.kb.jpa.knowledgebase.ConceptImpl

import scala.util.Try

/**
 * @author Brian Schlining
 * @since 2018-01-08T13:26:00
 */
class KnowledgebaseInitializer(daoFactory: KnowledgebaseDAOFactory) {

  private[this] val log = LoggerFactory.getLogger(getClass)

  /**
   * Reads a raw JSON dump of the database and returns the concept tree
   * built from the JSON.
   *
   * @param inputStream The source to read JSON from
   * @throws IOException when bad things happen
   * @return The concept tree build from the JSON.
   */
  @throws[IOException]
  def read(inputStream: InputStream): Concept = {
    val scanner = new Scanner(inputStream).useDelimiter("\\A")
    val content = if (scanner.hasNext) scanner.next
    else ""
    read(content)
  }

  def read(json: String): Concept = {
    val root = KnowledgebaseInitializer.GSON.fromJson(json, classOf[ConceptImpl])
    fixRelationships(root)
    root
  }

  /**
   * Overwrites the existing knowledgebase with the concept you provide
   * as the new root concept
   * @param concept
   */
  def persist(concept: Concept): Unit = {
    val allowInit = Try(Constants.CONFIG.getBoolean("database.allow.init"))
      .getOrElse(false)
    if (allowInit) {
      log.warn("Overwriting existing knowledgebase!!")
      val dao = daoFactory.newConceptDAO()
      dao.startTransaction()
      val root = dao.findRoot()
      if (root != null) {
        dao.cascadeRemove(root)
      }
      dao.endTransaction()
      dao.startTransaction()
      dao.persist(concept)
      dao.endTransaction()
      dao.close()
    } else {
      log.warn("An attempt to overwrite the knowledgebase occurred. The database is not configured to allow this!!!")
    }
  }

  /**
   * GSON does not correctly set 2-way relationships so we have to fix those here.
   *
   * @param concept
   */
  private def fixRelationships(concept: Concept): Unit = {

    val metadata = concept.getConceptMetadata

    concept.asInstanceOf[ConceptImpl].setConceptMetadata(metadata)

    val medias = new util.ArrayList(metadata.getMedias)
    for (m <- medias.asScala) {
      metadata.removeMedia(m)
      metadata.addMedia(m)
    }

    val linkTemplates = new util.ArrayList(metadata.getLinkTemplates)
    for (lt <- linkTemplates.asScala) {
      metadata.removeLinkTemplate(lt)
      metadata.addLinkTemplate(lt)
    }

    val linkRealizations = new util.ArrayList(metadata.getLinkRealizations)
    for (lr <- linkRealizations.asScala) {
      metadata.removeLinkRealization(lr)
      metadata.addLinkRealization(lr)
    }

    val children = new util.ArrayList(concept.getChildConcepts)
    for (child <- children.asScala) {
      concept.removeChildConcept(child)
      concept.addChildConcept(child)
      fixRelationships(child)
    }

    val names = new util.ArrayList(concept.getConceptNames)
    for (name <- names.asScala) {
      concept.removeConceptName(name)
      concept.addConceptName(name)
    }
  }

}

object KnowledgebaseInitializer {
  def GSON = new GsonBuilder()
    .setExclusionStrategies(new UnderscoreFieldExclusionStrategy, new AnnotatedFieldExclusionStrategy)
    .registerTypeAdapter(classOf[ConceptImpl], new ConceptSerializer)
    .setPrettyPrinting()
    .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    .create()
}
