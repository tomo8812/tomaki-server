package org.valkyrienskies.core.impl.shadow

import java.util.ArrayDeque
import java.util.Queue
import java.util.concurrent.Future

public class FX(p0: Future<Ik?>? = null, p1: Queue<Ip> = ArrayDeque() as Queue) {
   public final var a: Future<Ik?>?
   public final var b: Queue<Ip>

   init {
      this.a = var1
      this.b = var2
   }

   public fun c(): Boolean {
      return this.a == null && this.b.isEmpty()
   }

   fun FX() {
      this(null, null, 3, null)
   }
}
