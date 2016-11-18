package org.mbari.vars.kbserver.model

import com.typesafe.config.ConfigFactory
import org.mbari.vars.kbserver.Constants

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T14:14:00
 */
case class DbParams(
  user: String,
  password: String,
  url: String,
  driver: String,
  name: String = "Auto"
)
