package org.mbari.vars.kbserver.model

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
