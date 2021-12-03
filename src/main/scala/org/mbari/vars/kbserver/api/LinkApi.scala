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

import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

import org.mbari.vars.kbserver.dao.DAOFactory
import org.scalatra.BadRequest

import scala.jdk.CollectionConverters._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

/**
  * @author Brian Schlining
  * @since 2016-12-14T15:27:00
  */
class LinkApi(daoFactory: DAOFactory)(implicit val executor: ExecutionContext) extends ApiStack {

  before() {
    contentType = "application/json"
  }

  get("/") {
    val dao = daoFactory.newLinkNodeDAO()
    dao
      .findAll
      .map(_.asJava)
      .map(toJson)
  }

  // return all linktemplates applicable to the provided concept name
  get("/:name") {
    val name = params
      .get("name")
      .getOrElse(halt(BadRequest("Please provide a term to look up")))
    val dao = daoFactory.newLinkNodeDAO()
    dao
      .findAllApplicableTo(name)
      .map(_.asJava)
      .map(toJson)
  }

  // return all linktemlates with the matching linkname
  get("/:name/using/:linkname") {
    val name = params
      .get("name")
      .getOrElse(halt(BadRequest("Please provide a concept to look up")))
    val linkname = params
      .get("linkname")
      .getOrElse(halt(BadRequest("Please provide a linkname to look up")))
    val dao = daoFactory.newLinkNodeDAO()
    dao
      .findAllApplicableTo(name)
      .map(_.filter(_.linkName.equalsIgnoreCase(linkname)))
      .map(_.asJava)
      .map(toJson)
  }

  get("/query/linkrealizations/:linkname") {
    val linkname = params.get("linkname")
      .getOrElse(halt(BadRequest("Please provide a concept to look up")))
    val dao = daoFactory.newLinkNodeDAO()
    dao.findAllLinkRealizationsByLinkName(linkname)
      .map(_.asJava)
      .map(toJson)
  }

  get("/report/linkrealizations/:linkname") {
    val linkname = params.get("linkname")
      .getOrElse(halt(BadRequest("Please provide a concept to look up")))

    val dao = daoFactory.newLinkNodeDAO()
    val links = Await.result(dao.findAllLinkRealizationsByLinkName(linkname), Duration(30, TimeUnit.SECONDS))
    response.setContentType("text/plain")
    val out = response.getOutputStream
    links.foreach(x => {
      val s = s"${x.getFromConcept} | ${x.getLinkName} | ${x.getToConcept} | ${x.getLinkValue}\n".getBytes(StandardCharsets.UTF_8)
      out.write(s)
    })
    ()
  }

}
