package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.lang.reflect.Method

internal object ValueClassUnboxKeySerializer : StdSerializer(Object::class.java) {
   public override fun serialize(value: Any, gen: JsonGenerator, provider: SerializerProvider) {
      val method: Method = value.getClass().getMethod("unbox-impl")
      val unboxed: Any = method.invoke(value)
      if (unboxed == null) {
         provider.findNullKeySerializer(provider.getTypeFactory().constructType(method.getGenericReturnType()), null).serialize(null, gen, provider)
      } else {
         provider.findKeySerializer(unboxed.getClass(), null).serialize(unboxed, gen, provider)
      }
   }
}
