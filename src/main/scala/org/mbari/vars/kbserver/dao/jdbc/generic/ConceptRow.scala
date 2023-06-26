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

import java.time.Instant

import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
 * @author Brian Schlining
 * @since 2018-02-11T11:34:00
 */
case class ConceptRow(
    id: Long,
    parentId: Option[Long],
    name: String,
    rankLevel: Option[String],
    rankName: Option[String],
    nameType: String,
    conceptTimestamp: Instant,
    conceptNameTimestamp: Instant
) {

  lazy val rank: Option[String] = rankName.map(n => rankLevel.getOrElse("") + n)

  lazy val lastUpdate: Instant = Seq(conceptTimestamp, conceptNameTimestamp)
    .maxBy(i => i.toEpochMilli)

}

case class CName(name: String, nameType: String)

class MutableConcept {
  var id: Option[Long] = None
  var parent: Option[MutableConcept] = None
  var rank: Option[String] = None
  var names: Seq[CName] = Nil
  var children: Seq[MutableConcept] = Nil

  def copyUp(): MutableConcept = copyUp(Nil)

  private def copyUp(newChildren: Seq[MutableConcept]): MutableConcept = {
    val mc = new MutableConcept
    mc.id = id
    mc.rank = rank
    mc.names = names
    mc.children = newChildren
    mc.parent = parent.map(_.copyUp(Seq(mc)))
    mc
  }

  def toImmutable(): ImmutableConcept = {
    //println(s"${this.id} - ${this.names}")
    val primaryName = names.find(_.nameType.equalsIgnoreCase("primary"))
      .getOrElse(names.head)
    val alternativeNames = names.filter(!_.eq(primaryName))
    ImmutableConcept(
      primaryName.name,
      rank,
      alternativeNames.map(_.name),
      children.map(_.toImmutable())
    )
  }

  def root(): MutableConcept = parent match {
    case None => this
    case Some(p) => p.root()
  }

}

object MutableConcept {

  def newParent(parentId: Long): MutableConcept = {
    val mc = new MutableConcept
    mc.id = Some(parentId)
    mc
  }

  // TODO return the nodes too!!
  def toTree(rows: Seq[ConceptRow]): (Option[MutableConcept], Seq[MutableConcept]) = {
    val nodes = new mutable.ArrayBuffer[MutableConcept]
    for (row <- rows) {

      /*
        Find an existing parent or create one as needed
       */
      val parentOpt = row.parentId.map(parentId =>
        nodes.find(_.id.getOrElse(-1L) == parentId)
          .getOrElse({
            val mc = newParent(parentId)
            nodes += mc
            mc
          }))

      if (parentOpt.isEmpty) {
        LoggerFactory.getLogger(getClass).info(s"No Parent found for $row")
      }

      /*
        Find the existing concept or create one if needed
       */
      val concept = nodes.find(_.id.getOrElse(-1L) == row.id) match {
        case None =>
          val mc = new MutableConcept
          mc.id = Some(row.id)
          nodes += mc
          mc
        case Some(mc) => mc
      }

      // Set the parent of the concept!!
      parentOpt.foreach(parent => {
        concept.parent = parentOpt
        if (!parent.children.contains(concept)) {
          parent.children = parent.children :+ concept
        }
      })

      val cn = CName(row.name, row.nameType)
      concept.rank = row.rank
      concept.names = concept.names :+ cn

    }
    val root = nodes.find(_.parent.isEmpty)
    (root, nodes.toSeq)
  }

}

case class ImmutableConcept(
    name: String,
    rank: Option[String],
    alternativeNames: Seq[String],
    children: Seq[ImmutableConcept]
) {
  def containsName(n: String): Boolean = name.equals(n) ||
    alternativeNames.contains(n)
}

