package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class vv {
   private static final Map<Integer, vv> c = new HashMap<>();
   final sy a;
   final double[] b;

   private vv(int var1) {
      int var2;
      qd[][] var9 = new qd[var2 = --var1][var2];

      for (int var3 = 1; var3 <= var9.length; var3++) {
         qd[] var4 = var9[var3 - 1];
         int var5;
         int var6 = var5 = -var3;

         for (int var7 = 1; var7 <= var4.length; var7++) {
            var4[var7 - 1] = new qd(var6 * (var7 + 1));
            var6 *= var5;
         }
      }

      sx var10 = new sx<>(var9, false);
      sP.a var13 = new sP(var10).a();
      qd[] var14;
      Arrays.fill(var14 = new qd[var1], qd.b);
      var14 = var13.a(new sz<>(var14, false)).h();

      for (int var16 = (var11 = (qd[][])var10.c()).length - 1; var16 > 0; var16--) {
         var11[var16] = var11[var16 - 1];
      }

      var11[0] = new qd[var1];
      Arrays.fill(var11[0], qd.c);
      sx var17 = var13.a(new sx<>(var11, false));
      this.a = td.a(var17);
      this.b = new double[var1];

      for (int var12 = 0; var12 < var1; var12++) {
         this.b[var12] = var14[var12].doubleValue();
      }
   }

   public static vv a(int var0) {
      synchronized (c) {
         vv var2;
         if ((var2 = c.get(var0)) == null) {
            var2 = new vv(var0);
            c.put(var0, var2);
         }

         return var2;
      }
   }

   @Deprecated
   private int a() {
      return this.b.length;
   }

   private static sQ<qd> b(int var0) {
      qd[][] var6 = new qd[var0][var0];

      for (int var1 = 1; var1 <= var6.length; var1++) {
         qd[] var2 = var6[var1 - 1];
         int var3;
         int var4 = var3 = -var1;

         for (int var5 = 1; var5 <= var2.length; var5++) {
            var2[var5 - 1] = new qd(var4 * (var5 + 1));
            var4 *= var3;
         }
      }

      return new sx<>(var6, false);
   }

   public final sy a(double var1, double[] var3, double[][] var4, double[][] var5) {
      double[][] var6 = new double[this.b.length + 1][this.b.length + 1];
      double[][] var7 = new double[this.b.length + 1][var4[0].length];
      double[] var8 = var4[0];
      double[] var9 = var5[0];

      for (int var10 = 1; var10 < var4.length; var10++) {
         double var11;
         double var13 = (var11 = var3[var10] - var3[0]) / var1;
         double var15 = 1.0 / var1;
         double[] var17 = var6[2 * var10 - 2];
         double[] var18 = 2 * var10 - 1 < var6.length ? var6[2 * var10 - 1] : null;

         for (int var19 = 0; var19 < var17.length; var19++) {
            var15 *= var13;
            var17[var19] = var11 * var15;
            if (var18 != null) {
               var18[var19] = (var19 + 2) * var15;
            }
         }

         double[] var25 = var4[var10];
         double[] var21 = var5[var10];
         double[] var14 = var7[2 * var10 - 2];
         double[] var24 = 2 * var10 - 1 < var7.length ? var7[2 * var10 - 1] : null;

         for (int var16 = 0; var16 < var25.length; var16++) {
            var14[var16] = var25[var16] - var8[var16] - var11 * var9[var16];
            if (var24 != null) {
               var24[var16] = var21[var16] - var9[var16];
            }
         }
      }

      tq var20 = new tn(new sy(var6, false)).c().a(new sy(var7, false));
      sy var12 = new sy(var20.f() - 1, var20.g());

      for (int var22 = 0; var22 < var12.f(); var22++) {
         for (int var23 = 0; var23 < var12.g(); var23++) {
            var12.c(var22, var23, var20.b(var22, var23));
         }
      }

      return var12;
   }

   public final sy a(sy var1) {
      return this.a.a(var1);
   }

   public final void a(double[] var1, double[] var2, sy var3) {
      double[][] var9 = var3.a;

      for (int var4 = 0; var4 < var9.length; var4++) {
         double[] var5 = var9[var4];
         double var7 = this.b[var4];

         for (int var6 = 0; var6 < var5.length; var6++) {
            var5[var6] += var7 * (var1[var6] - var2[var6]);
         }
      }
   }
}
