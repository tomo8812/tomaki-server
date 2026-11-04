package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.exc.InputCoercionException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer

internal object UIntKeyDeserializer : StdKeyDeserializer(6, UInt::class.java) {
   public open fun deserializeKey(key: String?, ctxt: DeserializationContext): UInt? {
      val var3: Any = super.deserializeKey(key, ctxt)
      val var10000: UInt
      if (var3 == null) {
         var10000 = null
      } else {
         val var9: UInt = UnsignedNumbersKt.asUInt(var3 as java.lang.Long)
         if (var9 == null) {
            throw InputCoercionException(null, "Numeric value ($key) out of range of UInt (0 - 4294967295).", JsonToken.VALUE_NUMBER_INT, UInt::class.java)
         }

         var10000 = UInt.box_impl/* $VF was: box-impl */(var9.unbox_impl/* $VF was: unbox-impl */())
      }

      return var10000
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun deserializeKey(p0: java.lang.String, p1: DeserializationContext) {
      this.deserializeKey_uT2Fmlo/* $VF was: deserializeKey-uT2Fmlo */(p0, p1)
   }
}
