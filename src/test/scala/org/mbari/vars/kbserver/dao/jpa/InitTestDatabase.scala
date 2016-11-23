package org.mbari.vars.kbserver.dao.jpa

import java.io.{ File, IOException }
import java.net.URL
import java.util.Scanner
import java.util.zip.ZipFile

import com.google.inject.Guice
import org.mbari.net.URLUtilities
import vars.gson.Constants
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

  @volatile
  lazy val init: Boolean = {
    try {
      val url = getClass.getResource("/kb/kb-dump.json.zip")
      val concept = read(URLUtilities.toFile(url))
      val injector = Guice.createInjector(new InjectorModule)
      val daoFactory = injector.getInstance(classOf[KnowledgebaseDAOFactory])
      val dao = daoFactory.newConceptDAO()
      dao.startTransaction()
      dao.persist(concept)
      dao.endTransaction()
      dao.close()
      true
    } catch {
      case NonFatal(e) => false
    }
  }

  @throws[IOException]
  private def read(file: File): Concept = {
    val zipFile = new ZipFile(file)
    val entries = zipFile.entries().asScala
    var ok = true
    var concept: Concept = new ConceptImpl()
    for (entry <- entries) {
      if (ok) {
        try {
          val stream = zipFile.getInputStream(entry)
          val json = Source.fromInputStream(stream).mkString("\n")
          concept = Constants.GSON.fromJson(json, classOf[ConceptImpl])
          stream.close()
          ok = concept != null
        } catch {
          case NonFatal(e) => // Do nothing
        }
      }
    }
    concept
  }

}
