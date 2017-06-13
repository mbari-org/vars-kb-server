package org.mbari.vars.kbserver.gson

import java.lang.reflect.Type

import com.google.gson._

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-29T10:39:00
 */
class OptionSerializer extends JsonSerializer[Option[_]] {

  override def serialize(src: Option[_], typeOfSrc: Type, context: JsonSerializationContext): JsonElement =
    src match {
      case None => null
      case Some(x) => context.serialize(x)
    }

}
