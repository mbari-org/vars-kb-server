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

//import com.typesafe.config.ConfigFactory
import org.mbari.vars.kbserver.Constants
import org.mbari.vars.kbserver.dao.jpa.TestDatabase
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Await
//import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author Brian Schlining
 * @since 2018-02-14T18:54:00
 */
class FastPhylogenyDAOSpec extends AnyFlatSpec with Matchers  with BeforeAndAfterAll {

  //private[this] val config = ConfigFactory.load()

  override protected def beforeAll(): Unit = TestDatabase.init

  protected val dao = new FastPhylogenyDAO(Some("SELECT * FROM UniqueID"))
  // Try(config.getString("org.mbari.vars.knowledgebase.development.hikari.test")).toOption)

  "PhylogenyDAO2" should "find phylogeny for Nanomia bijuga" in {
    val cn = Await.result(dao.findUp("Nanomia bijuga"), TestDatabase.TIMEOUT)
    cn should not be (empty)
    println(Constants.GSON.toJson(cn.get))
  }

  it should "find children of Agalmatidae" in {
    val cn = Await.result(dao.findDown("Agalmatidae"), TestDatabase.TIMEOUT)
    cn should not be (empty)
    println(Constants.GSON.toJson(cn.get))
  }

}
