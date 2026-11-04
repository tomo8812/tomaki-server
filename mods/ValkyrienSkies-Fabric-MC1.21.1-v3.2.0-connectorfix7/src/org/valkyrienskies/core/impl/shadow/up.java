package org.valkyrienskies.core.impl.shadow;

public final class up {
   private final lS a;
   private final double b;

   public up(double var1, double var3, long var5) {
      if (var1 <= 0.0) {
         throw new pk(var1);
      }

      if (var3 >= 0.0) {
         throw new pm(var3, 0, false);
      }

      if (var5 <= 1L) {
         throw new pk(var5);
      }

      double var7 = var1;
      double var9 = var5;
      double var11 = var3 * 4.0 / var1;
      this.a = new lS(var7, var9, var11, 1.0, 0.0, 1.0);
      double var13 = this.a.a(0.0);
      this.b = var7 / var13;
   }

   public final double a(long var1) {
      return this.b * this.a.a(var1);
   }
}
