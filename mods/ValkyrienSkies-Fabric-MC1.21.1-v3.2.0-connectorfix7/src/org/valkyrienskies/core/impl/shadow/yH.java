package org.valkyrienskies.core.impl.shadow;

@Deprecated
public final class yH extends yz<kW> implements yn {
   private static final double b = 2.0 * BY.x(1.0);
   private final double c;
   private final double d;
   private final yH.a e;

   private yH(double var1, double var3, ya<yp> var5) {
      this(var1, var3, BY.a(var1), BY.a(var3), null);
   }

   private yH(double var1, double var3, double var5, double var7, ya<yp> var9) {
      super(var9);
      if (var1 < b) {
         throw new pn(var1, b, true);
      }

      if (var3 <= 0.0) {
         throw new pk(var3);
      }

      this.c = var1;
      this.d = var3;
      this.e = new yH.a(var5, var7);
   }

   private yH(double var1, double var3) {
      this(var1, var3, null);
   }

   private yH(double var1, double var3, double var5, double var7) {
      this(var1, var3, var5, var7, null);
   }

   @Override
   protected final yp d() {
      yf var1 = this.e();
      double[] var2;
      int var3;
      double[][] var4 = new double[var3 = (var2 = this.f()).length][var3];

      for (int var5 = 0; var5 < var3; var5++) {
         var4[var5][var5] = 1.0;
      }

      ya var30 = this.c();
      var2 = var2;
      double var7 = this.a(var2);
      double[] var6 = (double[])var2.clone();
      int var9 = 0;

      while (true) {
         var9++;
         double var11 = var7;
         double var15 = 0.0;
         int var10 = 0;

         for (int var17 = 0; var17 < var3; var17++) {
            double[] var19;
            double[] var20 = Ch.a(var19 = var4[var17], var19.length);
            double var13 = var7;
            zm var18;
            var7 = (var18 = this.e.a(var2, var20)).b;
            double var36 = var18.a;
            var2 = a(var2, var20, var36)[0];
            if (var13 - var7 > var15) {
               var15 = var13 - var7;
               var10 = var17;
            }
         }

         boolean var35 = 2.0 * (var11 - var7) <= this.c * (BY.w(var11) + BY.w(var7)) + this.d;
         yp var39 = new yp(var6, var11);
         yp var37 = new yp(var2, var7);
         if (!var35 && var30 != null) {
            var35 = var30.a(var9, var39, var37);
         }

         if (var35) {
            if (var1 == yf.MINIMIZE) {
               if (var7 < var11) {
                  return var37;
               }

               return var39;
            }

            if (var7 > var11) {
               return var37;
            }

            return var39;
         }

         double[] var40 = new double[var3];
         double[] var33 = new double[var3];

         for (int var25 = 0; var25 < var3; var25++) {
            var40[var25] = var2[var25] - var6[var25];
            var33[var25] = 2.0 * var2[var25] - var6[var25];
         }

         var6 = (double[])var2.clone();
         double var34 = this.a(var33);
         if (var11 > var34) {
            double var41 = 2.0 * (var11 + var34 - var7 * 2.0);
            double var27 = var11 - var7 - var15;
            var41 *= var27 * var27;
            var27 = var11 - var34;
            if ((var41 = var41 - var15 * var27 * var27) < 0.0) {
               zm var31;
               var7 = (var31 = this.e.a(var2, var40)).b;
               double var38 = var31.a;
               double[][] var32;
               var2 = (var32 = a(var2, var40, var38))[0];
               int var12 = var3 - 1;
               var4[var10] = var4[var12];
               var4[var12] = var32[1];
            }
         }
      }
   }

   private static double[][] a(double[] var0, double[] var1, double var2) {
      int var4;
      double[] var5 = new double[var4 = var0.length];
      double[] var6 = new double[var4];

      for (int var7 = 0; var7 < var4; var7++) {
         var6[var7] = var1[var7] * var2;
         var5[var7] = var0[var7] + var6[var7];
      }

      double[][] var8;
      (var8 = new double[2][])[0] = var5;
      var8[1] = var6;
      return var8;
   }

   class a extends zi {
      private static final double b = 1.0E-15;
      private static final double c = Double.MIN_VALUE;
      private final zh d = new zh();

      a(double var2, double var4) {
         super(1.0E-15, Double.MIN_VALUE, new zj(var2, var4));
      }

      public final zm a(final double[] var1, final double[] var2) {
         final int var3 = var1.length;
         lc var4 = new lc() {
            @Override
            public final double a(double var1x) {
               double[] var3x = new double[var3];

               for (int var4x = 0; var4x < var3; var4x++) {
                  var3x[var4x] = var1[var4x] + var1x * var2[var4x];
               }

               return yH.this.a(var3x);
            }
         };
         yH var5 = yH.this;
         yf var6 = yH.super.a;
         this.d.a(var4, var6, 0.0, 1.0);
         zh var7 = this.d;
         zh var8 = this.d;
         zh var9 = this.d;
         return this.a(Integer.MAX_VALUE, var4, var6, this.d.a, this.d.b, this.d.c);
      }
   }
}
