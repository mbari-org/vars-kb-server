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

import org.mbari.vars.kbserver.Constants

/**
 * @author Brian Schlining
 * @since 2016-12-14T14:47:00
 */
class ConceptApiSpec extends WebApiStack {

  private[this] val api = new ConceptApi(daoFactory)
  private[this] val path = "/v1/concept"
  addServlet(api, path)

  "ConceptApi" should "find root" in {
    get(s"$path/root") {
      status should be(200)
      body.contains("object") should be(true)
      //println(body)
    }
  }

  it should "find Nanomia bijuga by name" in {
    get(s"$path/Nanomia%20bijuga") {
      status should be(200)
      body.contains("Nanomia bijuga") should be(true)
      println(body)
    }
  }

  it should "find marine organism by name" in {
    get(s"$path/marine%20organism") {
      status should be(200)
      body.contains("marine organism") should be(true)
      //println(body)
    }
  }

  it should "find physical object by name" in {
    get(s"$path/physical%20object") {
      status should be(200)
      body.contains("physical object") should be(true)
      //println(body)
    }
  }

  it should "find all names as strings" in {
    get(s"$path") {
      status should be(200)
      val names = Constants.GSON.fromJson(body, classOf[Array[String]])
      names.size should be(5209)
      //println("=====> " + names.size)
    }
  }

}
