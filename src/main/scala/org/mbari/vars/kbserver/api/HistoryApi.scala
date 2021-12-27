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
import org.mbari.vars.kbserver.model.Msg

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

/**
  * @author Brian Schlining
  * @since 2019-11-19T14:17:00
  */
class HistoryApi(daoFactory: DAOFactory)(implicit val executor: ExecutionContext) extends ApiStack {

  before() {
    contentType = "application/json"
  }

  get("/pending") {
    val dao = daoFactory.newHistoryDAO()
    dao
      .findPendingHistories()
      .map(_.asJava)
      .map(Constants.GSON.toJson)
  }

  get("/approved") {
    val dao = daoFactory.newHistoryDAO()
    dao
      .findApprovedHistories()
      .map(_.asJava)
      .map(Constants.GSON.toJson)
  }

  get("/test") {
    toJson(Msg("Worked", 200))
  }

}
