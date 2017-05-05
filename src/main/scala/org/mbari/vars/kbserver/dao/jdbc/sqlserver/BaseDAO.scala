package org.mbari.vars.kbserver.dao.jdbc.sqlserver

import java.io.{ BufferedReader, InputStreamReader }
import java.net.URL

import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }
import org.mbari.vars.kbserver.{ Constants => Konstants }

import scala.io.Source

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T14:30:00
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
    config.setConnectionTestQuery("SELECT 1")
    config
  }

  def readSQL(url: URL): String = Source.fromURL(url).mkString("")
}
