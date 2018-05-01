package org.mbari.vars.kbserver.gson

import java.lang.reflect.Type

import com.google.gson.{ JsonElement, JsonObject, JsonSerializationContext, JsonSerializer }
import org.mbari.vars.kbserver.model.ConceptNode
import scala.collection.JavaConverters._

/**
 * Created by brian on 11/29/16.
 */
class ConceptNodeSerializer extends JsonSerializer[ConceptNode] {
  override def serialize(t: ConceptNode, `type`: Type, context: JsonSerializationContext): JsonElement = {
    val obj = new JsonObject
    obj.addProperty("name", t.name)
    obj.add("alternateNames", context.serialize(t.alternateNames.asJava))
    t.rank.foreach(r => obj.addProperty("rank", r))
    obj.add("media", context.serialize(t.media.asJava))
    obj.add("descriptors", context.serialize(t.descriptors.asJava))
    obj
  }
}
