package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.lang.reflect.Method
import kotlin.jvm.internal.DefaultConstructorMarker

internal sealed class ValueClassSerializer<T> protected constructor(t: Class<Any>) : StdSerializer(t) {
   @JvmSynthetic
   open fun ValueClassSerializer(t: Class, `$constructor_marker`: DefaultConstructorMarker) {
      this(t)
   }

   public companion object {
      public fun from(t: Class<*>): StdSerializer<*> {
         val var3: Method = KotlinSerializersKt.access$getStaticJsonValueGetter(t)
         val var10000: ValueClassSerializer.StaticJsonValue = if (var3 == null) null else ValueClassSerializer.StaticJsonValue(t, var3)
         return if (var10000 == null) ValueClassUnboxSerializer.INSTANCE else var10000
      }
   }

   public class StaticJsonValue<T>(t: Class<Any>, staticJsonValueGetter: Method) : ValueClassSerializer(t) {
      private final val staticJsonValueGetter: Method
      private final val unboxMethod: Method

      init {
         this.staticJsonValueGetter = staticJsonValueGetter
         val var3: Method = t.getMethod("unbox-impl")
         this.unboxMethod = var3
      }

      public override fun serialize(value: Any, gen: JsonGenerator, provider: SerializerProvider) {
         val var7: Any = this.staticJsonValueGetter.invoke(null, this.unboxMethod.invoke(value))
         val var14: Unit
         if (var7 == null) {
            var14 = null
         } else {
            provider.findValueSerializer(var7.getClass()).serialize(var7, gen, provider)
            var14 = Unit.INSTANCE
         }

         if (var14 == null) {
            provider.findNullValueSerializer(null).serialize(null, gen, provider)
         }
      }
   }
}
