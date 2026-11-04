package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.exc.InputCoercionException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

public object UShortDeserializer : StdDeserializer(UShort::class.java) {
   public open fun deserialize(p: JsonParser, ctxt: DeserializationContext): UShort {
      val var3: UShort = UnsignedNumbersKt.asUShort(p.getIntValue())
      if (var3 == null) {
         throw InputCoercionException(p, "Numeric value (${p.getText()}) out of range of UShort (0 - 65535).", JsonToken.VALUE_NUMBER_INT, UShort::class.java)
      } else {
         return var3.unbox_impl/* $VF was: unbox-impl */()
      }
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun deserialize(p0: JsonParser, p1: DeserializationContext) {
      UShort.box_impl/* $VF was: box-impl */(this.deserialize_ErzVvmY/* $VF was: deserialize-ErzVvmY */(p0, p1))
   }
}
