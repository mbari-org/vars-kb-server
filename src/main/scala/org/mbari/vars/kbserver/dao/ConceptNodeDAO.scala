package org.mbari.vars.kbserver.dao

import org.mbari.vars.kbserver.model.ConceptNode
import vars.knowledgebase.Concept

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T15:49:00
 */
trait ConceptNodeDAO {

  def findByName(name: String): ConceptNode

  def findAllNames(): Seq[String]

}
