package org.valkyrienskies.core.impl.shadow;

import java.util.concurrent.TimeUnit;

public final class qv implements qJ {
   private final long a;
   private long b = -1L;

   private qv(long var1) {
      this(var1, TimeUnit.SECONDS);
   }

   private qv(long var1, TimeUnit var3) {
      if (var1 < 0L) {
         throw new pn(var1, 0, true);
      }

      this.a = var3.toNanos(var1);
   }

   @Override
   public final boolean a() {
      if (this.b < 0L) {
         this.b = System.nanoTime() + this.a;
      }

      return System.nanoTime() >= this.b;
   }
}
