package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.exc.InputCoercionException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer
import java.math.BigInteger

internal object ULongKeyDeserializer : StdKeyDeserializer(6, ULong::class.java) {
   public open fun deserializeKey(key: String?, ctxt: DeserializationContext): ULong? {
      val var10000: ULong
      if (key == null) {
         var10000 = null
      } else {
         val var11: ULong = UnsignedNumbersKt.asULong(BigInteger(key))
         if (var11 == null) {
            throw InputCoercionException(
               null, "Numeric value ($key) out of range of ULong (0 - 18446744073709551615).", JsonToken.VALUE_NUMBER_INT, ULong::class.java
            )
         }

         var10000 = ULong.box_impl/* $VF was: box-impl */(var11.unbox_impl/* $VF was: unbox-impl */())
      }

      return var10000
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun deserializeKey(p0: java.lang.String, p1: DeserializationContext) {
      this.deserializeKey_woJcscw/* $VF was: deserializeKey-woJcscw */(p0, p1)
   }
}
