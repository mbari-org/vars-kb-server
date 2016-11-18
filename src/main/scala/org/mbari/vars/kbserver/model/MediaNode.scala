package org.mbari.vars.kbserver.model

import java.net.URL
import java.util.regex.Pattern
import scala.collection.JavaConverters._
import vars.knowledgebase.Media

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
