package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.exc.InputCoercionException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

public object UIntDeserializer : StdDeserializer(UInt::class.java) {
   public open fun deserialize(p: JsonParser, ctxt: DeserializationContext): UInt {
      val var3: UInt = UnsignedNumbersKt.asUInt(p.getLongValue())
      if (var3 == null) {
         throw InputCoercionException(p, "Numeric value (${p.getText()}) out of range of UInt (0 - 4294967295).", JsonToken.VALUE_NUMBER_INT, UInt::class.java)
      } else {
         return var3.unbox_impl/* $VF was: unbox-impl */()
      }
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun deserialize(p0: JsonParser, p1: DeserializationContext) {
      UInt.box_impl/* $VF was: box-impl */(this.deserialize_xfHcF5w/* $VF was: deserialize-xfHcF5w */(p0, p1))
   }
}
