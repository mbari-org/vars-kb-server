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

package org.mbari.vars.kbserver.model

import vars.knowledgebase.Concept
import scala.collection.JavaConverters._

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T15:54:00
 */
case class ConceptNode(
    name: String,
    alternateNames: Seq[String],
    media: Seq[MediaNode],
    descriptors: Seq[LinkNode],
    rank: Option[String] = None,
    author: Option[String] = None
) {

}

object ConceptNode {

  def apply(concept: Concept): ConceptNode = {
    val name = concept.getPrimaryConceptName.getName

    val alternateNames = concept.getConceptNames.asScala.toSeq.map(_.getName).filter(_ != name)

    val media = concept.getConceptMetadata.getMedias.asScala.toSeq.map(MediaNode(_))

    val descriptors = concept.getConceptMetadata.getLinkRealizations.asScala.toSeq.map(LinkNode(_))

    val rankLevel = concept.getRankLevel
    val rankName = concept.getRankName
    val rank: String =
      if (rankLevel == null && rankName == null) null
      else if (rankLevel == null) rankName
      else rankLevel + rankName

    val author = Option(concept.getPrimaryConceptName.getAuthor)

    ConceptNode(name, alternateNames, media, descriptors, Option(rank), author)
  }
}
