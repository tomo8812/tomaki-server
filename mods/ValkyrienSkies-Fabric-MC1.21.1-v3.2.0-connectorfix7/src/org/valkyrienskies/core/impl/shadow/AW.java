package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class AW extends An implements Serializable {
   private static final long a = -370076995648386763L;
   private int b;
   private double c;

   public AW() {
      this.c = 0.0;
      this.b = 0;
   }

   private AW(AW var1) {
      a(var1, this);
   }

   @Override
   public final void a(double var1) {
      this.c = this.c + BY.k(var1);
      this.b++;
   }

   @Override
   public final double c() {
      return this.c;
   }

   @Override
   public final long g_() {
      return this.b;
   }

   @Override
   public final void b() {
      this.c = 0.0;
      this.b = 0;
   }

   @Override
   public final double a(double[] var1, int var2, int var3) {
      double var4 = Double.NaN;
      boolean var6 = true;
      int var8 = var3;
      int var7 = var2;
      if (Ch.a(var1, var7, var8, true)) {
         var4 = 0.0;

         for (int var9 = var2; var9 < var2 + var3; var9++) {
            var4 += BY.k(var1[var9]);
         }
      }

      return var4;
   }

   private AW f() {
      AW var1 = new AW();
      a(this, var1);
      return var1;
   }

   public static void a(AW var0, AW var1) {
      Ci.a(var0);
      Ci.a(var1);
      var1.c(var0.e());
      var1.b = var0.b;
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
