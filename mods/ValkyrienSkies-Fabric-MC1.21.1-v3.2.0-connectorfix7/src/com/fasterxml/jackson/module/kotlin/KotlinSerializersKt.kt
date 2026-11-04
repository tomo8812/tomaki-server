package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.annotation.JsonValue
import java.lang.annotation.Annotation
import java.lang.reflect.Method
import java.lang.reflect.Modifier

private fun Class<*>.getStaticJsonValueGetter(): Method? {
   val var1: Array<Method> = `$this$getStaticJsonValueGetter`.getDeclaredMethods()
   val var5: Array<Any> = var1
   val var6: Int = var1.length
   var var7: Int = 0

   var var22: Any
   while (true) {
      if (var7 >= var6) {
         var22 = null
         break
      }

      var var8: Any
      run label54@{
         var8 = var5[var7]
         val method: Method = var5[var7] as Method
         if (Modifier.isStatic((var5[var7] as Method).getModifiers())) {
            val `$this$any$iv`: Array<Annotation> = method.getAnnotations()
            val var13: Array<Any> = `$this$any$iv`
            val var14: Int = `$this$any$iv`.length
            var var15: Int = 0

            while (true) {
               if (var15 >= var14) {
                  var10000 = false
                  break
               }

               if (var13[var15] as Annotation is JsonValue) {
                  var10000 = true
                  break
               }

               var15++
            }

            if (var10000) {
               var21 = true
               return@label54
            }
         }

         var21 = false
      }

      if (var21) {
         var22 = var8
         break
      }

      var7++
   }

   return var22 as Method
}

@JvmSynthetic
fun `access$getStaticJsonValueGetter`(`$receiver`: Class): Method {
   getStaticJsonValueGetter(`$receiver`)
}
