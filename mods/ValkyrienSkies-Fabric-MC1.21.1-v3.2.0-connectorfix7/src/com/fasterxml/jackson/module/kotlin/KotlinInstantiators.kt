package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.deser.ValueInstantiator
import com.fasterxml.jackson.databind.deser.ValueInstantiators
import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator

internal class KotlinInstantiators(cache: ReflectionCache,
      nullToEmptyCollection: Boolean,
      nullToEmptyMap: Boolean,
      nullIsSameAsDefault: Boolean,
      strictNullChecks: Boolean
   ) :
   ValueInstantiators {
   private final val cache: ReflectionCache
   private final val nullToEmptyCollection: Boolean
   private final val nullToEmptyMap: Boolean
   private final val nullIsSameAsDefault: Boolean
   private final val strictNullChecks: Boolean

   init {
      this.cache = cache
      this.nullToEmptyCollection = nullToEmptyCollection
      this.nullToEmptyMap = nullToEmptyMap
      this.nullIsSameAsDefault = nullIsSameAsDefault
      this.strictNullChecks = strictNullChecks
   }

   public override fun findValueInstantiator(deserConfig: DeserializationConfig, beanDescriptor: BeanDescription, defaultInstantiator: ValueInstantiator): ValueInstantiator {
      val var4: Class = beanDescriptor.getBeanClass()
      val var10000: ValueInstantiator
      if (KotlinModuleKt.isKotlinClass(var4)) {
         if (!(defaultInstantiator.getClass()::class == StdValueInstantiator::class)) {
            throw IllegalStateException("KotlinValueInstantiator requires that the default ValueInstantiator is StdValueInstantiator")
         }

         var10000 = KotlinValueInstantiator(
            defaultInstantiator as StdValueInstantiator,
            this.cache,
            this.nullToEmptyCollection,
            this.nullToEmptyMap,
            this.nullIsSameAsDefault,
            this.strictNullChecks
         )
      } else {
         var10000 = defaultInstantiator
      }

      return var10000
   }
}
