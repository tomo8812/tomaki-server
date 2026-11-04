package org.valkyrienskies.core.impl.shadow;

public final class of extends oe {
   private static double b;
   private static final long c = -1221965979403477668L;
   private final double d;
   private final double e;
   private double f;
   private final double g;

   private of(double var1, double var3) {
      this(var1, var3, 1.0E-9);
   }

   private of(double var1, double var3, double var5) {
      this(new zR(), var1, var3, 1.0E-9);
   }

   private of(zF var1, double var2, double var4) {
      this(var1, var2, var4, 1.0E-9);
   }

   public of(zF var1, double var2, double var4, double var6) {
      super(var1);
      this.d = var2;
      this.e = var4;
      this.f = Double.NaN;
      this.g = var6;
   }

   private double j() {
      return this.d;
   }

   private double k() {
      return this.e;
   }

   private void l() {
      if (Double.isNaN(this.f)) {
         this.f = zY.a(this.d) + zY.a(this.e) - zY.a(this.d + this.e);
      }
   }

   @Override
   public final double d(double var1) {
      double var3;
      return (var3 = this.c(var1)) == Double.NEGATIVE_INFINITY ? 0.0 : BY.i(var3);
   }

   @Override
   public final double c(double var1) {
      this.l();
      if (var1 < 0.0 || var1 > 1.0) {
         return Double.NEGATIVE_INFINITY;
      }

      if (var1 == 0.0) {
         if (this.d < 1.0) {
            throw new pn(px.CANNOT_COMPUTE_BETA_DENSITY_AT_0_FOR_SOME_ALPHA, this.d, 1, false);
         } else {
            return Double.NEGATIVE_INFINITY;
         }
      } else if (var1 != 1.0) {
         double var3 = BY.k(var1);
         double var5 = BY.l(-var1);
         return (this.d - 1.0) * var3 + (this.e - 1.0) * var5 - this.f;
      } else if (this.e < 1.0) {
         throw new pn(px.CANNOT_COMPUTE_BETA_DENSITY_AT_1_FOR_SOME_BETA, this.e, 1, false);
      } else {
         return Double.NEGATIVE_INFINITY;
      }
   }

   @Override
   public final double e(double var1) {
      if (var1 <= 0.0) {
         return 0.0;
      } else {
         return var1 >= 1.0 ? 1.0 : zW.a(var1, this.d, this.e);
      }
   }

   @Override
   protected final double a() {
      return this.g;
   }

   @Override
   public final double c() {
      double var1 = this.d;
      return this.d / (var1 + this.e);
   }

   @Override
   public final double d() {
      double var1 = this.d;
      double var3 = this.e;
      double var5 = var1 + var3;
      return var1 * var3 / (var5 * var5 * (var5 + 1.0));
   }

   @Override
   public final double e() {
      return 0.0;
   }

   @Override
   public final double f() {
      return 1.0;
   }

   @Override
   public final boolean g() {
      return false;
   }

   @Override
   public final boolean h() {
      return false;
   }

   @Override
   public final boolean i() {
      return true;
   }

