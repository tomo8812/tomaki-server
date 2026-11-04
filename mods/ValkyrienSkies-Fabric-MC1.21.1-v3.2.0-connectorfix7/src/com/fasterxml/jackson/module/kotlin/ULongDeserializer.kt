package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.exc.InputCoercionException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.math.BigInteger

public object ULongDeserializer : StdDeserializer(ULong::class.java) {
   public open fun deserialize(p: JsonParser, ctxt: DeserializationContext): ULong {
      val var4: BigInteger = p.getBigIntegerValue()
      val var3: ULong = UnsignedNumbersKt.asULong(var4)
      if (var3 == null) {
         throw InputCoercionException(
            p, "Numeric value (${p.getText()}) out of range of ULong (0 - 18446744073709551615).", JsonToken.VALUE_NUMBER_INT, ULong::class.java
         )
      } else {
         return var3.unbox_impl/* $VF was: unbox-impl */()
      }
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun deserialize(p0: JsonParser, p1: DeserializationContext) {
      ULong.box_impl/* $VF was: box-impl */(this.deserialize_ZIaKswc/* $VF was: deserialize-ZIaKswc */(p0, p1))
   }
}
