package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

public object UByteSerializer : StdSerializer(UByte::class.java) {
   public open fun serialize(value: UByte, gen: JsonGenerator, provider: SerializerProvider) {
      gen.writeNumber((short)((short)value and 255))
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun serialize(p0: Any, p1: JsonGenerator, p2: SerializerProvider) {
      this.serialize_d_jbwkw/* $VF was: serialize-d-jbwkw */((p0 as UByte).unbox_impl/* $VF was: unbox-impl */(), p1, p2)
   }
}
