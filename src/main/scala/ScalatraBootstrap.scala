import java.util.concurrent.Executors
import javax.servlet.ServletContext

import org.mbari.vars.kbserver.Constants
import org.mbari.vars.kbserver.api.{ConceptApi, LinkApi, PhylogenyApi}
import org.scalatra.LifeCycle
import org.slf4j.LoggerFactory

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

    println("STARTING UP NOW")

    implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors()))

    val daoFactory = Constants.DAO_FACTORY

    val phylogenyApi = new PhylogenyApi(daoFactory)
    val conceptApi = new ConceptApi(daoFactory)
    val linkApi = new LinkApi(daoFactory)

    context.mount(phylogenyApi, "/v1/phylogeny")
    context.mount(conceptApi, "/v1/concept")
    context.mount(linkApi, "/v1/links")

  }

}
