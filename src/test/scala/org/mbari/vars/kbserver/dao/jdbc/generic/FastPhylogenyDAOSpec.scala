package org.mbari.vars.kbserver.dao.jdbc.generic

//import com.typesafe.config.ConfigFactory
import org.mbari.vars.kbserver.Constants
import org.mbari.vars.kbserver.dao.jpa.TestDatabase
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.Await
//import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * @author Brian Schlining
  * @since 2018-02-14T18:54:00
  */
class FastPhylogenyDAOSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  //private[this] val config = ConfigFactory.load()

  override protected def beforeAll(): Unit = TestDatabase.init

  protected val dao = new FastPhylogenyDAO(Some("SELECT * FROM UniqueID"))
   // Try(config.getString("org.mbari.vars.knowledgebase.development.hikari.test")).toOption)

  "PhylogenyDAO2" should "find phylogeny for Nanomia bijuga" in {
    val cn = Await.result(dao.findUp("Nanomia bijuga"), TestDatabase.TIMEOUT)
    cn should not be (empty)
    println(Constants.GSON.toJson(cn.get))
  }

  it should "find children of Agalmatidae" in {
    val cn = Await.result(dao.findDown("Agalmatidae"), TestDatabase.TIMEOUT)
    cn should not be (empty)
    println(Constants.GSON.toJson(cn.get))
  }


}
