package org.mbari.vars.kbserver.dao.jpa

import org.scalatest.{ BeforeAndAfterAll, FlatSpec, Matchers }

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-22T16:49:00
 */
class PhylogenyDAOSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = TestDatabase.init

  private[this] val daoFactory = new DefaultDAOFactory(TestDatabase.KB_DAO_FACTORY)
  private[this] val dao = daoFactory.newPhylogenyDAO()

  "PhylogenyDAOImpl" should "find phylogeny for Nanomia bijuga" in {
    val cn = Await.result(dao.findUp("Nanomia bijuga"), TestDatabase.TIMEOUT)
    cn should not be (empty)
    //println(Constants.GSON.toJson(cn.get))
  }

  it should "find children of Agalmatidae" in {
    val cn = Await.result(dao.findDown("Agalmatidae"), TestDatabase.TIMEOUT)
    cn should not be (empty)
    //println(Constants.GSON.toJson(cn.get))
  }

}
