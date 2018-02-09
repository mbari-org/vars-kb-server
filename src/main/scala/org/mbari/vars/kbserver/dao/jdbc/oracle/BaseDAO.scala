package org.mbari.vars.kbserver.dao.jdbc.oracle

import java.net.URL

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import scala.io.Source
import org.mbari.vars.kbserver.{ Constants => Konstants }

/**
  * @author Brian Schlining
  * @since 2018-02-09T13:00:00
  */
abstract class BaseDAO {

  val dataSource = BaseDAO.dataSource

}

object BaseDAO {

  @volatile
  lazy val dataSource = new HikariDataSource(BaseDAO.HIKARI_CONFIG)


  @volatile
  lazy val HIKARI_CONFIG = {
    val config = new HikariConfig()
    val p = Konstants.DB_PARAMS
    config.setJdbcUrl(p.url)
    config.setUsername(p.user)
    config.setPassword(p.password)
    config.addDataSourceProperty("maximumPoolSize", 30)
    config.addDataSourceProperty("cachePrepStmts", "true")
    config.addDataSourceProperty("prepStmtCacheSize", "250")
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
    config.setConnectionTestQuery("SELECT 1 FROM DUAL")
    config
  }

  def readSQL(url: URL): String = Source.fromURL(url).mkString("")
}
