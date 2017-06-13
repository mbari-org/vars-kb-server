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
    rank: Option[String] = None
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

    ConceptNode(name, alternateNames, media, descriptors, Option(rank))
  }
}