   @Override
   public final double b() {
      double var4 = this.e;
      double var2 = this.d;
      zF var1 = super.a;
      double var6 = BY.d(var2, var4);
      double var8 = BY.e(var2, var4);
      double var10000;
      if (var6 > 1.0) {
         double var15 = var8;
         double var13 = var6;
         double var11 = var2;
         var1 = var1;
         double var17;
         double var19 = BY.a(((var17 = var13 + var15) - 2.0) / (var13 * 2.0 * var15 - var17));
         double var21 = var13 + 1.0 / var19;

         double var23;
         double var25;
         double var27;
         double var37;
         do {
            double var29 = var1.nextDouble();
            double var31 = var1.nextDouble();
            double var33 = var19 * (BY.k(var29) - BY.l(-var29));
            var25 = var13 * BY.i(var33);
            double var35 = var29 * var29 * var31;
            var23 = var21 * var33 - 1.3862944;
            if ((var37 = var13 + var23 - var25) + 2.609438 >= var35 * 5.0) {
               break;
            }

            var27 = BY.k(var35);
         } while (!(var37 >= var27) && var23 + var17 * (BY.k(var17) - BY.k(var15 + var25)) < var27);

         var25 = BY.d(var25, Double.MAX_VALUE);
         if (!Cq.a(var13, var11)) {
            return var15 / (var15 + var25);
         }

         var10000 = var25 / (var15 + var25);
      } else {
         double var43 = var6;
         double var42 = var8;
         double var41 = var2;
         var1 = var1;
         double var44 = var42 + var43;
         double var45 = 1.0 / var43;
         double var46;
         double var47 = (var46 = var42 + 1.0 - var43) * (0.0138889 + var43 * 0.0416667) / (var42 * var45 - 0.777778);
         double var49 = 0.25 + (0.5 + 0.25 / var46) * var43;

         double var50;
         double var55;
         double var57;
         label49:
         do {
            double var52;
            while (true) {
               var52 = var1.nextDouble();
               double var53 = var1.nextDouble();
               double var54 = var52 * var53;
               var55 = var52 * var54;
               if (var52 < 0.5) {
                  if (var53 * 0.25 + var55 - var54 >= var47) {
                     continue;
                  }
                  break;
               } else {
                  if (var55 <= 0.25) {
                     var57 = var45 * (BY.k(var52) - BY.l(-var52));
                     var50 = var42 * BY.i(var57);
                     break label49;
                  }

                  if (!(var55 >= var49)) {
                     break;
                  }
               }
            }

            var57 = var45 * (BY.k(var52) - BY.l(-var52));
            var50 = var42 * BY.i(var57);
         } while (!(var44 * (BY.k(var44) - BY.k(var43 + var50) + var57) - 1.3862944 >= BY.k(var55)));

         var50 = BY.d(var50, Double.MAX_VALUE);
         if (Cq.a(var42, var41)) {
            return var50 / (var43 + var50);
         }

         var10000 = var43 / (var43 + var50);
      }

      return var10000;
   }

   static final class a {
      private a() {
      }

      static double a(zF var0, double var1, double var3) {
         double var5 = BY.d(var1, var3);
         double var7 = BY.e(var1, var3);
         return var5 > 1.0 ? a(var0, var1, var5, var7) : b(var0, var1, var7, var5);
      }

      private static double a(zF var0, double var1, double var3, double var5) {
         double var7;
         double var9 = BY.a(((var7 = var3 + var5) - 2.0) / (var3 * 2.0 * var5 - var7));
         double var11 = var3 + 1.0 / var9;

         double var13;
         double var15;
         double var17;
         double var27;
         do {
            double var19 = var0.nextDouble();
            double var21 = var0.nextDouble();
            double var23 = var9 * (BY.k(var19) - BY.l(-var19));
            var15 = var3 * BY.i(var23);
            double var25 = var19 * var19 * var21;
            var13 = var11 * var23 - 1.3862944;
            if ((var27 = var3 + var13 - var15) + 2.609438 >= var25 * 5.0) {
               break;
            }

            var17 = BY.k(var25);
         } while (!(var27 >= var17) && var13 + var7 * (BY.k(var7) - BY.k(var5 + var15)) < var17);

         var15 = BY.d(var15, Double.MAX_VALUE);
         return Cq.a(var3, var1) ? var15 / (var5 + var15) : var5 / (var5 + var15);
      }

      private static double b(zF var0, double var1, double var3, double var5) {
         double var7 = var3 + var5;
         double var9 = 1.0 / var5;
         double var11;
         double var13 = (var11 = var3 + 1.0 - var5) * (0.0138889 + var5 * 0.0416667) / (var3 * var9 - 0.777778);
         double var15 = 0.25 + (0.5 + 0.25 / var11) * var5;

         double var17;
         while (true) {
            double var19 = var0.nextDouble();
            double var21 = var0.nextDouble();
            double var23 = var19 * var21;
            double var25 = var19 * var23;
            if (var19 < 0.5) {
               if (var21 * 0.25 + var25 - var23 >= var13) {
                  continue;
               }
            } else {
               if (var25 <= 0.25) {
                  double var30 = var9 * (BY.k(var19) - BY.l(-var19));
                  var17 = var3 * BY.i(var30);
                  break;
               }

               if (var25 >= var15) {
                  continue;
               }
            }

            double var27 = var9 * (BY.k(var19) - BY.l(-var19));
            var17 = var3 * BY.i(var27);
            if (var7 * (BY.k(var7) - BY.k(var5 + var17) + var27) - 1.3862944 >= BY.k(var25)) {
               break;
            }
         }

         var17 = BY.d(var17, Double.MAX_VALUE);
         return Cq.a(var3, var1) ? var17 / (var5 + var17) : var5 / (var5 + var17);
      }
   }
}
