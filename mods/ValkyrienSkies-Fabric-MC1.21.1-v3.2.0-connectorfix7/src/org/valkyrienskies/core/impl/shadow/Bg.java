package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;
import java.util.Collection;

public final class Bg {
   private static final Bf a = new Bf();
   private static final Ba b = new Ba();
   private static final Be c = new Be();
   private static final Bb d = new Bb();
   private static final Bc e = new Bc();

   private Bg() {
   }

   private static double a(double[] var0, double[] var1) {
      return a.c(var0, var1);
   }

   private static double a(At var0, At var1) {
      return a.b(var0, var1);
   }

   private static boolean a(double[] var0, double[] var1, double var2) {
      return a.c(var0, var1, var2);
   }

   private static double b(double[] var0, double[] var1) {
      return a.f(var0, var1);
   }

   private static double b(At var0, At var1) {
      return a.d(var0, var1);
   }

   private static double c(double[] var0, double[] var1) {
      return a.a(var0, var1);
   }

   private static boolean b(double[] var0, double[] var1, double var2) {
      return a.a(var0, var1, var2);
   }

   private static double d(double[] var0, double[] var1) {
      return a.b(var0, var1);
   }

   private static double a(double var0, double[] var2) {
      return a.a(var0, var2);
   }

   private static double a(double var0, At var2) {
      return a.a(var0, var2);
   }

   private static double e(double[] var0, double[] var1) {
      return a.d(var0, var1);
   }

   private static double c(At var0, At var1) {
      return a.a(var0, var1);
   }

   private static boolean a(double var0, double[] var2, double var3) {
      return a.a(var0, var2, var3);
   }

   private static double b(double var0, double[] var2) {
      return a.b(var0, var2);
   }

   private static boolean a(double var0, At var2, double var3) {
      return a.a(var0, var2, var3);
   }

   private static double b(double var0, At var2) {
      return a.b(var0, var2);
   }

   private static boolean c(double[] var0, double[] var1, double var2) {
      return a.b(var0, var1, var2);
   }

   private static double f(double[] var0, double[] var1) {
      return a.e(var0, var1);
   }

   private static boolean a(At var0, At var1, double var2) {
      return a.a(var0, var1, var2);
   }

   private static double d(At var0, At var1) {
      return a.c(var0, var1);
   }

   private static double a(double[] var0, long[] var1) {
      return Ba.a(var0, var1);
   }

   private static double a(long[][] var0) {
      return b.a(var0);
   }

   private static boolean a(double[] var0, long[] var1, double var2) {
      double var7 = var2;
      long[] var11 = var1;
      double[] var10 = var0;
      Ba var9 = b;
      if (!(var7 <= 0.0) && !(var7 > 0.5)) {
         return var9.b(var10, var11) < var7;
      } else {
         throw new po(px.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, var7, 0, 0.5);
      }
   }

   private static double b(double[] var0, long[] var1) {
      return b.b(var0, var1);
   }

   private static boolean a(long[][] var0, double var1) {
      double var5 = var1;
      long[][] var8 = var0;
      Ba var7 = b;
      if (!(var5 <= 0.0) && !(var5 > 0.5)) {
         return var7.b(var8) < var5;
      } else {
         throw new po(px.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, var5, 0, 0.5);
      }
   }

   private static double b(long[][] var0) {
      return b.b(var0);
   }

   private static double a(long[] var0, long[] var1) {
      return Ba.a(var0, var1);
   }

   private static double b(long[] var0, long[] var1) {
      return b.b(var0, var1);
   }

   private static boolean a(long[] var0, long[] var1, double var2) {
      double var7 = var2;
      long[] var11 = var1;
      var1 = var0;
      Ba var9 = b;
      if (!(var7 <= 0.0) && !(var7 > 0.5)) {
         return var9.b(var1, var11) < var7;
      } else {
         throw new po(px.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, var7, 0, 0.5);
      }
   }

   private static double a(Collection<double[]> var0) {
      Collection var1 = var0;
      Be var2 = c;
      return c.c(var1).c;
   }

   private static double b(Collection<double[]> var0) {
      return c.b(var0);
   }

   private static boolean a(Collection<double[]> var0, double var1) {
      double var5 = var1;
      Collection var8 = var0;
      Be var7 = c;
      if (!(var5 <= 0.0) && !(var5 > 0.5)) {
         return var7.b(var8) < var5;
      } else {
         throw new po(px.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, var5, 0, 0.5);
      }
   }

   private static double c(double[] var0, long[] var1) {
      return Bb.a(var0, var1);
   }

