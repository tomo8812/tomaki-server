package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

public final class Bc {
   private static int b;
   private static double c;
   private static double d;
   @Deprecated
   private static int e;
   private static int f;
   @Deprecated
   private static int g;
   final zF a;

   public Bc() {
      this.a = new zR();
   }

   @Deprecated
   private Bc(zF var1) {
      this.a = var1;
   }

   public final double a(oI var1, double[] var2, boolean var3) {
      return 1.0 - this.a(this.a(var1, var2), var2.length, var3);
   }

   public final double a(oI var1, double[] var2) {
      a(var2);
      int var3;
      double var4 = var3 = var2.length;
      double[] var6 = new double[var3];
      System.arraycopy(var2, 0, var6, 0, var3);
      Arrays.sort(var6);
      double var7 = 0.0;

      for (int var14 = 1; var14 <= var3; var14++) {
         double var10;
         double var12;
         if ((var12 = BY.e((var10 = var1.e(var6[var14 - 1])) - (var14 - 1) / var4, var14 / var4 - var10)) > var7) {
            var7 = var12;
         }
      }

      return var7;
   }

   public final double a(double[] var1, double[] var2, boolean var3) {
      long var4;
      double[] var6;
      double[] var7;
      if ((var4 = (long)var1.length * var2.length) < 10000L && e(var1, var2)) {
         double[] var18 = var1;
         var6 = Ch.a(var1, var18.length);
         double[] var19 = var2;
         var7 = Ch.a(var2, var19.length);
         double[] var9 = var7;
         double[] var8 = var6;
         double[][] var10 = new double[][]{var8, var9};
         int var11 = 0;
         double[][] var12 = var10;
         int var10000 = var10.length;

         for (int var14 = 0; var14 < 2; var14++) {
            double[] var15 = var12[var14];
            var11 += var15.length;
         }

         int var27 = 0;
         double[] var32 = new double[var11];

         for (int var34 = 0; var34 < var10.length; var34++) {
            int var13 = var10[var34].length;
            System.arraycopy(var10[var34], 0, var32, var27, var13);
            var27 += var13;
         }

         double[] var20 = var32;
         TreeSet var23 = new TreeSet();

         for (int var28 = 0; var28 < var20.length; var28++) {
            var23.add(var20[var28]);
         }

         int var29;
         double[] var30 = new double[var29 = var23.size()];
         Iterator var33 = var23.iterator();

         for (int var35 = 0; var33.hasNext(); var30[var29 - var35] = (Double)var33.next()) {
            var35++;
         }

         double[] var21 = var30;
         if (var30.length != var8.length + var9.length) {
            double var24 = 1.0;
            double var31 = var21[0];

            for (int var17 = 1; var17 < var21.length; var17++) {
               double var36;
               if ((var36 = var31 - var21[var17]) < var24) {
                  var24 = var36;
               }

               var31 = var21[var17];
            }

            double var25 = var24 / 2.0;
            oN var37 = new oN(new zy(100), -var25, var25);
            int var22 = 0;

            do {
               a(var8, var37);
               a(var9, var37);
               var26 = e(var8, var9);
            } while (var26 && ++var22 < 1000);

            if (var26) {
               throw new oY();
            }
         }
      } else {
         var6 = var1;
         var7 = var2;
      }

      return var4 < 10000L ? b(this.b(var6, var7), var1.length, var2.length, var3) : this.a(this.b(var1, var2), var1.length, var2.length);
   }

   public final double a(double[] var1, double[] var2) {
      return this.a(var1, var2, true);
   }

   public final double b(double[] var1, double[] var2) {
      return (double)this.c(var1, var2) / ((long)var1.length * var2.length);
   }

   private long c(double[] var1, double[] var2) {
      a(var1);
      a(var2);
      double[] var3 = var1;
      var1 = Ch.a(var1, var3.length);
      var3 = var2;
      var2 = Ch.a(var2, var3.length);
      Arrays.sort(var1);
      Arrays.sort(var2);
      int var18 = var1.length;
      int var4 = var2.length;
      int var5 = 0;
      int var6 = 0;
      long var9 = 0L;
      long var11 = 0L;

      do {
         double var13;
         for (var13 = Double.compare(var1[var5], var2[var6]) <= 0 ? var1[var5] : var2[var6];
            var5 < var18 && Double.compare(var1[var5], var13) == 0;
            var9 += var4
         ) {
            var5++;
         }

         while (var6 < var4 && Double.compare(var2[var6], var13) == 0) {
            var6++;
            var9 -= var18;
         }

         if (var9 > var11) {
            var11 = var9;
         } else if (-var9 > var11) {
            var11 = -var9;
         }
      } while (var5 < var18 && var6 < var4);

      return var11;
   }

