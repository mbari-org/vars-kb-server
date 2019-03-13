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
 *
 *
 * @author Brian Schlining
 * @since 2016-11-22T16:49:00
 */
class PhylogenyDAOSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = TestDatabase.init

  private[this] val daoFactory = new DefaultDAOFactory(TestDatabase.KB_DAO_FACTORY)
  private[this] val dao = daoFactory.newFastPhylogenyDAO()

  "PhylogenyDAOImpl" should "find phylogeny for Nanomia bijuga" in {
    val cn = Await.result(dao.findUp("Nanomia bijuga"), TestDatabase.TIMEOUT)
    cn should not be (empty)
    //println(Constants.GSON.toJson(cn.get))
  }

  it should "find children of Agalmatidae" in {
    val cn = Await.result(dao.findDown("Agalmatidae"), TestDatabase.TIMEOUT)
    cn should not be (empty)
    //println(Constants.GSON.toJson(cn.get))
  }

}
