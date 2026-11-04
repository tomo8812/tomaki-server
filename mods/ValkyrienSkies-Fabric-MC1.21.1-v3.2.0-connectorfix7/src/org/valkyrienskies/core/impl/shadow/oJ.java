package org.valkyrienskies.core.impl.shadow;

final class oJ {
   private static final double a = 0.5 * BY.k(Math.PI * 2);
   private static final double[] b = new double[]{
      0.0,
      0.15342640972002736,
      0.08106146679532726,
      0.05481412105191765,
      0.0413406959554093,
      0.03316287351993629,
      0.02767792568499834,
      0.023746163656297496,
      0.020790672103765093,
      0.018488450532673187,
      0.016644691189821193,
      0.015134973221917378,
      0.013876128823070748,
      0.012810465242920227,
      0.01189670994589177,
      0.011104559758206917,
      0.010411265261972096,
      0.009799416126158804,
      0.009255462182712733,
      0.008768700134139386,
      0.00833056343336287,
      0.00793411456431402,
      0.007573675487951841,
      0.007244554301320383,
      0.00694284010720953,
      0.006665247032707682,
      0.006408994188004207,
      0.006171712263039458,
      0.0059513701127588475,
      0.0057462165130101155,
      0.005554733551962801
   };

   private oJ() {
   }

   static double a(double var0) {
      double var2;
      if (var0 < 15.0) {
         double var4;
         if (BY.y(var4 = var0 * 2.0) == var4) {
            var2 = b[(int)var4];
         } else {
            var2 = zY.a(var0 + 1.0) - (var0 + 0.5) * BY.k(var0) + var0 - a;
         }
      } else {
         double var6 = var0 * var0;
         var2 = (
               0.08333333333333333
                  - (0.002777777777777778 - (7.936507936507937E-4 - (5.952380952380953E-4 - 8.417508417508417E-4 / var6) / var6) / var6) / var6
            )
            / var0;
      }

      return var2;
   }

   static double a(double var0, double var2) {
      double var4;
      if (BY.w(var0 - var2) < 0.1 * (var0 + var2)) {
         double var6;
         double var8;
         double var10 = (var8 = (var6 = var0 - var2) / (var0 + var2)) * var6;
         double var12 = Double.NaN;
         double var14 = var0 * 2.0 * var8;
         var8 *= var8;

         for (int var16 = 1; var10 != var12; var16++) {
            var12 = var10;
            var14 *= var8;
            var10 = var12 + var14 / ((var16 << 1) + 1);
         }

         var4 = var10;
      } else {
         var4 = var0 * BY.k(var0 / var2) + var2 - var0;
      }

      return var4;
   }

   static double a(int var0, int var1, double var2, double var4) {
      double var6;
      if (var0 == 0) {
         if (var2 < 0.1) {
            var6 = -a(var1, var1 * var4) - var1 * var2;
         } else {
            var6 = var1 * BY.k(var4);
         }
      } else if (var0 == var1) {
         if (var4 < 0.1) {
            var6 = -a(var1, var1 * var2) - var1 * var4;
         } else {
            var6 = var1 * BY.k(var2);
         }
      } else {
         var6 = a(var1) - a(var0) - a(var1 - var0) - a(var0, var1 * var2) - a(var1 - var0, var1 * var4);
         double var8 = (Math.PI * 2) * var0 * (var1 - var0) / var1;
         var6 = -0.5 * BY.k(var8) + var6;
      }

      return var6;
   }
}
