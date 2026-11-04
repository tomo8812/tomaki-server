package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.lang.reflect.Method
import kotlin.jvm.internal.DefaultConstructorMarker

internal class ValueClassStaticJsonValueSerializer<T> private constructor(innerClazz: Class<Any>, staticJsonValueGetter: Method) : StdSerializer(innerClazz) {
   private final val staticJsonValueGetter: Method

   init {
      this.staticJsonValueGetter = staticJsonValueGetter
   }

   public override fun serialize(value: Any?, gen: JsonGenerator, provider: SerializerProvider) {
      val var6: Any = this.staticJsonValueGetter.invoke(null, value)
      val var13: Unit
      if (var6 == null) {
         var13 = null
      } else {
         provider.findValueSerializer(var6.getClass()).serialize(var6, gen, provider)
         var13 = Unit.INSTANCE
      }

      if (var13 == null) {
         provider.findNullValueSerializer(null).serialize(null, gen, provider)
      }
   }

   @JvmSynthetic
   fun ValueClassStaticJsonValueSerializer(innerClazz: Class, staticJsonValueGetter: Method, `$constructor_marker`: DefaultConstructorMarker) {
      this(innerClazz, staticJsonValueGetter)
   }

   public companion object {
      public fun <T> createdOrNull(outerClazz: Class<out Any>, innerClazz: Class<Any>): ValueClassStaticJsonValueSerializer<Any>? {
         val var3: Method = KotlinSerializersKt.access$getStaticJsonValueGetter(outerClazz)
         return if (var3 == null) null else ValueClassStaticJsonValueSerializer<>(innerClazz, var3, null)
      }
   }
}
