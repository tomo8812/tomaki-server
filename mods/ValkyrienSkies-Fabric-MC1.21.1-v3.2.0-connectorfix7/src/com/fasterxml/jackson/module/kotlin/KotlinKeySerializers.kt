package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.ser.Serializers

internal class KotlinKeySerializers : Serializers.Base {
   public override fun findSerializer(config: SerializationConfig, type: JavaType, beanDesc: BeanDescription): JsonSerializer<*>? {
      val var4: Class = type.getRawClass()
      return if (ExtensionsKt.isUnboxableValueClass(var4)) ValueClassUnboxKeySerializer.INSTANCE else null
   }
}
