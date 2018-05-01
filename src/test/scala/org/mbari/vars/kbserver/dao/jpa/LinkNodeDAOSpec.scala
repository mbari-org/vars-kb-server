package org.mbari.vars.kbserver.dao.jpa

import org.scalatest.{ BeforeAndAfterAll, FlatSpec, Matchers }

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by brian on 11/30/16.
 */
class LinkNodeDAOSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = TestDatabase.init

  private[this] val daoFactory = new DefaultDAOFactory(TestDatabase.KB_DAO_FACTORY)
  private[this] val dao = daoFactory.newLinkNodeDAO()

  "LinkNodeDAO" should "findAllApplicableTo object" in {
    val ns = Await.result(dao.findAllApplicableTo("object"), TestDatabase.TIMEOUT)
    //println(Constants.GSON.toJson(ns.asJava))
    ns.size should be(63)
  }

  it should "findAllAppicableTo physical-object" in {
    val ns = Await.result(dao.findAllApplicableTo("physical-object"), TestDatabase.TIMEOUT)
    //println(Constants.GSON.toJson(ns.asJava))
    ns.size should be(315)
  }

  it should "findAllApplicableTo Nanomia bijuga" in {
    val ns = Await.result(dao.findAllApplicableTo("Nanomia bijuga"), TestDatabase.TIMEOUT)
    //println(Constants.GSON.toJson(ns.asJava))
    ns.size should be(612)
  }

}
