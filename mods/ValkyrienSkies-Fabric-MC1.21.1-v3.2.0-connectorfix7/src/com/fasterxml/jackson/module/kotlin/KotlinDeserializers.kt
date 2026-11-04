package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.Deserializers

internal class KotlinDeserializers : Deserializers.Base {
   public override fun findBeanDeserializer(type: JavaType, config: DeserializationConfig?, beanDesc: BeanDescription?): JsonDeserializer<*>? {
      return if (type.isInterface() && type.getRawClass() == Sequence::class.java)
         SequenceDeserializer.INSTANCE
         else
         (
            if (type.getRawClass() == Regex::class.java)
               RegexDeserializer.INSTANCE
               else
               (
                  if (type.getRawClass() == UByte::class.java)
                     UByteDeserializer.INSTANCE
                     else
                     (
                        if (type.getRawClass() == UShort::class.java)
                           UShortDeserializer.INSTANCE
                           else
                           (
                              if (type.getRawClass() == UInt::class.java)
                                 UIntDeserializer.INSTANCE
                                 else
                                 (if (type.getRawClass() == ULong::class.java) ULongDeserializer.INSTANCE else null)
                           )
                     )
               )
         )
      }
}
