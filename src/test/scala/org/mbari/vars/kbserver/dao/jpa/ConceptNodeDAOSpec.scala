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

import org.scalatest.{ BeforeAndAfterAll, FlatSpec, Matchers }

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by brian on 11/29/16.
 */
class ConceptNodeDAOSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = TestDatabase.init

  private[this] val daoFactory = new DefaultDAOFactory(TestDatabase.KB_DAO_FACTORY)
  private[this] val dao = daoFactory.newConceptNodeDAO()

  "ConceptNodeDAO" should "findRoot" in {
    val root = Await.result(dao.findRoot(), TestDatabase.TIMEOUT)
    root should not be empty
    root.get.name should be("object")
  }

  it should "findByName" in {
    val name = "Nanomia bijuga"
    val c = Await.result(dao.findByName(name), TestDatabase.TIMEOUT)
    c should not be empty
    c.get.name should be(name)
  }

  it should "findAllNames" in {
    val names = Await.result(dao.findAllNames(), TestDatabase.TIMEOUT)
    names.size should be(5209)
    names should contain("Nanomia bijuga")
  }

}
