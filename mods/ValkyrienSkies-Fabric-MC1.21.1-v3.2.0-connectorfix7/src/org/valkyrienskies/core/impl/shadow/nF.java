package org.valkyrienskies.core.impl.shadow;

public final class nF extends nq {
   private static final double d = 1.0E-6;

   public nF() {
      this(1.0E-6);
   }

   private nF(double var1) {
      super(1.0E-6);
   }

   private nF(double var1, double var3) {
      super(var1, var3);
   }

   @Override
   protected final double j() {
      double var1 = this.d();
      double var3 = this.e();
      double var45 = var3;
      nP.b(var1, var45);
      double var5 = this.h();
      double var7 = this.g();
      double var9 = this.i();
      double var11 = var1;
      double var13;
      if (BY.w(var13 = this.b(var11)) < var9) {
         return var11;
      }

      double var15 = var3;
      double var17;
      if (BY.w(var17 = this.b(var15)) < var9) {
         return var15;
      }

      if (var13 * var17 > 0.0) {
         throw new pe(var11, var15, var13, var17);
      }

      double var19 = 0.5 * (var11 + var15);
      double var21 = this.b(var19);
      double var23 = Double.POSITIVE_INFINITY;

      while (true) {
         double var25;
         double var27 = (var25 = (var19 - var15) / (var15 - var11)) * (var21 - (var25 + 1.0) * var17 + var25 * var13);
         double var29 = (var25 * 2.0 + 1.0) * var21 - (var25 + 1.0) * (var25 + 1.0) * var17 + var25 * var25 * var13;
         double var31 = (var25 + 1.0) * var21;
         double var33;
         double var37;
         if ((var33 = var29 * var29 - var27 * 4.0 * var31) >= 0.0) {
            double var39 = var29 + BY.a(var33);
            double var41 = var29 - BY.a(var33);
            var37 = BY.w(var39) > BY.w(var41) ? var39 : var41;
         } else {
            var37 = BY.a(var29 * var29 - var33);
         }

         double var35;
         if (var37 == 0.0) {
            var35 = var1 + BY.a() * (var3 - var1);
            var23 = Double.POSITIVE_INFINITY;
         } else {
            var35 = var19 - var31 * 2.0 * (var19 - var15) / var37;

            while (var35 == var15 || var35 == var19) {
               var35 += var7;
            }
         }

         double var47 = this.b(var35);
         double var48 = BY.e(var5 * BY.w(var35), var7);
         if (BY.w(var35 - var23) <= var48 || BY.w(var47) <= var9) {
            return var35;
         }

         var11 = var15;
         var13 = var17;
         var15 = var19;
         var17 = var21;
         var19 = var35;
         var21 = var47;
         var23 = var35;
      }
   }
}
