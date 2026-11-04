package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.ser.Serializers

internal class KotlinSerializers : Serializers.Base {
   public override fun findSerializer(config: SerializationConfig?, type: JavaType, beanDesc: BeanDescription?): JsonSerializer<*>? {
      val rawClass: Class = type.getRawClass()
      val var10000: JsonSerializer
      if (Sequence.class.isAssignableFrom(rawClass)) {
         var10000 = SequenceSerializer.INSTANCE
      } else if (UByte.class.isAssignableFrom(rawClass)) {
         var10000 = UByteSerializer.INSTANCE
      } else if (UShort.class.isAssignableFrom(rawClass)) {
         var10000 = UShortSerializer.INSTANCE
      } else if (UInt.class.isAssignableFrom(rawClass)) {
         var10000 = UIntSerializer.INSTANCE
      } else if (ULong.class.isAssignableFrom(rawClass)) {
         var10000 = ULongSerializer.INSTANCE
      } else {
         var10000 = if (ExtensionsKt.isUnboxableValueClass(rawClass)) ValueClassSerializer.Companion.from(rawClass) else null
      }

      return var10000
   }
}
