package org.mbari.vars.kbserver.gson

import java.lang.reflect.Type

import com.google.gson.{ JsonElement, JsonObject, JsonSerializationContext, JsonSerializer }
import org.mbari.vars.kbserver.model.PhylogenyNode
import scala.collection.JavaConverters._

/**
 *
 *
 * @author Brian Schlining
 * @since 2016-11-29T10:44:00
 */
class PhylogenyNodeSerializer extends JsonSerializer[PhylogenyNode] {
  override def serialize(src: PhylogenyNode, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {

    val obj = new JsonObject
    obj.addProperty("name", src.name)
    src.rank.foreach(r => obj.addProperty("rank", r))
    obj.add("children", context.serialize(src.children.asJava))
    obj

  }
}
