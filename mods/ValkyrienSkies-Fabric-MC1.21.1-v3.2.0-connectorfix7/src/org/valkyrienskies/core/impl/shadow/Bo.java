package org.valkyrienskies.core.impl.shadow;

public final class Bo implements Bj {
   @Override
   public final Bl a(int var1, int var2, double var3) {
      Bm.a(var1, var2, var3);
      double var5 = (1.0 - var3) / 2.0;
      double var8;
      double var10 = BY.a(var8 = new oE().a(1.0 - var5), 2);
      double var12 = (double)var2 / var1;
      double var14 = 1.0 / (1.0 + 1.0 / var1 * var10);
      double var16 = var12 + 1.0 / (2 * var1) * var10;
      double var18 = var8 * BY.a(1.0 / var1 * var12 * (1.0 - var12) + 1.0 / (4.0 * BY.a((double)var1, 2)) * var10);
      double var20 = var14 * (var16 - var18);
      double var22 = var14 * (var16 + var18);
      return new Bl(var20, var22, var3);
   }
}
