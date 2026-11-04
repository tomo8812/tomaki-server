package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class AV extends An implements Serializable {
   private static final long a = -8231831954703408316L;
   private long b;
   private double c;

   public AV() {
      this.b = 0L;
      this.c = 0.0;
   }

   private AV(AV var1) {
      a(var1, this);
   }

   @Override
   public final void a(double var1) {
      this.c += var1;
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
      this.b = 0L;
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
            var4 += var1[var9];
         }
      }

      return var4;
   }

   public final double b(double[] var1, double[] var2, int var3, int var4) {
      double var5 = Double.NaN;
      if (a(var1, var2, var3, var4, true)) {
         var5 = 0.0;

         for (int var7 = var3; var7 < var3 + var4; var7++) {
            var5 += var1[var7] * var2[var7];
         }
      }

      return var5;
   }

   private double a(double[] var1, double[] var2) {
      return this.b(var1, var2, 0, var1.length);
   }

   private AV f() {
      AV var1 = new AV();
      a(this, var1);
      return var1;
   }

   public static void a(AV var0, AV var1) {
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
