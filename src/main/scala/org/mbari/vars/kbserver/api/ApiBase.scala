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

package org.mbari.vars.kbserver.api

import org.scalatra.ScalatraServlet
import java.time.Instant
import org.scalatra.util.conversion.TypeConverter
import java.util.UUID
import scala.util.Try
import java.time.Duration
import java.net.URI
import java.net.URL
import org.mbari.vars.kbserver.Constants

trait ApiBase extends ScalatraServlet {

  implicit protected val stringToUUID: TypeConverter[String, UUID] = new TypeConverter[String, UUID] {
    override def apply(s: String): Option[UUID] = Try(UUID.fromString(s)).toOption
  }

  implicit protected val stringToInstant: TypeConverter[String, Instant] = new TypeConverter[String, Instant] {
    override def apply(s: String): Option[Instant] = Try(Instant.parse(s)).toOption
  }

  implicit protected val stringToDuration: TypeConverter[String, Duration] = new TypeConverter[String, Duration] {
    override def apply(s: String): Option[Duration] = Try(Duration.ofMillis(s.toLong)).toOption
  }

  implicit protected val stringToURI: TypeConverter[String, URI] = new TypeConverter[String, URI] {
    override def apply(s: String): Option[URI] = Try(URI.create(s)).toOption
  }

  implicit protected val stringToURL: TypeConverter[String, URL] = new TypeConverter[String, URL] {
    override def apply(s: String): Option[URL] = Try(URI.create(s).toURL()).toOption
  }

  def toJson(obj: Any): String = Constants.GSON.toJson(obj)

}
