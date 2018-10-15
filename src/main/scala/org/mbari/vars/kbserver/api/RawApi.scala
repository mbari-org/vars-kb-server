package org.mbari.vars.kbserver.api

import javax.inject.Inject

import org.mbari.vars.kbserver.dao.jpa.KnowledgebaseInitializer
import org.scalatra.BadRequest
import vars.knowledgebase.KnowledgebaseDAOFactory

import scala.concurrent.{ ExecutionContext, Future }

/**
 * @author Brian Schlining
 * @since 2018-01-08T13:07:00
 */
class RawApi @Inject() (knowledgebaseDAOFactory: KnowledgebaseDAOFactory)
    (implicit val executor: ExecutionContext) extends ApiStack {

  before() {
    contentType = "application/json"
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  get("/") {
    Future {
      val dao = knowledgebaseDAOFactory.newConceptDAO()
      dao.startTransaction()
      val root = dao.findRoot()
      val json = KnowledgebaseInitializer.GSON.toJson(root)
      dao.endTransaction()
      dao.close()
      json
    }
  }

  post("/") {
    request.getHeader("Content-Type") match {
      case "application/json" =>
        val kbInit = new KnowledgebaseInitializer(knowledgebaseDAOFactory)
        val concept = kbInit.read(request.body)
        kbInit.persist(concept)
      case _ =>
        halt(BadRequest("Raw post endpoint only accepts Content-Type: application/json"))
    }
  }

}

