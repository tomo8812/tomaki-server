package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class AI extends Ao implements Serializable {
   private static AI.a a;
   private static AI.a b;
   private static final long c = -2653430366886024994L;
   private boolean d = true;
   private AI.a e = AI.a.DOWNSIDE;

   public AI() {
   }

   private AI(boolean var1) {
      this.d = var1;
   }

   private AI(AI.a var1) {
      this.e = var1;
   }

   private AI(boolean var1, AI.a var2) {
      this.d = var1;
      this.e = var2;
   }

   private AI(AI var1) {
      a(var1, this);
   }

   private AI a() {
      AI var1 = new AI();
      a(this, var1);
      return var1;
   }

   private static void a(AI var0, AI var1) {
      Ci.a(var0);
      Ci.a(var1);
      var1.c(var0.e());
      var1.d = var0.d;
      var1.e = var0.e;
   }

   @Override
   public final double a(double[] var1, int var2, int var3) {
      double var4 = new AG().a(var1, var2, var3);
      return this.a(var1, var4, this.e, this.d, 0, var1.length);
   }

   private double a(double[] var1, AI.a var2) {
      double var3 = new AG().a(var1);
      return this.a(var1, var3, var2, this.d, 0, var1.length);
   }

   private double a(double[] var1, double var2) {
      return this.a(var1, var2, this.e, this.d, 0, var1.length);
   }

   private double a(double[] var1, double var2, AI.a var4) {
      return this.a(var1, var2, var4, this.d, 0, var1.length);
   }

   private double a(double[] var1, double var2, AI.a var4, boolean var5, int var6, int var7) {
      int var9 = var7;
      byte var8 = 0;
      Ch.a(var1, var8, var9, false);
      if (var1.length == 0) {
         return Double.NaN;
      }

      if (var1.length == 1) {
         return 0.0;
      }

      boolean var13 = var4.direction;
      double var11 = 0.0;

      for (int var14 = 0; var14 < var7; var14++) {
         if (var1[var14] > var2 == var13) {
            double var15 = var1[var14] - var2;
            var11 += var15 * var15;
         }
      }

      return var5 ? var11 / (var7 - 1.0) : var11 / var7;
   }

   private boolean b() {
      return this.d;
   }

   private void a(boolean var1) {
      this.d = var1;
   }

   private AI.a c() {
      return this.e;
   }

   private void a(AI.a var1) {
      this.e = var1;
   }

   // $VF: synthetic method
   @Override
   public final AA d() {
      AI var1 = this;
      AI var2 = new AI();
      a(var1, var2);
      return var2;
   }

   public enum a {
      UPSIDE(true),
      DOWNSIDE(false);

      boolean direction;

      a(boolean var3) {
         this.direction = var3;
      }

      final boolean a() {
         return this.direction;
      }
   }
}
