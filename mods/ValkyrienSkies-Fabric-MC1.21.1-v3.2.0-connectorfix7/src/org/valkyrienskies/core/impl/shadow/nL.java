package org.valkyrienskies.core.impl.shadow;

public final class nL extends nq {
   private static final double d = 1.0E-6;

   public nL() {
      this(1.0E-6);
   }

   private nL(double var1) {
      super(1.0E-6);
   }

   private nL(double var1, double var3) {
      super(var1, var3);
   }

   @Override
   protected final double j() {
      double var1 = this.d();
      double var3 = this.e();
      double var5 = var1;
      double var7 = this.b(var5);
      double var9 = var3;
      double var11 = this.b(var9);
      if (var7 == 0.0) {
         return var1;
      }

      if (var11 == 0.0) {
         return var3;
      }

      this.c(var1, var3);
      double var13 = this.g();
      double var15 = this.i();
      double var17 = this.h();
      double var19 = Double.POSITIVE_INFINITY;

      while (true) {
         double var21 = 0.5 * (var5 + var9);
         double var23;
         if (BY.w(var23 = this.b(var21)) <= var15) {
            return var21;
         }

         double var25 = 1.0 - var7 * var11 / (var23 * var23);
         double var27 = BY.h(var11) * BY.h(var23) * (var21 - var5) / BY.a(var25);
         double var29 = var21 - var27;
         double var31 = this.b(var29);
         double var33 = BY.e(var17 * BY.w(var29), var13);
         if (BY.w(var29 - var19) <= var33) {
            return var29;
         }

         if (BY.w(var31) <= var15) {
            return var29;
         }

         if (var27 > 0.0) {
            if (BY.h(var7) + BY.h(var31) == 0.0) {
               var9 = var29;
               var11 = var31;
            } else {
               var5 = var29;
               var9 = var21;
               var7 = var31;
               var11 = var23;
            }
         } else if (BY.h(var11) + BY.h(var31) == 0.0) {
            var5 = var29;
            var7 = var31;
         } else {
            var5 = var21;
            var9 = var29;
            var7 = var23;
            var11 = var31;
         }

         var19 = var29;
      }
   }
}
