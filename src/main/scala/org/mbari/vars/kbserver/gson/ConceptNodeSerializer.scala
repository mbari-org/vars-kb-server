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
    t.author.foreach(r => obj.addProperty("author", r))
    obj
  }
}
