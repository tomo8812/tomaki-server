package org.valkyrienskies.core.impl.shadow;

public final class Bf {
   public final double a(double[] var1, double[] var2) {
      a(var1);
      a(var2);
      double var3;
      return b(var3 = Aa.a(var1, var2), 0.0, Aa.a(var1, var2, var3), var1.length);
   }

   public final double b(double[] var1, double[] var2) {
      double var3 = Aa.a(var1, var2);
      return this.c(var3, 0.0, Aa.a(var1, var2, var3), var1.length);
   }

   public final boolean a(double[] var1, double[] var2, double var3) {
      a(var3);
      return this.b(var1, var2) < var3;
   }

   public final double a(double var1, double[] var3) {
      a(var3);
      return b(Aa.b(var3), var1, Aa.c(var3), var3.length);
   }

   public final double a(double var1, At var3) {
      a(var3);
      return b(var3.b(), var1, var3.g(), var3.d());
   }

   public final double c(double[] var1, double[] var2) {
      a(var1);
      a(var2);
      return b(Aa.b(var1), Aa.b(var2), Aa.c(var1), Aa.c(var2), var1.length, var2.length);
   }

   public final double d(double[] var1, double[] var2) {
      a(var1);
      a(var2);
      return a(Aa.b(var1), Aa.b(var2), Aa.c(var1), Aa.c(var2), var1.length, var2.length);
   }

   public final double a(At var1, At var2) {
      a(var1);
      a(var2);
      return a(var1.b(), var2.b(), var1.g(), var2.g(), var1.d(), var2.d());
   }

   public final double b(At var1, At var2) {
      a(var1);
      a(var2);
      return b(var1.b(), var2.b(), var1.g(), var2.g(), var1.d(), var2.d());
   }

   public final double b(double var1, double[] var3) {
      a(var3);
      return this.c(Aa.b(var3), var1, Aa.c(var3), var3.length);
   }

   public final boolean a(double var1, double[] var3, double var4) {
      a(var4);
      return this.b(var1, var3) < var4;
   }

   public final double b(double var1, At var3) {
      a(var3);
      return this.c(var3.b(), var1, var3.g(), var3.d());
   }

   public final boolean a(double var1, At var3, double var4) {
      a(var4);
      return this.b(var1, var3) < var4;
   }

   public final double e(double[] var1, double[] var2) {
      a(var1);
      a(var2);
      return this.c(Aa.b(var1), Aa.b(var2), Aa.c(var1), Aa.c(var2), var1.length, var2.length);
   }

   public final double f(double[] var1, double[] var2) {
      a(var1);
      a(var2);
      return this.d(Aa.b(var1), Aa.b(var2), Aa.c(var1), Aa.c(var2), var1.length, var2.length);
   }

   public final boolean b(double[] var1, double[] var2, double var3) {
      a(var3);
      return this.e(var1, var2) < var3;
   }

   public final boolean c(double[] var1, double[] var2, double var3) {
      a(var3);
      return this.f(var1, var2) < var3;
   }

   public final double c(At var1, At var2) {
      a(var1);
      a(var2);
      return this.c(var1.b(), var2.b(), var1.g(), var2.g(), var1.d(), var2.d());
   }

   public final double d(At var1, At var2) {
      a(var1);
      a(var2);
      return this.d(var1.b(), var2.b(), var1.g(), var2.g(), var1.d(), var2.d());
   }

   public final boolean a(At var1, At var2, double var3) {
      a(var3);
      return this.c(var1, var2) < var3;
   }

   private static double a(double var0, double var2, double var4, double var6) {
      return (var0 / var4 + var2 / var6)
         * (var0 / var4 + var2 / var6)
         / (var0 * var0 / (var4 * var4 * (var4 - 1.0)) + var2 * var2 / (var6 * var6 * (var6 - 1.0)));
   }

   private static double b(double var0, double var2, double var4, double var6) {
      return (var0 - var2) / BY.a(var4 / var6);
   }

   private static double a(double var0, double var2, double var4, double var6, double var8, double var10) {
      return (var0 - var2) / BY.a(var4 / var8 + var6 / var10);
   }

   private static double b(double var0, double var2, double var4, double var6, double var8, double var10) {
      double var12 = ((var8 - 1.0) * var4 + (var10 - 1.0) * var6) / (var8 + var10 - 2.0);
      return (var0 - var2) / BY.a(var12 * (1.0 / var8 + 1.0 / var10));
   }

   private double c(double var1, double var3, double var5, double var7) {
      double var9 = BY.w(b(var1, var3, var5, var7));
      oK var11 = new oK(null, var7 - 1.0);
      return 2.0 * var11.e(-var9);
   }

   private double c(double var1, double var3, double var5, double var7, double var9, double var11) {
      double var13 = BY.w(a(var1, var3, var5, var7, var9, var11));
      double var15 = a(var5, var7, var9, var11);
      oK var17 = new oK(null, var15);
      return 2.0 * var17.e(-var13);
   }

   private double d(double var1, double var3, double var5, double var7, double var9, double var11) {
      double var13 = BY.w(b(var1, var3, var5, var7, var9, var11));
      double var15 = var9 + var11 - 2.0;
      oK var17 = new oK(null, var15);
      return 2.0 * var17.e(-var13);
   }

   private static void a(double var0) {
      if (var0 <= 0.0 || var0 > 0.5) {
         throw new po(px.SIGNIFICANCE_LEVEL, var0, 0.0, 0.5);
      }
   }

   private static void a(double[] var0) {
      if (var0 == null) {
         throw new pl();
      }

      if (var0.length < 2) {
         throw new pn(px.INSUFFICIENT_DATA_FOR_T_STATISTIC, var0.length, 2, true);
      }
   }

   private static void a(At var0) {
      if (var0 == null) {
         throw new pl();
      }

      if (var0.d() < 2L) {
         throw new pn(px.INSUFFICIENT_DATA_FOR_T_STATISTIC, var0.d(), 2, true);
      }
   }
}
