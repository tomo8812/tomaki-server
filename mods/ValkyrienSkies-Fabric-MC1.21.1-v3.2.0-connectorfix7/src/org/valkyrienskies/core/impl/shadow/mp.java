package org.valkyrienskies.core.impl.shadow;

public final class mp extends mm {
   private static int a;

   private mp(double var1, double var3, int var5, int var6) {
      super(var1, var3, var5, var6);
      if (var6 > 64) {
         throw new pm(var6, 64, false);
      }
   }

   private mp(int var1, int var2) {
      super(var1, var2);
      if (var2 > 64) {
         throw new pm(var2, 64, false);
      }
   }

   public mp() {
      super(3, 64);
   }

   private double a(int var1, double var2, double var4, double var6) {
      long var8 = 1L << var1 - 1;
      double var10 = 0.0;
      double var12 = var6 / var8;
      double var14 = var4 + var12 * 0.5;

      for (long var16 = 0L; var16 < var8; var16++) {
         var10 += this.a(var14);
         var14 += var12;
      }

      return 0.5 * (var2 + var10 * var12);
   }

   @Override
   protected final double j() {
      double var1 = this.h();
      double var3 = this.i() - var1;
      double var5 = var1 + var3 * 0.5;
      double var7 = var3 * this.a(var5);

      while (true) {
         this.g();
         int var34 = this.f();
         double var22 = var3;
         double var20 = var1;
         double var18 = var7;
         int var9 = var34;
         mp var6 = this;
         long var24 = 1L << var9 - 1;
         double var26 = 0.0;
         double var28 = var22 / var24;
         double var30 = var20 + var28 * 0.5;

         for (long var32 = 0L; var32 < var24; var32++) {
            var26 += var6.a(var30);
            var30 += var28;
         }

         double var10 = 0.5 * (var18 + var26 * var28);
         if (var34 >= this.c()) {
            double var12 = BY.w(var10 - var7);
            double var14 = this.a() * (BY.w(var7) + BY.w(var10)) * 0.5;
            if (var12 <= var14 || var12 <= this.b()) {
               return var10;
            }
         }

         var7 = var10;
      }
   }
}
