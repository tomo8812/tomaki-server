package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.exc.InputCoercionException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer

internal object UByteKeyDeserializer : StdKeyDeserializer(3, UByte::class.java) {
   public open fun deserializeKey(key: String?, ctxt: DeserializationContext): UByte? {
      val var3: Any = super.deserializeKey(key, ctxt)
      val var10000: UByte
      if (var3 == null) {
         var10000 = null
      } else {
         val var9: UByte = UnsignedNumbersKt.asUByte(var3 as java.lang.Short)
         if (var9 == null) {
            throw InputCoercionException(null, "Numeric value ($key) out of range of UByte (0 - 255).", JsonToken.VALUE_NUMBER_INT, UByte::class.java)
         }

         var10000 = UByte.box_impl/* $VF was: box-impl */(var9.unbox_impl/* $VF was: unbox-impl */())
      }

      return var10000
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun deserializeKey(p0: java.lang.String, p1: DeserializationContext) {
      this.deserializeKey_lj4SQcc/* $VF was: deserializeKey-lj4SQcc */(p0, p1)
   }
}