   public final double b(oI var1, double[] var2) {
      return this.a(var1, var2, false);
   }

   public final boolean a(oI var1, double[] var2, double var3) {
      if (!(var3 <= 0.0) && !(var3 > 0.5)) {
         double[] var5 = var2;
         oI var6 = var1;
         return this.a(var6, var5, false) < var3;
      } else {
         throw new po(px.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, var3, 0, 0.5);
      }
   }

   private double a(double[] var1, double[] var2, int var3, boolean var4) {
      var4 = var1.length;
      int var5 = var2.length;
      double[] var6 = new double[var4 + var5];
      System.arraycopy(var1, 0, var6, 0, var4);
      System.arraycopy(var2, 0, var6, var4, var5);
      om var19 = new om(this.a, var6);
      long var9 = this.c(var1, var2);
      int var16 = 0;

      for (int var8 = 0; var8 < var3; var8++) {
         var2 = var19.a(var4);
         double[] var7 = var19.a(var5);
         if (this.c(var2, var7) > var9) {
            var16++;
         }
      }

      return (double)var16 / var3;
   }

   private double a(double[] var1, double[] var2, int var3) {
      boolean var20 = true;
      int var4 = var3;
      double[] var24 = var2;
      var2 = var1;
      Bc var21 = this;
      int var5 = var2.length;
      int var6 = var24.length;
      double[] var7 = new double[var5 + var6];
      System.arraycopy(var2, 0, var7, 0, var5);
      System.arraycopy(var24, 0, var7, var5, var6);
      om var26 = new om(var21.a, var7);
      long var13 = var21.c(var2, var24);
      int var23 = 0;

      for (int var9 = 0; var9 < var4; var9++) {
         double[] var25 = var26.a(var5);
         double[] var8 = var26.a(var6);
         if (var21.c(var25, var8) > var13) {
            var23++;
         }
      }

      return (double)var23 / var4;
   }

   private double a(double var1, int var3) {
      return this.a(var1, var3, false);
   }

   private double b(double var1, int var3) {
      return this.a(var1, var3, true);
   }

   private double a(double var1, int var3, boolean var4) {
      double var5 = 1.0 / var3;
      double var7 = var5 * 0.5;
      if (var1 <= var7) {
         return 0.0;
      }

      if (var7 < var1 && var1 <= var5) {
         double var9 = 1.0;
         double var11 = var1 * 2.0 - var5;

         for (int var13 = 1; var13 <= var3; var13++) {
            var9 *= var13 * var11;
         }

         return var9;
      } else if (1.0 - var5 <= var1 && var1 < 1.0) {
         return 1.0 - 2.0 * Math.pow(1.0 - var1, var3);
      } else if (1.0 <= var1) {
         return 1.0;
      } else if (var4) {
         return this.c(var1, var3);
      } else {
         return var3 <= 140 ? this.d(var1, var3) : e(var1, var3);
      }
   }

   private double c(double var1, int var3) {
      int var4 = (int)Math.ceil(var3 * var1);
      qd var5 = f(var1, var3).a(var3).b(var4 - 1, var4 - 1);

      for (int var2 = 1; var2 <= var3; var2++) {
         var5 = var5.c(var2).b(var3);
      }

      return var5.a(20, 4).doubleValue();
   }

   private double d(double var1, int var3) {
      int var4 = (int)Math.ceil(var3 * var1);
      double var7 = g(var1, var3).a(var3).b(var4 - 1, var4 - 1);

      for (int var9 = 1; var9 <= var3; var9++) {
         var7 *= (double)var9 / var3;
      }

      return var7;
   }

