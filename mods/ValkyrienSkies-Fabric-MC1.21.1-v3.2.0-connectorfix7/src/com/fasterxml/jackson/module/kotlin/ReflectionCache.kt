package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams
import com.fasterxml.jackson.databind.util.LRUMap
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import kotlin.jvm.internal.DefaultConstructorMarker
import kotlin.jvm.internal.Intrinsics
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.ReflectJvmMapping

internal class ReflectionCache(reflectionCacheSize: Int) {
   private final val javaClassToKotlin: LRUMap<Class<Any>, KClass<Any>>
   private final val javaConstructorToKotlin: LRUMap<Constructor<Any>, KFunction<Any>>
   private final val javaMethodToKotlin: LRUMap<Method, KFunction<*>>
   private final val javaConstructorToValueCreator: LRUMap<Constructor<Any>, ConstructorValueCreator<*>>
   private final val javaMethodToValueCreator: LRUMap<Method, MethodValueCreator<*>>
   private final val javaConstructorIsCreatorAnnotated: LRUMap<AnnotatedConstructor, Boolean>
   private final val javaMemberIsRequired: LRUMap<AnnotatedMember, com.fasterxml.jackson.module.kotlin.ReflectionCache.BooleanTriState?>
   private final val kotlinGeneratedMethod: LRUMap<AnnotatedMethod, Boolean>

   init {
      this.javaClassToKotlin = LRUMap<>(reflectionCacheSize, reflectionCacheSize)
      this.javaConstructorToKotlin = LRUMap<>(reflectionCacheSize, reflectionCacheSize)
      this.javaMethodToKotlin = LRUMap<>(reflectionCacheSize, reflectionCacheSize)
      this.javaConstructorToValueCreator = LRUMap<>(reflectionCacheSize, reflectionCacheSize)
      this.javaMethodToValueCreator = LRUMap<>(reflectionCacheSize, reflectionCacheSize)
      this.javaConstructorIsCreatorAnnotated = LRUMap<>(reflectionCacheSize, reflectionCacheSize)
      this.javaMemberIsRequired = LRUMap<>(reflectionCacheSize, reflectionCacheSize)
      this.kotlinGeneratedMethod = LRUMap<>(reflectionCacheSize, reflectionCacheSize)
   }

   public fun kotlinFromJava(key: Class<Any>): KClass<Any> {
      val var2: KClass = this.javaClassToKotlin.get(key)
      val var10000: KClass
      if (var2 == null) {
         val it: KClass = JvmClassMappingKt.getKotlinClass(key)
         val var8: KClass = this.javaClassToKotlin.putIfAbsent(key, it)
         var10000 = if (var8 == null) it else var8
      } else {
         var10000 = var2
      }

      return var10000
   }

   public fun kotlinFromJava(key: Constructor<Any>): KFunction<Any>? {
      val var2: KFunction = this.javaConstructorToKotlin.get(key)
      val var10000: KFunction
      if (var2 == null) {
         val var3: KFunction = ReflectJvmMapping.getKotlinFunction(key)
         if (var3 == null) {
            var10000 = null
         } else {
            val var9: KFunction = this.javaConstructorToKotlin.putIfAbsent(key, var3)
            var10000 = if (var9 == null) var3 else var9
         }
      } else {
         var10000 = var2
      }

      return var10000
   }

   public fun kotlinFromJava(key: Method): KFunction<*>? {
      val var2: KFunction = this.javaMethodToKotlin.get(key)
      val var10000: KFunction
      if (var2 == null) {
         val var3: KFunction = ReflectJvmMapping.getKotlinFunction(key)
         if (var3 == null) {
            var10000 = null
         } else {
            val var9: KFunction = this.javaMethodToKotlin.putIfAbsent(key, var3)
            var10000 = if (var9 == null) var3 else var9
         }
      } else {
         var10000 = var2
      }

      return var10000
   }

