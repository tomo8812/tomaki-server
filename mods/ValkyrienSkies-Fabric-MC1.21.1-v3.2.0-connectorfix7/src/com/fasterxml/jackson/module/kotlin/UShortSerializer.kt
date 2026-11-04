package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

public object UShortSerializer : StdSerializer(UShort::class.java) {
   public open fun serialize(value: UShort, gen: JsonGenerator, provider: SerializerProvider) {
      gen.writeNumber(value and 65535)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun serialize(p0: Any, p1: JsonGenerator, p2: SerializerProvider) {
      this.serialize__TFR7lA/* $VF was: serialize-_TFR7lA */((p0 as UShort).unbox_impl/* $VF was: unbox-impl */(), p1, p2)
   }
}
