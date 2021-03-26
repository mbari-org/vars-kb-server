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

import org.mbari.vars.kbserver.dao.DAOFactory
import org.mbari.vars.kbserver.model.Msg
import org.scalatra.BadRequest

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

class DeepSeaGuideApi(daoFactory: DAOFactory)(implicit val executor: ExecutionContext) extends ApiStack {

  before() {
    contentType = "application/json"
    response.headers.set("Access-Control-Allow-Origin", "*")
  }

  get("/images/representative/:name") {
    val name = params
      .get("name")
      .getOrElse(halt(BadRequest(toJson(Msg("Please provide a term to look up")))))
    val count = params.get("limit").map(_.toInt).getOrElse(10)
    val dao = daoFactory.newDeepSeaGuideDAO()
    dao.findRepresentativeImages(name, count)
      .map(_.asJava)
      .map(toJson)
  }

  get("/test") {
    toJson(Msg("Worked", 200))
  }


}
