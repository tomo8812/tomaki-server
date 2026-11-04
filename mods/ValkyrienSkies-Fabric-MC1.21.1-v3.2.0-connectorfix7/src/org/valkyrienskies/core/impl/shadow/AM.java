package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class AM extends An implements Serializable, AB {
   private static final long c = -9111962718267217978L;
   protected AH a = null;
   private boolean d = true;
   public boolean b = true;

   public AM() {
      this.a = new AH();
   }

   public AM(AH var1) {
      this.d = false;
      this.a = var1;
   }

   public AM(boolean var1) {
      this.a = new AH();
      this.b = var1;
   }

   public AM(boolean var1, AH var2) {
      this.d = false;
      this.a = var2;
      this.b = var1;
   }

   private AM(AM var1) {
      a(var1, this);
   }

   @Override
   public final void a(double var1) {
      if (this.d) {
         this.a.a(var1);
      }
   }

   @Override
   public final double c() {
      if (this.a.a == 0L) {
         return Double.NaN;
      } else if (this.a.a == 1L) {
         return 0.0;
      } else {
         return this.b ? this.a.e / (this.a.a - 1.0) : this.a.e / this.a.a;
      }
   }

   @Override
   public final long g_() {
      return this.a.g_();
   }

   @Override
   public final void b() {
      if (this.d) {
         this.a.b();
      }
   }

   @Override
   public final double a(double[] var1) {
      if (var1 == null) {
         throw new pl(px.INPUT_ARRAY);
      } else {
         return this.a(var1, 0, var1.length);
      }
   }

   @Override
   public final double a(double[] var1, int var2, int var3) {
      double var4 = Double.NaN;
      int var8 = var3;
      int var7 = var2;
      if (Ch.a(var1, var7, var8, false)) {
         this.b();
         if (var3 == 1) {
            var4 = 0.0;
         } else if (var3 > 1) {
            double var9 = new AG().a(var1, var2, var3);
            var4 = this.a(var1, var9, var2, var3);
         }
      }

      return var4;
   }

   @Override
   public final double a(double[] var1, double[] var2, int var3, int var4) {
      double var5 = Double.NaN;
      if (a_(var1, var2, var3, var4)) {
         this.b();
         if (var4 == 1) {
            var5 = 0.0;
         } else if (var4 > 1) {
            double var8 = new AG().a(var1, var2, var3, var4);
            var5 = this.a(var1, var2, var8, var3, var4);
         }
      }

      return var5;
   }

   @Override
   public final double a(double[] var1, double[] var2) {
      return this.a(var1, var2, 0, var1.length);
   }

   public final double a(double[] var1, double var2, int var4, int var5) {
      double var6 = Double.NaN;
      int var10 = var5;
      int var9 = var4;
      if (Ch.a(var1, var9, var10, false)) {
         if (var5 == 1) {
            var6 = 0.0;
         } else if (var5 > 1) {
            double var8 = 0.0;
            double var12 = 0.0;

            for (int var14 = var4; var14 < var4 + var5; var14++) {
               double var16 = var1[var14] - var2;
               var8 += var16 * var16;
               var12 += var16;
            }

            double var17 = var5;
            if (this.b) {
               var6 = (var8 - var12 * var12 / var17) / (var17 - 1.0);
            } else {
               var6 = (var8 - var12 * var12 / var17) / var17;
            }
         }
      }

      return var6;
   }

   public final double a(double[] var1, double var2) {
      return this.a(var1, var2, 0, var1.length);
   }

   private double a(double[] var1, double[] var2, double var3, int var5, int var6) {
      double var7 = Double.NaN;
      if (a_(var1, var2, var5, var6)) {
         if (var6 == 1) {
            var7 = 0.0;
         } else if (var6 > 1) {
            double var9 = 0.0;
            double var13 = 0.0;

            for (int var15 = var5; var15 < var5 + var6; var15++) {
               double var11 = var1[var15] - var3;
               var9 += var2[var15] * (var11 * var11);
               var13 += var2[var15] * var11;
            }

            double var18 = 0.0;

            for (int var17 = var5; var17 < var5 + var6; var17++) {
               var18 += var2[var17];
            }

            if (this.b) {
               var7 = (var9 - var13 * var13 / var18) / (var18 - 1.0);
            } else {
               var7 = (var9 - var13 * var13 / var18) / var18;
            }
         }
      }

      return var7;
   }

   private double a(double[] var1, double[] var2, double var3) {
      return this.a(var1, var2, var3, 0, var1.length);
   }

   public final boolean f() {
      return this.b;
   }

   public final void a(boolean var1) {
      this.b = var1;
   }

   public final AM g() {
      AM var1 = new AM();
      a(this, var1);
      return var1;
   }

   public static void a(AM var0, AM var1) {
      Ci.a(var0);
      Ci.a(var1);
      var1.c(var0.e());
      var1.a = var0.a.i();
      var1.b = var0.b;
      var1.d = var0.d;
   }

   // $VF: synthetic method
   @Override
   public final An a() {
      return this.g();
   }

   // $VF: synthetic method
   @Override
   public final AA d() {
      return this.g();
   }
}
