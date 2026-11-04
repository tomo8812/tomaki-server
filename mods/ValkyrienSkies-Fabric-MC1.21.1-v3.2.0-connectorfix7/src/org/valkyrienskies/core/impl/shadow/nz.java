package org.valkyrienskies.core.impl.shadow;

public final class nz extends nq {
   private static final double d = 1.0E-6;

   public nz() {
      this(1.0E-6);
   }

   public nz(double var1) {
      super(var1);
   }

   private nz(double var1, double var3) {
      super(var1, var3);
   }

   private nz(double var1, double var3, double var5) {
      super(var1, var3, var5);
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
      if (BY.w(var9 = this.b(var5)) <= var7) {
         return var5;
      } else {
         double var11;
         if (BY.w(var11 = this.b(var1)) <= var7) {
            return var1;
         } else if (var9 * var11 < 0.0) {
            return this.a(var1, var5, var11, var9);
         } else {
            double var13;
            if (BY.w(var13 = this.b(var3)) <= var7) {
               return var3;
            } else if (var9 * var13 < 0.0) {
               return this.a(var5, var3, var9, var13);
            } else {
               throw new pe(var1, var3, var11, var13);
            }
         }
      }
   }

   private double a(double var1, double var3, double var5, double var7) {
      double var9 = var1;
      double var11 = var5;
      double var13 = var3;
      double var15 = var7;
      double var17 = var9;
      double var19 = var11;
      double var21;
      double var23 = var21 = var13 - var9;
      double var25 = this.g();
      double var27 = this.h();

      while (true) {
         if (BY.w(var19) < BY.w(var15)) {
            var9 = var13;
            var13 = var17;
            var17 = var9;
            var11 = var15;
            var15 = var19;
            var19 = var11;
         }

         double var29 = var27 * 2.0 * BY.w(var13) + var25;
         double var31;
         if (BY.w(var31 = 0.5 * (var17 - var13)) <= var29 || Cq.a(var15, 0.0)) {
            return var13;
         }

         if (!(BY.w(var23) < var29) && !(BY.w(var11) <= BY.w(var15))) {
            double var33 = var15 / var11;
            double var35;
            double var37;
            if (var9 == var17) {
               var35 = var31 * 2.0 * var33;
               var37 = 1.0 - var33;
            } else {
               var37 = var11 / var19;
               double var39 = var15 / var19;
               var35 = var33 * (var31 * 2.0 * var37 * (var37 - var39) - (var13 - var9) * (var39 - 1.0));
               var37 = (var37 - 1.0) * (var39 - 1.0) * (var33 - 1.0);
            }

            if (var35 > 0.0) {
               var37 = -var37;
            } else {
               var35 = -var35;
            }

            var33 = var23;
            var23 = var21;
            if (!(var35 >= var31 * 1.5 * var37 - BY.w(var29 * var37)) && !(var35 >= BY.w(var33 * 0.5 * var37))) {
               var21 = var35 / var37;
            } else {
               var21 = var31;
               var23 = var31;
            }
         } else {
            var21 = var31;
            var23 = var31;
         }

         var9 = var13;
         var11 = var15;
         if (BY.w(var21) > var29) {
            var13 += var21;
         } else if (var31 > 0.0) {
            var13 += var29;
         } else {
            var13 -= var29;
         }

         if ((var15 = this.b(var13)) > 0.0 && var19 > 0.0 || var15 <= 0.0 && var19 <= 0.0) {
            var17 = var9;
            var19 = var11;
            var23 = var21 = var13 - var9;
         }
      }
   }
}
