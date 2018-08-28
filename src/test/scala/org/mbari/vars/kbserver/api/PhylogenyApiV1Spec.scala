package org.mbari.vars.kbserver.api

/**
 * @author Brian Schlining
 * @since 2016-12-12T16:42:00
 */
class PhylogenyApiV1Spec extends WebApiStack {

  private[this] val api = new PhylogenyApiV1(daoFactory)
  private[this] val path = "/v1/phylogeny"
  addServlet(api, path)

  "PhylogenyApi" should "find ancestors" in {
    get(s"$path/up/Nanomia") {
      status should be(200)
      body.contains("Nanomia") should be(true)
      body.contains("Agalmatidae") should be(true)
      body.contains("Physonectae") should be(true)
      body.contains("Siphonophorae") should be(true)
      body.contains("Hydroidolina") should be(true)
      body.contains("Hydrozoa") should be(true)
      body.contains("Cnidaria") should be(true)
      body.contains("Animalia") should be(true)
      body.contains("Eukaryota") should be(true)
      //println(body)
    }
  }

  it should "find descendents" in {
    get(s"$path/down/Agalmatidae") {
      status should be(200)
      body.contains("Agalmatidae") should be(true)
      body.contains("Nanomia") should be(true)
      body.contains("Nanomia bijuga") should be(true)
      body.contains("Halistemma rubrum") should be(true)
      //println(body)
    }
  }

}