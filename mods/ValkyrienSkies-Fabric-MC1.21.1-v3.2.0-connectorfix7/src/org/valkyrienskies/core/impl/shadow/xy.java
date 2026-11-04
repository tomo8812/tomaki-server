package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;

public final class xy extends xu {
   private static final double d = 1.0;
   private static final double e = 2.0;
   private static final double f = 0.5;
   private static final double g = 0.5;
   private final double h;
   private final double i;
   private final double j;
   private final double k;

   private xy(int var1) {
      this(var1, 1.0);
   }

   private xy(int var1, double var2) {
      this(var1, 1.0, 1.0, 2.0, 0.5, 0.5);
   }

   private xy(int var1, double var2, double var4, double var6, double var8, double var10) {
      super(var1, var2);
      this.h = var4;
      this.i = var6;
      this.j = var8;
      this.k = var10;
   }

   private xy(int var1, double var2, double var4, double var6, double var8) {
      this(var1, 1.0, var2, var4, var6, var8);
   }

   private xy(double[] var1) {
      this(var1, 1.0, 2.0, 0.5, 0.5);
   }

   private xy(double[] var1, double var2, double var4, double var6, double var8) {
      super(var1);
      this.h = 1.0;
      this.i = 2.0;
      this.j = 0.5;
      this.k = 0.5;
   }

   private xy(double[][] var1) {
      this(var1, 1.0, 2.0, 0.5, 0.5);
   }

   private xy(double[][] var1, double var2, double var4, double var6, double var8) {
      super(var1);
      this.h = 1.0;
      this.i = 2.0;
      this.j = 0.5;
      this.k = 0.5;
   }

   @Override
   public final void a(kW var1, Comparator<wQ> var2) {
      int var3 = this.a();
      wQ var4 = this.a(0);
      wQ var5 = this.a(var3 - 1);
      wQ var6;
      double[] var7 = (double[])(var6 = this.a(var3)).a;
      double[] var8 = new double[var3];

      for (int var9 = 0; var9 < var3; var9++) {
         double[] var10 = (double[])this.a(var9).a;

         for (int var11 = 0; var11 < var3; var11++) {
            var8[var11] += var10[var11];
         }
      }

      double var25 = 1.0 / var3;

      for (int var28 = 0; var28 < var3; var28++) {
         var8[var28] *= var25;
      }

      double[] var29 = new double[var3];

      for (int var26 = 0; var26 < var3; var26++) {
         var29[var26] = var8[var26] + this.h * (var8[var26] - var7[var26]);
      }

      wQ var27 = new wQ(var29, var1.a(var29), false);
      if (var2.compare(var4, var27) <= 0 && var2.compare(var27, var5) < 0) {
         this.a(var27, var2);
      } else {
         if (var2.compare(var27, var4) < 0) {
            double[] var12 = new double[var3];

            for (int var16 = 0; var16 < var3; var16++) {
               var12[var16] = var8[var16] + this.i * (var29[var16] - var8[var16]);
            }

            var5 = new wQ(var12, var1.a(var12), false);
            if (var2.compare(var5, var27) >= 0) {
               this.a(var27, var2);
               return;
            }

            this.a(var5, var2);
         } else {
            if (var2.compare(var27, var6) < 0) {
               double[] var14 = new double[var3];

               for (int var20 = 0; var20 < var3; var20++) {
                  var14[var20] = var8[var20] + this.j * (var29[var20] - var8[var20]);
               }

               var5 = new wQ(var14, var1.a(var14), false);
               if (var2.compare(var5, var27) <= 0) {
                  this.a(var5, var2);
                  return;
               }
            } else {
               double[] var13 = new double[var3];

               for (int var18 = 0; var18 < var3; var18++) {
                  var13[var18] = var8[var18] - this.j * (var8[var18] - var7[var18]);
               }

               var5 = new wQ(var13, var1.a(var13), false);
               if (var2.compare(var5, var6) < 0) {
                  this.a(var5, var2);
                  return;
               }
            }

            double[] var15 = (double[])this.a(0).a;

            for (int var22 = 1; var22 <= var3; var22++) {
               double[] var23 = this.a(var22).a();

               for (int var24 = 0; var24 < var3; var24++) {
                  var23[var24] = var15[var24] + this.k * (var23[var24] - var15[var24]);
               }

               this.a(var22, new wQ(var23, Double.NaN, false));
            }

            this.a((kW)var1, (Comparator<wQ>)var2);
         }
      }
   }
}
