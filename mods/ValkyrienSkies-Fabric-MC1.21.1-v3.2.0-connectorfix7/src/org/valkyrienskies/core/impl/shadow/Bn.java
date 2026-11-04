package org.valkyrienskies.core.impl.shadow;

public final class Bn implements Bj {
   @Override
   public final Bl a(int var1, int var2, double var3) {
      Bm.a(var1, var2, var3);
      double var5 = (double)var2 / var1;
      double var7 = (1.0 - var3) / 2.0;
      double var10 = new oE().a(1.0 - var7) * BY.a(1.0 / var1 * var5 * (1.0 - var5));
      return new Bl(var5 - var10, var5 + var10, var3);
   }
}
