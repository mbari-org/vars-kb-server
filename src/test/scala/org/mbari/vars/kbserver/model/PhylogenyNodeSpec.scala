package org.mbari.vars.kbserver.model

import org.scalatest.{ FlatSpec, Matchers }

/**
 * Created by brian on 11/29/16.
 */
class PhylogenyNodeSpec extends FlatSpec with Matchers {

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
