package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier

public object KotlinBeanDeserializerModifier : BeanDeserializerModifier {
   public override fun modifyDeserializer(config: DeserializationConfig, beanDesc: BeanDescription, deserializer: JsonDeserializer<*>): JsonDeserializer<
         out Any
      > {
      val modifiedFromParent: JsonDeserializer = super.modifyDeserializer(config, beanDesc, deserializer)
      val var6: Class = beanDesc.getBeanClass()
      val objectSingletonInstance: Any = KotlinBeanDeserializerModifierKt.access$objectSingletonInstance(var6)
      val var10000: JsonDeserializer
      if (objectSingletonInstance != null) {
         var10000 = KotlinObjectSingletonDeserializer(objectSingletonInstance, modifiedFromParent)
      } else {
         var10000 = modifiedFromParent
      }

      return var10000
   }
}
