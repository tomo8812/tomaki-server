package org.valkyrienskies.core.impl.shadow;

public final class ms extends mm {
   private static int a;
   private double b;

   private ms(double var1, double var3, int var5, int var6) {
      super(var1, var3, var5, var6);
      if (var6 > 64) {
         throw new pm(var6, 64, false);
      }
   }

   private ms(int var1, int var2) {
      super(var1, var2);
      if (var2 > 64) {
         throw new pm(var2, 64, false);
      }
   }

   public ms() {
      super(3, 64);
   }

   final double a(mm var1, int var2) {
      if (var2 == 0) {
         double var17 = var1.i();
         double var18 = var1.h();
         this.b = 0.5 * (var17 - var18) * (var1.a(var18) + var1.a(var17));
         return this.b;
      }

      long var3 = 1L << var2 - 1;
      double var5 = 0.0;
      double var7 = var1.i();
      double var9 = var1.h();
      double var11 = (var7 - var9) / var3;
      double var13 = var9 + var11 * 0.5;

      for (long var15 = 0L; var15 < var3; var15++) {
         var5 += var1.a(var13);
         var13 += var11;
      }

      this.b = 0.5 * (this.b + var5 * var11);
      return this.b;
   }

   @Override
   protected final double j() {
      double var1 = this.a(this, 0);
      this.g();

      while (true) {
         int var3 = this.f();
         double var4 = this.a(this, var3);
         if (var3 >= this.c()) {
            double var6 = BY.w(var4 - var1);
            double var8 = this.a() * (BY.w(var1) + BY.w(var4)) * 0.5;
            if (var6 <= var8 || var6 <= this.b()) {
               return var4;
            }
         }

         var1 = var4;
         this.g();
      }
   }
}
