package org.mbari.vars.kbserver.dao.jpa

import java.io.{File, IOException}
import java.net.URL
import java.util.Scanner
import java.util.concurrent.TimeUnit
import java.util.zip.ZipFile

import com.google.inject.Guice
import org.mbari.net.URLUtilities
import org.slf4j.LoggerFactory
import vars.gson.{Constants, InitializeKnowledgebaseApp}
import vars.jpa.InjectorModule
import vars.knowledgebase.{Concept, KnowledgebaseDAOFactory}

import scala.concurrent.duration.Duration
import scala.util.control.NonFatal

/**
 * Reads a zipped up JSON dump of MBARI's KB and loads it into the database.
 *
 * @author Brian Schlining
 * @since 2016-11-22T15:28:00
 */
object TestDatabase {

  private[this] val log = LoggerFactory.getLogger(getClass)
  private[this] val injector = Guice.createInjector(new InjectorModule)

  val TIMEOUT = Duration(2, TimeUnit.SECONDS)

  @volatile
  lazy val init: Boolean = {
    try {
      val url = getClass.getResource("/kb/kb-dump.json.zip")
      val file = URLUtilities.toFile(url)
      InitializeKnowledgebaseApp.run(file, KB_DAO_FACTORY)
      true
    } catch {
      case NonFatal(e) => false
    }
  }

  val KB_DAO_FACTORY = injector.getInstance(classOf[KnowledgebaseDAOFactory])


}
