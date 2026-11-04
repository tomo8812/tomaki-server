package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public final class lf {
   private static AtomicReference<lf[][]> c = new AtomicReference<>(null);
   public final int a;
   public final int b;
   private final int[][] d;
   private final int[][] e;
   private final int[] f;
   private final int[][][] g;
   private final int[][][] h;

   private lf(int var1, int var2, lf var3, lf var4) {
      this.a = var1;
      this.b = var2;
      lf var7 = var3;
      int var6 = var2;
      int var5 = var1;
      int[][] var8 = new int[var1 + 1][var6 + 1];
      if (var5 == 0) {
         Arrays.fill(var8[0], 1);
      } else {
         System.arraycopy(var7.d, 0, var8, 0, var5);
         var8[var5][0] = 1;

         for (int var9 = 0; var9 < var6; var9++) {
            var8[var5][var9 + 1] = var8[var5][var9] + var8[var5 - 1][var9 + 1];
         }
      }

      this.d = var8;
      lf var39 = var4;
      var7 = var3;
      var6 = var2;
      var5 = var1;
      int[][] var10001;
      if (var1 != 0 && var6 != 0) {
         int var43 = var7.e.length;
         int var10 = var39.e.length;
         int[][] var11 = new int[var43 + var10][var5];

         for (int var12 = 0; var12 < var43; var12++) {
            System.arraycopy(var7.e[var12], 0, var11[var12], 0, var5 - 1);
         }

         for (int var53 = 0; var53 < var10; var53++) {
            System.arraycopy(var39.e[var53], 0, var11[var43 + var53], 0, var5);
            var11[var43 + var53][var5 - 1]++;
         }

         var10001 = var11;
      } else {
         var10001 = new int[1][var5];
      }

      this.e = var10001;
      lf var40 = var4;
      var7 = var3;
      var6 = var2;
      int[] var70;
      if (var1 != 0 && var6 > 1) {
         int var44 = var7.f.length;
         int var47 = var40.f.length;
         int[] var50 = new int[var44 + var47];
         System.arraycopy(var7.f, 0, var50, 0, var44);

         for (int var54 = 0; var54 < var47; var54++) {
            var50[var44 + var54] = var7.c() + var40.f[var54];
         }

         var70 = var50;
      } else {
         var70 = new int[]{0};
      }

      this.f = var70;
      int[] var45 = this.f;
      lf var41 = var4;
      var7 = var3;
      var6 = var2;
      int[][][] var71;
      if (var1 != 0 && var6 != 0) {
         int var48 = var7.g.length;
         int var51 = var41.g.length;
         int[][][] var55 = new int[var48 + var51][][];
         System.arraycopy(var7.g, 0, var55, 0, var48);

         for (int var13 = 0; var13 < var51; var13++) {
            int[][] var36 = var41.g[var13];
            ArrayList var14 = new ArrayList(var36.length << 1);

            for (int var15 = 0; var15 < var36.length; var15++) {
               var14.add(new int[]{var36[var15][0], var45[var36[var15][1]], var48 + var36[var15][2]});
               var14.add(new int[]{var36[var15][0], var48 + var36[var15][1], var45[var36[var15][2]]});
            }

            ArrayList var59 = new ArrayList(var14.size());

            for (int var16 = 0; var16 < var14.size(); var16++) {
               int[] var17;
               if ((var17 = (int[])var14.get(var16))[0] > 0) {
                  for (int var18 = var16 + 1; var18 < var14.size(); var18++) {
                     int[] var19 = (int[])var14.get(var18);
                     if (var17[1] == var19[1] && var17[2] == var19[2]) {
                        var17[0] += var19[0];
                        var19[0] = 0;
                     }
                  }

                  var59.add(var17);
               }
            }

            var55[var48 + var13] = var59.toArray(new int[var59.size()][]);
         }

         var71 = var55;
      } else {
         var71 = new int[][][]{{{1, 0, 0}}};
      }

      this.g = var71;
      int[][] var49 = this.e;
      int[][] var46 = this.d;
      lf var42 = var4;
      var7 = var3;
      var6 = var2;
      var5 = var1;
      int[][][] var72;
      if (var1 != 0 && var6 != 0) {
         int var52 = var7.h.length;
         int var56 = var42.h.length;
         int[][][] var57 = new int[var52 + var56][][];
         System.arraycopy(var7.h, 0, var57, 0, var52);

         for (int var38 = 0; var38 < var56; var38++) {
            ArrayList var58 = new ArrayList();
            int[][] var60;
            int var62 = (var60 = var42.h[var38]).length;

            for (int var64 = 0; var64 < var62; var64++) {
               int[] var66;
               int[] var68;
               (var68 = new int[(var66 = var60[var64]).length + 1])[0] = var66[0];
               var68[1] = var66[1] + 1;
               int[] var20;
               (var20 = new int[var5])[var5 - 1] = 1;
               var68[var66.length] = a(var5, var6, var46, var20);

               for (int var22 = 2; var22 < var66.length; var22++) {
                  var68[var22] = a(var66[var22], var5, var42.e, var5, var6, var46);
               }

               Arrays.sort(var68, 2, var68.length);
               var58.add(var68);

               for (int var23 = 2; var23 < var66.length; var23++) {
                  int[] var25;
                  (var25 = new int[var66.length])[0] = var66[0];
                  var25[1] = var66[1];

                  for (int var26 = 2; var26 < var66.length; var26++) {
                     var25[var26] = a(var66[var26], var5, var42.e, var5, var6, var46);
                     if (var26 == var23) {
                        System.arraycopy(var49[var25[var26]], 0, var20, 0, var5);
                        var20[var5 - 1]++;
                        var25[var26] = a(var5, var6, var46, var20);
                     }
                  }

                  Arrays.sort(var25, 2, var25.length);
                  var58.add(var25);
               }
            }

            ArrayList var61 = new ArrayList(var58.size());

            for (int var63 = 0; var63 < var58.size(); var63++) {
               int[] var65;
               if ((var65 = (int[])var58.get(var63))[0] > 0) {
                  for (int var67 = var63 + 1; var67 < var58.size(); var67++) {
                     int[] var69 = (int[])var58.get(var67);
                     boolean var21 = var65.length == var69.length;

                     for (int var24 = 1; var21 && var24 < var65.length; var24++) {
                        var21 &= var65[var24] == var69[var24];
                     }

                     if (var21) {
                        var65[0] += var69[0];
                        var69[0] = 0;
                     }
                  }

                  var61.add(var65);
               }
            }

            var57[var52 + var38] = var61.toArray(new int[var61.size()][]);
         }

         var72 = var57;
      } else {
         var72 = new int[][][]{{{1, 0}}};
      }

      this.h = var72;
   }

   public static lf a(int var0, int var1) {
      lf[][] var2;
      if ((var2 = c.get()) != null && var2.length > var0 && var2[var0].length > var1 && var2[var0][var1] != null) {
         return var2[var0][var1];
      }

      int var3 = BY.b(var0, var2 == null ? 0 : var2.length);
      int var4 = BY.b(var1, var2 == null ? 0 : var2[0].length);
      lf[][] var9 = new lf[var3 + 1][var4 + 1];
      if (var2 != null) {
         for (int var10 = 0; var10 < var2.length; var10++) {
            System.arraycopy(var2[var10], 0, var9[var10], 0, var2[var10].length);
         }
      }

      for (int var11 = 0; var11 <= var0 + var1; var11++) {
         for (int var5 = BY.b(0, var11 - var0); var5 <= BY.a(var1, var11); var5++) {
            int var6 = var11 - var5;
            if (var9[var6][var5] == null) {
               lf var7 = var6 == 0 ? null : var9[var6 - 1][var5];
               lf var8 = var5 == 0 ? null : var9[var6][var5 - 1];
               var9[var6][var5] = new lf(var6, var5, var7, var8);
            }
         }
      }

      c.compareAndSet(var2, var9);
      return var9[var0][var1];
   }

   private static int[][] a(int var0, int var1, lf var2) {
      int[][] var3 = new int[var0 + 1][var1 + 1];
      if (var0 == 0) {
         Arrays.fill(var3[0], 1);
      } else {
         System.arraycopy(var2.d, 0, var3, 0, var0);
         var3[var0][0] = 1;

         for (int var4 = 0; var4 < var1; var4++) {
            var3[var0][var4 + 1] = var3[var0][var4] + var3[var0 - 1][var4 + 1];
         }
      }

      return var3;
   }

   private static int[][] a(int var0, int var1, lf var2, lf var3) {
      if (var0 != 0 && var1 != 0) {
         var1 = var2.e.length;
         int var4 = var3.e.length;
         int[][] var5 = new int[var1 + var4][var0];

         for (int var6 = 0; var6 < var1; var6++) {
            System.arraycopy(var2.e[var6], 0, var5[var6], 0, var0 - 1);
         }

         for (int var8 = 0; var8 < var4; var8++) {
            System.arraycopy(var3.e[var8], 0, var5[var1 + var8], 0, var0);
            var5[var1 + var8][var0 - 1]++;
         }

         return var5;
      } else {
         return new int[1][var0];
      }
   }

   private static int[] b(int var0, int var1, lf var2, lf var3) {
      if (var0 != 0 && var1 > 1) {
         var0 = var2.f.length;
         var1 = var3.f.length;
         int[] var4 = new int[var0 + var1];
         System.arraycopy(var2.f, 0, var4, 0, var0);

         for (int var5 = 0; var5 < var1; var5++) {
            var4[var0 + var5] = var2.c() + var3.f[var5];
         }

         return var4;
      } else {
         return new int[]{0};
      }
   }

   private static int[][][] a(int var0, int var1, lf var2, lf var3, int[] var4) {
      if (var0 != 0 && var1 != 0) {
         var0 = var2.g.length;
         var1 = var3.g.length;
         int[][][] var5 = new int[var0 + var1][][];
         System.arraycopy(var2.g, 0, var5, 0, var0);

         for (int var14 = 0; var14 < var1; var14++) {
            int[][] var6 = var3.g[var14];
            ArrayList var7 = new ArrayList(var6.length << 1);

            for (int var8 = 0; var8 < var6.length; var8++) {
               var7.add(new int[]{var6[var8][0], var4[var6[var8][1]], var0 + var6[var8][2]});
               var7.add(new int[]{var6[var8][0], var0 + var6[var8][1], var4[var6[var8][2]]});
            }

            ArrayList var16 = new ArrayList(var7.size());

            for (int var15 = 0; var15 < var7.size(); var15++) {
               int[] var9;
               if ((var9 = (int[])var7.get(var15))[0] > 0) {
                  for (int var10 = var15 + 1; var10 < var7.size(); var10++) {
                     int[] var11 = (int[])var7.get(var10);
                     if (var9[1] == var11[1] && var9[2] == var11[2]) {
                        var9[0] += var11[0];
                        var11[0] = 0;
                     }
                  }

                  var16.add(var9);
               }
            }

            var5[var0 + var14] = var16.toArray(new int[var16.size()][]);
         }

         return var5;
      } else {
         return new int[][][]{{{1, 0, 0}}};
      }
   }

   private static int[][][] a(int var0, int var1, lf var2, lf var3, int[][] var4, int[][] var5) {
      if (var0 != 0 && var1 != 0) {
         int var6 = var2.h.length;
         int var7 = var3.h.length;
         int[][][] var8 = new int[var6 + var7][][];
         System.arraycopy(var2.h, 0, var8, 0, var6);

         for (int var18 = 0; var18 < var7; var18++) {
            ArrayList var9 = new ArrayList();
            int[][] var10;
            int var11 = (var10 = var3.h[var18]).length;

            for (int var12 = 0; var12 < var11; var12++) {
               int[] var13;
               int[] var14;
               (var14 = new int[(var13 = var10[var12]).length + 1])[0] = var13[0];
               var14[1] = var13[1] + 1;
               int[] var15;
               (var15 = new int[var0])[var0 - 1] = 1;
               var14[var13.length] = a(var0, var1, var4, var15);

               for (int var16 = 2; var16 < var13.length; var16++) {
                  var14[var16] = a(var13[var16], var0, var3.e, var0, var1, var4);
               }

               Arrays.sort(var14, 2, var14.length);
               var9.add(var14);

               for (int var26 = 2; var26 < var13.length; var26++) {
                  (var14 = new int[var13.length])[0] = var13[0];
                  var14[1] = var13[1];

                  for (int var17 = 2; var17 < var13.length; var17++) {
                     var14[var17] = a(var13[var17], var0, var3.e, var0, var1, var4);
                     if (var17 == var26) {
                        System.arraycopy(var5[var14[var17]], 0, var15, 0, var0);
                        var15[var0 - 1]++;
                        var14[var17] = a(var0, var1, var4, var15);
                     }
                  }

                  Arrays.sort(var14, 2, var14.length);
                  var9.add(var14);
               }
            }

            ArrayList var19 = new ArrayList(var9.size());

            for (int var20 = 0; var20 < var9.size(); var20++) {
               int[] var21;
               if ((var21 = (int[])var9.get(var20))[0] > 0) {
                  for (int var22 = var20 + 1; var22 < var9.size(); var22++) {
                     int[] var24 = (int[])var9.get(var22);
                     boolean var25 = var21.length == var24.length;

                     for (int var27 = 1; var25 && var27 < var21.length; var27++) {
                        var25 &= var21[var27] == var24[var27];
                     }

                     if (var25) {
                        var21[0] += var24[0];
                        var24[0] = 0;
                     }
                  }

                  var19.add(var21);
               }
            }

            var8[var6 + var18] = var19.toArray(new int[var19.size()][]);
         }

         return var8;
      } else {
         return new int[][][]{{{1, 0}}};
      }
   }

   public final int a(int... var1) {
      if (var1.length != this.a) {
         throw new oS(var1.length, this.a);
      } else {
         return a(this.a, this.b, this.d, var1);
      }
   }

   private static int a(int var0, int var1, int[][] var2, int... var3) {
      int var4 = 0;
      int var5 = var1;
      int var6 = 0;
      var0--;

      while (var0 >= 0) {
         int var7 = var3[var0];
         if ((var6 += var7) > var1) {
            throw new pm(var6, var1, true);
         }

         while (var7-- > 0) {
            var4 += var2[var0][var5--];
         }

         var0--;
      }

      return var4;
   }

   private static int a(int var0, int var1, int[][] var2, int var3, int var4, int[][] var5) {
      int[] var6 = new int[var3];
      System.arraycopy(var2[var0], 0, var6, 0, BY.a(var1, var3));
      return a(var3, var4, var5, var6);
   }

   private int[] a(int var1) {
      return this.e[var1];
   }

   public final int a() {
      return this.a;
   }

   public final int b() {
      return this.b;
   }

   public final int c() {
      return this.d[this.a][this.b];
   }

   public final void a(double var1, double[] var3, int var4, double var5, double[] var7, int var8, double[] var9, int var10) {
      for (int var11 = 0; var11 < this.c(); var11++) {
         var9[var11 + 0] = Ch.a(var1, var3[var11 + 0], var5, var7[var11 + 0]);
      }
   }

   public final void a(
      double var1, double[] var3, int var4, double var5, double[] var7, int var8, double var9, double[] var11, int var12, double[] var13, int var14
   ) {
      for (int var15 = 0; var15 < this.c(); var15++) {
         var13[var15 + 0] = Ch.a(var1, var3[var15 + 0], var5, var7[var15 + 0], var9, var11[var15 + 0]);
      }
   }

   public final void a(
      double var1,
      double[] var3,
      int var4,
      double var5,
      double[] var7,
      int var8,
      double var9,
      double[] var11,
      int var12,
      double var13,
      double[] var15,
      int var16,
      double[] var17,
      int var18
   ) {
      for (int var19 = 0; var19 < this.c(); var19++) {
         var17[var19 + 0] = Ch.a(var1, var3[var19 + 0], var5, var7[var19 + 0], var9, var11[var19 + 0], var13, var15[var19 + 0]);
      }
   }

   public final void a(double[] var1, int var2, double[] var3, int var4, double[] var5, int var6) {
      for (int var7 = 0; var7 < this.c(); var7++) {
         var5[var7 + 0] = var1[var7 + 0] + var3[var4 + var7];
      }
   }

   public final void b(double[] var1, int var2, double[] var3, int var4, double[] var5, int var6) {
      for (int var7 = 0; var7 < this.c(); var7++) {
         var5[var7 + 0] = var1[var7 + 0] - var3[var4 + var7];
      }
   }

   public final void c(double[] var1, int var2, double[] var3, int var4, double[] var5, int var6) {
      for (int var7 = 0; var7 < this.g.length; var7++) {
         int[][] var8 = this.g[var7];
         double var9 = 0.0;

         for (int var11 = 0; var11 < var8.length; var11++) {
            var9 += var8[var11][0] * var1[var2 + var8[var11][1]] * var3[var4 + var8[var11][2]];
         }

         var5[var6 + var7] = var9;
      }
   }

   public final void a(double[] var1, int var2, double[] var3, double[] var4, int var5) {
      double[] var6 = new double[this.c()];
      this.a(var3, var2, -1, var6, 0);
      this.c(var1, var2, var6, 0, var4, 0);
   }

   public final void d(double[] var1, int var2, double[] var3, int var4, double[] var5, int var6) {
      double var7 = BY.g(var1[0], var3[0]);
      double var9 = BY.A((var1[0] - var7) / var3[0]);
      var5[0] = var7;

      for (int var11 = 1; var11 < this.c(); var11++) {
         var5[var11 + 0] = var1[var11 + 0] - var9 * var3[var11 + 0];
      }
   }

   public final void a(double var1, double[] var3, int var4, double[] var5, int var6) {
      double[] var12 = new double[1 + this.b];
      if (var1 == 0.0) {
         if (var3[0] == 0.0) {
            var12[0] = 1.0;
            double var8 = Double.POSITIVE_INFINITY;

            for (int var10 = 1; var10 < var12.length; var10++) {
               var8 = -var8;
               var12[var10] = var8;
            }
         } else if (var3[0] < 0.0) {
            Arrays.fill(var12, Double.NaN);
         }
      } else {
         var12[0] = BY.a(var1, var3[0]);
         double var13 = BY.k(var1);

         for (int var11 = 1; var11 < var12.length; var11++) {
            var12[var11] = var13 * var12[var11 - 1];
         }
      }

      this.b(var3, 0, var12, var5, 0);
   }

   public final void a(double[] var1, int var2, double var3, double[] var5, int var6) {
      double[] var12 = new double[1 + this.b];
      double var8 = BY.a(var1[0], var3 - this.b);

      for (int var10 = this.b; var10 > 0; var10--) {
         var12[var10] = var8;
         var8 *= var1[0];
      }

      var12[0] = var8;
      double var14 = var3;

      for (int var13 = 1; var13 <= this.b; var13++) {
         var12[var13] *= var14;
         var14 *= var3 - var13;
      }

      this.b(var1, 0, var12, var5, 0);
   }

   public final void a(double[] var1, int var2, int var3, double[] var4, int var5) {
      if (var3 == 0) {
         var4[0] = 1.0;
         Arrays.fill(var4, 1, 0 + this.c(), 0.0);
      } else {
         double[] var11 = new double[1 + this.b];
         if (var3 > 0) {
            int var7 = BY.a(this.b, var3);
            double var8 = BY.a(var1[var2], var3 - var7);

            for (int var10 = var7; var10 > 0; var10--) {
               var11[var10] = var8;
               var8 *= var1[var2];
            }

            var11[0] = var8;
         } else {
            double var12;
            double var9 = BY.a(var12 = 1.0 / var1[var2], -var3);

            for (int var6 = 0; var6 <= this.b; var6++) {
               var11[var6] = var9;
               var9 *= var12;
            }
         }

         double var13 = var3;

         for (int var14 = 1; var14 <= this.b; var14++) {
            var11[var14] *= var13;
            var13 *= var3 - var14;
         }

         this.b(var1, var2, var11, var4, 0);
      }
   }

   public final void e(double[] var1, int var2, double[] var3, int var4, double[] var5, int var6) {
      double[] var8 = new double[this.c()];
      this.c(var1, 0, var8, 0);
      var1 = new double[this.c()];
      this.c(var8, 0, var3, 0, var1, 0);
      this.a(var1, 0, var5, 0);
   }

   public final void b(double[] var1, int var2, int var3, double[] var4, int var5) {
      double[] var13 = new double[1 + this.b];
      double var7;
      if (var3 == 2) {
         var13[0] = BY.a(var1[0]);
         var7 = 0.5 / var13[0];
      } else if (var3 == 3) {
         var13[0] = BY.t(var1[0]);
         var7 = 1.0 / (3.0 * var13[0] * var13[0]);
      } else {
         var13[0] = BY.a(var1[0], 1.0 / var3);
         var7 = 1.0 / (var3 * BY.a(var13[0], var3 - 1));
      }

      double var9 = 1.0 / var3;
      double var11 = 1.0 / var1[0];

      for (int var14 = 1; var14 <= this.b; var14++) {
         var13[var14] = var7;
         var7 *= var11 * (var9 - var14);
      }

      this.b(var1, 0, var13, var4, 0);
   }

   public final void a(double[] var1, int var2, double[] var3, int var4) {
      double[] var5;
      Arrays.fill(var5 = new double[1 + this.b], BY.i(var1[0]));
      this.b(var1, 0, var5, var3, var4);
   }

   public final void b(double[] var1, int var2, double[] var3, int var4) {
      double[] var5;
      (var5 = new double[1 + this.b])[0] = BY.j(var1[0]);
      Arrays.fill(var5, 1, 1 + this.b, BY.i(var1[0]));
      this.b(var1, 0, var5, var3, 0);
   }

   public final void c(double[] var1, int var2, double[] var3, int var4) {
      double[] var10;
      (var10 = new double[1 + this.b])[0] = BY.k(var1[var2]);
      if (this.b > 0) {
         double var6;
         double var8 = var6 = 1.0 / var1[var2];

         for (int var5 = 1; var5 <= this.b; var5++) {
            var10[var5] = var8;
            var8 *= -var5 * var6;
         }
      }

      this.b(var1, var2, var10, var3, 0);
   }

   public final void d(double[] var1, int var2, double[] var3, int var4) {
      double[] var10;
      (var10 = new double[1 + this.b])[0] = BY.l(var1[0]);
      if (this.b > 0) {
         double var6;
         double var8 = var6 = 1.0 / (1.0 + var1[0]);

         for (int var11 = 1; var11 <= this.b; var11++) {
            var10[var11] = var8;
            var8 *= -var11 * var6;
         }
      }

      this.b(var1, 0, var10, var3, 0);
   }

   public final void e(double[] var1, int var2, double[] var3, int var4) {
      double[] var10;
      (var10 = new double[1 + this.b])[0] = BY.m(var1[0]);
      if (this.b > 0) {
         double var6;
         double var8 = (var6 = 1.0 / var1[0]) / BY.k(10.0);

         for (int var11 = 1; var11 <= this.b; var11++) {
            var10[var11] = var8;
            var8 *= -var11 * var6;
         }
      }

      this.b(var1, 0, var10, var3, 0);
   }

   public final void f(double[] var1, int var2, double[] var3, int var4) {
      double[] var5;
      (var5 = new double[1 + this.b])[0] = BY.o(var1[0]);
      if (this.b > 0) {
         var5[1] = -BY.n(var1[0]);

         for (int var6 = 2; var6 <= this.b; var6++) {
            var5[var6] = -var5[var6 - 2];
         }
      }

      this.b(var1, 0, var5, var3, 0);
   }

   public final void g(double[] var1, int var2, double[] var3, int var4) {
      double[] var5;
      (var5 = new double[1 + this.b])[0] = BY.n(var1[0]);
      if (this.b > 0) {
         var5[1] = BY.o(var1[0]);

         for (int var6 = 2; var6 <= this.b; var6++) {
            var5[var6] = -var5[var6 - 2];
         }
      }

      this.b(var1, 0, var5, var3, 0);
   }

   public final void h(double[] var1, int var2, double[] var3, int var4) {
      double[] var14 = new double[1 + this.b];
      double var6 = BY.p(var1[0]);
      var14[0] = var6;
      if (this.b > 0) {
         double[] var15;
         (var15 = new double[this.b + 2])[1] = 1.0;
         double var9 = var6 * var6;

         for (int var5 = 1; var5 <= this.b; var5++) {
            double var12 = 0.0;
            var15[var5 + 1] = var5 * var15[var5];

            for (int var8 = var5 + 1; var8 >= 0; var8 -= 2) {
               var12 = var12 * var9 + var15[var8];
               if (var8 > 2) {
                  var15[var8 - 2] = (var8 - 1) * var15[var8 - 1] + (var8 - 3) * var15[var8 - 3];
               } else if (var8 == 2) {
                  var15[0] = var15[1];
               }
            }

            if ((var5 & 1) == 0) {
               var12 *= var6;
            }

            var14[var5] = var12;
         }
      }

      this.b(var1, 0, var14, var3, 0);
   }

   public final void i(double[] var1, int var2, double[] var3, int var4) {
      double[] var18 = new double[1 + this.b];
      double var6 = var1[0];
      var18[0] = BY.s(var6);
      if (this.b > 0) {
         double[] var19;
         (var19 = new double[this.b])[0] = -1.0;
         double var9 = var6 * var6;
         double var11;
         double var13 = BY.a(var11 = 1.0 / (1.0 - var9));
         var18[1] = var13 * var19[0];

         for (int var5 = 2; var5 <= this.b; var5++) {
            double var16 = 0.0;
            var19[var5 - 1] = (var5 - 1) * var19[var5 - 2];

            for (int var8 = var5 - 1; var8 >= 0; var8 -= 2) {
               var16 = var16 * var9 + var19[var8];
               if (var8 > 2) {
                  var19[var8 - 2] = (var8 - 1) * var19[var8 - 1] + (2 * var5 - var8) * var19[var8 - 3];
               } else if (var8 == 2) {
                  var19[0] = var19[1];
               }
            }

            if ((var5 & 1) == 0) {
               var16 *= var6;
            }

            var13 *= var11;
            var18[var5] = var13 * var16;
         }
      }

      this.b(var1, 0, var18, var3, 0);
   }

   public final void j(double[] var1, int var2, double[] var3, int var4) {
      double[] var18 = new double[1 + this.b];
      double var6 = var1[0];
      var18[0] = BY.r(var6);
      if (this.b > 0) {
         double[] var19;
         (var19 = new double[this.b])[0] = 1.0;
         double var9 = var6 * var6;
         double var11;
         double var13 = BY.a(var11 = 1.0 / (1.0 - var9));
         var18[1] = var13 * var19[0];

         for (int var5 = 2; var5 <= this.b; var5++) {
            double var16 = 0.0;
            var19[var5 - 1] = (var5 - 1) * var19[var5 - 2];

            for (int var8 = var5 - 1; var8 >= 0; var8 -= 2) {
               var16 = var16 * var9 + var19[var8];
               if (var8 > 2) {
                  var19[var8 - 2] = (var8 - 1) * var19[var8 - 1] + (2 * var5 - var8) * var19[var8 - 3];
               } else if (var8 == 2) {
                  var19[0] = var19[1];
               }
            }

            if ((var5 & 1) == 0) {
               var16 *= var6;
            }

            var13 *= var11;
            var18[var5] = var13 * var16;
         }
      }

      this.b(var1, 0, var18, var3, 0);
   }

   public final void k(double[] var1, int var2, double[] var3, int var4) {
      double[] var18 = new double[1 + this.b];
      double var6 = var1[0];
      var18[0] = BY.q(var6);
      if (this.b > 0) {
         double[] var19;
         (var19 = new double[this.b])[0] = 1.0;
         double var9 = var6 * var6;
         double var11;
         double var13 = var11 = 1.0 / (var9 + 1.0);
         var18[1] = var13 * var19[0];

         for (int var5 = 2; var5 <= this.b; var5++) {
            double var16 = 0.0;
            var19[var5 - 1] = -var5 * var19[var5 - 2];

            for (int var8 = var5 - 1; var8 >= 0; var8 -= 2) {
               var16 = var16 * var9 + var19[var8];
               if (var8 > 2) {
                  var19[var8 - 2] = (var8 - 1) * var19[var8 - 1] + (var8 - 1 - 2 * var5) * var19[var8 - 3];
               } else if (var8 == 2) {
                  var19[0] = var19[1];
               }
            }

            if ((var5 & 1) == 0) {
               var16 *= var6;
            }

            var13 *= var11;
            var18[var5] = var13 * var16;
         }
      }

      this.b(var1, 0, var18, var3, 0);
   }

   public final void f(double[] var1, int var2, double[] var3, int var4, double[] var5, int var6) {
      double[] var7 = new double[this.c()];
      this.c(var3, 0, var3, 0, var7, 0);
      double[] var10 = new double[this.c()];
      this.c(var1, 0, var1, 0, var10, 0);
      this.a(var7, 0, var10, 0, var10, 0);
      this.b(var10, 0, 2, var7, 0);
      if (var3[0] >= 0.0) {
         this.a(var7, 0, var3, 0, var10, 0);
         this.a(var1, 0, var10, var7, 0);
         this.k(var7, 0, var10, 0);

         for (int var8 = 0; var8 < var10.length; var8++) {
            var5[var8 + 0] = 2.0 * var10[var8];
         }
      } else {
         this.b(var7, 0, var3, 0, var10, 0);
         this.a(var1, 0, var10, var7, 0);
         this.k(var7, 0, var10, 0);
         var5[0] = (var10[0] <= 0.0 ? -Math.PI : Math.PI) - 2.0 * var10[0];

         for (int var9 = 1; var9 < var10.length; var9++) {
            var5[var9 + 0] = -2.0 * var10[var9];
         }
      }

      var5[0] = BY.b(var1[0], var3[0]);
   }

   public final void l(double[] var1, int var2, double[] var3, int var4) {
      double[] var5;
      (var5 = new double[1 + this.b])[0] = BY.b(var1[0]);
      if (this.b > 0) {
         var5[1] = BY.c(var1[0]);

         for (int var6 = 2; var6 <= this.b; var6++) {
            var5[var6] = var5[var6 - 2];
         }
      }

      this.b(var1, 0, var5, var3, 0);
   }

   public final void m(double[] var1, int var2, double[] var3, int var4) {
      double[] var5;
      (var5 = new double[1 + this.b])[0] = BY.c(var1[0]);
      if (this.b > 0) {
         var5[1] = BY.b(var1[0]);

         for (int var6 = 2; var6 <= this.b; var6++) {
            var5[var6] = var5[var6 - 2];
         }
      }

      this.b(var1, 0, var5, var3, 0);
   }

   public final void n(double[] var1, int var2, double[] var3, int var4) {
      double[] var14 = new double[1 + this.b];
      double var6 = BY.d(var1[0]);
      var14[0] = var6;
      if (this.b > 0) {
         double[] var15;
         (var15 = new double[this.b + 2])[1] = 1.0;
         double var9 = var6 * var6;

         for (int var5 = 1; var5 <= this.b; var5++) {
            double var12 = 0.0;
            var15[var5 + 1] = -var5 * var15[var5];

            for (int var8 = var5 + 1; var8 >= 0; var8 -= 2) {
               var12 = var12 * var9 + var15[var8];
               if (var8 > 2) {
                  var15[var8 - 2] = (var8 - 1) * var15[var8 - 1] - (var8 - 3) * var15[var8 - 3];
               } else if (var8 == 2) {
                  var15[0] = var15[1];
               }
            }

            if ((var5 & 1) == 0) {
               var12 *= var6;
            }

            var14[var5] = var12;
         }
      }

      this.b(var1, 0, var14, var3, 0);
   }

   public final void o(double[] var1, int var2, double[] var3, int var4) {
      double[] var18 = new double[1 + this.b];
      double var6 = var1[0];
      var18[0] = BY.e(var6);
      if (this.b > 0) {
         double[] var19;
         (var19 = new double[this.b])[0] = 1.0;
         double var9 = var6 * var6;
         double var11;
         double var13 = BY.a(var11 = 1.0 / (var9 - 1.0));
         var18[1] = var13 * var19[0];

         for (int var5 = 2; var5 <= this.b; var5++) {
            double var16 = 0.0;
            var19[var5 - 1] = (1 - var5) * var19[var5 - 2];

            for (int var8 = var5 - 1; var8 >= 0; var8 -= 2) {
               var16 = var16 * var9 + var19[var8];
               if (var8 > 2) {
                  var19[var8 - 2] = (1 - var8) * var19[var8 - 1] + (var8 - 2 * var5) * var19[var8 - 3];
               } else if (var8 == 2) {
                  var19[0] = -var19[1];
               }
            }

            if ((var5 & 1) == 0) {
               var16 *= var6;
            }

            var13 *= var11;
            var18[var5] = var13 * var16;
         }
      }

      this.b(var1, 0, var18, var3, 0);
   }

   public final void p(double[] var1, int var2, double[] var3, int var4) {
      double[] var18 = new double[1 + this.b];
      double var6 = var1[0];
      var18[0] = BY.f(var6);
      if (this.b > 0) {
         double[] var19;
         (var19 = new double[this.b])[0] = 1.0;
         double var9 = var6 * var6;
         double var11;
         double var13 = BY.a(var11 = 1.0 / (var9 + 1.0));
         var18[1] = var13 * var19[0];

         for (int var5 = 2; var5 <= this.b; var5++) {
            double var16 = 0.0;
            var19[var5 - 1] = (1 - var5) * var19[var5 - 2];

            for (int var8 = var5 - 1; var8 >= 0; var8 -= 2) {
               var16 = var16 * var9 + var19[var8];
               if (var8 > 2) {
                  var19[var8 - 2] = (var8 - 1) * var19[var8 - 1] + (var8 - 2 * var5) * var19[var8 - 3];
               } else if (var8 == 2) {
                  var19[0] = var19[1];
               }
            }

            if ((var5 & 1) == 0) {
               var16 *= var6;
            }

            var13 *= var11;
            var18[var5] = var13 * var16;
         }
      }

      this.b(var1, 0, var18, var3, 0);
   }

   public final void q(double[] var1, int var2, double[] var3, int var4) {
      double[] var18 = new double[1 + this.b];
      double var6 = var1[0];
      var18[0] = BY.g(var6);
      if (this.b > 0) {
         double[] var19;
         (var19 = new double[this.b])[0] = 1.0;
         double var9 = var6 * var6;
         double var11;
         double var13 = var11 = 1.0 / (1.0 - var9);
         var18[1] = var13 * var19[0];

         for (int var5 = 2; var5 <= this.b; var5++) {
            double var16 = 0.0;
            var19[var5 - 1] = var5 * var19[var5 - 2];

            for (int var8 = var5 - 1; var8 >= 0; var8 -= 2) {
               var16 = var16 * var9 + var19[var8];
               if (var8 > 2) {
                  var19[var8 - 2] = (var8 - 1) * var19[var8 - 1] + (2 * var5 - var8 + 1) * var19[var8 - 3];
               } else if (var8 == 2) {
                  var19[0] = var19[1];
               }
            }

            if ((var5 & 1) == 0) {
               var16 *= var6;
            }

            var13 *= var11;
            var18[var5] = var13 * var16;
         }
      }

      this.b(var1, 0, var18, var3, 0);
   }

   public final void b(double[] var1, int var2, double[] var3, double[] var4, int var5) {
      for (int var6 = 0; var6 < this.h.length; var6++) {
         int[][] var7 = this.h[var6];
         double var8 = 0.0;

         for (int var10 = 0; var10 < var7.length; var10++) {
            int[] var11;
            double var12 = (var11 = var7[var10])[0] * var3[var11[1]];

            for (int var14 = 2; var14 < var11.length; var14++) {
               var12 *= var1[var2 + var11[var14]];
            }

            var8 += var12;
         }

         var4[var5 + var6] = var8;
      }
   }

   public final double a(double[] var1, int var2, double... var3) {
      double var4 = 0.0;

      for (int var11 = this.c() - 1; var11 >= 0; var11--) {
         int var7 = var11;
         int[] var6 = this.e[var7];
         double var8 = var1[var11 + 0];

         for (int var12 = 0; var12 < var6.length; var12++) {
            if (var6[var12] > 0) {
               try {
                  var8 *= BY.a(var3[var12], var6[var12]) / BR.a(var6[var12]);
               } catch (pj var10) {
                  throw new oY(var10);
               }
            }
         }

         var4 += var8;
      }

      return var4;
   }

   public final void a(lf var1) {
      if (this.a != var1.a) {
         throw new oS(this.a, var1.a);
      }

      if (this.b != var1.b) {
         throw new oS(this.b, var1.b);
      }
   }
}
