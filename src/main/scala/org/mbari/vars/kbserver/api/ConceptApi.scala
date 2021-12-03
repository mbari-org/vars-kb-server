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
import org.mbari.vars.kbserver.dao.DAOFactory
import org.scalatra.{BadRequest, NotFound}

import scala.jdk.CollectionConverters._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

/**
  * @author Brian Schlining
  * @since 2016-12-14T14:40:00
  */
class ConceptApi(daoFactory: DAOFactory)(implicit val executor: ExecutionContext) extends ApiBase {

  before() {
    contentType = "application/json"
  }

  get("/") {
    val dao = daoFactory.newConceptNodeDAO()
    Await.result(dao
      .findAllNames()
      .map(_.asJava)
      .map(Constants.GSON.toJson), Duration(60, TimeUnit.SECONDS))
  }

  get("/parent/:name") {
    val name = params
      .get("name")
      .getOrElse(halt(BadRequest("Please provide a term to look up")))
    val dao = daoFactory.newConceptNodeDAO()
    Await.result(dao
      .findParent(name)
      .map({
        case None    => halt(NotFound(s"No parent was found for the concept $name"))
        case Some(c) => toJson(c)
      }), Duration(60, TimeUnit.SECONDS))
  }

  get("/children/:name") {
    val name = params
      .get("name")
      .getOrElse(halt(BadRequest("Please provide a term to look up")))
    val dao = daoFactory.newConceptNodeDAO()
    Await.result(dao
      .findChildren(name)
      .map(_.asJava)
      .map(toJson), Duration(60, TimeUnit.SECONDS))
  }

  get("/:name") {
    val name = params
      .get("name")
      .getOrElse(halt(BadRequest("Please provide a term to look up")))
    val dao = daoFactory.newConceptNodeDAO()
    Await.result(dao
      .findByName(name)
      .map({
        case None    => halt(NotFound(s"No concept named $name was found"))
        case Some(c) => toJson(c)
      }), Duration(60, TimeUnit.SECONDS))
  }

  get("/root") {
    val dao = daoFactory.newConceptNodeDAO()
    Await.result(dao
      .findRoot()
      .map({
        case None    => halt(NotFound("Unable to find the root concept. That's crazy!!"))
        case Some(c) => toJson(c)
      }), Duration(60, TimeUnit.SECONDS))
  }

  get("/find/:glob") {
    val glob = params
      .get("glob")
      .getOrElse(halt(BadRequest("Please provide a term to look up")))
    val dao = daoFactory.newConceptNodeDAO()
    Await.result(dao.findAllNamesContaining(glob)
      .map(_.asJava)
      .map(toJson), Duration(60, TimeUnit.SECONDS))
  }

}
