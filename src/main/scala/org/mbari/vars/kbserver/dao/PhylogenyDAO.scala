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

package org.mbari.vars.kbserver.dao

import org.mbari.vars.kbserver.model.PhylogenyNode

import scala.collection.mutable
import scala.concurrent.{ ExecutionContext, Future }

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T13:45:00
 */
trait PhylogenyDAO {

  def findUp(name: String)(implicit ec: ExecutionContext): Future[Option[PhylogenyNode]]

  def findDown(name: String)(implicit ec: ExecutionContext): Future[Option[PhylogenyNode]]

  protected def rowsToConceptNode(rows: Seq[PhylogenyRow]): Option[PhylogenyNode] = {
    val nodes = new mutable.ArrayBuffer[PhylogenyNode]
    for (row <- rows) {
      val parent = nodes
        .find(n => n.name == row.parentName)
        .getOrElse({
          val node = PhylogenyNode(row.parentName, Option(row.parentRank), None)
          nodes += node
          node
        })

      nodes.find(_.name == row.childName) match {
        case None =>
          val child = PhylogenyNode(row.childName, Option(row.childRank), Option(parent))
          nodes += child
          parent.children += child
        case Some(child) =>
          val newChild = child.copy(parent = Option(parent))
          nodes -= child
          nodes += newChild
          parent.children -= child
          parent.children += newChild
      }

    }

    nodes.find(_.parent.isEmpty)

  }

}

case class PhylogenyRow(
  parentId: Long,
  parentName: String,
  parentRank: String,
  childId: Long,
  childName: String,
  childRank: String
)