   private static double e(double var0, int var2) {
      double var3 = BY.a((double)var2);
      double var5 = var0 * var3;
      double var7;
      double var9;
      double var11 = (var9 = (var7 = var0 * var0 * var2) * var7) * var7;
      double var13 = var9 * var9;
      double var17 = 0.0;
      double var23 = 9.869604401089358 / (var7 * 8.0);

      for (var42 = 1; var42 < 100000; var42++) {
         double var21 = 2 * var42 - 1;
         double var19 = BY.i(-var23 * var21 * var21);
         var17 += var19;
         if (var19 <= var17 * 1.0E-10) {
            break;
         }
      }

      if (var42 == 100000) {
         throw new pq(100000);
      }

      double var15 = var17 * BY.a(Math.PI * 2) / var5;
      double var26 = var7 * 2.0;
      var17 = 0.0;

      for (var43 = 0; var43 < 100000; var43++) {
         double var58;
         double var28 = (var58 = var43 + 0.5) * var58;
         double var53 = (var28 * 9.869604401089358 - var7) * BY.i(var28 * -9.869604401089358 / var26);
         var17 += var53;
         if (BY.w(var53) < 1.0E-10 * BY.w(var17)) {
            break;
         }
      }

      if (var43 == 100000) {
         throw new pq(100000);
      }

      double var30 = BY.a(Math.PI / 2);
      var15 += var17 * var30 / (var9 * 3.0 * var3);
      double var32 = var9 * 2.0;
      double var34 = var11 * 6.0;
      var23 = var7 * 5.0;
      var17 = 0.0;

      for (var44 = 0; var44 < 100000; var44++) {
         double var59;
         double var62 = (var59 = var44 + 0.5) * var59;
         double var54 = (var34 + var32 + 9.869604401089358 * (var32 - var23) * var62 + 97.40909103400243 * (1.0 - var26) * var62 * var62)
            * BY.i(var62 * -9.869604401089358 / var26);
         var17 += var54;
         if (BY.w(var54) < 1.0E-10 * BY.w(var17)) {
            break;
         }
      }

      if (var44 == 100000) {
         throw new pq(100000);
      }

      double var36 = 0.0;

      for (var45 = 1; var45 < 100000; var45++) {
         double var63 = var45 * var45;
         double var55 = var63 * 9.869604401089358 * BY.i(var63 * -9.869604401089358 / var26);
         var36 += var55;
         if (BY.w(var55) < 1.0E-10 * BY.w(var36)) {
            break;
         }
      }

      if (var45 == 100000) {
         throw new pq(100000);
      }

      var15 += var30 / var2 * (var17 / (var7 * 36.0 * var7 * var7 * var5) - var36 / (var7 * 18.0 * var5));
      var17 = 0.0;

      for (var46 = 0; var46 < 100000; var46++) {
         double var38;
         double var60;
         double var64;
         double var40 = (var38 = (var64 = (var60 = var46 + 0.5) * var60) * var64) * var64;
         double var56 = (
               var40 * 961.3891935753043 * (5.0 - var7 * 30.0)
                  + var38 * 97.40909103400243 * (var7 * -60.0 + var9 * 212.0)
                  + var64 * 9.869604401089358 * (var9 * 135.0 - var11 * 96.0)
                  - var11 * 30.0
                  - var13 * 90.0
            )
            * BY.i(var64 * -9.869604401089358 / var26);
         var17 += var56;
         if (BY.w(var56) < 1.0E-10 * BY.w(var17)) {
            break;
         }
      }

      if (var46 == 100000) {
         throw new pq(100000);
      }

      var36 = 0.0;

      for (var47 = 1; var47 < 100000; var47++) {
         double var65;
         double var67 = (var65 = var47 * var47) * var65;
         double var57 = (var67 * -97.40909103400243 + var65 * 29.608813203268074 * var7) * BY.i(var65 * -9.869604401089358 / var26);
         var36 += var57;
         if (BY.w(var57) < 1.0E-10 * BY.w(var36)) {
            break;
         }
      }

      if (var47 == 100000) {
         throw new pq(100000);
      } else {
         return var15 + var30 / (var3 * var2) * (var17 / (var11 * 3240.0 * var9) + var36 / (var11 * 108.0));
      }
   }

