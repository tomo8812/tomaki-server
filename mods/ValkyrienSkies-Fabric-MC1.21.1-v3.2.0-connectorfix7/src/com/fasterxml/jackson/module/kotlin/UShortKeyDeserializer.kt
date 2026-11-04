package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.exc.InputCoercionException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer

internal object UShortKeyDeserializer : StdKeyDeserializer(5, UShort::class.java) {
   public open fun deserializeKey(key: String?, ctxt: DeserializationContext): UShort? {
      val var3: Any = super.deserializeKey(key, ctxt)
      val var10000: UShort
      if (var3 == null) {
         var10000 = null
      } else {
         val var9: UShort = UnsignedNumbersKt.asUShort(var3 as Int)
         if (var9 == null) {
            throw InputCoercionException(null, "Numeric value ($key) out of range of UShort (0 - 65535).", JsonToken.VALUE_NUMBER_INT, UShort::class.java)
         }

         var10000 = UShort.box_impl/* $VF was: box-impl */(var9.unbox_impl/* $VF was: unbox-impl */())
      }

      return var10000
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun deserializeKey(p0: java.lang.String, p1: DeserializationContext) {
      this.deserializeKey_QDdqv_c/* $VF was: deserializeKey-QDdqv-c */(p0, p1)
   }
}
