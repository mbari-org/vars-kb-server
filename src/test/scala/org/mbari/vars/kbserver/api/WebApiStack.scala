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

package org.mbari.vars.kbserver.api

import java.util.concurrent.TimeUnit

import org.mbari.vars.kbserver.Constants
import org.mbari.vars.kbserver.dao.jpa.{ DefaultDAOFactory, TestDatabase }
import org.scalatest.BeforeAndAfterAll
import org.scalatra.test.scalatest.ScalatraFlatSpec

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{ Duration => SDuration }
import com.google.gson.Gson
import org.mbari.vars.kbserver.dao.DAOFactory

/**
 * @author Brian Schlining
 * @since 2016-12-12T16:43:00
 */
trait WebApiStack extends ScalatraFlatSpec with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    TestDatabase.init
  }

  protected[this] val gson: Gson = Constants.GSON
  protected[this] val daoFactory: DAOFactory = new DefaultDAOFactory(TestDatabase.KB_DAO_FACTORY)
  protected[this] implicit val executionContext: ExecutionContext = ExecutionContext.global
  protected[this] val timeout: SDuration = SDuration(3000, TimeUnit.MILLISECONDS)

}
