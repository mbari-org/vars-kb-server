package org.mbari.vars.kbserver.dao.jpa

import java.io.{ File, IOException }
import java.net.URL
import java.util.Scanner
import java.util.zip.ZipFile

import com.google.inject.Guice
import org.mbari.net.URLUtilities
import org.slf4j.LoggerFactory
import vars.gson.{ Constants, InitializeKnowledgebaseApp }
import vars.jpa.InjectorModule
import vars.knowledgebase.{ Concept, KnowledgebaseDAOFactory }
import vars.knowledgebase.jpa.ConceptImpl

import scala.collection.JavaConverters._
import scala.io.Source
import scala.util.control.NonFatal

/**
 * Reads a zipped up JSON dump of MBARI's KB and loads it into the database.
 *
 * @author Brian Schlining
 * @since 2016-11-22T15:28:00
 */
object InitTestDatabase {

  private[this] val log = LoggerFactory.getLogger(getClass)

  @volatile
  lazy val init: Boolean = {
    try {
      val url = getClass.getResource("/kb/kb-dump.json.zip")
      val file = URLUtilities.toFile(url)
      val injector = Guice.createInjector(new InjectorModule)
      val daoFactory = injector.getInstance(classOf[KnowledgebaseDAOFactory])
      InitializeKnowledgebaseApp.run(file, daoFactory)
      true
    } catch {
      case NonFatal(e) => false
    }
  }

}
