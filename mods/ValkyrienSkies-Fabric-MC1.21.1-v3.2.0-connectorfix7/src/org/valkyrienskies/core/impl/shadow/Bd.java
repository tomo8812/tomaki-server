package org.valkyrienskies.core.impl.shadow;

public final class Bd {
   private Bq a;

   public Bd() {
      this.a = new Bq(Bp.FIXED, Bs.AVERAGE$139d6b26);
   }

   private Bd(Bp var1, int var2) {
      this.a = new Bq(var1, var2);
   }

   private static void a(double[] var0, double[] var1) {
      if (var0 == null || var1 == null) {
         throw new pl();
      } else if (var0.length == 0 || var1.length == 0) {
         throw new pf();
      }
   }

   private static double[] b(double[] var0, double[] var1) {
      double[] var2 = new double[var0.length + var1.length];
      System.arraycopy(var0, 0, var2, 0, var0.length);
      System.arraycopy(var1, 0, var2, var0.length, var1.length);
      return var2;
   }

   private double c(double[] var1, double[] var2) {
      a(var1, var2);
      double[] var4 = var2;
      double[] var3 = var1;
      double[] var5 = new double[var1.length + var4.length];
      System.arraycopy(var3, 0, var5, 0, var3.length);
      System.arraycopy(var4, 0, var5, var3.length, var4.length);
      var3 = var5;
      var3 = this.a.a(var3);
      double var13 = 0.0;

      for (int var7 = 0; var7 < var1.length; var7++) {
         var13 += var3[var7];
      }

      double var14 = var13 - (long)var1.length * (var1.length + 1) / 2L;
      double var9 = (long)var1.length * var2.length - var14;
      return BY.e(var14, var9);
   }

   private static double a(double var0, int var2, int var3) {
      long var4;
      double var6 = (var4 = (long)var2 * var3) / 2.0;
      double var8 = var4 * (var2 + var3 + 1) / 12.0;
      double var10 = (var0 - var6) / BY.a(var8);
      oE var12 = new oE(null, 0.0, 1.0);
      return 2.0 * var12.e(var10);
   }

   private double d(double[] var1, double[] var2) {
      a(var1, var2);
      double[] var3 = var2;
      double[] var8 = var1;
      Bd var7 = this;
      a(var8, var3);
      double[] var19 = var3;
      double[] var18 = var8;
      double[] var4 = new double[var8.length + var19.length];
      System.arraycopy(var18, 0, var4, 0, var18.length);
      System.arraycopy(var19, 0, var4, var18.length, var19.length);
      var4 = var4;
      var4 = var7.a.a(var4);
      double var12 = 0.0;

      for (int var14 = 0; var14 < var8.length; var14++) {
         var12 += var4[var14];
      }

      double var28 = var12 - (long)var8.length * (var8.length + 1) / 2L;
      double var16 = (long)var8.length * var3.length - var28;
      double var21 = BY.e(var28, var16);
      double var10000 = (long)var1.length * var2.length - var21;
      int var10001 = var1.length;
      int var25 = var2.length;
      int var22 = var10001;
      double var26 = var10000;
      long var27;
      var28 = (var27 = (long)var22 * var25) / 2.0;
      var16 = var27 * (var22 + var25 + 1) / 12.0;
      double var31 = (var26 - var28) / BY.a(var16);
      oE var20 = new oE(null, 0.0, 1.0);
      return 2.0 * var20.e(var31);
   }
}
