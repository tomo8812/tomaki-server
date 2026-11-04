package org.valkyrienskies.core.impl.shadow;

import java.io.PrintStream;

final class BZ {
   private static final long a = 1073741824L;
   private static final double[] b = new double[]{
      1.0,
      1.0,
      2.0,
      6.0,
      24.0,
      120.0,
      720.0,
      5040.0,
      40320.0,
      362880.0,
      3628800.0,
      3.99168E7,
      4.790016E8,
      6.227021E9F,
      8.71782912E10,
      1.307674368E12,
      2.0922789888E13,
      3.55687428096E14,
      6.402373705728E15,
      1.21645100408832E17
   };
   private static final double[][] c = new double[][]{
      {2.0, 0.0},
      {0.6666666F, 3.9736429850260626E-8},
      {0.39999998F, 2.3841857910019882E-8},
      {0.28571427F, 1.7029898543501842E-8},
      {0.22222221F, 1.3245471311735498E-8},
      {0.18181816F, 2.4384203044354907E-8},
      {0.15384614F, 9.140260083262505E-9},
      {0.13333333F, 9.220590270857665E-9},
      {0.11764701F, 1.2393345855018391E-8},
      {0.10526404F, 8.251545029714408E-9},
      {0.09522332F, 1.2675934823758863E-8},
      {0.087136224F, 1.1430250008909141E-8},
      {0.07842259F, 2.404307984052299E-9},
      {0.08371849F, 1.176342548272881E-8},
      {0.03058958F, 1.2958646899018938E-9},
      {0.14982304F, 1.225743062930824E-8}
   };
   private static final String d = "    {";
   private static final String e = "    };";

   private BZ() {
   }

   private static void a(double[] var0, double[] var1, double[] var2, double[] var3, int var4, double[] var5, double[] var6) {
      double[] var7 = new double[2];

      for (int var8 = 0; var8 < 7; var8++) {
         double var9;
         double var10000 = var9 = var8 / 8.0;
         double[] var11 = var7;
         double var14 = var10000;
         double[] var12 = new double[2];
         double[] var13 = new double[2];
         double[] var16 = new double[2];
         double[] var17 = new double[2];
         d(var14, var12);
         var13[0] = var13[1] = 0.0;

         for (int var30 = b.length - 1; var30 >= 0; var30--) {
            a(var12, var13, var17);
            var13[0] = var17[0];
            var13[1] = var17[1];
            if ((var30 & 1) != 0) {
               d(b[var30], var17);
               a(var17, var16);
               if ((var30 & 2) != 0) {
                  var16[0] = -var16[0];
                  var16[1] = -var16[1];
               }

               b(var13, var16, var17);
               var13[0] = var17[0];
               var13[1] = var17[1];
            }
         }

         var11[0] = var13[0];
         var11[1] = var13[1];
         var10000 = var13[0] + var13[1];
         var0[var8] = var7[0];
         var1[var8] = var7[1];
         var11 = var7;
         var14 = var9;
         var12 = new double[2];
         var13 = new double[2];
         var16 = new double[2];
         var17 = new double[2];
         d(var14, var12);
         var13[0] = var13[1] = 0.0;

         for (int var32 = b.length - 1; var32 >= 0; var32--) {
            a(var12, var13, var17);
            var13[0] = var17[0];
            var13[1] = var17[1];
            if ((var32 & 1) == 0) {
               d(b[var32], var17);
               a(var17, var16);
               if ((var32 & 2) != 0) {
                  var16[0] = -var16[0];
                  var16[1] = -var16[1];
               }

               b(var13, var16, var17);
               var13[0] = var17[0];
               var13[1] = var17[1];
            }
         }

         var11[0] = var13[0];
         var11[1] = var13[1];
         var10000 = var13[0] + var13[1];
         var2[var8] = var7[0];
         var3[var8] = var7[1];
      }

      for (int var18 = 7; var18 < var4; var18++) {
         double[] var20 = new double[2];
         double[] var10 = new double[2];
         double[] var24 = new double[2];
         double[] var27 = new double[2];
         double[] var29 = new double[2];
         if ((var18 & 1) == 0) {
            var20[0] = var0[var18 / 2];
            var20[1] = var1[var18 / 2];
            var10[0] = var2[var18 / 2];
            var10[1] = var3[var18 / 2];
            a(var20, var10, var7);
            var0[var18] = var7[0] * 2.0;
            var1[var18] = var7[1] * 2.0;
            a(var10, var10, var24);
            a(var20, var20, var29);
            var29[0] = -var29[0];
            var29[1] = -var29[1];
            b(var24, var29, var7);
            var2[var18] = var7[0];
            var3[var18] = var7[1];
         } else {
            var20[0] = var0[var18 / 2];
            var20[1] = var1[var18 / 2];
            var10[0] = var2[var18 / 2];
            var10[1] = var3[var18 / 2];
            var24[0] = var0[var18 / 2 + 1];
            var24[1] = var1[var18 / 2 + 1];
            var27[0] = var2[var18 / 2 + 1];
            var27[1] = var3[var18 / 2 + 1];
            a(var20, var27, var29);
            a(var10, var24, var7);
            b(var7, var29, var7);
            var0[var18] = var7[0];
            var1[var18] = var7[1];
            a(var10, var27, var7);
            a(var20, var24, var29);
            var29[0] = -var29[0];
            var29[1] = -var29[1];
            b(var7, var29, var7);
            var2[var18] = var7[0];
            var3[var18] = var7[1];
         }
      }

      for (int var19 = 0; var19 < var4; var19++) {
         double[] var21 = new double[2];
         double[] var22 = new double[2];
         double[] var25;
         (var25 = new double[2])[0] = var2[var19];
         var25[1] = var3[var19];
         a(var25, var22);
         var21[0] = var0[var19];
         var21[1] = var1[var19];
         a(var21, var22, var25);
         var5[var19] = var25[0];
         var6[var19] = var25[1];
      }
   }

