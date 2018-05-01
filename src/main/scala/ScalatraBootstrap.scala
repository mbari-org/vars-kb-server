import java.util.concurrent.Executors
import javax.servlet.ServletContext

import org.mbari.vars.kbserver.Constants
import org.mbari.vars.kbserver.api._
import org.scalatra.LifeCycle
import org.slf4j.LoggerFactory
import vars.knowledgebase.KnowledgebaseDAOFactory

import scala.concurrent.ExecutionContext

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-05-20T14:41:00
 */
class ScalatraBootstrap extends LifeCycle {

  private[this] val log = LoggerFactory.getLogger(getClass)

  override def init(context: ServletContext): Unit = {

    log.info("STARTING UP NOW")

    implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors()))

    val daoFactory = Constants.DAO_FACTORY

    val phylogenyApiV1 = new PhylogenyApiV1(daoFactory)
    val conceptApi = new ConceptApi(daoFactory)
    val linkApi = new LinkApi(daoFactory)

    val kbDaoFactory = Constants.GUICE_INJECTOR.getInstance(classOf[KnowledgebaseDAOFactory])
    val rawApi = new RawApi(kbDaoFactory)

    context.mount(phylogenyApiV1, "/v1/phylogeny")
    context.mount(conceptApi, "/v1/concept")
    context.mount(linkApi, "/v1/links")
    context.mount(rawApi, "/v1/raw")

  }

}
