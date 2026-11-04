package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class AF extends An implements Serializable {
   private static final long a = 2784465764798260919L;
   private AD b;
   private boolean c;

   public AF() {
      this.c = true;
      this.b = new AD();
   }

   private AF(AD var1) {
      this.c = false;
      this.b = var1;
   }

   private AF(AF var1) {
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
      double var1 = Double.NaN;
      if (this.b.g_() > 3L) {
         double var3 = this.b.e / (this.b.a - 1L);
         if (this.b.a > 3L && !(var3 < 1.0E-19)) {
            double var5;
            var1 = ((var5 = this.b.a) * (var5 + 1.0) * this.b.c() - 3.0 * this.b.e * this.b.e * (var5 - 1.0))
               / ((var5 - 1.0) * (var5 - 2.0) * (var5 - 3.0) * var3 * var3);
         } else {
            var1 = 0.0;
         }
      }

      return var1;
   }

   @Override
   public final void b() {
      if (this.c) {
         this.b.b();
      }
   }

   @Override
   public final long g_() {
      return this.b.g_();
   }

   @Override
   public final double a(double[] var1, int var2, int var3) {
      double var4 = Double.NaN;
      int var8 = var3;
      int var7 = var2;
      if (Ch.a(var1, var7, var8, false) && var3 > 3) {
         AM var19;
         (var19 = new AM()).b(var1, var2, var3);
         double var20 = var19.a.b;
         double var9 = BY.a(var19.c());
         double var11 = 0.0;

         for (int var13 = var2; var13 < var2 + var3; var13++) {
            var11 += BY.a(var1[var13] - var20, 4.0);
         }

         var11 /= BY.a(var9, 4.0);
         double var22;
         double var15 = (var22 = var3) * (var22 + 1.0) / ((var22 - 1.0) * (var22 - 2.0) * (var22 - 3.0));
         double var17 = 3.0 * BY.a(var22 - 1.0, 2.0) / ((var22 - 2.0) * (var22 - 3.0));
         var4 = var15 * var11 - var17;
      }

      return var4;
   }

   private AF f() {
      AF var1 = new AF();
      a(this, var1);
      return var1;
   }

   private static void a(AF var0, AF var1) {
      Ci.a(var0);
      Ci.a(var1);
      var1.c(var0.e());
      var1.b = var0.b.g();
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
