package org.valkyrienskies.core.impl.shadow;

public final class mw {
   private final mz a = new mz();
   private final my b = new my();
   private final mx c = new mx();

   private mv a(int var1) {
      int var2 = var1;
      mz var3 = this.a;
      return new mv(this.a.a(var2));
   }

   private mv b(int var1, double var2, double var4) {
      int var6 = var1;
      mz var7 = this.a;
      return new mv(a(this.a.a(var6), var2, var4));
   }

   private mv b(int var1) {
      int var2 = var1;
      my var3 = this.b;
      return new mv(this.b.a(var2));
   }

   public final mv a(int var1, double var2, double var4) {
      int var6 = var1;
      my var7 = this.b;
      return new mv(a(this.b.a(var6), var2, var4));
   }

   private mA c(int var1) {
      int var2 = var1;
      mx var3 = this.c;
      return new mA(this.c.a(var2));
   }

   private static Co<double[], double[]> a(mu<? extends Number> var0, int var1) {
      return var0.a(var1);
   }

   private static Co<double[], double[]> a(Co<double[], double[]> var0, double var1, double var3) {
      double[] var5 = (double[])var0.a;
      double[] var11 = (double[])var0.b;
      double var7 = (var3 - var1) / 2.0;
      double var9 = var1 + var7;

      for (int var12 = 0; var12 < var5.length; var12++) {
         var5[var12] = var5[var12] * var7 + var9;
         var11[var12] *= var7;
      }

      return new Co<>(var5, var11);
   }
}
