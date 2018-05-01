package org.mbari.vars.kbserver.api

/**
 * @author Brian Schlining
 * @since 2016-12-14T15:31:00
 */
class LinkApiSpec extends WebApiStack {

  private[this] val api = new LinkApi(daoFactory)
  private[this] val path = "/v1/links"

  addServlet(api, path)

  "LinkApi" should "find links for object" in {
    get(s"$path/object") {
      status should be(200)
      //println(body)
    }
  }

  it should "find links for Nanomia bijuga" in {
    get(s"$path/Nanomia%20bijuga") {
      status should be(200)
      //println(body)
    }
  }

  it should "find links for Nanomia bijuga named 'surface-color'" in {
    get(s"$path/Nanomia%20bijuga/using/surface-color") {
      status should be(200)
      //println(body)
    }

  }

}
