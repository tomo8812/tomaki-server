package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public final class Bv implements BB {
   private final int a;
   private final double[] b;
   private final double[] c;
   private final double[] d;
   private final double[] e;
   private final double[] f;
   private final int[] g;
   private final double[] h;
   private long i = 0L;
   private double j = 0.0;
   private boolean k = false;
   private boolean l = false;
   private final boolean[] m;
   private final double[] n;
   private final double[] o;
   private double p = 0.0;
   private double q = 0.0;
   private boolean r;
   private final double s;

   private Bv() {
      this(-1, false, Double.NaN);
   }

   private Bv(int var1, boolean var2, double var3) {
      if (var1 <= 0) {
         throw new Bw(px.NO_REGRESSORS);
      }

      if (var2) {
         this.a = var1 + 1;
      } else {
         this.a = var1;
      }

      this.r = var2;
      this.i = 0L;
      this.b = new double[this.a];
      this.c = new double[this.a];
      this.d = new double[this.a * (this.a - 1) / 2];
      this.e = new double[this.a];
      this.f = new double[this.a];
      this.g = new int[this.a];
      this.n = new double[this.a];
      this.o = new double[this.a];
      this.h = new double[this.a];
      this.m = new boolean[this.a];
      var1 = 0;

      while (var1 < this.a) {
         this.g[var1] = var1++;
      }

      if (var3 > 0.0) {
         this.s = var3;
      } else {
         this.s = -var3;
      }
   }

   private Bv(int var1, boolean var2) {
      this(var1, var2, Cq.a);
   }

   @Override
   public final boolean a() {
      return this.r;
   }

   @Override
   public final long b() {
      return this.i;
   }

   @Override
   public final void a(double[] var1, double var2) {
      if ((this.r || var1.length == this.a) && (!this.r || var1.length + 1 == this.a)) {
         if (!this.r) {
            this.a(Ch.a(var1, var1.length), 1.0, var2);
         } else {
            double[] var4 = new double[var1.length + 1];
            System.arraycopy(var1, 0, var4, 1, var1.length);
            var4[0] = 1.0;
            this.a(var4, 1.0, var2);
         }

         this.i++;
      } else {
         throw new Bw(px.INVALID_REGRESSION_OBSERVATION, var1.length, this.a);
      }
   }

   @Override
   public final void a(double[][] var1, double[] var2) {
      if (var1 == null || var2 == null || var1.length != var2.length) {
         throw new Bw(px.DIMENSIONS_MISMATCH_SIMPLE, var1 == null ? 0 : var1.length, var2 == null ? 0 : var2.length);
      }

      if (var1.length == 0) {
         throw new Bw(px.NO_DATA);
      }

      if (var1[0].length + 1 > var1.length) {
         throw new Bw(px.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS, var1.length, var1[0].length);
      }

      for (int var3 = 0; var3 < var1.length; var3++) {
         double[] var10001 = var1[var3];
         double var6 = var2[var3];
         double[] var5 = var10001;
         Bv var4 = this;
         if (!this.r && var5.length != var4.a || var4.r && var5.length + 1 != var4.a) {
            throw new Bw(px.INVALID_REGRESSION_OBSERVATION, var5.length, var4.a);
         }

         if (!var4.r) {
            var4.a(Ch.a(var5, var5.length), 1.0, var6);
         } else {
            double[] var8 = new double[var5.length + 1];
            System.arraycopy(var5, 0, var8, 1, var5.length);
            var8[0] = 1.0;
            var4.a(var8, 1.0, var6);
         }

         var4.i++;
      }
   }

   private void a(double[] var1, double var2, double var4) {
      int var6 = 0;
      double var7 = var2;
      double var9 = var4;
      this.k = false;
      this.p = a(var4, this.p);
      this.q = a(this.q, var4 * var4);

      for (int var26 = 0; var26 < var1.length; var26++) {
         if (var7 == 0.0) {
            return;
         }

         double var11;
         if ((var11 = var1[var26]) == 0.0) {
            var6 += this.a - var26 - 1;
         } else {
            double var13 = this.b[var26];
            double var15 = var7 * var11;
            double var21 = var7;
            double var17;
            if (var13 != 0.0) {
               var17 = a(var13, var15 * var11);
               if (BY.w(var15 * var11 / var13) > Cq.a) {
                  var7 = var13 * var7 / var17;
               }
            } else {
               var17 = var15 * var11;
               var7 = 0.0;
            }

            this.b[var26] = var17;

            for (int var24 = var26 + 1; var24 < this.a; var24++) {
               double var19 = var1[var24];
               var1[var24] = a(var19, -var11 * this.d[var6]);
               if (var13 != 0.0) {
                  this.d[var6] = a(var13 * this.d[var6], var21 * var11 * var19) / var17;
               } else {
                  this.d[var6] = var19 / var11;
               }

               var6++;
            }

            double var27 = var9;
            var9 = a(var9, -var11 * this.c[var26]);
            if (var13 != 0.0) {
               this.c[var26] = a(var13 * this.c[var26], var15 * var27) / var17;
            } else {
               this.c[var26] = var27 / var11;
            }
         }
      }

      this.j = a(this.j, var7 * var9 * var9);
   }

   private static double a(double var0, double var2) {
      double var4 = BY.w(var0);
      double var6 = BY.w(var2);
      if (var4 > var6) {
         double var10 = var4 * Cq.a;
         return var6 > var10 ? var0 + var2 : var0;
      } else {
         double var8 = var6 * Cq.a;
         return var4 > var8 ? var0 + var2 : var2;
      }
   }

   @Override
   public final void c() {
      Arrays.fill(this.b, 0.0);
      Arrays.fill(this.c, 0.0);
      Arrays.fill(this.d, 0.0);
      Arrays.fill(this.e, 0.0);
      Arrays.fill(this.f, 0.0);
      Arrays.fill(this.h, 0.0);
      Arrays.fill(this.o, 0.0);
      Arrays.fill(this.n, 0.0);
      Arrays.fill(this.m, false);
      int var1 = 0;

      while (var1 < this.a) {
         this.g[var1] = var1++;
      }

      this.i = 0L;
      this.j = 0.0;
      this.p = 0.0;
      this.q = 0.0;
      this.k = false;
      this.l = false;
   }

   private void e() {
      double var4 = this.s;

      for (int var6 = 0; var6 < this.a; var6++) {
         this.h[var6] = BY.a(this.b[var6]);
      }

      this.e[0] = var4 * this.h[0];

      for (int var8 = 1; var8 < this.a; var8++) {
         int var1 = var8 - 1;
         double var2 = this.h[var8];

         for (int var7 = 0; var7 < var8; var7++) {
            var2 += BY.w(this.d[var1]) * this.h[var7];
            var1 += this.a - var7 - 2;
         }

         this.e[var8] = var4 * var2;
      }

      this.l = true;
   }

   private double[] a(int var1) {
      if (var1 <= 0) {
         throw new Bw(px.NO_REGRESSORS);
      }

      if (var1 > this.a) {
         throw new Bw(px.TOO_MANY_REGRESSORS, var1, this.a);
      }

      if (!this.l) {
         this.e();
      }

      double[] var3 = new double[var1];
      boolean var4 = false;

      for (int var5 = var1 - 1; var5 >= 0; var5--) {
         if (BY.a(this.b[var5]) < this.e[var5]) {
            var3[var5] = 0.0;
            this.b[var5] = 0.0;
            var4 = true;
         } else {
            var3[var5] = this.c[var5];
            int var2 = var5 * (this.a + this.a - var5 - 1) / 2;

            for (int var6 = var5 + 1; var6 < var1; var6++) {
               var3[var5] = a(var3[var5], -this.d[var2] * var3[var6]);
               var2++;
            }
         }
      }

      if (var4) {
         for (int var7 = 0; var7 < var1; var7++) {
            if (this.m[var7]) {
               var3[var7] = Double.NaN;
            }
         }
      }

      return var3;
   }

   private void f() {
      for (int var2 = 0; var2 < this.a; var2++) {
         this.o[var2] = BY.a(this.b[var2]);
      }

      for (int var10 = 0; var10 < this.a; var10++) {
         double var3 = this.e[var10];
         int var1 = var10 - 1;

         for (int var5 = 0; var5 < var10 - 1; var5++) {
            if (BY.w(this.d[var1]) * this.o[var5] < var3) {
               this.d[var1] = 0.0;
            }

            var1 += this.a - var5 - 2;
         }

         this.m[var10] = false;
         if (this.o[var10] < var3) {
            this.m[var10] = true;
            if (var10 >= this.a - 1) {
               this.j = this.j + this.b[var10] * this.c[var10] * this.c[var10];
            } else {
               Arrays.fill(this.n, 0.0);
               int var11 = var10 * (this.a + this.a - var10 - 1) / 2;

               for (int var6 = var10 + 1; var6 < this.a; var11++) {
                  this.n[var6] = this.d[var11];
                  this.d[var11] = 0.0;
                  var6++;
               }

               double var12 = this.c[var10];
               double var8 = this.b[var10];
               this.b[var10] = 0.0;
               this.c[var10] = 0.0;
               this.a(this.n, var8, var12);
            }
         }
      }
   }

   private void g() {
      double var1 = this.j;
      this.f[this.a - 1] = this.j;

      for (int var3 = this.a - 1; var3 > 0; var3--) {
         var1 += this.b[var3] * this.c[var3] * this.c[var3];
         this.f[var3 - 1] = var1;
      }

      this.k = true;
   }

   private double[] b(int var1) {
      if (this.i <= var1) {
         return null;
      }

      double var2 = 0.0;

      for (int var4 = 0; var4 < var1; var4++) {
         if (!this.m[var4]) {
            var2++;
         }
      }

      double var26 = this.f[var1 - 1] / (this.i - var2);
      double[] var24 = new double[var1 * (var1 - 1) / 2];
      int var7 = var1;
      double[] var6 = var24;
      Bv var3 = this;
      int var8 = var7 * (var7 - 1) / 2 - 1;
      Arrays.fill(var6, Double.NaN);

      for (int var11 = var7 - 1; var11 > 0; var11--) {
         if (var3.m[var11]) {
            var8 -= var7 - var11;
         } else {
            int var12 = (var11 - 1) * (var3.a + var3.a - var11) / 2;

            for (int var13 = var7; var13 > var11; var13--) {
               int var9 = var12;
               int var10 = var8;
               double var22 = 0.0;

               for (int var14 = var11; var14 < var13 - 1; var14++) {
                  var10 += var7 - var14 - 1;
                  if (!var3.m[var14]) {
                     var22 += -var3.d[var9] * var6[var10];
                  }

                  var9++;
               }

               var6[var8] = var22 - var3.d[var9];
               var8--;
            }
         }
      }

      double[] var25;
      Arrays.fill(var25 = new double[var1 * (var1 + 1) / 2], Double.NaN);
      var8 = 0;

      for (int var30 = 0; var30 < var1; var30++) {
         int var27 = var8;
         if (!this.m[var30]) {
            for (int var31 = var30; var31 < var1; var31++) {
               if (this.m[var31]) {
                  var27 += var1 - var31 - 1;
               } else {
                  var7 = var8 + var31 - var30;
                  double var32;
                  if (var30 == var31) {
                     var32 = 1.0 / this.b[var31];
                  } else {
                     var32 = var24[var7 - 1] / this.b[var31];
                  }

                  for (int var33 = var31 + 1; var33 < var1; var33++) {
                     if (!this.m[var33]) {
                        var32 += var24[var7] * var24[var27] / this.b[var33];
                     }

                     var7++;
                     var27++;
                  }

                  var25[(var31 + 1) * var31 / 2 + var30] = var32 * var26;
               }
            }
         }

         var8 += var1 - var30 - 1;
      }

      return var25;
   }

   private void a(double[] var1, int var2) {
      int var3 = var2 * (var2 - 1) / 2 - 1;
      Arrays.fill(var1, Double.NaN);

      for (int var8 = var2 - 1; var8 > 0; var8--) {
         if (this.m[var8]) {
            var3 -= var2 - var8;
         } else {
            int var9 = (var8 - 1) * (this.a + this.a - var8) / 2;

            for (int var10 = var2; var10 > var8; var10--) {
               int var4 = var9;
               int var5 = var3;
               double var6 = 0.0;

               for (int var11 = var8; var11 < var10 - 1; var11++) {
                  var5 += var2 - var11 - 1;
                  if (!this.m[var11]) {
                     var6 += -this.d[var4] * var1[var5];
                  }

                  var4++;
               }

               var1[var3] = var6 - this.d[var4];
               var3--;
            }
         }
      }
   }

   private double[] c(int var1) {
      double[] var2 = new double[(this.a - var1 + 1) * (this.a - var1) / 2];
      int var5 = -var1;
      int var6 = -(var1 + 1);
      double[] var7 = new double[this.a - var1];
      double[] var8 = new double[this.a - var1 - 1];
      int var9 = (this.a - var1) * (this.a - var1 - 1) / 2;
      if (var1 >= -1 && var1 < this.a) {
         int var3 = this.a - 1;
         int var16 = this.d.length - (var3 - var1) * (var3 - var1 + 1) / 2;
         if (this.b[var1] > 0.0) {
            var7[var1 + var5] = 1.0 / BY.a(this.b[var1]);
         }

         for (int var17 = var1 + 1; var17 < this.a; var17++) {
            var3 = var16 + var17 - 1 - var1;
            double var10 = this.b[var17];

            for (int var18 = var1; var18 < var17; var18++) {
               var10 += this.b[var18] * this.d[var3] * this.d[var3];
               var3 += this.a - var18 - 2;
            }

            if (var10 > 0.0) {
               var7[var17 + var5] = 1.0 / BY.a(var10);
            } else {
               var7[var17 + var5] = 0.0;
            }
         }

         double var14 = this.j;

         for (int var24 = var1; var24 < this.a; var24++) {
            var14 += this.b[var24] * this.c[var24] * this.c[var24];
         }

         if (var14 > 0.0) {
            var14 = 1.0 / BY.a(var14);
         }

         for (int var25 = var1; var25 < this.a; var25++) {
            double var12 = 0.0;
            Arrays.fill(var8, 0.0);
            var3 = var16 + var25 - var1 - 1;

            for (int var26 = var1; var26 < var25; var26++) {
               int var4 = var3 + 1;

               for (int var22 = var25 + 1; var22 < this.a; var22++) {
                  var8[var22 + var6] = var8[var22 + var6] + this.b[var26] * this.d[var3] * this.d[var4];
                  var4++;
               }

               var12 += this.b[var26] * this.d[var3] * this.c[var26];
               var3 += this.a - var26 - 2;
            }

            int var21 = var3 + 1;

            for (int var27 = var25 + 1; var27 < this.a; var27++) {
               var8[var27 + var6] = var8[var27 + var6] + this.b[var25] * this.d[var21];
               var21++;
               var2[(var27 - 1 - var1) * (var27 - var1) / 2 + var25 - var1] = var8[var27 + var6] * var7[var25 + var5] * var7[var27 + var5];
            }

            var12 += this.b[var25] * this.c[var25];
            var2[var25 + var5 + var9] = var12 * var7[var25 + var5] * var14;
         }

         return var2;
      } else {
         return null;
      }
   }

   private void a(int var1, int var2) {
      boolean var21 = false;
      if (var1 != var2) {
         if (!this.k) {
            this.g();
         }

         int var3;
         byte var19;
         int var22;
         if (var1 < var2) {
            var3 = var1;
            var19 = 1;
            var22 = var2 - var1;
         } else {
            var3 = var1 - 1;
            var19 = -1;
            var22 = var1 - var2;
         }

         int var23 = var3;

         for (int var24 = 0; var24 < var22; var24++) {
            var2 = (var1 = var23 * (this.a + this.a - var23 - 1) / 2) + this.a - var23 - 1;
            int var20 = var23 + 1;
            double var29 = this.b[var23];
            double var5 = this.b[var20];
            if (var29 > this.s || var5 > this.s) {
               double var7;
               if (BY.w(var7 = this.d[var1]) * BY.a(var29) < this.e[var20]) {
                  var7 = 0.0;
               }

               if (var29 < this.s || BY.w(var7) < this.s) {
                  this.b[var23] = var5;
                  this.b[var20] = var29;
                  this.d[var1] = 0.0;

                  for (int var33 = var23 + 2; var33 < this.a; var33++) {
                     var7 = this.d[++var1];
                     this.d[var1] = this.d[var2];
                     this.d[var2] = var7;
                     var2++;
                  }

                  var7 = this.c[var23];
                  this.c[var23] = this.c[var20];
                  this.c[var20] = var7;
                  var21 = true;
               } else if (var5 < this.s) {
                  this.b[var23] = var29 * var7 * var7;
                  this.d[var1] = 1.0 / var7;

                  for (int var9 = var1 + 1; var9 < var1 + this.a - var23 - 1; var9++) {
                     this.d[var9] = this.d[var9] / var7;
                  }

                  this.c[var23] = this.c[var23] / var7;
                  var21 = true;
               }

               if (!var21) {
                  double var34 = var5 + var29 * var7 * var7;
                  double var13 = var5 / var34;
                  double var15 = var7 * var29 / var34;
                  double var11 = var29 * var13;
                  this.b[var23] = var34;
                  this.b[var20] = var11;
                  this.d[var1] = var15;

                  for (int var35 = var23 + 2; var35 < this.a; var35++) {
                     double var17 = this.d[++var1];
                     this.d[var1] = var13 * this.d[var2] + var15 * var17;
                     this.d[var2] = var17 - var7 * this.d[var2];
                     var2++;
                  }

                  double var37 = this.c[var23];
                  this.c[var23] = var13 * this.c[var20] + var15 * var37;
                  this.c[var20] = var37 - var7 * this.c[var20];
               }
            }

            if (var23 > 0) {
               var1 = var23;

               for (int var36 = 0; var36 < var23; var36++) {
                  double var31 = this.d[var1];
                  this.d[var1] = this.d[var1 - 1];
                  this.d[var1 - 1] = var31;
                  var1 += this.a - var36 - 2;
               }
            }

            var1 = this.g[var23];
            this.g[var23] = this.g[var20];
            this.g[var20] = var1;
            double var32 = this.e[var23];
            this.e[var23] = this.e[var20];
            this.e[var20] = var32;
            this.f[var23] = this.f[var20] + this.b[var20] * this.c[var20] * this.c[var20];
            var23 += var19;
         }
      }
   }

   private int a(int[] var1, int var2) {
      if (var1.length > 0 && var1.length <= this.a + 1) {
         var2 = 0;

         for (int var3 = 0; var3 < this.a; var3++) {
            int var4 = this.g[var3];

            for (int var5 = 0; var5 < var1.length; var5++) {
               if (var4 == var1[var5] && var3 > var2) {
                  int var6 = var2;
                  var5 = var3;
                  Bv var30 = this;
                  boolean var9 = false;
                  if (var5 != var6) {
                     if (!var30.k) {
                        var30.g();
                     }

                     int var7;
                     byte var8;
                     int var26;
                     if (var5 < var6) {
                        var7 = var5;
                        var8 = 1;
                        var26 = var6 - var5;
                     } else {
                        var7 = var5 - 1;
                        var8 = -1;
                        var26 = var5 - var6;
                     }

                     int var27 = var7;

                     for (int var28 = 0; var28 < var26; var28++) {
                        var6 = (var5 = var27 * (var30.a + var30.a - var27 - 1) / 2) + var30.a - var27 - 1;
                        var7 = var27 + 1;
                        double var10 = var30.b[var27];
                        double var12 = var30.b[var7];
                        if (var10 > var30.s || var12 > var30.s) {
                           double var14;
                           if (BY.w(var14 = var30.d[var5]) * BY.a(var10) < var30.e[var7]) {
                              var14 = 0.0;
                           }

                           if (var10 < var30.s || BY.w(var14) < var30.s) {
                              var30.b[var27] = var12;
                              var30.b[var7] = var10;
                              var30.d[var5] = 0.0;

                              for (int var40 = var27 + 2; var40 < var30.a; var40++) {
                                 var14 = var30.d[++var5];
                                 var30.d[var5] = var30.d[var6];
                                 var30.d[var6] = var14;
                                 var6++;
                              }

                              var14 = var30.c[var27];
                              var30.c[var27] = var30.c[var7];
                              var30.c[var7] = var14;
                              var9 = true;
                           } else if (var12 < var30.s) {
                              var30.b[var27] = var10 * var14 * var14;
                              var30.d[var5] = 1.0 / var14;

                              for (int var16 = var5 + 1; var16 < var5 + var30.a - var27 - 1; var16++) {
                                 var30.d[var16] = var30.d[var16] / var14;
                              }

                              var30.c[var27] = var30.c[var27] / var14;
                              var9 = true;
                           }

                           if (!var9) {
                              double var41 = var12 + var10 * var14 * var14;
                              double var20 = var12 / var41;
                              double var22 = var14 * var10 / var41;
                              double var18 = var10 * var20;
                              var30.b[var27] = var41;
                              var30.b[var7] = var18;
                              var30.d[var5] = var22;

                              for (int var42 = var27 + 2; var42 < var30.a; var42++) {
                                 double var24 = var30.d[++var5];
                                 var30.d[var5] = var20 * var30.d[var6] + var22 * var24;
                                 var30.d[var6] = var24 - var14 * var30.d[var6];
                                 var6++;
                              }

                              double var44 = var30.c[var27];
                              var30.c[var27] = var20 * var30.c[var7] + var22 * var44;
                              var30.c[var7] = var44 - var14 * var30.c[var7];
                           }
                        }

                        if (var27 > 0) {
                           var5 = var27;

                           for (int var43 = 0; var43 < var27; var43++) {
                              double var38 = var30.d[var5];
                              var30.d[var5] = var30.d[var5 - 1];
                              var30.d[var5 - 1] = var38;
                              var5 += var30.a - var43 - 2;
                           }
                        }

                        var5 = var30.g[var27];
                        var30.g[var27] = var30.g[var7];
                        var30.g[var7] = var5;
                        double var39 = var30.e[var27];
                        var30.e[var27] = var30.e[var7];
                        var30.e[var7] = var39;
                        var30.f[var27] = var30.f[var7] + var30.b[var7] * var30.c[var7] * var30.c[var7];
                        var27 += var8;
                     }
                  }

                  if (++var2 >= var1.length) {
                     return 0;
                  }
                  break;
               }
            }
         }

         return 0;
      } else {
         return -1;
      }
   }

   private double a(double[] var1) {
      double[] var2 = new double[this.a];
      if (var1.length > this.a) {
         return Double.NaN;
      }

      double[] var3;
      if (this.r) {
         (var3 = new double[var1.length + 1])[0] = 1.0;
         System.arraycopy(var1, 0, var3, 1, var1.length);
      } else {
         var3 = var1;
      }

      double var7 = 0.0;

      for (int var6 = 0; var6 < var3.length; var6++) {
         if (BY.a(this.b[var6]) < this.e[var6]) {
            var2[var6] = 0.0;
         } else {
            int var10 = var6 - 1;
            double var4 = var3[var6];

            for (int var9 = 0; var9 < var6; var9++) {
               var4 = a(var4, -var2[var9] * this.d[var10]);
               var10 += this.a - var9 - 2;
            }

            var2[var6] = var4;
            var7 = a(var7, var4 * var4 / this.b[var6]);
         }
      }

      return var7;
   }

   private int[] h() {
      int[] var1 = this.g;
      return Ch.a(this.g, var1.length);
   }

   @Override
   public final Bz d() {
      int var2 = this.a;
      Bv var1 = this;
      if (this.i <= var2) {
         throw new Bw(px.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS, var1.i, var2);
      }

      if (var2 > var1.a) {
         throw new Bw(px.TOO_MANY_REGRESSORS, var2, var1.a);
      }

      var1.e();
      var1.f();
      double[] var3 = var1.a(var2);
      var1.g();
      double[] var4 = var1.b(var2);
      int var5 = 0;

      for (int var6 = 0; var6 < var1.m.length; var6++) {
         if (!var1.m[var6]) {
            var5++;
         }
      }

      boolean var14 = false;

      for (int var7 = 0; var7 < var2; var7++) {
         if (var1.g[var7] != var7) {
            var14 = true;
            break;
         }
      }

      if (!var14) {
         return new Bz(var3, new double[][]{var4}, true, var1.i, var5, var1.p, var1.q, var1.j, var1.r, false);
      }

      double[] var16 = new double[var3.length];
      double[] var15 = new double[var4.length];
      int[] var8 = new int[var3.length];

      for (int var9 = 0; var9 < var1.a; var9++) {
         for (int var10 = 0; var10 < var2; var10++) {
            if (var1.g[var10] == var9) {
               var16[var9] = var3[var10];
               var8[var9] = var10;
            }
         }
      }

      int var17 = 0;

      for (int var11 = 0; var11 < var3.length; var11++) {
         var2 = var8[var11];

         for (int var12 = 0; var12 <= var11; var17++) {
            int var18 = var8[var12];
            if (var2 > var18) {
               var18 = var2 * (var2 + 1) / 2 + var18;
            } else {
               var18 = var18 * (var18 + 1) / 2 + var2;
            }

            var15[var17] = var4[var18];
            var12++;
         }
      }

      return new Bz(var16, new double[][]{var15}, true, var1.i, var5, var1.p, var1.q, var1.j, var1.r, false);
   }

   private Bz d(int var1) {
      if (this.i <= var1) {
         throw new Bw(px.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS, this.i, var1);
      }

      if (var1 > this.a) {
         throw new Bw(px.TOO_MANY_REGRESSORS, var1, this.a);
      }

      this.e();
      this.f();
      double[] var2 = this.a(var1);
      this.g();
      double[] var3 = this.b(var1);
      int var4 = 0;

      for (int var5 = 0; var5 < this.m.length; var5++) {
         if (!this.m[var5]) {
            var4++;
         }
      }

      boolean var13 = false;

      for (int var6 = 0; var6 < var1; var6++) {
         if (this.g[var6] != var6) {
            var13 = true;
            break;
         }
      }

      if (!var13) {
         return new Bz(var2, new double[][]{var3}, true, this.i, var4, this.p, this.q, this.j, this.r, false);
      }

      double[] var15 = new double[var2.length];
      double[] var14 = new double[var3.length];
      int[] var7 = new int[var2.length];

      for (int var8 = 0; var8 < this.a; var8++) {
         for (int var9 = 0; var9 < var1; var9++) {
            if (this.g[var9] == var8) {
               var15[var8] = var2[var9];
               var7[var8] = var9;
            }
         }
      }

      int var16 = 0;

      for (int var10 = 0; var10 < var2.length; var10++) {
         var1 = var7[var10];

         for (int var11 = 0; var11 <= var10; var16++) {
            int var17 = var7[var11];
            if (var1 > var17) {
               var17 = var1 * (var1 + 1) / 2 + var17;
            } else {
               var17 = var17 * (var17 + 1) / 2 + var1;
            }

            var14[var16] = var3[var17];
            var11++;
         }
      }

      return new Bz(var15, new double[][]{var14}, true, this.i, var4, this.p, this.q, this.j, this.r, false);
   }

   @Override
   public final Bz a(int[] var1) {
      if (var1.length > this.a) {
         throw new Bw(px.TOO_MANY_REGRESSORS, var1.length, this.a);
      }

      if (this.i <= this.a) {
         throw new Bw(px.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS, this.i, this.a);
      }

      Arrays.sort(var1);
      int var2 = 0;

      for (int var3 = 0; var3 < var1.length; var3++) {
         if (var3 >= this.a) {
            throw new Bw(px.INDEX_LARGER_THAN_MAX, var3, this.a);
         }

         if (var3 > 0 && var1[var3] == var1[var3 - 1]) {
            var1[var3] = -1;
            var2++;
         }
      }

      int[] var49;
      if (var2 > 0) {
         int var4 = 0;
         var49 = new int[var1.length - var2];

         for (int var46 = 0; var46 < var1.length; var46++) {
            if (var1[var46] >= 0) {
               var49[var4] = var1[var46];
               var4++;
            }
         }
      } else {
         var49 = var1;
      }

      boolean var43 = false;
      int[] var47 = var49;
      Bv var44 = this;
      if (var47.length > 0 && var47.length <= var44.a + 1) {
         int var52 = 0;

         label219:
         for (int var5 = 0; var5 < var44.a; var5++) {
            int var6 = var44.g[var5];

            for (int var7 = 0; var7 < var47.length; var7++) {
               if (var6 == var47[var7] && var5 > var52) {
                  int var8 = var52;
                  var7 = var5;
                  Bv var57 = var44;
                  boolean var11 = false;
                  if (var7 != var8) {
                     if (!var57.k) {
                        var57.g();
                     }

                     int var9;
                     byte var10;
                     int var12;
                     if (var7 < var8) {
                        var9 = var7;
                        var10 = 1;
                        var12 = var8 - var7;
                     } else {
                        var9 = var7 - 1;
                        var10 = -1;
                        var12 = var7 - var8;
                     }

                     int var13 = var9;

                     for (int var14 = 0; var14 < var12; var14++) {
                        var8 = (var7 = var13 * (var57.a + var57.a - var13 - 1) / 2) + var57.a - var13 - 1;
                        var9 = var13 + 1;
                        double var27 = var57.b[var13];
                        double var29 = var57.b[var9];
                        if (var27 > var57.s || var29 > var57.s) {
                           double var31;
                           if (BY.w(var31 = var57.d[var7]) * BY.a(var27) < var57.e[var9]) {
                              var31 = 0.0;
                           }

                           if (var27 < var57.s || BY.w(var31) < var57.s) {
                              var57.b[var13] = var29;
                              var57.b[var9] = var27;
                              var57.d[var7] = 0.0;

                              for (int var73 = var13 + 2; var73 < var57.a; var73++) {
                                 var31 = var57.d[++var7];
                                 var57.d[var7] = var57.d[var8];
                                 var57.d[var8] = var31;
                                 var8++;
                              }

                              var31 = var57.c[var13];
                              var57.c[var13] = var57.c[var9];
                              var57.c[var9] = var31;
                              var11 = true;
                           } else if (var29 < var57.s) {
                              var57.b[var13] = var27 * var31 * var31;
                              var57.d[var7] = 1.0 / var31;

                              for (int var15 = var7 + 1; var15 < var7 + var57.a - var13 - 1; var15++) {
                                 var57.d[var15] = var57.d[var15] / var31;
                              }

                              var57.c[var13] = var57.c[var13] / var31;
                              var11 = true;
                           }

                           if (!var11) {
                              double var33 = var29 + var27 * var31 * var31;
                              double var37 = var29 / var33;
                              double var39 = var31 * var27 / var33;
                              double var35 = var27 * var37;
                              var57.b[var13] = var33;
                              var57.b[var9] = var35;
                              var57.d[var7] = var39;

                              for (int var74 = var13 + 2; var74 < var57.a; var74++) {
                                 double var41 = var57.d[++var7];
                                 var57.d[var7] = var37 * var57.d[var8] + var39 * var41;
                                 var57.d[var8] = var41 - var31 * var57.d[var8];
                                 var8++;
                              }

                              double var79 = var57.c[var13];
                              var57.c[var13] = var37 * var57.c[var9] + var39 * var79;
                              var57.c[var9] = var79 - var31 * var57.c[var9];
                           }
                        }

                        if (var13 > 0) {
                           var7 = var13;

                           for (int var75 = 0; var75 < var13; var75++) {
                              double var77 = var57.d[var7];
                              var57.d[var7] = var57.d[var7 - 1];
                              var57.d[var7 - 1] = var77;
                              var7 += var57.a - var75 - 2;
                           }
                        }

                        var7 = var57.g[var13];
                        var57.g[var13] = var57.g[var9];
                        var57.g[var9] = var7;
                        double var78 = var57.e[var13];
                        var57.e[var13] = var57.e[var9];
                        var57.e[var9] = var78;
                        var57.f[var13] = var57.f[var9] + var57.b[var9] * var57.c[var9] * var57.c[var9];
                        var13 += var10;
                     }
                  }

                  if (++var52 >= var47.length) {
                     break label219;
                  }
                  break;
               }
            }
         }

         boolean var80 = false;
      } else {
         byte var10000 = -1;
      }

      this.e();
      this.f();
      double[] var53 = this.a(var49.length);
      this.g();
      double[] var48 = this.b(var49.length);
      int var45 = 0;

      for (int var54 = 0; var54 < this.m.length; var54++) {
         if (!this.m[var54]) {
            var45++;
         }
      }

      boolean var55 = false;

      for (int var58 = 0; var58 < this.a; var58++) {
         if (this.g[var58] != var49[var58]) {
            var55 = true;
            break;
         }
      }

      if (!var55) {
         return new Bz(var53, new double[][]{var48}, true, this.i, var45, this.p, this.q, this.j, this.r, false);
      }

      double[] var59 = new double[var53.length];
      int[] var56 = new int[var53.length];

      for (int var64 = 0; var64 < var49.length; var64++) {
         for (int var67 = 0; var67 < this.g.length; var67++) {
            if (this.g[var67] == var49[var64]) {
               var59[var64] = var53[var67];
               var56[var64] = var67;
            }
         }
      }

      double[] var65 = new double[var48.length];
      int var68 = 0;

      for (int var71 = 0; var71 < var53.length; var71++) {
         int var70 = var56[var71];

         for (int var72 = 0; var72 <= var71; var68++) {
            int var50 = var56[var72];
            int var51;
            if (var70 > var50) {
               var51 = var70 * (var70 + 1) / 2 + var50;
            } else {
               var51 = var50 * (var50 + 1) / 2 + var70;
            }

            var65[var68] = var48[var51];
            var72++;
         }
      }

      return new Bz(var59, new double[][]{var65}, true, this.i, var45, this.p, this.q, this.j, this.r, false);
   }
}