   private static sQ<qd> f(double var0, int var2) {
      int var3 = (int)Math.ceil(var2 * var0);
      int var4 = 2 * var3 - 1;
      double var5;
      if ((var5 = var3 - var2 * var0) >= 1.0) {
         throw new pm(var5, 1.0, false);
      }

      try {
         var9 = new qd(var5, 1.0E-20, 10000);
      } catch (qh var8) {
         try {
            var9 = new qd(var5, 1.0E-10, 10000);
         } catch (qh var7) {
            var9 = new qd(var5, 1.0E-5, 10000);
         }
      }

      qd[][] var1 = new qd[var4][var4];

      for (int var11 = 0; var11 < var4; var11++) {
         for (int var14 = 0; var14 < var4; var14++) {
            if (var11 - var14 + 1 < 0) {
               var1[var11][var14] = qd.c;
            } else {
               var1[var11][var14] = qd.b;
            }
         }
      }

      qd[] var12;
      (var12 = new qd[var4])[0] = var9;

      for (int var15 = 1; var15 < var4; var15++) {
         var12[var15] = var9.d(var12[var15 - 1]);
      }

      for (int var16 = 0; var16 < var4; var16++) {
         var1[var16][0] = var1[var16][0].e(var12[var16]);
         var1[var4 - 1][var16] = var1[var4 - 1][var16].e(var12[var4 - var16 - 1]);
      }

      if (var9.b(qd.e) == 1) {
         var1[var4 - 1][0] = var1[var4 - 1][0].a(var9.c(2).e(1).d(var4));
      }

      for (int var17 = 0; var17 < var4; var17++) {
         for (int var10 = 0; var10 < var17 + 1; var10++) {
            if (var17 - var10 + 1 > 0) {
               for (int var13 = 2; var13 <= var17 - var10 + 1; var13++) {
                  var1[var17][var10] = var1[var17][var10].b(var13);
               }
            }
         }
      }

      return new sx<>(qe.d(), var1);
   }

   private static tq g(double var0, int var2) {
      int var3 = (int)Math.ceil(var2 * var0);
      int var4 = 2 * var3 - 1;
      double var5;
      if ((var5 = var3 - var2 * var0) >= 1.0) {
         throw new pm(var5, 1.0, false);
      }

      double[][] var7 = new double[var4][var4];

      for (int var1 = 0; var1 < var4; var1++) {
         for (int var10 = 0; var10 < var4; var10++) {
            if (var1 - var10 + 1 < 0) {
               var7[var1][var10] = 0.0;
            } else {
               var7[var1][var10] = 1.0;
            }
         }
      }

      double[] var8;
      (var8 = new double[var4])[0] = var5;

      for (int var11 = 1; var11 < var4; var11++) {
         var8[var11] = var5 * var8[var11 - 1];
      }

      for (int var12 = 0; var12 < var4; var12++) {
         var7[var12][0] = var7[var12][0] - var8[var12];
         var7[var4 - 1][var12] = var7[var4 - 1][var12] - var8[var4 - var12 - 1];
      }

      if (Double.compare(var5, 0.5) > 0) {
         var7[var4 - 1][0] = var7[var4 - 1][0] + BY.a(var5 * 2.0 - 1.0, var4);
      }

      for (int var13 = 0; var13 < var4; var13++) {
         for (int var9 = 0; var9 < var13 + 1; var9++) {
            if (var13 - var9 + 1 > 0) {
               for (int var14 = 2; var14 <= var13 - var9 + 1; var14++) {
                  var7[var13][var9] = var7[var13][var9] / var14;
               }
            }
         }
      }

      return td.a(var7);
   }

   private static void a(double[] var0) {
      if (var0 == null) {
         throw new pl(px.NULL_NOT_ALLOWED);
      }

      if (var0.length < 2) {
         throw new oT(px.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, var0.length, 2);
      }
   }

   private static double a(double var0, double var2, int var4) {
      if (var0 == 0.0) {
         return 0.0;
      }

      double var5 = var0 * -2.0 * var0;
      int var14 = -1;
      long var8 = 1L;
      double var10 = 0.5;

      for (double var12 = 1.0; var12 > 1.0E-20 && var8 < 100000L; var8++) {
         var12 = BY.i(var5 * var8 * var8);
         var10 += var14 * var12;
         var14 = -var14;
      }

      if (var8 == 100000L) {
         throw new pq(100000);
      } else {
         return var10 * 2.0;
      }
   }

   static long a(double var0, int var2, int var3, boolean var4) {
      long var5 = (long)var2 * var3;
      long var7 = (long)BY.z((var0 - 1.0E-12) * var5);
      long var9 = (long)BY.y((var0 + 1.0E-12) * var5);
      return var4 && var9 == var7 ? var7 + 1L : var7;
   }

   public static double b(double var0, int var2, int var3, boolean var4) {
      return 1.0 - b(var3, var2, var3, var2, a(var0, var3, var2, var4), var4) / BR.b(var2 + var3, var3);
   }

   public final double a(double var1, int var3, int var4) {
      double var5 = var4;
      double var7 = var3;
      return 1.0 - a(var1 * BY.a(var5 * var7 / (var5 + var7)), 1.0E-20, 100000);
   }

