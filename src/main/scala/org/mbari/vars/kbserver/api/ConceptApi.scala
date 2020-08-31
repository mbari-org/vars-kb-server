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
import org.mbari.vars.kbserver.dao.DAOFactory
import org.scalatra.{BadRequest, NotFound}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

/**
  * @author Brian Schlining
  * @since 2016-12-14T14:40:00
  */
class ConceptApi(daoFactory: DAOFactory)(implicit val executor: ExecutionContext) extends ApiStack {

  before() {
    contentType = "application/json"
    response.headers.set("Access-Control-Allow-Origin", "*")
  }

  get("/") {
    val dao = daoFactory.newConceptNodeDAO()
    dao
      .findAllNames()
      .map(_.asJava)
      .map(Constants.GSON.toJson)
  }

  get("/parent/:name") {
    val name = params
      .get("name")
      .getOrElse(halt(BadRequest("Please provide a term to look up")))
    val dao = daoFactory.newConceptNodeDAO()
    dao
      .findParent(name)
      .map({
        case None    => halt(NotFound(s"No parent was found for the concept $name"))
        case Some(c) => toJson(c)
      })
  }

  get("/children/:name") {
    val name = params
      .get("name")
      .getOrElse(halt(BadRequest("Please provide a term to look up")))
    val dao = daoFactory.newConceptNodeDAO()
    dao
      .findChildren(name)
      .map(_.asJava)
      .map(toJson)
  }

  get("/:name") {
    val name = params
      .get("name")
      .getOrElse(halt(BadRequest("Please provide a term to look up")))
    val dao = daoFactory.newConceptNodeDAO()
    dao
      .findByName(name)
      .map({
        case None    => halt(NotFound(s"No concept named $name was found"))
        case Some(c) => toJson(c)
      })
  }

  get("/root") {
    val dao = daoFactory.newConceptNodeDAO()
    dao
      .findRoot()
      .map({
        case None    => halt(NotFound("Unable to find the root concept. That's crazy!!"))
        case Some(c) => toJson(c)
      })
  }

}
