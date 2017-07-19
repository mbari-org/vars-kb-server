package org.mbari.vars.kbserver.dao

import org.mbari.vars.kbserver.model.LinkNode

import scala.concurrent.{ ExecutionContext, Future }

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T16:40:00
 */
trait LinkNodeDAO {

  def findAllApplicableTo(conceptName: String)(
    implicit
    ec: ExecutionContext
  ): Future[Seq[LinkNode]]

}
