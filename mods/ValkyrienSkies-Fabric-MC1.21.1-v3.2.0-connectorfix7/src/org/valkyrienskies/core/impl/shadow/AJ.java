package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class AJ extends An implements Serializable {
   private static final long a = 7101857578996691352L;
   private AL b = null;
   private boolean c;

   public AJ() {
      this.c = true;
      this.b = new AL();
   }

   private AJ(AL var1) {
      this.c = false;
      this.b = var1;
   }

   private AJ(AJ var1) {
      a(var1, this);
   }

   @Override
   public final void a(double var1) {
      if (this.c) {
         this.b.a(var1);
      }
   }

   @Override
   public final double c() {
      if (this.b.a < 3L) {
         return Double.NaN;
      }

      double var1;
      double var3;
      return (var1 = this.b.e / (this.b.a - 1L)) < 1.0E-19 ? 0.0 : (var3 = this.b.g_()) * this.b.f / ((var3 - 1.0) * (var3 - 2.0) * BY.a(var1) * var1);
   }

   @Override
   public final long g_() {
      return this.b.g_();
   }

   @Override
   public final void b() {
      if (this.c) {
         this.b.b();
      }
   }

   @Override
   public final double a(double[] var1, int var2, int var3) {
      double var4 = Double.NaN;
      int var8 = var3;
      int var7 = var2;
      if (Ch.a(var1, var7, var8, false) && var3 > 2) {
         double var20 = new AG().a(var1, var2, var3);
         double var9 = 0.0;
         double var11 = 0.0;

         for (int var13 = var2; var13 < var2 + var3; var13++) {
            double var14 = var1[var13] - var20;
            var9 += var14 * var14;
            var11 += var14;
         }

         double var21 = (var9 - var11 * var11 / var3) / (var3 - 1);
         double var15 = 0.0;

         for (int var17 = var2; var17 < var2 + var3; var17++) {
            double var18 = var1[var17] - var20;
            var15 += var18 * var18 * var18;
         }

         var15 /= var21 * BY.a(var21);
         double var23;
         var4 = (var23 = var3) / ((var23 - 1.0) * (var23 - 2.0)) * var15;
      }

      return var4;
   }

   private AJ f() {
      AJ var1 = new AJ();
      a(this, var1);
      return var1;
   }

   private static void a(AJ var0, AJ var1) {
      Ci.a(var0);
      Ci.a(var1);
      var1.c(var0.e());
      var1.b = new AL(var0.b.h());
      var1.c = var0.c;
   }

   // $VF: synthetic method
   @Override
   public final An a() {
      return this.f();
   }

   // $VF: synthetic method
   @Override
   public final AA d() {
      return this.f();
   }
}
