package org.valkyrienskies.core.impl.shadow;

import java.util.List;

public final class Aa {
   private static final AV a = new AV();
   private static final AX b = new AX();
   private static final AU c = new AU();
   private static final AW d = new AW();
   private static final AR e = new AR();
   private static final AP f = new AP();
   private static final AG g = new AG();
   private static final AM h = new AM();
   private static final AT i = new AT();
   private static final AE j = new AE();

   private Aa() {
   }

   private static double d(double[] var0) {
      return a.a(var0);
   }

   private static double a(double[] var0, int var1, int var2) {
      return a.a(var0, var1, var2);
   }

   public static double a(double[] var0) {
      return b.a(var0);
   }

   private static double b(double[] var0, int var1, int var2) {
      return b.a(var0, var1, var2);
   }

   private static double e(double[] var0) {
      return c.a(var0);
   }

   private static double c(double[] var0, int var1, int var2) {
      return c.a(var0, var1, var2);
   }

   private static double f(double[] var0) {
      return d.a(var0);
   }

   private static double d(double[] var0, int var1, int var2) {
      return d.a(var0, var1, var2);
   }

   public static double b(double[] var0) {
      return g.a(var0);
   }

   private static double e(double[] var0, int var1, int var2) {
      return g.a(var0, var1, var2);
   }

   private static double g(double[] var0) {
      return j.a(var0);
   }

   private static double f(double[] var0, int var1, int var2) {
      return j.a(var0, var1, var2);
   }

   public static double c(double[] var0) {
      return h.a(var0);
   }

   private static double g(double[] var0, int var1, int var2) {
      return h.a(var0, var1, var2);
   }

   private static double a(double[] var0, double var1, int var3, int var4) {
      return h.a(var0, var1, var3, var4);
   }

   private static double a(double[] var0, double var1) {
      return h.a(var0, var1);
   }

   private static double h(double[] var0) {
      return new AM(false).a(var0);
   }

   private static double h(double[] var0, int var1, int var2) {
      return new AM(false).a(var0, var1, var2);
   }

   private static double b(double[] var0, double var1, int var3, int var4) {
      return new AM(false).a(var0, var1, var3, var4);
   }

   private static double b(double[] var0, double var1) {
      return new AM(false).a(var0, var1);
   }

   private static double i(double[] var0) {
      return f.a(var0);
   }

   private static double i(double[] var0, int var1, int var2) {
      return f.a(var0, var1, var2);
   }

   private static double j(double[] var0) {
      return e.a(var0);
   }

   private static double j(double[] var0, int var1, int var2) {
      return e.a(var0, var1, var2);
   }

   private static double c(double[] var0, double var1) {
      return i.a(var0, var1);
   }

   private static double a(double[] var0, int var1, int var2, double var3) {
      return i.a(var0, var1, var2, var3);
   }

   private static double b(double[] var0, double[] var1) {
      int var2;
      if ((var2 = var0.length) != var1.length) {
         throw new oS(var2, var1.length);
      }

      if (var2 <= 0) {
         throw new pf(px.INSUFFICIENT_DIMENSION);
      }

      double var3 = 0.0;

      for (int var5 = 0; var5 < var2; var5++) {
         var3 += var0[var5] - var1[var5];
      }

      return var3;
   }

   public static double a(double[] var0, double[] var1) {
      double[] var2 = var1;
      var1 = var0;
      int var3;
      if ((var3 = var0.length) != var2.length) {
         throw new oS(var3, var2.length);
      }

      if (var3 <= 0) {
         throw new pf(px.INSUFFICIENT_DIMENSION);
      }

      double var5 = 0.0;

      for (int var4 = 0; var4 < var3; var4++) {
         var5 += var1[var4] - var2[var4];
      }

      return var5 / var0.length;
   }

   public static double a(double[] var0, double[] var1, double var2) {
      double var4 = 0.0;
      double var6 = 0.0;
      int var10;
      if ((var10 = var0.length) != var1.length) {
         throw new oS(var10, var1.length);
      }

      if (var10 < 2) {
         throw new pn(var10, 2, true);
      }

      for (int var11 = 0; var11 < var10; var11++) {
         double var8 = var0[var11] - var1[var11];
         var4 += (var8 - var2) * (var8 - var2);
         var6 += var8 - var2;
      }

      return (var4 - var6 * var6 / var10) / (var10 - 1);
   }

   private static double[] k(double[] var0) {
      Aq var1 = new Aq();

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1.a(var0[var2]);
      }

      double var8 = var1.b();
      double var4 = var1.e();
      double[] var7 = new double[var0.length];

      for (int var6 = 0; var6 < var0.length; var6++) {
         var7[var6] = (var0[var6] - var8) / var4;
      }

      return var7;
   }

   private static double[] l(double[] var0) {
      if (var0 == null) {
         throw new pl(px.INPUT_ARRAY);
      } else {
         return l(var0, 0, var0.length);
      }
   }

   private static double[] k(double[] var0, int var1, int var2) {
      if (var0 == null) {
         throw new pl(px.INPUT_ARRAY);
      } else if (var1 < 0) {
         throw new pj(px.START_POSITION, var1);
      } else if (var2 < 0) {
         throw new pj(px.LENGTH, var2);
      } else {
         return l(var0, var1, var2);
      }
   }

   private static double[] l(double[] var0, int var1, int var2) {
      zZ var3 = new zZ();

      for (int var4 = var1; var4 < var1 + var2; var4++) {
         double var5;
         if (!Double.isNaN(var5 = var0[var4])) {
            Double var6 = var5;
            var3.a(var6, 1L);
         }
      }

      List var9;
      double[] var10 = new double[(var9 = var3.a()).size()];
      int var11 = 0;

      for (Comparable var8 : var9) {
         var10[var11++] = (Double)var8;
      }

      return var10;
   }
}
