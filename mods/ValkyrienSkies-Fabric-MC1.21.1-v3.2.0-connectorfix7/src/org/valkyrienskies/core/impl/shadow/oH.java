package org.valkyrienskies.core.impl.shadow;

public final class oH extends oc {
   private static int b;
   private static double c;
   private static final long d = -3349935121172596109L;
   private final oE e;
   private final on f;
   private final double g;
   private final int h;
   private final double i;

   private oH(double var1) {
      this(var1, 1.0E-12, 10000000);
   }

   private oH(double var1, double var3, int var5) {
      this(new zR(), var1, var3, var5);
   }

   public oH(zF var1, double var2, double var4, int var6) {
      super(var1);
      if (var2 <= 0.0) {
         throw new pk(px.MEAN, var2);
      }

      this.g = var2;
      this.i = var4;
      this.h = var6;
      this.e = new oE(var1, var2, BY.a(var2), 1.0E-9);
      this.f = new on(var1, 1.0, 1.0E-9);
   }

   private oH(double var1, double var3) {
      this(var1, var3, 10000000);
   }

   private oH(double var1, int var3) {
      this(var1, 1.0E-12, var3);
   }

   private double g() {
      return this.g;
   }

   @Override
   public final double c(int var1) {
      double var2;
      return (var2 = this.b(var1)) == Double.NEGATIVE_INFINITY ? 0.0 : BY.i(var2);
   }

   @Override
   public final double b(int var1) {
      double var2;
      if (var1 < 0 || var1 == Integer.MAX_VALUE) {
         var2 = Double.NEGATIVE_INFINITY;
      } else if (var1 == 0) {
         var2 = -this.g;
      } else {
         var2 = -oJ.a(var1) - oJ.a(var1, this.g) - 0.5 * BY.k(Math.PI * 2) - 0.5 * BY.k(var1);
      }

      return var2;
   }

   @Override
   public final double d(int var1) {
      if (var1 < 0) {
         return 0.0;
      } else {
         return var1 == Integer.MAX_VALUE ? 1.0 : zY.b(var1 + 1.0, this.g, this.i, this.h);
      }
   }

   private double e(int var1) {
      return this.e.e(var1 + 0.5);
   }

   @Override
   public final double b() {
      return this.g;
   }

   @Override
   public final double c() {
      return this.g;
   }

   @Override
   public final int d() {
      return 0;
   }

   @Override
   public final int e() {
      return Integer.MAX_VALUE;
   }

   @Override
   public final boolean f() {
      return true;
   }

   @Override
   public final int a() {
      return (int)BY.a(this.b(this.g), 2147483647L);
   }

   private long b(double var1) {
      if (var1 < 40.0) {
         double var51 = BY.i(-var1);
         long var52 = 0L;
         double var53 = 1.0;

         while (var52 < var1 * 1000.0) {
            double var54 = super.a.nextDouble();
            if (!((var53 *= var54) >= var51)) {
               return var52;
            }

            var52++;
         }

         return var52;
      } else {
         double var3 = BY.y(var1);
         double var5 = var1 - var3;
         double var7 = BY.k(var3);
         double var9 = BR.c((int)var3);
         long var11 = var5 < Double.MIN_VALUE ? 0L : this.b(var5);
         double var13;
         double var15 = (var13 = BY.a(var3 * BY.k(var3 * 32.0 / Math.PI + 1.0))) / 2.0;
         double var17 = var3 * 2.0 + var13;
         double var19 = BY.a(var17 * Math.PI) * BY.i(1.0 / (var3 * 8.0));
         double var21 = var17 / var13 * BY.i(-var13 * (var13 + 1.0) / var17);
         double var23 = var19 + var21 + 1.0;
         double var25 = var19 / var23;
         double var27 = var21 / var23;
         double var29 = 1.0 / (var3 * 8.0);

         double var55;
         while (true) {
            double var31;
            double var35;
            double var44;
            if ((var44 = super.a.nextDouble()) <= var25) {
               double var46;
               if ((var31 = (var46 = super.a.nextGaussian()) * BY.a(var3 + var15) - 0.5) > var13 || var31 < -var3) {
                  continue;
               }

               var55 = var31 < 0.0 ? BY.y(var31) : BY.z(var31);
               var35 = -this.f.b() - var46 * var46 / 2.0 + var29;
            } else {
               if (var44 > var25 + var27) {
                  var55 = var3;
                  break;
               }

               var55 = BY.z(var31 = var13 + var17 / var13 * this.f.b());
               var35 = -this.f.b() - var13 * (var31 + 1.0) / var17;
            }

            int var50 = var31 < 0.0 ? 1 : 0;
            double var38 = var55 * (var55 + 1.0) / (var3 * 2.0);
            if (var35 < -var38 && var50 == 0) {
               var55 = var3 + var55;
               break;
            }

            double var40;
            double var42 = (var40 = var38 * ((var55 * 2.0 + 1.0) / (var3 * 6.0) - 1.0)) - var38 * var38 / (3.0 * (var3 + var50 * (var55 + 1.0)));
            if (var35 < var42) {
               var55 = var3 + var55;
               break;
            }

            if (!(var35 > var40) && var35 < var55 * var7 - BR.c((int)(var55 + var3)) + var9) {
               var55 = var3 + var55;
               break;
            }
         }

         return var11 + (long)var55;
      }
   }
}
