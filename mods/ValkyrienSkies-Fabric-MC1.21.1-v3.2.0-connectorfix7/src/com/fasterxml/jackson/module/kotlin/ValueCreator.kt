package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.MapperFeature
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.Intrinsics
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

internal sealed class ValueCreator<T> protected constructor() {
   public final val valueParameters: List<KParameter> by LazyKt.lazy({
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function0)
      public final get() {
         return this.valueParameters$delegate.getValue() as MutableList<KParameter>
      }


   protected abstract val callable: KFunction<Any>

   protected abstract val accessible: Boolean

   public fun checkAccessibility(ctxt: DeserializationContext) {
      if ((this.accessible || !ctxt.getConfig().isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS))
         && (!this.accessible || !ctxt.getConfig().isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS))) {
         throw IllegalAccessException(Intrinsics.stringPlus("Cannot access to function or companion object instance, target: ", this.callable))
      }
   }

   public fun callBy(args: Map<KParameter, Any?>): Any {
      return (T)this.callable.callBy(args)
   }
}
