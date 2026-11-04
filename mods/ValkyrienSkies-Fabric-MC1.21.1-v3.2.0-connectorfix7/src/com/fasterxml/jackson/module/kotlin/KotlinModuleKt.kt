package com.fasterxml.jackson.module.kotlin

import java.lang.annotation.Annotation

private const val metadataFqName: String = "kotlin.Metadata"

public fun Class<*>.isKotlinClass(): Boolean {
   val `$this$any$iv`: Array<Annotation> = `$this$isKotlinClass`.getDeclaredAnnotations()
   val var3: Array<Any> = `$this$any$iv`
   val var4: Int = `$this$any$iv`.length
   var var5: Int = 0

   var var10000: Boolean
   while (true) {
      if (var5 >= var4) {
         var10000 = false
         break
      }

      if (JvmClassMappingKt.getJavaClass(JvmClassMappingKt.getAnnotationClass(var3[var5] as Annotation)).getName() == "kotlin.Metadata") {
         var10000 = true
         break
      }

      var5++
   }

   return var10000
}
