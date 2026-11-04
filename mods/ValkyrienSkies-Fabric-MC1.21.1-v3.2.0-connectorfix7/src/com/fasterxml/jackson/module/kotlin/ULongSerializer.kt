package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.math.BigInteger

public object ULongSerializer : StdSerializer(ULong::class.java) {
   public open fun serialize(value: ULong, gen: JsonGenerator, provider: SerializerProvider) {
      if (value >= 0L) {
         gen.writeNumber(value)
      } else {
         gen.writeNumber(BigInteger(java.lang.Long.toUnsignedString(value)))
      }
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun serialize(p0: Any, p1: JsonGenerator, p2: SerializerProvider) {
      this.serialize_E0BElUM/* $VF was: serialize-E0BElUM */((p0 as ULong).unbox_impl/* $VF was: unbox-impl */(), p1, p2)
   }
}
