package org.mbari.vars.kbserver.api

import java.util.concurrent.TimeUnit

import org.mbari.vars.kbserver.Constants
import org.mbari.vars.kbserver.dao.jpa.{DefaultDAOFactory, TestDatabase}
import org.scalatest.BeforeAndAfterAll
import org.scalatra.test.scalatest.ScalatraFlatSpec

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration => SDuration}

/**
  * @author Brian Schlining
  * @since 2016-12-12T16:43:00
  */
class WebApiStack extends ScalatraFlatSpec with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    TestDatabase.init
  }

  protected[this] val gson = Constants.GSON
  protected[this] val daoFactory = new DefaultDAOFactory(TestDatabase.KB_DAO_FACTORY)
  protected[this] implicit val executionContext = ExecutionContext.global
  protected[this] val timeout = SDuration(3000, TimeUnit.MILLISECONDS)


}
