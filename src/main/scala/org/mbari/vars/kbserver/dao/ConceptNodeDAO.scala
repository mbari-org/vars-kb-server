package org.mbari.vars.kbserver.dao

import org.mbari.vars.kbserver.model.ConceptNode
import scala.concurrent.{ ExecutionContext, Future }

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T15:49:00
 */
trait ConceptNodeDAO {

  def findByName(name: String)(implicit ec: ExecutionContext): Future[Option[ConceptNode]]

  def findAllNames()(implicit ec: ExecutionContext): Future[Seq[String]]

  def findRoot()(implicit ec: ExecutionContext): Future[Option[ConceptNode]]

}
