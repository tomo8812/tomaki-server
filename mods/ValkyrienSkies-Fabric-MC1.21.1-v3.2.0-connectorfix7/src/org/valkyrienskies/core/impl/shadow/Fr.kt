package org.valkyrienskies.core.impl.shadow

import com.fasterxml.jackson.databind.JsonNode
import io.netty.buffer.ByteBuf

public class Fr<T>(p0: Fq<Any>, p1: Any? = null) {
   private final val a: Fq<Any>
   public final var b: Any?

   init {
      this.a = var1
      this.b = (T)var2
   }

   public fun a(p0: ByteBuf): Any {
      val var10000: Fq = this.a
      val var10001: Any = this.b
      val var3: JsonNode
      this.b = (T)(var3 = (JsonNode)var10000.a(var10001, var1))
      return (T)var3
   }
}
