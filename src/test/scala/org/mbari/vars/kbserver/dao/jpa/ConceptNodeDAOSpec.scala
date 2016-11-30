package org.mbari.vars.kbserver.dao.jpa

import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by brian on 11/29/16.
  */
class ConceptNodeDAOSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = TestDatabase.init

  private[this] val daoFactory = new DefaultDAOFactory(TestDatabase.KB_DAO_FACTORY)
  private[this] val dao = daoFactory.newConceptNodeDAO()

  "ConceptNodeDAO" should "findRoot" in {
    val root = Await.result(dao.findRoot(), TestDatabase.TIMEOUT)
    root should not be empty
    root.get.name should be ("object")
  }

  it should "findByName" in {
    val name = "Nanomia bijuga"
    val c = Await.result(dao.findByName(name), TestDatabase.TIMEOUT)
    c should not be empty
    c.get.name should be (name)
    //println(Constants.GSON.toJson(c.get))
  }

  it should "findAllNames" in {
    val names = Await.result(dao.findAllNames(), TestDatabase.TIMEOUT)
    names.size should be (5209)
    names should contain("Nanomia bijuga")
  }

}
