package org.mbari.vars.kbserver.api

import scala.concurrent.ExecutionContext

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T13:37:00
 */
class PhylogenyApi(val executor: ExecutionContext) extends ApiStack {

  before() {
    contentType = "application/json"
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  get("/up/:name") {}

  get("/down/:name") {}

}
