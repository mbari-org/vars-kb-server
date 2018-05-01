package org.mbari.vars.kbserver.api

import org.mbari.vars.kbserver.Constants

/**
 * @author Brian Schlining
 * @since 2016-12-14T14:47:00
 */
class ConceptApiSpec extends WebApiStack {

  private[this] val api = new ConceptApi(daoFactory)
  private[this] val path = "/v1/concept"
  addServlet(api, path)

  "ConceptApi" should "find root" in {
    get(s"$path/root") {
      status should be(200)
      body.contains("object") should be(true)
      //println(body)
    }
  }

  it should "find Nanomia bijuga by name" in {
    get(s"$path/Nanomia%20bijuga") {
      status should be(200)
      body.contains("Nanomia bijuga") should be(true)
      println(body)
    }
  }

  it should "find marine organism by name" in {
    get(s"$path/marine%20organism") {
      status should be(200)
      body.contains("marine organism") should be(true)
      //println(body)
    }
  }

  it should "find physical object by name" in {
    get(s"$path/physical%20object") {
      status should be(200)
      body.contains("physical object") should be(true)
      //println(body)
    }
  }

  it should "find all names as strings" in {
    get(s"$path") {
      status should be(200)
      val names = Constants.GSON.fromJson(body, classOf[Array[String]])
      names.size should be(5209)
      //println("=====> " + names.size)
    }
  }

}
