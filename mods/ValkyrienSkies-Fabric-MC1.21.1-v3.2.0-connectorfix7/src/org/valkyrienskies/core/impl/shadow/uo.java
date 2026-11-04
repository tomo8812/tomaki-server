package org.valkyrienskies.core.impl.shadow;

public final class uo {
   private final double a;
   private final double b;

   public uo(double var1, double var3, long var5) {
      if (var1 <= 0.0) {
         throw new pk(var1);
      }

      if (var3 <= 0.0) {
         throw new pk(var3);
      }

      if (var3 >= var1) {
         throw new pm(var3, var1, false);
      }

      if (var5 <= 0L) {
         throw new pk(var5);
      }

      this.a = var1;
      this.b = -BY.k(var3 / var1) / var5;
   }

   public final double a(long var1) {
      return this.a * BY.i(-var1 * this.b);
   }
}
