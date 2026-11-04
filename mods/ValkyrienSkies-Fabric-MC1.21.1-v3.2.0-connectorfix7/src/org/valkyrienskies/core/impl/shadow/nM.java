package org.valkyrienskies.core.impl.shadow;

public final class nM extends nq {
   private static double d;

   public nM() {
      super(1.0E-6);
   }

   private nM(double var1) {
      super(var1);
   }

   private nM(double var1, double var3) {
      super(var1, var3);
   }

   @Override
   protected final double j() {
      double var1 = this.d();
      double var3 = this.e();
      double var5 = this.b(var1);
      double var7 = this.b(var3);
      if (var5 == 0.0) {
         return var1;
      }

      if (var7 == 0.0) {
         return var3;
      }

      this.c(var1, var3);
      double var9 = this.i();
      double var11 = this.g();
      double var13 = this.h();

      do {
         double var15 = var3 - var7 * (var3 - var1) / (var7 - var5);
         double var17;
         if ((var17 = this.b(var15)) == 0.0) {
            return var15;
         }

         var1 = var3;
         var5 = var7;
         var3 = var15;
         var7 = var17;
         if (BY.w(var17) <= var9) {
            return var3;
         }
      } while (!(BY.w(var3 - var1) < BY.e(var13 * BY.w(var3), var11)));

      return var3;
   }
}
