package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.exc.InputCoercionException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

public object UByteDeserializer : StdDeserializer(UByte::class.java) {
   public open fun deserialize(p: JsonParser, ctxt: DeserializationContext): UByte {
      val var3: UByte = UnsignedNumbersKt.asUByte(p.getShortValue())
      if (var3 == null) {
         throw InputCoercionException(p, "Numeric value (${p.getText()}) out of range of UByte (0 - 255).", JsonToken.VALUE_NUMBER_INT, UByte::class.java)
      } else {
         return var3.unbox_impl/* $VF was: unbox-impl */()
      }
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun deserialize(p0: JsonParser, p1: DeserializationContext) {
      UByte.box_impl/* $VF was: box-impl */(this.deserialize_Iymvxus/* $VF was: deserialize-Iymvxus */(p0, p1))
   }
}
