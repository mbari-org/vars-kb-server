package org.mbari.vars.kbserver.gson

import java.lang.reflect.Type

import com.google.gson.{JsonElement, JsonObject, JsonSerializationContext, JsonSerializer}
import org.mbari.vars.kbserver.dao.jdbc.generic.ImmutableConcept
import scala.collection.JavaConverters._

/**
  * @author Brian Schlining
  * @since 2018-02-11T13:45:00
  */
class ImmutableConceptSerializer extends JsonSerializer[ImmutableConcept] {
  override def serialize(src: ImmutableConcept, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
    val obj = new JsonObject
    obj.addProperty("name", src.name)
    if (src.alternativeNames.nonEmpty) {
      obj.add("alternative_names", context.serialize(src.alternativeNames.asJava))
    }
    src.rank.foreach(r => obj.addProperty("rank", r))
    if (src.children.nonEmpty) {
      obj.add("children", context.serialize(src.children.asJava))
    }
    obj
  }
}
