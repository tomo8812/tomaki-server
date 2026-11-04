package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

public object SequenceSerializer : StdSerializer(Sequence::class.java) {
   public open fun serialize(value: Sequence<*>, gen: JsonGenerator, provider: SerializerProvider) {
      provider.defaultSerializeValue(SequencesKt.toList(value), gen)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun serialize(p0: Any, p1: JsonGenerator, p2: SerializerProvider) {
      this.serialize(p0 as Sequence<*>, p1, p2)
   }
}
