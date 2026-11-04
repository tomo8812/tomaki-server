package org.valkyrienskies.core.impl.shadow;

public final class Bi implements Bj {
   @Override
   public final Bl a(int var1, int var2, double var3) {
      Bm.a(var1, var2, var3);
      double var5 = (1.0 - var3) / 2.0;
      double var8;
      double var10 = BY.a(var8 = new oE().a(1.0 - var5), 2);
      double var12 = var1 + var10;
      double var14 = 1.0 / var12 * (var2 + var10 * 0.5);
      double var16 = var8 * BY.a(1.0 / var12 * var14 * (1.0 - var14));
      return new Bl(var14 - var16, var14 + var16, var3);
   }
}
