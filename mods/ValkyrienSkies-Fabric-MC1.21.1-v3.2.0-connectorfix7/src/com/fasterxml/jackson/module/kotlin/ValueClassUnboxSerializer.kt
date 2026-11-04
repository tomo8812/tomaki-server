package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

public object ValueClassUnboxSerializer : StdSerializer(Object::class.java) {
   public override fun serialize(value: Any, gen: JsonGenerator, provider: SerializerProvider) {
      val unboxed: Any = value.getClass().getMethod("unbox-impl").invoke(value)
      if (unboxed == null) {
         provider.findNullValueSerializer(null).serialize(null, gen, provider)
      } else {
         provider.findValueSerializer(unboxed.getClass()).serialize(unboxed, gen, provider)
      }
   }
}
