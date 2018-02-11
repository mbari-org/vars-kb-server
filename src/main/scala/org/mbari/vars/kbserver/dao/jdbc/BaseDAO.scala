package org.mbari.vars.kbserver.dao.jdbc

import java.net.URL

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import scala.io.Source

import org.mbari.vars.kbserver.{ Constants => Konstants }

/**
  * @param connectionTestQuery The SQL used by hikari to veryify the connection.
  *                            See the usages in sqlserver and oracle packages
  *                            for examples.
  * @author Brian Schlining
  * @since 2018-02-11T11:04:00
  */
class BaseDAO(connectionTestQuery: Option[String] = None) {

  @volatile
  lazy val dataSource = new HikariDataSource(hikariConfig)

  @volatile
  lazy val hikariConfig = {
    val config = new HikariConfig()
    val p = Konstants.DB_PARAMS
    config.setJdbcUrl(p.url)
    config.setUsername(p.user)
    config.setPassword(p.password)
    config.addDataSourceProperty("maximumPoolSize", 30)
    config.addDataSourceProperty("cachePrepStmts", "true")
    config.addDataSourceProperty("prepStmtCacheSize", "250")
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
    val testQuery = if (connectionTestQuery.isEmpty) p.testQuery
        else connectionTestQuery
    testQuery.foreach(config.setConnectionTestQuery)
    config
  }

  def readSQL(url: URL): String = Source.fromURL(url).mkString("")

}
