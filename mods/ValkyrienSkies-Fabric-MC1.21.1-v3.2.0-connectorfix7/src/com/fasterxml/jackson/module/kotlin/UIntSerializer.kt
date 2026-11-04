package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

public object UIntSerializer : StdSerializer(UInt::class.java) {
   public open fun serialize(value: UInt, gen: JsonGenerator, provider: SerializerProvider) {
      gen.writeNumber((long)value and 4294967295L)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun serialize(p0: Any, p1: JsonGenerator, p2: SerializerProvider) {
      this.serialize_OzbTU_A/* $VF was: serialize-OzbTU-A */((p0 as UInt).unbox_impl/* $VF was: unbox-impl */(), p1, p2)
   }
}
