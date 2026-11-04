package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.lang.reflect.Method

internal class ValueClassBoxSerializer<T>(outerClazz: Class<out Any>, innerClazz: Class<Any>) : StdSerializer(innerClazz) {
   private final val outerClazz: Class<out Any>
   private final val boxMethod: Method

   init {
      this.outerClazz = outerClazz
      this.boxMethod = this.outerClazz.getMethod("box-impl", innerClazz)
   }

   public override fun serialize(value: Any?, gen: JsonGenerator, provider: SerializerProvider) {
      val var10000: Method = this.boxMethod
      provider.findValueSerializer(this.outerClazz).serialize(var10000.invoke(null, value), gen, provider)
   }
}