   private static double a(double var0, double[] var2) {
      double[] var3 = new double[2];
      double[] var4 = new double[2];
      double[] var5 = new double[2];
      double[] var6 = new double[2];
      d(var0, var3);
      var4[0] = var4[1] = 0.0;

      for (int var7 = b.length - 1; var7 >= 0; var7--) {
         a(var3, var4, var6);
         var4[0] = var6[0];
         var4[1] = var6[1];
         if ((var7 & 1) == 0) {
            d(b[var7], var6);
            a(var6, var5);
            if ((var7 & 2) != 0) {
               var5[0] = -var5[0];
               var5[1] = -var5[1];
            }

            b(var4, var5, var6);
            var4[0] = var6[0];
            var4[1] = var6[1];
         }
      }

      var2[0] = var4[0];
      var2[1] = var4[1];
      return var4[0] + var4[1];
   }

   private static double b(double var0, double[] var2) {
      double[] var3 = new double[2];
      double[] var4 = new double[2];
      double[] var5 = new double[2];
      double[] var6 = new double[2];
      d(var0, var3);
      var4[0] = var4[1] = 0.0;

      for (int var7 = b.length - 1; var7 >= 0; var7--) {
         a(var3, var4, var6);
         var4[0] = var6[0];
         var4[1] = var6[1];
         if ((var7 & 1) != 0) {
            d(b[var7], var6);
            a(var6, var5);
            if ((var7 & 2) != 0) {
               var5[0] = -var5[0];
               var5[1] = -var5[1];
            }

            b(var4, var5, var6);
            var4[0] = var6[0];
            var4[1] = var6[1];
         }
      }

      var2[0] = var4[0];
      var2[1] = var4[1];
      return var4[0] + var4[1];
   }

   private static double c(double var0, double[] var2) {
      double[] var3 = new double[2];
      double[] var4 = new double[2];
      double[] var5 = new double[2];
      double[] var6 = new double[2];
      d(var0, var3);
      var4[0] = var4[1] = 0.0;

      for (int var7 = b.length - 1; var7 >= 0; var7--) {
         a(var3, var4, var6);
         var4[0] = var6[0];
         var4[1] = var6[1];
         d(b[var7], var6);
         a(var6, var5);
         b(var4, var5, var6);
         var4[0] = var6[0];
         var4[1] = var6[1];
      }

      if (var2 != null) {
         var2[0] = var4[0];
         var2[1] = var4[1];
      }

      return var4[0] + var4[1];
   }

   private static void d(double var0, double[] var2) {
      if (var0 < 8.0E298 && var0 > -8.0E298) {
         double var5 = var0 * 1.0737418E9F;
         var2[0] = var0 + var5 - var5;
         var2[1] = var0 - var2[0];
      } else {
         double var3 = var0 * 9.313226E-10F;
         var2[0] = (var0 + var3 - var0) * 1.0737418E9F;
         var2[1] = var0 - var2[0];
      }
   }

   private static void a(double[] var0) {
      double var1;
      double var3 = -((var1 = var0[0] + var0[1]) - var0[0] - var0[1]);
      if (var1 < 8.0E298 && var1 > -8.0E298) {
         double var7 = var1 * 1.0737418E9F;
         var0[0] = var1 + var7 - var7;
         var0[1] = var1 - var0[0] + var3;
      } else {
         double var5 = var1 * 9.313226E-10F;
         var0[0] = (var1 + var5 - var1) * 1.0737418E9F;
         var0[1] = var1 - var0[0] + var3;
      }
   }

   private static void a(double[] var0, double[] var1, double[] var2) {
      var2[0] = var0[0] * var1[0];
      var2[1] = var0[0] * var1[1] + var0[1] * var1[0] + var0[1] * var1[1];
      a(var2);
   }

   private static void b(double[] var0, double[] var1, double[] var2) {
      var2[0] = var0[0] + var1[0];
      var2[1] = var0[1] + var1[1];
      a(var2);
   }

