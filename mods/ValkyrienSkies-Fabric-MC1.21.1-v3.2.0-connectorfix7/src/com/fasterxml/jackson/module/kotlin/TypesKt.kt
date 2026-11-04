package com.fasterxml.jackson.module.kotlin

import java.lang.reflect.Array
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.lang.reflect.WildcardType
import kotlin.reflect.KType
import kotlin.reflect.jvm.KTypesJvm

internal fun Type.erasedType(): Class<Any> {
   val var10000: Class
   if (`$this$erasedType` is Class) {
      var10000 = `$this$erasedType` as Class
   } else if (`$this$erasedType` is ParameterizedType) {
      val elementType: Type = (`$this$erasedType` as ParameterizedType).getRawType()
      var10000 = erasedType(elementType)
   } else if (`$this$erasedType` is GenericArrayType) {
      val testArray: Type = (`$this$erasedType` as GenericArrayType).getGenericComponentType()
      val var6: Any = Array.newInstance(erasedType(testArray), 0)
      var10000 = var6.getClass()
   } else {
      if (`$this$erasedType` is TypeVariable) {
         throw IllegalStateException("Not sure what to do here yet")
      }

      if (`$this$erasedType` !is WildcardType) {
         throw IllegalStateException("Should not get here.")
      }

      val var5: Type = (`$this$erasedType` as WildcardType).getUpperBounds()[0]
      var10000 = erasedType(var5)
   }

   return var10000
}

internal fun KType.erasedType(): Class<out Any> {
   return JvmClassMappingKt.getJavaClass(KTypesJvm.getJvmErasure(`$this$erasedType`))
}
