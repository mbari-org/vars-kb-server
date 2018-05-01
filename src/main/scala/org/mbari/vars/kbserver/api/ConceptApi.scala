package org.mbari.vars.kbserver.api

import org.mbari.vars.kbserver.Constants
import org.mbari.vars.kbserver.dao.DAOFactory
import org.scalatra.{ BadRequest, NotFound }

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

/**
 * @author Brian Schlining
 * @since 2016-12-14T14:40:00
 */
class ConceptApi(daoFactory: DAOFactory)(implicit val executor: ExecutionContext) extends ApiStack {

  before() {
    contentType = "application/json"
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  get("/") {
    val dao = daoFactory.newConceptNodeDAO()
    dao.findAllNames()
      .map(_.asJava)
      .map(Constants.GSON.toJson)
  }

  get("/:name") {
    val name = params.get("name")
      .getOrElse(halt(BadRequest("Please provide a term to look up")))
    val dao = daoFactory.newConceptNodeDAO()
    dao.findByName(name)
      .map({
        case None => halt(NotFound(s"No concept named $name was found"))
        case Some(c) => toJson(c)
      })
  }

  get("/root") {
    val dao = daoFactory.newConceptNodeDAO()
    dao.findRoot()
      .map({
        case None => halt(NotFound("Unable to find the root concept. That's crazy!!"))
        case Some(c) => toJson(c)
      })
  }

}
