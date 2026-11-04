package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class AP extends An implements Serializable {
   private static final long a = -5593383832225844641L;
   private long b;
   private double c;

   public AP() {
      this.b = 0L;
      this.c = Double.NaN;
   }

   private AP(AP var1) {
      a(var1, this);
   }

   @Override
   public final void a(double var1) {
      if (var1 > this.c || Double.isNaN(this.c)) {
         this.c = var1;
      }

      this.b++;
   }

   @Override
   public final void b() {
      this.c = Double.NaN;
      this.b = 0L;
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
   public final double a(double[] var1, int var2, int var3) {
      double var4 = Double.NaN;
      int var8 = var3;
      int var7 = var2;
      if (Ch.a(var1, var7, var8, false)) {
         var4 = var1[var2];

         for (int var6 = var2; var6 < var2 + var3; var6++) {
            if (!Double.isNaN(var1[var6])) {
               var4 = var4 > var1[var6] ? var4 : var1[var6];
            }
         }
      }

      return var4;
   }

   private AP f() {
      AP var1 = new AP();
      a(this, var1);
      return var1;
   }

   public static void a(AP var0, AP var1) {
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
