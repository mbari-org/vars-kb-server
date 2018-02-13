package org.mbari.vars.kbserver.api

import org.mbari.vars.kbserver.dao.DAOFactory
import org.scalatra.{BadRequest, NotFound}

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

  private[this] val phylogenyDAO2 = daoFactory.newPhylogenyDAO2()

  get("/up/:name") {
    val name = params.get("name")
      .getOrElse(halt(BadRequest("Please provide a term to look up")))
    val dao = phylogenyDAO2
    dao.findUp(name)
          .map({
            case None => halt(NotFound(s"No concept named $name was found"))
            case Some(c) => toJson(c)
          })
  }

  get("/down/:name") {
    val name = params.get("name")
      .getOrElse(halt(BadRequest("Please provide a term to look up")))
    val dao = phylogenyDAO2
    dao.findDown(name)
      .map({
        case None => halt(NotFound(s"No concept named $name was found"))
        case Some(c) => toJson(c)
      })
  }

  get("/up2/:name") {
    val name = params.get("name")
      .getOrElse(halt(BadRequest("Please provide a term to look up")))
    val dao = daoFactory.newPhylogenyDAO()
    dao.findUp(name)
        .map({
          case None => halt(NotFound(s"No concept named $name was found"))
          case Some(c) => toJson(c)
        })
  }

  get("/down2/:name") {
    val name = params.get("name")
      .getOrElse(halt(BadRequest("Please provide a term to look up")))
    val dao = daoFactory.newPhylogenyDAO()
    dao.findDown(name)
      .map({
        case None => halt(NotFound(s"No concept named $name was found"))
        case Some(c) => toJson(c)
      })
  }

}