   public fun valueCreatorFromJava(_withArgsCreator: AnnotatedWithParams): ValueCreator<*>? {
      var var25: ValueCreator
      if (_withArgsCreator is AnnotatedConstructor) {
         val var4: Constructor = (_withArgsCreator as AnnotatedConstructor).getAnnotated()
         if (var4 == null) {
            throw NullPointerException("null cannot be cast to non-null type java.lang.reflect.Constructor<kotlin.Any>")
         }

         val var14: ConstructorValueCreator = this.javaConstructorToValueCreator.get(var4)
         if (var14 == null) {
            val var5: KFunction = this.kotlinFromJava(var4)
            if (var5 == null) {
               var25 = null
            } else {
               val value: ConstructorValueCreator = ConstructorValueCreator(var5)
               val var12: ConstructorValueCreator = this.javaConstructorToValueCreator.putIfAbsent(var4, value)
               var25 = if (var12 == null) value else var12
            }
         } else {
            var25 = var14
         }

         var25 = var25
      } else {
         if (_withArgsCreator !is AnnotatedMethod) {
            throw IllegalStateException(
               Intrinsics.stringPlus(
                  "Expected a constructor or method to create a Kotlin object, instead found ", _withArgsCreator.getAnnotated().getClass().getName()
               )
            )
         }

         val var15: Method = (_withArgsCreator as AnnotatedMethod).getAnnotated()
         if (var15 == null) {
            throw NullPointerException("null cannot be cast to non-null type java.lang.reflect.Method")
         }

         val var16: MethodValueCreator = this.javaMethodToValueCreator.get(var15)
         if (var16 == null) {
            val var17: KFunction = this.kotlinFromJava(var15)
            if (var17 == null) {
               var25 = null
            } else {
               val var23: MethodValueCreator = MethodValueCreator.Companion.of(var17)
               val var24: MethodValueCreator = this.javaMethodToValueCreator.putIfAbsent(var15, var23)
               var25 = if (var24 == null) var23 else var24
            }
         } else {
            var25 = var16
         }

         var25 = var25
      }

      return var25
   }

   public fun checkConstructorIsCreatorAnnotated(key: AnnotatedConstructor, calc: (AnnotatedConstructor) -> Boolean): Boolean {
      val var3: java.lang.Boolean = this.javaConstructorIsCreatorAnnotated.get(key)
      val var10000: Boolean
      if (var3 == null) {
         val it: Boolean = calc(key) as java.lang.Boolean
         val var9: java.lang.Boolean = this.javaConstructorIsCreatorAnnotated.putIfAbsent(key, it)
         var10000 = if (var9 == null) it else var9
      } else {
         var10000 = var3
      }

      return var10000
   }

   public fun javaMemberIsRequired(key: AnnotatedMember, calc: (AnnotatedMember) -> Boolean?): Boolean? {
      val var4: ReflectionCache.BooleanTriState = this.javaMemberIsRequired.get(key)
      val var3: java.lang.Boolean = if (var4 == null) null else var4.value
      val var10000: java.lang.Boolean
      if (var3 == null) {
         val it: java.lang.Boolean = calc(key) as java.lang.Boolean
         val var9: ReflectionCache.BooleanTriState = this.javaMemberIsRequired.putIfAbsent(key, ReflectionCache.BooleanTriState.Companion.fromBoolean(it))
         if (var9 == null) {
            var10000 = it
         } else {
            val var10: java.lang.Boolean = var9.value
            var10000 = if (var10 == null) it else var10
         }
      } else {
         var10000 = var3
      }

      return var10000
   }

   public fun isKotlinGeneratedMethod(key: AnnotatedMethod, calc: (AnnotatedMethod) -> Boolean): Boolean {
      val var3: java.lang.Boolean = this.kotlinGeneratedMethod.get(key)
      val var10000: Boolean
      if (var3 == null) {
         val it: Boolean = calc(key) as java.lang.Boolean
         val var9: java.lang.Boolean = this.kotlinGeneratedMethod.putIfAbsent(key, it)
         var10000 = if (var9 == null) it else var9
      } else {
         var10000 = var3
      }

      return var10000
   }

   public sealed class BooleanTriState protected constructor(value: Boolean?) {
      public final val value: Boolean?

      init {
         this.value = value
      }

      @JvmSynthetic
      open fun BooleanTriState(value: java.lang.Boolean, `$constructor_marker`: DefaultConstructorMarker) {
         this(value)
      }

      public companion object {
         private final val EMPTY: com.fasterxml.jackson.module.kotlin.ReflectionCache.BooleanTriState.Empty
         private final val FALSE: com.fasterxml.jackson.module.kotlin.ReflectionCache.BooleanTriState.False
         private final val TRUE: com.fasterxml.jackson.module.kotlin.ReflectionCache.BooleanTriState.True

         public fun fromBoolean(value: Boolean?): com.fasterxml.jackson.module.kotlin.ReflectionCache.BooleanTriState {
            val var10000: ReflectionCache.BooleanTriState
            if (value == null) {
               var10000 = ReflectionCache.BooleanTriState.EMPTY
            } else if (value == true) {
               var10000 = ReflectionCache.BooleanTriState.TRUE
            } else {
               if (!(value == false)) {
                  throw NoWhenBranchMatchedException()
               }

               var10000 = ReflectionCache.BooleanTriState.FALSE
            }

            return var10000
         }
      }

      public class Empty : ReflectionCache.BooleanTriState(null)

      public class False : ReflectionCache.BooleanTriState(false)

      public class True : ReflectionCache.BooleanTriState(true)
   }
}
