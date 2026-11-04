package org.valkyrienskies.core.impl.shadow;

final class mF implements kP {
   private static final short f = 4;
   private final double[][] g = new double[4][4];
   final <unrepresentable> a;
   final <unrepresentable> b;
   final <unrepresentable> c;
   final <unrepresentable> d;
   final <unrepresentable> e;

   private mF(double[] var1) {
      this(var1, false);
   }

   mF(double[] var1, boolean var2) {
      for (int var3 = 0; var3 < 4; var3++) {
         for (int var4 = 0; var4 < 4; var4++) {
            this.g[var3][var4] = var1[(var3 << 2) + var4];
         }
      }

      if (!var2) {
         this.a = null;
         this.b = null;
         this.c = null;
         this.d = null;
         this.e = null;
      } else {
         final double[][] var14 = new double[4][4];
         final double[][] var15 = new double[4][4];
         final double[][] var12 = new double[4][4];
         final double[][] var13 = new double[4][4];
         final double[][] var5 = new double[4][4];

         for (int var6 = 0; var6 < 4; var6++) {
            for (int var7 = 0; var7 < 4; var7++) {
               double var10 = this.g[var6][var7];
               var14[var6][var7] = var6 * var10;
               var15[var6][var7] = var7 * var10;
               var12[var6][var7] = (var6 - 1) * var14[var6][var7];
               var13[var6][var7] = (var7 - 1) * var15[var6][var7];
               var5[var6][var7] = var7 * var14[var6][var7];
            }
         }

         this.a = new kP() {
            @Override
            public final double a(double var1, double var3) {
               double var5x = var1 * var1;
               double[] var12x = new double[]{0.0, 1.0, var1, var5x};
               double var8;
               double var10 = (var8 = var3 * var3) * var3;
               double[] var2x = new double[]{1.0, var3, var8, var10};
               double[][] var4 = var14;
               double[] var15x = var2x;
               var2x = var12x;
               mF var13x = mF.this;
               return mF.a(var2x, var15x, var4);
            }
         };
         this.b = new kP() {
            @Override
            public final double a(double var1, double var3) {
               double var5;
               double var7 = (var5 = var1 * var1) * var1;
               double[] var12 = new double[]{1.0, var1, var5, var7};
               double var10 = var3 * var3;
               double[] var2 = new double[]{0.0, 1.0, var3, var10};
               double[][] var4 = var15;
               double[] var15x = var2;
               var2 = var12;
               mF var13 = mF.this;
               return mF.a(var2, var15x, var4);
            }
         };
         this.c = new kP() {
            @Override
            public final double a(double var1, double var3) {
               double[] var10 = new double[]{0.0, 0.0, 1.0, var1};
               double var6;
               double var8 = (var6 = var3 * var3) * var3;
               double[] var2 = new double[]{1.0, var3, var6, var8};
               double[][] var4 = var12;
               double[] var13 = var2;
               var2 = var10;
               mF var11 = mF.this;
               return mF.a(var2, var13, var4);
            }
         };
         this.d = new kP() {
            @Override
            public final double a(double var1, double var3) {
               double var5;
               double var7 = (var5 = var1 * var1) * var1;
               double[] var9 = new double[]{1.0, var1, var5, var7};
               double[] var2 = new double[]{0.0, 0.0, 1.0, var3};
               double[][] var4 = var13;
               double[] var12 = var2;
               var2 = var9;
               mF var10 = mF.this;
               return mF.a(var2, var12, var4);
            }
         };
         this.e = new kP() {
            @Override
            public final double a(double var1, double var3) {
               double var5x = var1 * var1;
               double[] var10 = new double[]{0.0, 1.0, var1, var5x};
               double var8 = var3 * var3;
               double[] var2 = new double[]{0.0, 1.0, var3, var8};
               double[][] var4 = var5;
               double[] var13 = var2;
               var2 = var10;
               mF var11 = mF.this;
               return mF.a(var2, var13, var4);
            }
         };
      }
   }

   @Override
   public final double a(double var1, double var3) {
      if (var1 < 0.0 || var1 > 1.0) {
         throw new po(var1, 0, 1);
      } else if (!(var3 < 0.0) && !(var3 > 1.0)) {
         double var5;
         double var7 = (var5 = var1 * var1) * var1;
         double[] var14 = new double[]{1.0, var1, var5, var7};
         double var10;
         double var12 = (var10 = var3 * var3) * var3;
         double[] var2 = new double[]{1.0, var3, var10, var12};
         return a(var14, var2, this.g);
      } else {
         throw new po(var3, 0, 1);
      }
   }

   static double a(double[] var0, double[] var1, double[][] var2) {
      double var3 = 0.0;

      for (int var5 = 0; var5 < 4; var5++) {
         for (int var6 = 0; var6 < 4; var6++) {
            var3 += var2[var5][var6] * var0[var5] * var1[var6];
         }
      }

      return var3;
   }

   public final kP a() {
      return this.a;
   }

   public final kP b() {
      return this.b;
   }

   public final kP c() {
      return this.c;
   }

   public final kP d() {
      return this.d;
   }

   public final kP e() {
      return this.e;
   }

   // $VF: synthetic method
   static double a(mF var0, double[] var1, double[] var2, double[][] var3) {
      return a(var1, var2, var3);
   }
}