   private static double d(double[] var0, long[] var1) {
      return d.b(var0, var1);
   }

   private static double e(double[] var0, long[] var1) {
      long[] var2 = var1;
      double[] var5 = var0;
      Bb var3 = d;
      oi var4 = new oi(null, var5.length - 2.0);
      return 1.0 - var4.e(Bb.a(var5, var2));
   }

   private static boolean b(double[] var0, long[] var1, double var2) {
      double var7 = var2;
      long[] var11 = var1;
      double[] var10 = var0;
      Bb var9 = d;
      if (!(var7 <= 0.0) && !(var7 > 0.5)) {
         return var9.b(var10, var11) < var7;
      } else {
         throw new po(px.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, var7, 0, 0.5);
      }
   }

   private static double c(long[] var0, long[] var1) {
      return d.a(var0, var1);
   }

   private static double a(long var0, long var2, long var4, long var6) {
      long var15 = var6;
      long var13 = var4;
      long var11 = var2;
      long var9 = var0;
      Bb var21 = d;
      double var19 = BY.a(d.a(new long[]{var9, var11}, new long[]{var13, var15}));
      if ((double)var9 / (var9 + var11) < (double)var13 / (var13 + var15)) {
         var19 = -var19;
      }

      return var19;
   }

   private static double d(long[] var0, long[] var1) {
      return d.b(var0, var1);
   }

   private static boolean b(long[] var0, long[] var1, double var2) {
      double var7 = var2;
      long[] var11 = var1;
      var1 = var0;
      Bb var9 = d;
      if (!(var7 <= 0.0) && !(var7 > 0.5)) {
         return var9.b(var1, var11) < var7;
      } else {
         throw new po(px.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, var7, 0, 0.5);
      }
   }

   private static double a(oI var0, double[] var1) {
      return e.a(var0, var1);
   }

   private static double b(oI var0, double[] var1) {
      double[] var2 = var1;
      oI var4 = var0;
      Bc var3 = e;
      return e.a(var4, var2, false);
   }

   private static double a(oI var0, double[] var1, boolean var2) {
      return e.a(var0, var1, var2);
   }

   private static boolean a(oI var0, double[] var1, double var2) {
      double var7 = var2;
      double[] var12 = var1;
      oI var10 = var0;
      Bc var9 = e;
      if (!(var7 <= 0.0) && !(var7 > 0.5)) {
         double[] var13 = var12;
         oI var11 = var10;
         return var9.a(var11, var13, false) < var7;
      } else {
         throw new po(px.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, var7, 0, 0.5);
      }
   }

   private static double g(double[] var0, double[] var1) {
      return e.b(var0, var1);
   }

   private static double h(double[] var0, double[] var1) {
      double[] var2 = var1;
      var1 = var0;
      Bc var3 = e;
      return e.a(var1, var2, true);
   }

   private static double a(double[] var0, double[] var1, boolean var2) {
      return e.a(var0, var1, var2);
   }

   private static double a(double var0, int var2, int var3, boolean var4) {
      return Bc.b(var0, var3, var2, var4);
   }

   private static double a(double var0, int var2, int var3) {
      return e.a(var0, var2, var3);
   }

   private static double a(double var0, int var2, int var3, boolean var4, int var5) {
      Bc var10000 = e;
      int var37 = var5;
      boolean var35 = (boolean)var4;
      int var31 = var3;
      int var1 = var2;
      double var7 = var0;
      Bc var27 = e;
      long var10001 = Bc.a(var7, var1, var31, var35);
      var3 = var37;
      var2 = var31;
      var1 = var1;
      long var14 = var10001;
      Bc var28 = var10000;
      var4 = BY.b(var1, var2);
      var1 = BY.a(var1, var2);
      var2 = var4 + var1;
      var5 = 0;
      boolean[] var34 = new boolean[var2];

      for (int var6 = 0; var6 < var3; var6++) {
         zF var9 = var28.a;
         int var8 = var4;
         boolean[] var40 = var34;
         Arrays.fill(var34, true);

         for (int var42 = var8; var42 < var40.length; var42++) {
            int var10 = var9.nextInt(var42 + 1);
            var40[var40[var10] ? var10 : var42] = false;
         }

         long var25 = 0L;

         for (int var41 = 0; var41 < var34.length; var41++) {
            if (var34[var41]) {
               if ((var25 += var1) >= var14) {
                  var5++;
                  break;
               }
            } else if ((var25 -= var4) <= -var14) {
               var5++;
               break;
            }
         }
      }

      return (double)var5 / var3;
   }
}
