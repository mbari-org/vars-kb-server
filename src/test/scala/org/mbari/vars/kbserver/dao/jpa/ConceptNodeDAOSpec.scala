package org.mbari.vars.kbserver.dao.jpa

import org.mbari.vars.kbserver.Constants
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by brian on 11/29/16.
  */
class ConceptNodeDAOSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = TestDatabase.init

  val daoFactory = new DefaultDAOFactory(TestDatabase.knowledgebaseDAOFactory)
  val dao = daoFactory.newConceptNodeDAO()

  "ConceptNodeDAO" should "findRoot" in {
    val root = Await.result(dao.findRoot(), TestDatabase.timeout)
    root should not be empty
    root.get.name should be ("object")
  }

  it should "findByName" in {
    val c = Await.result(dao.findByName("Nanomia bijuga"), TestDatabase.timeout)
    c should not be empty
    c.get.name should be ("Nanomia bijuga")
    println(Constants.GSON.toJson(c.get))
  }

}
