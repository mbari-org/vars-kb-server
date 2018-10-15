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

/**
 * @author Brian Schlining
 * @since 2016-12-14T15:31:00
 */
class LinkApiSpec extends WebApiStack {

  private[this] val api = new LinkApi(daoFactory)
  private[this] val path = "/v1/links"

  addServlet(api, path)

  "LinkApi" should "find links for object" in {
    get(s"$path/object") {
      status should be(200)
      //println(body)
    }
  }

  it should "find links for Nanomia bijuga" in {
    get(s"$path/Nanomia%20bijuga") {
      status should be(200)
      //println(body)
    }
  }

  it should "find links for Nanomia bijuga named 'surface-color'" in {
    get(s"$path/Nanomia%20bijuga/using/surface-color") {
      status should be(200)
      //println(body)
    }

  }

}
