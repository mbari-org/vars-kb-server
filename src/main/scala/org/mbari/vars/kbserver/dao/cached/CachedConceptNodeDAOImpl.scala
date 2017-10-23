package org.mbari.vars.kbserver.dao.cached

import java.util.concurrent.TimeUnit

import com.github.benmanes.caffeine.cache.{Cache, Caffeine}
import org.mbari.vars.kbserver.dao.ConceptNodeDAO
import org.mbari.vars.kbserver.model.ConceptNode

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Brian Schlining
  * @since 2017-10-23T09:41:00
  */
class CachedConceptNodeDAOImpl(val proxied: ConceptNodeDAO) extends ConceptNodeDAO {

  import CachedConceptNodeDAOImpl._

  val nameCache: Cache[String, ConceptNode] = Caffeine.newBuilder()
      .expireAfterWrite(15, TimeUnit.MINUTES)
      .build[String, ConceptNode]()

  val allNamesCache = Caffeine.newBuilder()
      .expireAfterWrite(15, TimeUnit.MINUTES)
      .build[String, Seq[String]]()

  override def findByName(name: String)(implicit ec: ExecutionContext): Future[Option[ConceptNode]] = {
    Option(nameCache.getIfPresent(name)) match {
      case Some(node) => Future(Some(node))
      case None =>
        val future = proxied.findByName(name)
        future.foreach({
          case Some(conceptNode) => nameCache.put(name, conceptNode)
          case None => // Do nothing
        })
        future
    }
  }

  override def findAllNames()(implicit ec: ExecutionContext): Future[Seq[String]] = {
    val allNames = Option(allNamesCache.getIfPresent(AllNamesCacheKey))
    if (allNames.isDefined && allNames.get.nonEmpty) {
      Future(allNames.get)
    }
    else {
      val future = proxied.findAllNames()
      future.foreach(names => allNamesCache.put(AllNamesCacheKey, names))
      future
    }
  }

  override def findRoot()(implicit ec: ExecutionContext): Future[Option[ConceptNode]] = {
    proxied.findRoot()
  }
}

object CachedConceptNodeDAOImpl {
  val AllNamesCacheKey = "all-names"
}
