/*
 * Copyright 2017 Monterey Bay Aquarium Research Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mbari.vars.kbserver.dao.jdbc

import java.net.URL

import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }

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
    config.addDataSourceProperty("maximumPoolSize", 6)
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
