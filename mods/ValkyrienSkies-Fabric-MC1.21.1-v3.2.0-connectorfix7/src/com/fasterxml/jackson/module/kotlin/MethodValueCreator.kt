package com.fasterxml.jackson.module.kotlin

import java.lang.reflect.Field
import kotlin.jvm.internal.DefaultConstructorMarker
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.KCallables
import kotlin.reflect.jvm.KCallablesJvm

internal class MethodValueCreator<T> private constructor(callable: KFunction<Any>, accessible: Boolean, companionObjectInstance: Any) : ValueCreator() {
   protected open val callable: KFunction<Any>
   protected open val accessible: Boolean
   public final val companionObjectInstance: Any
   public final val instanceParameter: KParameter

   init {
      this.callable = callable
      this.accessible = accessible
      this.companionObjectInstance = companionObjectInstance
      val var10001: KParameter = KCallables.getInstanceParameter(this.callable as KCallable)
      this.instanceParameter = var10001
   }

   @JvmSynthetic
   fun MethodValueCreator(callable: KFunction, accessible: Boolean, companionObjectInstance: Any, `$constructor_marker`: DefaultConstructorMarker) {
      this(callable, accessible, companionObjectInstance)
   }

   public companion object {
      public fun <T> of(callable: KFunction<Any>): MethodValueCreator<Any>? {
         if (KCallables.getExtensionReceiverParameter(callable as KCallable) != null) {
            return null
         } else {
            val var10000: KParameter = KCallables.getInstanceParameter(callable as KCallable)
            val possibleCompanion: KClass = JvmClassMappingKt.getKotlinClass(TypesKt.erasedType(var10000.getType()))
            if (!possibleCompanion.isCompanion()) {
               return null
            } else {
               val initialCallableAccessible: Boolean = KCallablesJvm.isAccessible(callable as KCallable)
               if (!initialCallableAccessible) {
                  KCallablesJvm.setAccessible(callable as KCallable, true)
               }

               var companionObjectInstance: Pair
               try {
                  val var29: Any = possibleCompanion.getObjectInstance()
                  companionObjectInstance = TuplesKt.to(var29, initialCallableAccessible)
               } catch (var18: IllegalAccessException) {
                  val `$this$firstOrNull$iv`: Array<Field> = JvmClassMappingKt.getJavaClass(possibleCompanion).getEnclosingClass().getDeclaredFields()
                  val var11: Array<Any> = `$this$firstOrNull$iv`
                  val it: Int = `$this$firstOrNull$iv`.length
                  var var13: Int = 0

                  while (true) {
                     if (var13 >= it) {
                        var27 = null
                        break
                     }

                     val `element$iv`: Any = var11[var13]
                     val var17: Class = (var11[var13] as Field).getType()
                     if (JvmClassMappingKt.getKotlinClass(var17).isCompanion()) {
                        var27 = `element$iv`
                        break
                     }

                     var13++
                  }

                  val var8: Field = var27 as Field
                  val var28: Pair
                  if (var27 as Field == null) {
                     var28 = null
                  } else {
                     var8.setAccessible(true)
                     var28 = TuplesKt.to(var8.get(null), false)
                  }

                  if (var28 == null) {
                     throw var18
                  }

                  companionObjectInstance = var28
               }

               return MethodValueCreator<>(callable, companionObjectInstance.component2() as java.lang.Boolean, companionObjectInstance.component1(), null)
            }
         }
      }
   }
}
