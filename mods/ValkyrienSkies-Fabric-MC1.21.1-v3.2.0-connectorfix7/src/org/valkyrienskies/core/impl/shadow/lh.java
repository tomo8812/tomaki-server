package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class lh implements Serializable, lq, lr, ls {
   private static final long a = 20120917L;
   private final int b;
   private final double c;
   private final double d;
   private final double e;
   private final double f;

   private lh(int var1, double var2) {
      this(var1, var2, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
   }

   private lh(int var1, double var2, double var4, double var6) {
      if (var1 <= 1) {
         throw new pn(var2, 1, false);
      }

      this.b = var1;
      if (var2 <= 0.0) {
         throw new pj(var2);
      }

      this.c = var2;
      this.d = var2 * 0.5 * (var1 - 1);
      if (2.0 * this.d >= Double.POSITIVE_INFINITY) {
         throw new pm(2.0 * this.d, Double.POSITIVE_INFINITY, false);
      }

      double var8 = BY.x(this.d);
      this.e = (Double.NEGATIVE_INFINITY) + this.d + var8;
      this.f = (Double.POSITIVE_INFINITY) - this.d - var8;
   }

   private int a() {
      return this.b;
   }

   private double b() {
      return this.c;
   }

   private lg a(lg var1, double var2, double[] var4) {
      double[] var5 = new double[this.b];
      double[] var6 = new double[this.b];

      for (int var7 = 0; var7 < this.b; var7++) {
         var6[var7] = var4[var7];

         for (int var8 = 1; var8 <= var7; var8++) {
            var6[var7 - var8] = (var6[var7 - var8 + 1] - var6[var7 - var8]) / (var8 * this.c);
         }

         var5[var7] = var6[0];
      }

      int var16 = var1.D();
      int var17 = var1.C();
      var4 = var1.F();
      double var10 = var1.b[0] - var2;
      var1 = new lg(var17, var16, 0.0);
      lg var13 = null;

      for (int var3 = 0; var3 < this.b; var3++) {
         if (var3 == 0) {
            var13 = new lg(var17, var16, 1.0);
         } else {
            var4[0] = var10 - (var3 - 1) * this.c;
            lg var15 = new lg(var17, var16, var4);
            var13 = var13.b(var15);
         }

         var1 = var1.a(var13.j(var5[var3]));
      }

      return var1;
   }

   @Override
   public final <unrepresentable> a(final lc var1) {
      return new ln() {
         @Override
         public final double a(double var1x) {
            return var1.a(var1x);
         }

         @Override
         public final lg a(lg var1x) {
            if (var1x.D() >= lh.this.b) {
               throw new pm(var1x.D(), lh.this.b, false);
            }

            double var2 = BY.e(BY.d(var1x.b[0], lh.this.f), lh.this.e) - lh.this.d;
            double[] var4 = new double[lh.this.b];

            for (int var5 = 0; var5 < lh.this.b; var5++) {
               var4[var5] = var1.a(var2 + var5 * lh.this.c);
            }

            return lh.a(lh.this, var1x, var2, var4);
         }
      };
   }

   @Override
   public final <unrepresentable> a(final le var1) {
      return new lp() {
         @Override
         public final double[] a(double var1x) {
            return var1.a(var1x);
         }

         @Override
         public final lg[] a(lg var1x) {
            if (var1x.D() >= lh.this.b) {
               throw new pm(var1x.D(), lh.this.b, false);
            }

            double var2 = BY.e(BY.d(var1x.b[0], lh.this.f), lh.this.e) - lh.this.d;
            double[][] var4 = null;

            for (int var5 = 0; var5 < lh.this.b; var5++) {
               double[] var6 = var1.a(var2 + var5 * lh.this.c);
               if (var5 == 0) {
                  var4 = new double[var6.length][lh.this.b];
               }

               for (int var7 = 0; var7 < var6.length; var7++) {
                  var4[var7][var5] = var6[var7];
               }
            }

            lg[] var8 = new lg[var4.length];

            for (int var9 = 0; var9 < var8.length; var9++) {
               var8[var9] = lh.a(lh.this, var1x, var2, var4[var9]);
            }

            return var8;
         }
      };
   }

   @Override
   public final <unrepresentable> a(final ld var1) {
      return new lo() {
         @Override
         public final double[][] a(double var1x) {
            return var1.a(var1x);
         }

         @Override
         public final lg[][] a(lg var1x) {
            if (var1x.D() >= lh.this.b) {
               throw new pm(var1x.D(), lh.this.b, false);
            }

            double var2 = BY.e(BY.d(var1x.b[0], lh.this.f), lh.this.e) - lh.this.d;
            double[][][] var4 = null;

            for (int var5 = 0; var5 < lh.this.b; var5++) {
               double[][] var6 = var1.a(var2 + var5 * lh.this.c);
               if (var5 == 0) {
                  var4 = new double[var6.length][var6[0].length][lh.this.b];
               }

               for (int var7 = 0; var7 < var6.length; var7++) {
                  for (int var8 = 0; var8 < var6[var7].length; var8++) {
                     var4[var7][var8][var5] = var6[var7][var8];
                  }
               }
            }

            lg[][] var9 = new lg[var4.length][var4[0].length];

            for (int var10 = 0; var10 < var9.length; var10++) {
               for (int var11 = 0; var11 < var4[var10].length; var11++) {
                  var9[var10][var11] = lh.a(lh.this, var1x, var2, var4[var10][var11]);
               }
            }

            return var9;
         }
      };
   }

   // $VF: synthetic method
   static lg a(lh var0, lg var1, double var2, double[] var4) {
      double[] var20 = var4;
      double var7 = var2;
      var1 = var1;
      lh var17 = var0;
      double[] var3 = new double[var0.b];
      var4 = new double[var17.b];

      for (int var5 = 0; var5 < var17.b; var5++) {
         var4[var5] = var20[var5];

         for (int var6 = 1; var6 <= var5; var6++) {
            var4[var5 - var6] = (var4[var5 - var6 + 1] - var4[var5 - var6]) / (var6 * var17.c);
         }

         var3[var5] = var4[0];
      }

      int var24 = var1.D();
      int var25 = var1.C();
      double[] var21 = var1.F();
      double var15 = var1.b[0] - var7;
      var1 = new lg(var25, var24, 0.0);
      lg var23 = null;

      for (int var26 = 0; var26 < var17.b; var26++) {
         if (var26 == 0) {
            var23 = new lg(var25, var24, 1.0);
         } else {
            var21[0] = var15 - (var26 - 1) * var17.c;
            lg var8 = new lg(var25, var24, var21);
            var23 = var23.b(var8);
         }

         var1 = var1.a(var23.j(var3[var26]));
      }

      return var1;
   }
}
