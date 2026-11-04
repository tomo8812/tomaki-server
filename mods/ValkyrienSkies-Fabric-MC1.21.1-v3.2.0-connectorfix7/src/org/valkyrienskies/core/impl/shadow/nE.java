package org.valkyrienskies.core.impl.shadow;

public final class nE extends nq {
   private static final double d = 1.0E-6;

   public nE() {
      this(1.0E-6);
   }

   private nE(double var1) {
      super(1.0E-6);
   }

   private nE(double var1, double var3) {
      super(var1, var3);
   }

   @Override
   protected final double j() {
      double var1 = this.d();
      double var3 = this.e();
      double var5 = this.f();
      double var7 = this.i();
      double var19 = var3;
      double var17 = var5;
      nP.b(var1, var17, var19);
      double var9;
      if (BY.w(var9 = this.b(var1)) < var7) {
         return var1;
      }

      double var11;
      if (BY.w(var11 = this.b(var3)) < var7) {
         return var3;
      }

      double var13;
      if (BY.w(var13 = this.b(var5)) < var7) {
         return var5;
      }

      this.c(var1, var3);
      return this.a(var1, var5) ? this.a(var1, var5, var9, var13) : this.a(var5, var3, var13, var11);
   }

   private double a(double var1, double var3, double var5, double var7) {
      double var9 = this.h();
      double var11 = this.g();
      double var13 = this.i();
      double var15 = var1;
      double var17 = var5;
      double var19 = var3;
      double var21 = var7;
      double var23 = 0.5 * (var15 + var19);
      double var25 = this.b(var23);
      double var27 = Double.POSITIVE_INFINITY;

      while (true) {
         double var29 = (var25 - var17) / (var23 - var15);
         double var33 = ((var21 - var25) / (var19 - var23) - var29) / (var19 - var15);
         double var35;
         double var37 = (var35 = var29 + (var23 - var15) * var33) * var35 - var25 * 4.0 * var33;
         double var39 = var23 + var25 * -2.0 / (var35 + BY.a(var37));
         double var41 = var23 + var25 * -2.0 / (var35 - BY.a(var37));
         double var43 = a(var15, var39, var19) ? var39 : var41;
         double var45 = this.b(var43);
         double var47 = BY.e(var9 * BY.w(var43), var11);
         if (BY.w(var43 - var27) <= var47 || BY.w(var45) <= var13) {
            return var43;
         }

         if ((!(var43 < var23) || !(var23 - var15 > 0.95 * (var19 - var15)))
            && (!(var43 > var23) || !(var19 - var23 > 0.95 * (var19 - var15)))
            && var43 != var23) {
            var15 = var43 < var23 ? var15 : var23;
            var17 = var43 < var23 ? var17 : var25;
            var19 = var43 > var23 ? var19 : var23;
            var21 = var43 > var23 ? var21 : var25;
            var23 = var43;
            var25 = var45;
            var27 = var43;
         } else {
            double var50 = 0.5 * (var15 + var19);
            double var52 = this.b(var50);
            if (BY.h(var17) + BY.h(var52) == 0.0) {
               var19 = var50;
               var21 = var52;
            } else {
               var15 = var50;
               var17 = var52;
            }

            var23 = 0.5 * (var15 + var19);
            var25 = this.b(var23);
            var27 = Double.POSITIVE_INFINITY;
         }
      }
   }
}