   private static void a(double[] var0, double[] var1) {
      if (var0[0] == 0.0) {
         var0[0] = var0[1];
         var0[1] = 0.0;
      }

      var1[0] = 0.99999976F / var0[0];
      var1[1] = (2.3841858E-7F * var0[0] - 0.99999976F * var0[1]) / (var0[0] * var0[0] + var0[0] * var0[1]);
      if (var1[1] != var1[1]) {
         var1[1] = 0.0;
      }

      a(var1);

      for (int var2 = 0; var2 < 2; var2++) {
         double var3 = (1.0 - var1[0] * var0[0] - var1[0] * var0[1] - var1[1] * var0[0] - var1[1] * var0[1]) * (var1[0] + var1[1]);
         var1[1] += var3;
      }
   }

   private static void c(double[] var0, double[] var1, double[] var2) {
      double[] var3 = new double[2];
      double[] var4 = new double[2];
      double[] var5 = new double[2];
      d(var0[0], var3);
      d(var1[0], var4);
      a(var3, var4, var5);
      var2[0] = var5[0];
      var2[1] = var5[1];
      d(var1[1], var4);
      a(var3, var4, var5);
      double var6 = var2[0] + var5[0];
      var2[1] -= var6 - var2[0] - var5[0];
      var2[0] = var6;
      var6 = var2[0] + var5[1];
      var2[1] -= var6 - var2[0] - var5[1];
      var2[0] = var6;
      d(var0[1], var3);
      d(var1[0], var4);
      a(var3, var4, var5);
      var6 = var2[0] + var5[0];
      var2[1] -= var6 - var2[0] - var5[0];
      var2[0] = var6;
      var6 = var2[0] + var5[1];
      var2[1] -= var6 - var2[0] - var5[1];
      var2[0] = var6;
      d(var0[1], var3);
      d(var1[1], var4);
      a(var3, var4, var5);
      var6 = var2[0] + var5[0];
      var2[1] -= var6 - var2[0] - var5[0];
      var2[0] = var6;
      var6 = var2[0] + var5[1];
      var2[1] -= var6 - var2[0] - var5[1];
      var2[0] = var6;
   }

   private static double a(int var0, double[] var1) {
      double[] var2 = new double[2];
      double[] var3 = new double[2];
      double[] var4 = new double[2];
      var2[0] = Math.E;
      var2[1] = 1.4456468917292502E-16;
      d(1.0, var4);

      while (var0 > 0) {
         if ((var0 & 1) != 0) {
            c(var4, var2, var3);
            var4[0] = var3[0];
            var4[1] = var3[1];
         }

         c(var2, var2, var3);
         var2[0] = var3[0];
         var2[1] = var3[1];
         var0 >>= 1;
      }

      if (var1 != null) {
         var1[0] = var4[0];
         var1[1] = var4[1];
         a(var1);
      }

      return var4[0] + var4[1];
   }

   private static double[] a(double var0) {
      double[] var2 = new double[2];
      double[] var3 = new double[2];
      double[] var4 = new double[2];
      double[] var5 = new double[2];
      d(var0, var2);
      var2[0]++;
      a(var2);
      a(var2, var5);
      var2[0] -= 2.0;
      a(var2);
      a(var2, var5, var4);
      var2[0] = var4[0];
      var2[1] = var4[1];
      a(var2, var2, var3);
      var4[0] = c[c.length - 1][0];
      var4[1] = c[c.length - 1][1];

      for (int var6 = c.length - 2; var6 >= 0; var6--) {
         a(var4, var3, var5);
         var4[0] = var5[0];
         var4[1] = var5[1];
         b(var4, c[var6], var5);
         var4[0] = var5[0];
         var4[1] = var5[1];
      }

      a(var4, var2, var5);
      var4[0] = var5[0];
      var4[1] = var5[1];
      return var4;
   }

   static void a(PrintStream var0, String var1, int var2, double[][] var3) {
      var0.println(var1);
      a(1024, var3.length);
      var0.println("    { ");
      int var14 = 0;
      double[][] var15 = var3;
      int var16 = var3.length;

      for (int var4 = 0; var4 < var16; var4++) {
         double[] var5 = var15[var4];
         var0.print("        {");
         double[] var17;
         int var6 = (var17 = var5).length;

         for (int var7 = 0; var7 < var6; var7++) {
            double var12 = var17[var7];
            var0.printf("%-25.25s", b(var12));
         }

         var0.println("}, // " + var14++);
      }

      var0.println("    };");
   }

   static void a(PrintStream var0, String var1, int var2, double[] var3) {
      var0.println(var1 + "=");
      a(var2, var3.length);
      var0.println("    {");
      double[] var9 = var3;
      var2 = var3.length;

      for (int var11 = 0; var11 < var2; var11++) {
         double var7 = var9[var11];
         var0.printf("        %s%n", b(var7));
      }

      var0.println("    };");
   }

   private static String b(double var0) {
      return var0 != var0 ? "Double.NaN," : (var0 >= 0.0 ? "+" : "") + Double.toString(var0) + "d,";
   }

   private static void a(int var0, int var1) {
      if (var0 != var1) {
         throw new oS(var1, var0);
      }
   }
}
