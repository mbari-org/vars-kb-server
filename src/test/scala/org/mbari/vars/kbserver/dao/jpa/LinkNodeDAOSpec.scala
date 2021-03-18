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


import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by brian on 11/30/16.
 */
class LinkNodeDAOSpec extends AnyFlatSpec with Matchers  with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = TestDatabase.init

  private[this] val daoFactory = new DefaultDAOFactory(TestDatabase.KB_DAO_FACTORY)
  private[this] val dao = daoFactory.newLinkNodeDAO()

  "LinkNodeDAO" should "findAllApplicableTo object" in {
    val ns = Await.result(dao.findAllApplicableTo("object"), TestDatabase.TIMEOUT)
    //println(Constants.GSON.toJson(ns.asJava))
    ns.size should be(63)
  }

  it should "findAllAppicableTo physical-object" in {
    val ns = Await.result(dao.findAllApplicableTo("physical-object"), TestDatabase.TIMEOUT)
    //println(Constants.GSON.toJson(ns.asJava))
    ns.size should be(315)
  }

  it should "findAllApplicableTo Nanomia bijuga" in {
    val ns = Await.result(dao.findAllApplicableTo("Nanomia bijuga"), TestDatabase.TIMEOUT)
    //println(Constants.GSON.toJson(ns.asJava))
    ns.size should be(612)
  }

}
