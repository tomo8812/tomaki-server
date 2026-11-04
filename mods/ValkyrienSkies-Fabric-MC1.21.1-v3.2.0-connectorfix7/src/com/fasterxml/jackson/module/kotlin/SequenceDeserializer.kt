package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

public object SequenceDeserializer : StdDeserializer(Sequence::class.java) {
   public open fun deserialize(p: JsonParser, ctxt: DeserializationContext): Sequence<*> {
      val var3: Any = ctxt.readValue(p, java.util.List.class)
      return CollectionsKt.asSequence(var3 as java.lang.Iterable)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun deserialize(p0: JsonParser, p1: DeserializationContext) {
      this.deserialize(p0, p1)
   }
}
