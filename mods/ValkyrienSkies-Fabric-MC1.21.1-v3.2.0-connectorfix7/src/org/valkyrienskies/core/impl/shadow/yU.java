package org.valkyrienskies.core.impl.shadow;

@Deprecated
public final class yU extends yQ {
   private final int c;
   private final yV d;
   private final nO e;
   private double f;
   double[] b;

   @Deprecated
   private yU(int var1) {
      this(var1, new yt());
   }

   private yU(int var1, ya<yp> var2) {
      this(var1, var2, new nz(), new yU.a());
   }

   private yU(int var1, ya<yp> var2, nO var3) {
      this(var1, var2, var3, new yU.a());
   }

   private yU(int var1, ya<yp> var2, nO var3, yV var4) {
      super(var2);
      this.c = var1;
      this.e = var3;
      this.d = var4;
      this.f = 1.0;
   }

   private void a(double var1) {
      if (var1 <= 0.0) {
         this.f = 1.0;
      } else {
         this.f = var1;
      }
   }

   @Override
   public final yp d() {
      ya var1 = this.c();
      this.b = this.f();
      yf var2 = this.e();
      int var3 = this.b.length;
      double[] var4 = this.b(this.b);
      if (var2 == yf.MINIMIZE) {
         for (int var5 = 0; var5 < var3; var5++) {
            var4[var5] = -var4[var5];
         }
      }

      double[] var44;
      double[] var6 = (double[])(var44 = this.d.a(var4)).clone();
      double var7 = 0.0;

      for (int var9 = 0; var9 < var3; var9++) {
         var7 += var4[var9] * var6[var9];
      }

      yp var45 = null;
      int var10 = 0;
      int var11 = this.a();

      label95:
      while (true) {
         var10++;
         double var12 = this.a(this.b);
         yp var41 = var45;
         var45 = new yp(this.b, var12);
         if (var41 != null && var1.a(var10, var41, var45)) {
            return var45;
         }

         yU.b var42;
         yU.b var10000 = var42 = new yU.b(var6);
         double var31 = this.f;
         double var29 = 0.0;
         yU.b var46 = var10000;
         double var33 = var10000.a(0.0);
         double var37 = var31;

         while (var37 < Double.MAX_VALUE) {
            double var39 = var37 + 0.0;
            double var35 = var46.a(var39);
            if (var33 * var35 <= 0.0) {
               double var16 = var39;
               double var18 = this.e.b(var11, var42, 0.0, var16, 1.0E-15);
               var11 -= this.e.c();

               for (int var20 = 0; var20 < this.b.length; var20++) {
                  this.b[var20] = this.b[var20] + var18 * var6[var20];
               }

               var4 = this.b(this.b);
               if (var2 == yf.MINIMIZE) {
                  for (int var48 = 0; var48 < var3; var48++) {
                     var4[var48] = -var4[var48];
                  }
               }

               double var49 = var7;
               double[] var47 = this.d.a(var4);
               var7 = 0.0;

               for (int var23 = 0; var23 < var3; var23++) {
                  var7 += var4[var23] * var47[var23];
               }

               double var50;
               if (this.c == yR.FLETCHER_REEVES$7966eac9) {
                  var50 = var7 / var49;
               } else {
                  double var25 = 0.0;

                  for (int var13 = 0; var13 < var4.length; var13++) {
                     var25 += var4[var13] * var44[var13];
                  }

                  var50 = (var7 - var25) / var49;
               }

               var44 = var47;
               if (var10 % var3 != 0 && !(var50 < 0.0)) {
                  for (int var51 = 0; var51 < var3; var51++) {
                     var6[var51] = var44[var51] + var50 * var6[var51];
                  }
                  continue label95;
               }

               var6 = (double[])var44.clone();
               continue label95;
            }

            var37 *= BY.e(2.0, var33 / var35);
         }

         throw new oX(px.UNABLE_TO_BRACKET_OPTIMUM_IN_LINE_SEARCH);
      }
   }

   private static double a(yU.b var0, double var1, double var3) {
      double var5 = var0.a(0.0);
      double var9 = var3;

      while (var9 < Double.MAX_VALUE) {
         double var11 = var9 + 0.0;
         double var7 = var0.a(var11);
         if (var5 * var7 <= 0.0) {
            return var11;
         }

         var9 *= BY.e(2.0, var5 / var7);
      }

      throw new oX(px.UNABLE_TO_BRACKET_OPTIMUM_IN_LINE_SEARCH);
   }

   // $VF: synthetic method
   static double[] a(yU var0) {
      return var0.b;
   }

   public static class a implements yV {
      @Override
      public final double[] a(double[] var1) {
         return (double[])var1.clone();
      }
   }

   class b implements lc {
      private final double[] a;

      b(double[] var2) {
         this.a = var2;
      }

      @Override
      public final double a(double var1) {
         yU var3 = yU.this;
         double[] var8 = (double[])yU.this.b.clone();

         for (int var4 = 0; var4 < var8.length; var4++) {
            var8[var4] += var1 * this.a[var4];
         }

         double[] var9 = yU.this.b(var8);
         double var5 = 0.0;

         for (int var7 = 0; var7 < var9.length; var7++) {
            var5 += var9[var7] * this.a[var7];
         }

         return var5;
      }
   }
}
