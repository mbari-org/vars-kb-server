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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers


/**
 * Created by brian on 11/29/16.
 */
class PhylogenyNodeSpec extends AnyFlatSpec with Matchers {

  val root = PhylogenyNode("A")
  val b1 = PhylogenyNode("B1", parent = Some(root))
  root.children.add(b1)
  val b2 = PhylogenyNode("B2", parent = Some(root))
  root.children.add(b2)
  val c = PhylogenyNode("C", parent = Some(b1))
  b1.children.add(c)

  "PhylogenyNode" should "find subnode" in {

    val sub = root.subnode("B2")
    sub should not be empty
    sub.get should be(b2)
  }

  it should "find root" in {
    c.root should be(root)
  }
}
