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

package org.mbari.vars.kbserver.model

import org.mbari.kb.core.knowledgebase.Media

import java.net.URL
import java.util.regex.Pattern

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-17T16:07:00
 */
case class MediaNode(
  url: URL,
  caption: String,
  credit: String,
  mimeType: String,
  isPrimary: Boolean
)

object MediaNode {
  def apply(media: Media): MediaNode =
    MediaNode(
      new URL(media.getUrl),
      media.getCaption,
      media.getCredit,
      resolveMimeType(media.getType, media.getUrl),
      media.isPrimary
    )

  private def resolveMimeType(t: String, url: String): String = {
    val ext = url.split(Pattern.quote(".")).last
    t match {
      case Media.TYPE_IMAGE => s"image/$ext"
      case Media.TYPE_VIDEO =>
        ext match {
          case "mov" => "video/quicktime"
          case _ => s"video/$ext"
        }
      case _ => "application/octet-stream"
    }
  }
}
