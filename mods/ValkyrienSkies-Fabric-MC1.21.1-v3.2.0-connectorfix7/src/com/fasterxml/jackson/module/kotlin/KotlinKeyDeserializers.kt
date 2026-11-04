package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializers

internal object KotlinKeyDeserializers : StdKeyDeserializers {
   public override fun findKeyDeserializer(type: JavaType, config: DeserializationConfig?, beanDesc: BeanDescription?): KeyDeserializer? {
      val var4: Class = type.getRawClass()
      return if (var4 == UByte::class.java)
         UByteKeyDeserializer.INSTANCE
         else
         (
            if (var4 == UShort::class.java)
               UShortKeyDeserializer.INSTANCE
               else
               (if (var4 == UInt::class.java) UIntKeyDeserializer.INSTANCE else (if (var4 == ULong::class.java) ULongKeyDeserializer.INSTANCE else null))
         )
      }
}
