package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import java.lang.annotation.Annotation
import java.lang.reflect.Method
import java.util.ArrayList
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.KClasses
import kotlin.reflect.jvm.ReflectJvmMapping

@Deprecated(message = "To be removed in 2.14", replaceWith = @ReplaceWith(expression = "with(receiver) { declaringClass.declaredMethods.any { it.name.contains('-') } }", imports = []))
private fun AnnotatedMethod.isInlineClass(): Boolean {
   val `$this$any$iv`: Array<Method> = `$this$isInlineClass`.getDeclaringClass().getDeclaredMethods()
   val var3: Array<Any> = `$this$any$iv`
   val var4: Int = `$this$any$iv`.length
   var var5: Int = 0

   var var10000: Boolean
   while (true) {
      if (var5 >= var4) {
         var10000 = false
         break
      }

      val var9: java.lang.String = (var3[var5] as Method).getName()
      if (StringsKt.contains$default(var9, '-', false, 2, null)) {
         var10000 = true
         break
      }

      var5++
   }

   return var10000
}

private fun AnnotatedConstructor.isKotlinConstructorWithParameters(): Boolean {
   if (`$this$isKotlinConstructorWithParameters`.getParameterCount() > 0) {
      val var1: Class = `$this$isKotlinConstructorWithParameters`.getDeclaringClass()
      if (KotlinModuleKt.isKotlinClass(var1) && !`$this$isKotlinConstructorWithParameters`.getDeclaringClass().isEnum()) {
         return true
      }
   }

   return false
}

private fun KFunction<*>.isPossibleSingleString(propertyNames: Set<String>): Boolean {
   if (`$this$isPossibleSingleString`.getParameters().size() == 1
      && !CollectionsKt.contains(propertyNames, (`$this$isPossibleSingleString`.getParameters().get(0) as KParameter).getName())
      && ReflectJvmMapping.getJavaType((`$this$isPossibleSingleString`.getParameters().get(0) as KParameter).getType()) == java.lang.String::class.java) {
      val var8: java.util.Iterator = (`$this$isPossibleSingleString`.getParameters().get(0) as KAnnotatedElement).getAnnotations().iterator()

      var var10000: Any
      while (true) {
         if (!var8.hasNext()) {
            var10000 = null
            break
         }

         val `element$iv$iv$iv`: Any = var8.next()
         if (`element$iv$iv$iv` as Annotation is JsonProperty) {
            var10000 = `element$iv$iv$iv`
            break
         }
      }

      if (var10000 as JsonProperty == null) {
         return true
      }
   }

   return false
}

private fun Collection<KFunction<*>>.filterOutSingleStringCallables(propertyNames: Set<String>): Collection<KFunction<*>> {
   val `$this$filterTo$iv$iv`: java.lang.Iterable = `$this$filterOutSingleStringCallables`
   val `destination$iv$iv`: java.util.Collection = ArrayList()

   for (`element$iv$iv` in `$this$filterTo$iv$iv`) {
      if (!isPossibleSingleString(`element$iv$iv` as KFunction<*>, propertyNames)) {
         `destination$iv$iv`.add(`element$iv$iv`)
      }
   }

   return `destination$iv$iv`
}

private fun KClass<*>.isPrimaryConstructor(kConstructor: KFunction<*>): Boolean {
   val it: KFunction = KClasses.getPrimaryConstructor(`$this$isPrimaryConstructor`)
   return it == kConstructor || it == null && `$this$isPrimaryConstructor`.getConstructors().size() == 1
}

@JvmSynthetic
fun `access$isPossibleSingleString`(`$receiver`: KFunction, propertyNames: java.util.Set): Boolean {
   isPossibleSingleString(`$receiver`, propertyNames)
}

@JvmSynthetic
fun `access$isPrimaryConstructor`(`$receiver`: KClass, kConstructor: KFunction): Boolean {
   isPrimaryConstructor(`$receiver`, kConstructor)
}

@JvmSynthetic
fun `access$filterOutSingleStringCallables`(`$receiver`: java.util.Collection, propertyNames: java.util.Set): java.util.Collection {
   filterOutSingleStringCallables(`$receiver`, propertyNames)
}

@JvmSynthetic
fun `access$isKotlinConstructorWithParameters`(`$receiver`: AnnotatedConstructor): Boolean {
   isKotlinConstructorWithParameters(`$receiver`)
}
