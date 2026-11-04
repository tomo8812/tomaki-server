package org.valkyrienskies.core.impl.shadow;

public class Ah {
   private final tq a;
   private final int b;

   public Ah() {
      this.a = null;
      this.b = 0;
   }

   private Ah(double[][] var1, boolean var2) {
      this(new sD(var1), true);
   }

   public Ah(double[][] var1) {
      this(var1, true);
   }

   private Ah(tq var1, boolean var2) {
      tq var3 = var1;
      int var4 = var1.f();
      int var5 = var3.g();
      if (var4 >= 2 && var5 > 0) {
         this.b = var1.f();
         this.a = this.a(var1, var2);
      } else {
         throw new oV(px.INSUFFICIENT_ROWS_AND_COLUMNS, var4, var5);
      }
   }

   private Ah(tq var1) {
      this(var1, true);
   }

   public tq a() {
      return this.a;
   }

   public int b() {
      return this.b;
   }

   private sD a(tq var1, boolean var2) {
      int var3 = var1.g();
      AM var4 = new AM(var2);
      sD var5 = new sD(var3, var3);

      for (int var6 = 0; var6 < var3; var6++) {
         for (int var7 = 0; var7 < var6; var7++) {
            double var8 = a(var1.g(var6), var1.g(var7), var2);
            var5.c(var6, var7, var8);
            var5.c(var7, var6, var8);
         }

         var5.c(var6, var6, var4.a(var1.g(var6)));
      }

      return var5;
   }

   private tq a(tq var1) {
      return this.a(var1, true);
   }

   private tq a(double[][] var1, boolean var2) {
      return this.a(new sD(var1), true);
   }

   private tq a(double[][] var1) {
      boolean var3 = true;
      double[][] var2 = var1;
      return this.a(new sD(var2), true);
   }

   private static double a(double[] var0, double[] var1, boolean var2) {
      AG var3 = new AG();
      double var4 = 0.0;
      int var6;
      if ((var6 = var0.length) != var1.length) {
         throw new oV(px.DIMENSIONS_MISMATCH_SIMPLE, var6, var1.length);
      }

      if (var6 < 2) {
         throw new oV(px.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, var6, 2);
      }

      double var7 = var3.a(var0);
      double var9 = var3.a(var1);

      for (int var16 = 0; var16 < var6; var16++) {
         double var12 = var0[var16] - var7;
         double var14 = var1[var16] - var9;
         var4 += (var12 * var14 - var4) / (var16 + 1);
      }

      return var2 ? var4 * ((double)var6 / (var6 - 1)) : var4;
   }

   private double a(double[] var1, double[] var2) {
      return a(var1, var2, true);
   }

   private static void b(tq var0) {
      int var1 = var0.f();
      int var2 = var0.g();
      if (var1 < 2 || var2 <= 0) {
         throw new oV(px.INSUFFICIENT_ROWS_AND_COLUMNS, var1, var2);
      }
   }
}
