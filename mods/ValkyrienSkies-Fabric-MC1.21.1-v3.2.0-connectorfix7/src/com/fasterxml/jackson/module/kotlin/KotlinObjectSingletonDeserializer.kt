package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer

internal class KotlinObjectSingletonDeserializer(singletonInstance: Any, defaultDeserializer: JsonDeserializer<*>)
   : JsonDeserializer<Object>,
   ContextualDeserializer,
   ResolvableDeserializer {
   private final val singletonInstance: Any
   private final val defaultDeserializer: JsonDeserializer<*>

   init {
      this.singletonInstance = singletonInstance
      this.defaultDeserializer = defaultDeserializer
   }

   public override fun resolve(ctxt: DeserializationContext?) {
      if (this.defaultDeserializer is ResolvableDeserializer) {
         (this.defaultDeserializer as ResolvableDeserializer).resolve(ctxt)
      }
   }

   public override fun createContextual(ctxt: DeserializationContext?, property: BeanProperty?): JsonDeserializer<*> {
      val var10000: JsonDeserializer
      if (this.defaultDeserializer is ContextualDeserializer) {
         val var3: JsonDeserializer = (this.defaultDeserializer as ContextualDeserializer).createContextual(ctxt, property)
         var10000 = KotlinObjectSingletonDeserializerKt.asSingletonDeserializer(var3, this.singletonInstance)
      } else {
         var10000 = this
      }

      return var10000
   }

   public override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Any {
      this.defaultDeserializer.deserialize(p, ctxt)
      return this.singletonInstance
   }
}