   private static void a(boolean[] var0, int var1, zF var2) {
      Arrays.fill(var0, true);

      for (int var4 = var1; var4 < var0.length; var4++) {
         int var3 = var2.nextInt(var4 + 1);
         var0[var0[var3] ? var3 : var4] = false;
      }
   }

   public final double a(double var1, int var3, int var4, boolean var5, int var6) {
      long var10001 = a(var1, var3, var4, (boolean)var5);
      int var26 = var6;
      int var23 = var4;
      int var2 = var3;
      long var8 = var10001;
      Bc var21 = this;
      var5 = BY.b(var2, var23);
      var2 = BY.a(var2, var23);
      var3 = var5 + var2;
      var6 = 0;
      boolean[] var25 = new boolean[var3];

      for (int var7 = 0; var7 < var26; var7++) {
         zF var12 = var21.a;
         int var11 = var5;
         boolean[] var10 = var25;
         Arrays.fill(var25, true);

         for (int var30 = var11; var30 < var10.length; var30++) {
            int var13 = var12.nextInt(var30 + 1);
            var10[var10[var13] ? var13 : var30] = false;
         }

         long var19 = 0L;

         for (int var29 = 0; var29 < var25.length; var29++) {
            if (var25[var29]) {
               if ((var19 += var2) >= var8) {
                  var6++;
                  break;
               }
            } else if ((var19 -= var5) <= -var8) {
               var6++;
               break;
            }
         }
      }

      return (double)var6 / var26;
   }

   private double a(long var1, int var3, int var4, int var5) {
      int var6 = BY.b(var3, var4);
      var3 = BY.a(var3, var4);
      var4 = var6 + var3;
      int var7 = 0;
      boolean[] var16 = new boolean[var4];

      for (int var8 = 0; var8 < var5; var8++) {
         a(var16, var6, this.a);
         long var12 = 0L;

         for (int var9 = 0; var9 < var16.length; var9++) {
            if (var16[var9]) {
               if ((var12 += var3) >= var1) {
                  var7++;
                  break;
               }
            } else if ((var12 -= var6) <= -var1) {
               var7++;
               break;
            }
         }
      }

      return (double)var7 / var5;
   }

   private static void d(double[] var0, double[] var1) {
      double[] var2;
      if ((var2 = Ch.f(Ch.a(var0, var1))).length != var0.length + var1.length) {
         double var3 = 1.0;
         double var5 = var2[0];

         for (int var9 = 1; var9 < var2.length; var9++) {
            double var7;
            if ((var7 = var5 - var2[var9]) < var3) {
               var3 = var7;
            }

            var5 = var2[var9];
         }

         var3 /= 2.0;
         oN var13 = new oN(new zy(100), -var3, var3);
         int var10 = 0;

         do {
            a(var0, var13);
            a(var1, var13);
            var12 = e(var0, var1);
         } while (var12 && ++var10 < 1000);

         if (var12) {
            throw new oY();
         }
      }
   }

   private static boolean e(double[] var0, double[] var1) {
      HashSet var2 = new HashSet();

      for (int var3 = 0; var3 < var0.length; var3++) {
         if (!var2.add(var0[var3])) {
            return true;
         }
      }

      for (int var4 = 0; var4 < var1.length; var4++) {
         if (!var2.add(var1[var4])) {
            return true;
         }
      }

      return false;
   }

   private static void a(double[] var0, oN var1) {
      for (int var2 = 0; var2 < var0.length; var2++) {
         var0[var2] += var1.b();
      }
   }

   private static int a(int var0, int var1, int var2, int var3, long var4, boolean var6) {
      if (var6) {
         return BY.a((long)var0 * var3 - (long)var1 * var2) <= var4 ? 1 : 0;
      } else {
         return BY.a((long)var0 * var3 - (long)var1 * var2) < var4 ? 1 : 0;
      }
   }

   private static double b(int var0, int var1, int var2, int var3, long var4, boolean var6) {
      double[] var7 = new double[var3];
      double var8 = 0.0;

      for (int var10 = 0; var10 < var3; var10++) {
         var7[var10] = a(0, var10 + 1, var2, var3, var4, var6);
      }

      for (int var12 = 1; var12 <= var0; var12++) {
         var8 = a(var12, 0, var2, var3, var4, var6);

         for (int var11 = 1; var11 <= var1; var11++) {
            var7[var11 - 1] = a(var12, var11, var2, var3, var4, var6) * (var8 + var7[var11 - 1]);
            var8 = var7[var11 - 1];
         }
      }

      return var8;
   }
}
