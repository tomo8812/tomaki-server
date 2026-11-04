package org.valkyrienskies.mod.common.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import net.minecraft.class_2338

public class BlockPosSerializer : JsonSerializer<class_2338> {
   @Throws(java/io/IOException::class)
   public open fun serialize(value: class_2338, gen: JsonGenerator, serializers: SerializerProvider) {
      gen.writeStartObject()
      gen.writeNumberField("long", value.method_10063())
      gen.writeEndObject()
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun serialize(p0: Any, p1: JsonGenerator, p2: SerializerProvider) {
      this.serialize(p0 as class_2338, p1, p2)
   }
}
