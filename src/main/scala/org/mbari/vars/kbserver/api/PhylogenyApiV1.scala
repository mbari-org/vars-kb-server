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
import org.scalatra.{ BadRequest, NotFound }

import scala.concurrent.ExecutionContext

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T13:37:00
 */
class PhylogenyApiV1(daoFactory: DAOFactory)(implicit val executor: ExecutionContext) extends ApiStack {

  before() {
    contentType = "application/json"
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  //  get("/up/:name") {
  //    val name = params.get("name")
  //      .getOrElse(halt(BadRequest("Please provide a term to look up")))
  //    val dao = daoFactory.newPhylogenyDAO()
  //    dao.findUp(name)
  //        .map({
  //          case None => halt(NotFound(s"No concept named $name was found"))
  //          case Some(c) => toJson(c)
  //        })
  //  }
  //
  //  get("/down/:name") {
  //    val name = params.get("name")
  //      .getOrElse(halt(BadRequest("Please provide a term to look up")))
  //    val dao = daoFactory.newPhylogenyDAO()
  //    dao.findDown(name)
  //      .map({
  //        case None => halt(NotFound(s"No concept named $name was found"))
  //        case Some(c) => toJson(c)
  //      })
  //  }

  get("/up/:name") {
    val name = params.get("name")
      .getOrElse(halt(BadRequest(s"{'reason': 'Please provide a term to look up'}")))
    val dao = daoFactory.newFastPhylogenyDAO()
    dao.findUp(name)
      .map({
        case None => halt(NotFound(s"{'reason': 'No concept named $name was found'}"))
        case Some(c) => toJson(c)
      })
  }

  get("/down/:name") {
    val name = params.get("name")
      .getOrElse(halt(BadRequest(s"{'reason': 'Please provide a term to look up'")))
    val dao = daoFactory.newFastPhylogenyDAO()
    dao.findDown(name)
      .map({
        case None => halt(NotFound(s"{'reason': 'No concept named $name was found'}"))
        case Some(c) => toJson(c)
      })
  }

}
