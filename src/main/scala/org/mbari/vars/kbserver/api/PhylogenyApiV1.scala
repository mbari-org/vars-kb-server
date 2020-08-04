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
import org.mbari.vars.kbserver.model.{BasicConceptNode, Msg}
import org.scalatra.{BadRequest, NotFound}

import scala.concurrent.ExecutionContext
import scala.concurrent.Await
import java.util.concurrent.TimeUnit

/**
  *
  *
  * @author Brian Schlining
  * @since 2016-11-17T13:37:00
  */
class PhylogenyApiV1(daoFactory: DAOFactory)(implicit val executor: ExecutionContext)
    extends ApiBase {

  before() {
    contentType = "application/json"
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  get("/up/:name") {
    val name = params
      .get("name")
      .getOrElse(halt(BadRequest(toJson(Msg("Please provide a term to look up")))))
    val dao = daoFactory.newFastPhylogenyDAO()
    Await.result(dao.findUp(name), scala.concurrent.duration.Duration(60, TimeUnit.SECONDS)) match {
      case None    => halt(NotFound(toJson(Msg(s"No concept named $name was found", 404))))
      case Some(c) => toJson(c)
    }
    // dao
    //   .findUp(name)
    //   .map({
    //     case None    => halt(NotFound(toJson(Msg(s"No concept named $name was found", 404))))
    //     case Some(c) => toJson(c)
    //   })
  }

  get("/down/:name") {
    val name = params
      .get("name")
      .getOrElse(halt(BadRequest(toJson(Msg("Please provide a term to look up")))))
    val dao = daoFactory.newFastPhylogenyDAO()
    Await.result(dao.findDown(name), scala.concurrent.duration.Duration(60, TimeUnit.SECONDS)) match {
      case None    => halt(NotFound(toJson(Msg(s"No concept named $name was found", 404))))
      case Some(c) => toJson(c)
    }
    // dao
    //   .findDown(name)
    //   .map({
    //     case None    => halt(NotFound(toJson(Msg(s"No concept named $name was found", 404))))
    //     case Some(c) => toJson(c)
    //   })
  }

  get("/basic/:name") {
    val name = params
      .get("name")
      .getOrElse(halt(BadRequest(toJson(Msg("Please provide a term to look up")))))
    val dao = daoFactory.newFastPhylogenyDAO()
    Await
      .result(dao.findUp(name), scala.concurrent.duration.Duration(60, TimeUnit.SECONDS)) match {
      case None    => halt(NotFound(toJson(Msg(s"No concept named $name was found", 404))))
      case Some(c) => toJson(BasicConceptNode.flatten(c).toArray)
    }
    // dao
    //   .findUp(name)
    //   .map({
    //     case None    => halt(NotFound(toJson(Msg(s"No concept named $name was found", 404))))
    //     case Some(c) => toJson(BasicConceptNode.flatten(c).toArray)
    //   })
  }

  get("/taxa/:name") {
    val name = params
      .get("name")
      .getOrElse(halt(BadRequest(toJson(Msg("Please provide a term to look up")))))
    val dao = daoFactory.newFastPhylogenyDAO()
    Await
      .result(dao.findDown(name), scala.concurrent.duration.Duration(60, TimeUnit.SECONDS)) match {
      case None    => halt(NotFound(toJson(Msg(s"No concept named $name was found", 404))))
      case Some(c) => toJson(BasicConceptNode.taxa(c).toArray)
    }
    // dao
    //   .findDown(name)
    //   .map({
    //     case None    => halt(NotFound(toJson(Msg(s"No concept named $name was found", 404))))
    //     case Some(c) => toJson(BasicConceptNode.taxa(c).toArray)
    //   })
  }

}
