package org.valkyrienskies.core.impl.shadow;

public final class Bk implements Bj {
   @Override
   public final Bl a(int var1, int var2, double var3) {
      Bm.a(var1, var2, var3);
      double var5 = 0.0;
      double var7 = 0.0;
      double var9 = (1.0 - var3) / 2.0;
      double var12 = new oo(2 * (var1 - var2 + 1), 2 * var2).a(1.0 - var9);
      if (var2 > 0) {
         var5 = var2 / (var2 + (var1 - var2 + 1) * var12);
      }

      double var15 = new oo(2 * (var2 + 1), 2 * (var1 - var2)).a(1.0 - var9);
      if (var2 > 0) {
         var7 = (var2 + 1) * var15 / (var1 - var2 + (var2 + 1) * var15);
      }

      return new Bl(var5, var7, var3);
   }
}
