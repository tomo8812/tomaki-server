package org.valkyrienskies.core.impl.shadow;

public final class mr extends mm {
   private static int a;

   private mr(double var1, double var3, int var5, int var6) {
      super(var1, var3, var5, var6);
      if (var6 > 64) {
         throw new pm(var6, 64, false);
      }
   }

   private mr(int var1, int var2) {
      super(var1, var2);
      if (var2 > 64) {
         throw new pm(var2, 64, false);
      }
   }

   public mr() {
      super(3, 64);
   }

   @Override
   protected final double j() {
      ms var1 = new ms();
      if (this.c() == 1) {
         return (4.0 * var1.a(this, 1) - var1.a(this, 0)) / 3.0;
      }

      double var2 = 0.0;
      double var4 = var1.a(this, 0);

      while (true) {
         double var6 = var1.a(this, this.f());
         this.g();
         double var8 = (var6 * 4.0 - var4) / 3.0;
         if (this.f() >= this.c()) {
            double var10 = BY.w(var8 - var2);
            double var12 = this.a() * (BY.w(var2) + BY.w(var8)) * 0.5;
            if (var10 <= var12 || var10 <= this.b()) {
               return var8;
            }
         }

         var2 = var8;
         var4 = var6;
      }
   }
}
