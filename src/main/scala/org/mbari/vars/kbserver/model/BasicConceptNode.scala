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

import org.mbari.vars.kbserver.dao.jdbc.generic.ImmutableConcept

/**
  * Use this class to return flattened conept branches. i.e. instead of a
  * hierarchical `up` call. We return an ordered list of the concepts from the root
  * down. This is easier to process for most scientists.
  * @param name
  * @param rank
  */
case class BasicConceptNode(name: String, rank: Option[String])

object BasicConceptNode {
  def flatten(root: ImmutableConcept): List[BasicConceptNode] = {
    def builder(node: ImmutableConcept, stack: List[BasicConceptNode] = Nil): List[BasicConceptNode] = {
      val next = apply(node) :: stack
      node.children.headOption match {
        case None => next
        case Some(c) => builder(c) ++ next
      }
    }
    builder(root).reverse
  }

  def apply(immutableConcept: ImmutableConcept): BasicConceptNode =
    BasicConceptNode(immutableConcept.name, immutableConcept.rank)

}
