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
 * @since 2016-12-12T16:42:00
 */
class PhylogenyApiV1Spec extends WebApiStack {

  private[this] val api = new PhylogenyApiV1(daoFactory)
  private[this] val path = "/v1/phylogeny"
  addServlet(api, path)

  "PhylogenyApi" should "find ancestors" in {
    get(s"$path/up/Nanomia") {
      status should be(200)
      body.contains("Nanomia") should be(true)
      body.contains("Agalmatidae") should be(true)
      body.contains("Physonectae") should be(true)
      body.contains("Siphonophorae") should be(true)
      body.contains("Hydroidolina") should be(true)
      body.contains("Hydrozoa") should be(true)
      body.contains("Cnidaria") should be(true)
      body.contains("Animalia") should be(true)
      body.contains("Eukaryota") should be(true)
      //println(body)
    }
  }

  it should "find descendents" in {
    get(s"$path/down/Agalmatidae") {
      status should be(200)
      body.contains("Agalmatidae") should be(true)
      body.contains("Nanomia") should be(true)
      body.contains("Nanomia bijuga") should be(true)
      body.contains("Halistemma rubrum") should be(true)
      //println(body)
    }
  }

}
