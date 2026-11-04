package org.valkyrienskies.core.impl.shadow;

public final class Bh {
   private Bq a;

   public Bh() {
      this.a = new Bq(Bp.FIXED, Bs.AVERAGE$139d6b26);
   }

   private Bh(Bp var1, int var2) {
      this.a = new Bq(var1, var2);
   }

   private static void a(double[] var0, double[] var1) {
      if (var0 == null || var1 == null) {
         throw new pl();
      }

      if (var0.length != 0 && var1.length != 0) {
         if (var1.length != var0.length) {
            throw new oS(var1.length, var0.length);
         }
      } else {
         throw new pf();
      }
   }

   private static double[] b(double[] var0, double[] var1) {
      double[] var2 = new double[var0.length];

      for (int var3 = 0; var3 < var0.length; var3++) {
         var2[var3] = var1[var3] - var0[var3];
      }

      return var2;
   }

   private static double[] a(double[] var0) {
      if (var0 == null) {
         throw new pl();
      }

      if (var0.length == 0) {
         throw new pf();
      }

      double[] var1 = new double[var0.length];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = BY.w(var0[var2]);
      }

      return var1;
   }

   private double c(double[] var1, double[] var2) {
      a(var1, var2);
      var2 = var2;
      double[] var3 = var1;
      double[] var5 = new double[var1.length];

      for (int var4 = 0; var4 < var3.length; var4++) {
         var5[var4] = var2[var4] - var3[var4];
      }

      var2 = var5;
      var3 = var5;
      if (var5 == null) {
         throw new pl();
      }

      if (var3.length == 0) {
         throw new pf();
      }

      double[] var16 = new double[var3.length];

      for (int var19 = 0; var19 < var3.length; var19++) {
         var16[var19] = BY.w(var3[var19]);
      }

      var3 = var16;
      var3 = this.a.a(var3);
      double var6 = 0.0;

      for (int var17 = 0; var17 < var2.length; var17++) {
         if (var2[var17] > 0.0) {
            var6 += var3[var17];
         }
      }

      int var18;
      double var9 = (var18 = var1.length) * (var18 + 1) / 2.0 - var6;
      return BY.e(var6, var9);
   }

   private static double a(double var0, int var2) {
      int var3 = 1 << var2;
      int var4 = 0;

      for (int var5 = 0; var5 < var3; var5++) {
         int var6 = 0;

         for (int var7 = 0; var7 < var2; var7++) {
            if ((var5 >> var7 & 1) == 1) {
               var6 += var7 + 1;
            }
         }

         if (var6 >= var0) {
            var4++;
         }
      }

      return 2.0 * var4 / var3;
   }

   private static double b(double var0, int var2) {
      double var3;
      double var5 = (var3 = var2 * (var2 + 1) / 4.0) * ((2 * var2 + 1) / 6.0);
      double var7 = (var0 - var3 - 0.5) / BY.a(var5);
      oE var9 = new oE(null, 0.0, 1.0);
      return 2.0 * var9.e(var7);
   }

   private double a(double[] var1, double[] var2, boolean var3) {
      a(var1, var2);
      int var4 = var1.length;
      double[] var20 = var2;
      double[] var10 = var1;
      Bh var9 = this;
      a(var10, var20);
      var1 = var20;
      var2 = var10;
      double[] var6 = new double[var10.length];

      for (int var5 = 0; var5 < var2.length; var5++) {
         var6[var5] = var1[var5] - var2[var5];
      }

      var1 = var6;
      var2 = var6;
      if (var6 == null) {
         throw new pl();
      }

      if (var2.length == 0) {
         throw new pf();
      }

      double[] var27 = new double[var2.length];

      for (int var29 = 0; var29 < var2.length; var29++) {
         var27[var29] = BY.w(var2[var29]);
      }

      double[] var13 = var27;
      double[] var14 = var9.a.a(var13);
      double var15 = 0.0;

      for (int var17 = 0; var17 < var1.length; var17++) {
         if (var1[var17] > 0.0) {
            var15 += var14[var17];
         }
      }

      int var37;
      double var18 = (var37 = var10.length) * (var37 + 1) / 2.0 - var15;
      double var28 = BY.e(var15, var18);
      if (var3 && var4 > 30) {
         throw new pm(var4, 30, true);
      }

      if (!var3) {
         double var10000 = var4 * (var4 + 1) / 2.0 - var28;
         int var24 = var4;
         double var31 = var10000;
         double var33;
         var15 = (var33 = var24 * (var24 + 1) / 4.0) * ((2 * var24 + 1) / 6.0);
         double var39 = (var31 - var33 - 0.5) / BY.a(var15);
         oE var19 = new oE(null, 0.0, 1.0);
         return 2.0 * var19.e(var39);
      }

      int var23 = var4;
      double var30 = var28;
      int var32 = 1 << var23;
      int var34 = 0;

      for (int var35 = 0; var35 < var32; var35++) {
         int var16 = 0;

         for (int var38 = 0; var38 < var23; var38++) {
            if ((var35 >> var38 & 1) == 1) {
               var16 += var38 + 1;
            }
         }

         if (var16 >= var30) {
            var34++;
         }
      }

      return 2.0 * var34 / var32;
   }
}
