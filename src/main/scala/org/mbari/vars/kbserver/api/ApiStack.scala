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

import java.net.{ URI, URL }
import java.time.{ Duration, Instant }
import java.util.UUID

import org.mbari.vars.kbserver.Constants
import org.scalatra.util.conversion.TypeConverter
import org.scalatra.{ ContentEncodingSupport, FutureSupport, ScalatraServlet }
import org.slf4j.LoggerFactory

import scala.util.Try

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T13:29:00
 */
abstract class ApiStack extends ScalatraServlet
    with ContentEncodingSupport with FutureSupport {

  protected[this] val log = LoggerFactory.getLogger(getClass)

  //protected[this] implicit val jsonFormats: Formats = DefaultFormats ++ JavaTypesSerializers.all

  protected implicit val stringToUUID = new TypeConverter[String, UUID] {
    override def apply(s: String): Option[UUID] = Try(UUID.fromString(s)).toOption
  }

  protected implicit val stringToInstant = new TypeConverter[String, Instant] {
    override def apply(s: String): Option[Instant] = Try(Instant.parse(s)).toOption
  }

  protected implicit val stringToDuration = new TypeConverter[String, Duration] {
    override def apply(s: String): Option[Duration] = Try(Duration.ofMillis(s.toLong)).toOption
  }

  protected implicit val stringToURI = new TypeConverter[String, URI] {
    override def apply(s: String): Option[URI] = Try(URI.create(s)).toOption
  }

  protected implicit val stringToURL = new TypeConverter[String, URL] {
    override def apply(s: String): Option[URL] = Try(new URL(s)).toOption
  }

  def toJson(obj: Any): String = Constants.GSON.toJson(obj)

}
