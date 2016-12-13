package org.mbari.vars.kbserver.api

import org.mbari.vars.kbserver.Constants
import org.mbari.vars.kbserver.dao.DAOFactory
import org.scalatra.BadRequest

import scala.concurrent.ExecutionContext

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T13:37:00
 */
class PhylogenyApi(daoFactory: DAOFactory)(implicit val executor: ExecutionContext) extends ApiStack {

  before() {
    contentType = "application/json"
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  get("/up/:name") {
    val name = params.get("name")
      .getOrElse(halt(BadRequest("Please provide a term to look up")))
    val dao = daoFactory.newPhylogenyDAO()
    dao.findUp(name)
        .map(_.map(Constants.GSON.toJson).getOrElse("{}"))

  }

  get("/down/:name") {
    val name = params.get("name")
      .getOrElse(halt(BadRequest("Please provide a term to look up")))
    val dao = daoFactory.newPhylogenyDAO()
    dao.findDown(name)
      .map(_.map(Constants.GSON.toJson).getOrElse("{}"))
  }

}
