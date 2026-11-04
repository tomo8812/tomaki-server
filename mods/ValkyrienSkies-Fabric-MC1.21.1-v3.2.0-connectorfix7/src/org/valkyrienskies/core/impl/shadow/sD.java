package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;

public final class sD extends sv implements Serializable {
   private static int c;
   private static final long d = 4991895511313664478L;
   final double[][] a;
   private final int e;
   private final int f;
   private final int g;
   final int b;

   public sD(int var1, int var2) {
      super(var1, var2);
      this.e = var1;
      this.f = var2;
      this.g = (var1 + 52 - 1) / 52;
      this.b = (var2 + 52 - 1) / 52;
      this.a = c(var1, var2);
   }

   public sD(double[][] var1) {
      this(var1.length, var1[0].length, a(var1), false);
   }

   public sD(int var1, int var2, double[][] var3, boolean var4) {
      super(var1, var2);
      this.e = var1;
      this.f = var2;
      this.g = (var1 + 52 - 1) / 52;
      this.b = (var2 + 52 - 1) / 52;
      this.a = var3;
      var1 = 0;

      for (int var7 = 0; var7 < this.g; var7++) {
         var4 = this.k(var7);

         for (int var5 = 0; var5 < this.b; var1++) {
            if (var3[var1].length != var4 * this.h(var5)) {
               throw new oS(var3[var1].length, var4 * this.h(var5));
            }

            var5++;
         }
      }
   }

   private static double[][] a(double[][] var0) {
      int var1 = var0.length;
      int var2 = var0[0].length;
      int var3 = (var1 + 52 - 1) / 52;
      int var4 = (var2 + 52 - 1) / 52;

      for (int var5 = 0; var5 < var0.length; var5++) {
         int var6;
         if ((var6 = var0[var5].length) != var2) {
            throw new oS(var2, var6);
         }
      }

      double[][] var17 = new double[var3 * var4][];
      int var18 = 0;

      for (int var7 = 0; var7 < var3; var7++) {
         int var8;
         int var9;
         int var10 = (var9 = BY.a((var8 = var7 * 52) + 52, var1)) - var8;

         for (int var11 = 0; var11 < var4; var11++) {
            int var12;
            int var13 = BY.a((var12 = var11 * 52) + 52, var2) - var12;
            double[] var14 = new double[var10 * var13];
            var17[var18] = var14;
            int var15 = 0;

            for (int var16 = var8; var16 < var9; var16++) {
               System.arraycopy(var0[var16], var12, var14, var15, var13);
               var15 += var13;
            }

            var18++;
         }
      }

      return var17;
   }

   public static double[][] c(int var0, int var1) {
      int var2 = (var0 + 52 - 1) / 52;
      int var3 = (var1 + 52 - 1) / 52;
      double[][] var4 = new double[var2 * var3][];
      int var5 = 0;

      for (int var6 = 0; var6 < var2; var6++) {
         int var7;
         var7 = BY.a((var7 = var6 * 52) + 52, var0) - var7;

         for (int var8 = 0; var8 < var3; var8++) {
            int var9;
            var9 = BY.a((var9 = var8 * 52) + 52, var1) - var9;
            var4[var5] = new double[var7 * var9];
            var5++;
         }
      }

      return var4;
   }

   private static sD d(int var0, int var1) {
      return new sD(var0, var1);
   }

   private sD j() {
      sD var1 = new sD(this.e, this.f);

      for (int var2 = 0; var2 < this.a.length; var2++) {
         System.arraycopy(this.a[var2], 0, var1.a[var2], 0, this.a[var2].length);
      }

      return var1;
   }

   private sD e(tq var1) {
      try {
         sD var16 = (sD)var1;
         sD var15 = this;
         td.a(this, var16);
         sD var17 = new sD(var15.e, var15.f);

         for (int var18 = 0; var18 < var17.a.length; var18++) {
            double[] var19 = var17.a[var18];
            double[] var20 = var15.a[var18];
            double[] var22 = var16.a[var18];

            for (int var23 = 0; var23 < var19.length; var23++) {
               var19[var23] = var20[var23] + var22[var23];
            }
         }

         return var17;
      } catch (ClassCastException var14) {
         td.a(this, var1);
         sD var2 = new sD(this.e, this.f);
         int var3 = 0;

         for (int var4 = 0; var4 < var2.g; var4++) {
            for (int var5 = 0; var5 < var2.b; var5++) {
               double[] var6 = var2.a[var3];
               double[] var7 = this.a[var3];
               int var8;
               int var9 = BY.a((var8 = var4 * 52) + 52, this.e);
               int var10;
               int var11 = BY.a((var10 = var5 * 52) + 52, this.f);
               int var12 = 0;

               for (int var21 = var8; var21 < var9; var21++) {
                  for (int var13 = var10; var13 < var11; var13++) {
                     var6[var12] = var7[var12] + var1.b(var21, var13);
                     var12++;
                  }
               }

               var3++;
            }
         }

         return var2;
      }
   }

   private sD a(sD var1) {
      td.a(this, var1);
      sD var2 = new sD(this.e, this.f);

      for (int var3 = 0; var3 < var2.a.length; var3++) {
         double[] var4 = var2.a[var3];
         double[] var5 = this.a[var3];
         double[] var6 = var1.a[var3];

         for (int var7 = 0; var7 < var4.length; var7++) {
            var4[var7] = var5[var7] + var6[var7];
         }
      }

      return var2;
   }

   private sD f(tq var1) {
      try {
         sD var16 = (sD)var1;
         sD var15 = this;
         td.a(this, var16);
         sD var17 = new sD(var15.e, var15.f);

         for (int var18 = 0; var18 < var17.a.length; var18++) {
            double[] var19 = var17.a[var18];
            double[] var20 = var15.a[var18];
            double[] var22 = var16.a[var18];

            for (int var23 = 0; var23 < var19.length; var23++) {
               var19[var23] = var20[var23] - var22[var23];
            }
         }

         return var17;
      } catch (ClassCastException var14) {
         td.a(this, var1);
         sD var2 = new sD(this.e, this.f);
         int var3 = 0;

         for (int var4 = 0; var4 < var2.g; var4++) {
            for (int var5 = 0; var5 < var2.b; var5++) {
               double[] var6 = var2.a[var3];
               double[] var7 = this.a[var3];
               int var8;
               int var9 = BY.a((var8 = var4 * 52) + 52, this.e);
               int var10;
               int var11 = BY.a((var10 = var5 * 52) + 52, this.f);
               int var12 = 0;

               for (int var21 = var8; var21 < var9; var21++) {
                  for (int var13 = var10; var13 < var11; var13++) {
                     var6[var12] = var7[var12] - var1.b(var21, var13);
                     var12++;
                  }
               }

               var3++;
            }
         }

         return var2;
      }
   }

   private sD b(sD var1) {
      td.a(this, var1);
      sD var2 = new sD(this.e, this.f);

      for (int var3 = 0; var3 < var2.a.length; var3++) {
         double[] var4 = var2.a[var3];
         double[] var5 = this.a[var3];
         double[] var6 = var1.a[var3];

         for (int var7 = 0; var7 < var4.length; var7++) {
            var4[var7] = var5[var7] - var6[var7];
         }
      }

      return var2;
   }

   private sD c(double var1) {
      sD var3 = new sD(this.e, this.f);

      for (int var4 = 0; var4 < var3.a.length; var4++) {
         double[] var5 = var3.a[var4];
         double[] var6 = this.a[var4];

         for (int var7 = 0; var7 < var5.length; var7++) {
            var5[var7] = var6[var7] + var1;
         }
      }

      return var3;
   }

   @Override
   public final tq b(double var1) {
      sD var3 = new sD(this.e, this.f);

      for (int var4 = 0; var4 < var3.a.length; var4++) {
         double[] var5 = var3.a[var4];
         double[] var6 = this.a[var4];

         for (int var7 = 0; var7 < var5.length; var7++) {
            var5[var7] = var6[var7] * var1;
         }
      }

      return var3;
   }

   private sD g(tq var1) {
      try {
         sD var50 = (sD)var1;
         sD var49 = this;
         td.b(this, var50);
         sD var51 = new sD(var49.e, var50.f);
         int var52 = 0;

         for (int var53 = 0; var53 < var51.g; var53++) {
            int var54;
            int var55 = BY.a((var54 = var53 * 52) + 52, var49.e);

            for (int var56 = 0; var56 < var51.b; var56++) {
               int var57;
               int var58;
               int var59;
               int var60 = (var59 = (var58 = (var57 = var51.h(var56)) + var57) + var57) + var57;
               double[] var61 = var51.a[var52];

               for (int var62 = 0; var62 < var49.b; var62++) {
                  int var63 = var49.h(var62);
                  double[] var64 = var49.a[var53 * var49.b + var62];
                  double[] var65 = var50.a[var62 * var50.b + var56];
                  int var66 = 0;

                  for (int var67 = var54; var67 < var55; var67++) {
                     int var21;
                     int var68 = (var21 = (var67 - var54) * var63) + var63;

                     for (int var69 = 0; var69 < var57; var69++) {
                        double var46 = 0.0;
                        int var24 = var21;

                        int var25;
                        for (var25 = var69; var24 < var68 - 3; var25 += var60) {
                           var46 += var64[var24] * var65[var25]
                              + var64[var24 + 1] * var65[var25 + var57]
                              + var64[var24 + 2] * var65[var25 + var58]
                              + var64[var24 + 3] * var65[var25 + var59];
                           var24 += 4;
                        }

                        while (var24 < var68) {
                           var46 += var64[var24++] * var65[var25];
                           var25 += var57;
                        }

                        var61[var66] += var46;
                        var66++;
                     }
                  }
               }

               var52++;
            }
         }

         return var51;
      } catch (ClassCastException var48) {
         td.b(this, var1);
         sD var2 = new sD(this.e, var1.g());
         int var3 = 0;

         for (int var4 = 0; var4 < var2.g; var4++) {
            int var5;
            int var6 = BY.a((var5 = var4 * 52) + 52, this.e);

            for (int var7 = 0; var7 < var2.b; var7++) {
               int var8;
               int var9 = BY.a((var8 = var7 * 52) + 52, var1.g());
               double[] var10 = var2.a[var3];

               for (int var11 = 0; var11 < this.b; var11++) {
                  int var12 = this.h(var11);
                  double[] var13 = this.a[var4 * this.b + var11];
                  int var14 = var11 * 52;
                  int var15 = 0;

                  for (int var16 = var5; var16 < var6; var16++) {
                     int var17;
                     int var18 = (var17 = (var16 - var5) * var12) + var12;

                     for (int var19 = var8; var19 < var9; var19++) {
                        double var20 = 0.0;
                        int var22 = var14;

                        for (int var23 = var17; var23 < var18; var23++) {
                           var20 += var13[var23] * var1.b(var22, var19);
                           var22++;
                        }

                        var10[var15] += var20;
                        var15++;
                     }
                  }
               }

               var3++;
            }
         }

         return var2;
      }
   }

   private sD c(sD var1) {
      td.b(this, var1);
      sD var2 = new sD(this.e, var1.f);
      int var3 = 0;

      for (int var4 = 0; var4 < var2.g; var4++) {
         int var5;
         int var6 = BY.a((var5 = var4 * 52) + 52, this.e);

         for (int var7 = 0; var7 < var2.b; var7++) {
            int var8;
            int var9;
            int var10;
            int var11 = (var10 = (var9 = (var8 = var2.h(var7)) + var8) + var8) + var8;
            double[] var12 = var2.a[var3];

            for (int var13 = 0; var13 < this.b; var13++) {
               int var14 = this.h(var13);
               double[] var15 = this.a[var4 * this.b + var13];
               double[] var16 = var1.a[var13 * var1.b + var7];
               int var17 = 0;

               for (int var18 = var5; var18 < var6; var18++) {
                  int var19;
                  int var20 = (var19 = (var18 - var5) * var14) + var14;

                  for (int var21 = 0; var21 < var8; var21++) {
                     double var22 = 0.0;
                     int var24 = var19;

                     int var25;
                     for (var25 = var21; var24 < var20 - 3; var25 += var11) {
                        var22 += var15[var24] * var16[var25]
                           + var15[var24 + 1] * var16[var25 + var8]
                           + var15[var24 + 2] * var16[var25 + var9]
                           + var15[var24 + 3] * var16[var25 + var10];
                        var24 += 4;
                     }

                     while (var24 < var20) {
                        var22 += var15[var24++] * var16[var25];
                        var25 += var8;
                     }

                     var12[var17] += var22;
                     var17++;
                  }
               }
            }

            var3++;
         }
      }

      return var2;
   }

   @Override
   public final double[][] a() {
      double[][] var1 = new double[this.e][this.f];
      int var2 = this.f - (this.b - 1) * 52;

      for (int var3 = 0; var3 < this.g; var3++) {
         int var4;
         int var5 = BY.a((var4 = var3 * 52) + 52, this.e);
         byte var6 = 0;
         int var7 = 0;

         for (int var12 = var4; var12 < var5; var12++) {
            double[] var8 = var1[var12];
            int var9 = var3 * this.b;
            byte var10 = 0;

            for (int var11 = 0; var11 < this.b - 1; var11++) {
               System.arraycopy(this.a[var9++], var6, var8, var10, 52);
               var10 += 52;
            }

            System.arraycopy(this.a[var9], var7, var8, var10, var2);
            var6 += 52;
            var7 += var2;
         }
      }

      return var1;
   }

   @Override
   public final double b() {
      double[] var1 = new double[52];
      double var2 = 0.0;

      for (int var4 = 0; var4 < this.b; var4++) {
         int var5 = this.h(var4);
         Arrays.fill(var1, 0, var5, 0.0);

         for (int var6 = 0; var6 < this.g; var6++) {
            int var7 = this.k(var6);
            double[] var8 = this.a[var6 * this.b + var4];

            for (int var9 = 0; var9 < var5; var9++) {
               double var10 = 0.0;

               for (int var12 = 0; var12 < var7; var12++) {
                  var10 += BY.w(var8[var12 * var5 + var9]);
               }

               var1[var9] += var10;
            }
         }

         for (int var13 = 0; var13 < var5; var13++) {
            var2 = BY.e(var2, var1[var13]);
         }
      }

      return var2;
   }

   @Override
   public final double c() {
      double var1 = 0.0;

      for (int var3 = 0; var3 < this.a.length; var3++) {
         double[] var4;
         int var5 = (var4 = this.a[var3]).length;

         for (int var6 = 0; var6 < var5; var6++) {
            double var7 = var4[var6];
            var1 += var7 * var7;
         }
      }

      return BY.a(var1);
   }

   private sD b(int var1, int var2, int var3, int var4) {
      td.a(this, var1, var2, var3, var4);
      sD var18 = new sD(var2 - var1 + 1, var4 - var3 + 1);
      var4 = var1 / 52;
      var1 %= 52;
      int var5 = var3 / 52;
      var3 %= 52;
      var4 = var4;

      for (int var6 = 0; var6 < var18.g; var6++) {
         int var7 = var18.k(var6);
         int var8 = var5;

         for (int var9 = 0; var9 < var18.b; var9++) {
            int var10 = var18.h(var9);
            int var11 = var6 * var18.b + var9;
            double[] var22 = var18.a[var11];
            int var12 = var4 * this.b + var8;
            int var13 = this.h(var8);
            int var14 = var7 + var1 - 52;
            int var15 = var10 + var3 - 52;
            if (var14 > 0) {
               if (var15 > 0) {
                  int var16 = this.h(var8 + 1);
                  a(this.a[var12], var13, var1, 52, var3, 52, var22, var10, 0, 0);
                  a(this.a[var12 + 1], var16, var1, 52, 0, var15, var22, var10, 0, var10 - var15);
                  a(this.a[var12 + this.b], var13, 0, var14, var3, 52, var22, var10, var7 - var14, 0);
                  a(this.a[var12 + this.b + 1], var16, 0, var14, 0, var15, var22, var10, var7 - var14, var10 - var15);
               } else {
                  a(this.a[var12], var13, var1, 52, var3, var10 + var3, var22, var10, 0, 0);
                  a(this.a[var12 + this.b], var13, 0, var14, var3, var10 + var3, var22, var10, var7 - var14, 0);
               }
            } else if (var15 > 0) {
               int var23 = this.h(var8 + 1);
               a(this.a[var12], var13, var1, var7 + var1, var3, 52, var22, var10, 0, 0);
               a(this.a[var12 + 1], var23, var1, var7 + var1, 0, var15, var22, var10, 0, var10 - var15);
            } else {
               a(this.a[var12], var13, var1, var7 + var1, var3, var10 + var3, var22, var10, 0, 0);
            }

            var8++;
         }

         var4++;
      }

      return var18;
   }

   private static void a(double[] var0, int var1, int var2, int var3, int var4, int var5, double[] var6, int var7, int var8, int var9) {
      var5 -= var4;
      var4 = var2 * var1 + var4;
      var8 = var8 * var7 + var9;

      for (int var10 = var2; var10 < var3; var10++) {
         System.arraycopy(var0, var4, var6, var8, var5);
         var4 += var1;
         var8 += var7;
      }
   }

   @Override
   public final void a(double[][] var1, int var2, int var3) {
      Ci.a(var1);
      int var4;
      if ((var4 = var1[0].length) == 0) {
         throw new pf(px.AT_LEAST_ONE_COLUMN);
      }

      int var5 = var2 + var1.length - 1;
      int var6 = var3 + var4 - 1;
      td.a(this, var2, var5, var3, var6);
      double[][] var7 = var1;
      int var8 = var1.length;

      for (int var9 = 0; var9 < var8; var9++) {
         double[] var10;
         if ((var10 = var7[var9]).length != var4) {
            throw new oS(var4, var10.length);
         }
      }

      int var21 = var2 / 52;
      var8 = (var5 + 52) / 52;
      int var25 = var3 / 52;
      int var26 = (var6 + 52) / 52;

      for (int var20 = var21; var20 < var8; var20++) {
         int var22 = this.k(var20);
         int var11 = var20 * 52;
         int var12 = BY.b(var2, var11);
         int var23 = BY.a(var5 + 1, var11 + var22);

         for (int var13 = var25; var13 < var26; var13++) {
            int var14 = this.h(var13);
            int var15 = var13 * 52;
            int var16 = BY.b(var3, var15);
            int var17 = BY.a(var6 + 1, var15 + var14) - var16;
            double[] var18 = this.a[var20 * this.b + var13];

            for (int var19 = var12; var19 < var23; var19++) {
               System.arraycopy(var1[var19 - var2], var16 - var3, var18, (var19 - var11) * var14 + (var16 - var15), var17);
            }
         }
      }
   }

   private sD i(int var1) {
      td.a(this, var1);
      sD var2 = new sD(1, this.f);
      int var3 = var1 / 52;
      var1 -= var3 * 52;
      int var4 = 0;
      int var5 = 0;
      double[] var6 = var2.a[0];

      for (int var7 = 0; var7 < this.b; var7++) {
         int var8 = this.h(var7);
         double[] var9 = this.a[var3 * this.b + var7];
         int var10 = var6.length - var5;
         if (var8 > var10) {
            System.arraycopy(var9, var1 * var8, var6, var5, var10);
            var6 = var2.a[++var4];
            System.arraycopy(var9, var1 * var8, var6, 0, var8 - var10);
            var5 = var8 - var10;
         } else {
            System.arraycopy(var9, var1 * var8, var6, var5, var8);
            var5 += var8;
         }
      }

      return var2;
   }

   @Override
   public final void a(int var1, tq var2) {
      try {
         sD var5 = (sD)var2;
         int var4 = var1;
         sD var3 = this;
         td.a(this, var4);
         int var6 = var3.f;
         if (var5.e == 1 && var5.f == var6) {
            var6 = var4 / 52;
            var4 -= var6 * 52;
            int var7 = 0;
            int var8 = 0;
            double[] var9 = var5.a[0];

            for (int var10 = 0; var10 < var3.b; var10++) {
               int var11 = var3.h(var10);
               double[] var12 = var3.a[var6 * var3.b + var10];
               int var13 = var9.length - var8;
               if (var11 > var13) {
                  System.arraycopy(var9, var8, var12, var4 * var11, var13);
                  System.arraycopy(var9 = var5.a[++var7], 0, var12, var4 * var11, var11 - var13);
                  var8 = var11 - var13;
               } else {
                  System.arraycopy(var9, var8, var12, var4 * var11, var11);
                  var8 += var11;
               }
            }
         } else {
            throw new tc(var5.e, var5.f, 1, var6);
         }
      } catch (ClassCastException var14) {
         super.a(var1, var2);
      }
   }

   private void a(int var1, sD var2) {
      td.a(this, var1);
      int var3 = this.f;
      if (var2.e == 1 && var2.f == var3) {
         var3 = var1 / 52;
         var1 -= var3 * 52;
         int var4 = 0;
         int var5 = 0;
         double[] var6 = var2.a[0];

         for (int var7 = 0; var7 < this.b; var7++) {
            int var8 = this.h(var7);
            double[] var9 = this.a[var3 * this.b + var7];
            int var10 = var6.length - var5;
            if (var8 > var10) {
               System.arraycopy(var6, var5, var9, var1 * var8, var10);
               System.arraycopy(var6 = var2.a[++var4], 0, var9, var1 * var8, var8 - var10);
               var5 = var8 - var10;
            } else {
               System.arraycopy(var6, var5, var9, var1 * var8, var8);
               var5 += var8;
            }
         }
      } else {
         throw new tc(var2.e, var2.f, 1, var3);
      }
   }

   private sD j(int var1) {
      td.b(this, var1);
      sD var2 = new sD(this.e, 1);
      int var3 = var1 / 52;
      var1 -= var3 * 52;
      int var4 = this.h(var3);
      int var5 = 0;
      int var6 = 0;
      double[] var7 = var2.a[0];

      for (int var8 = 0; var8 < this.g; var8++) {
         int var9 = this.k(var8);
         double[] var10 = this.a[var8 * this.b + var3];

         for (int var11 = 0; var11 < var9; var11++) {
            if (var6 >= var7.length) {
               var7 = var2.a[++var5];
               var6 = 0;
            }

            var7[var6++] = var10[var11 * var4 + var1];
         }
      }

      return var2;
   }

   @Override
   public final void b(int var1, tq var2) {
      try {
         sD var5 = (sD)var2;
         int var4 = var1;
         sD var3 = this;
         td.b(this, var4);
         int var6 = var3.e;
         if (var5.e == var6 && var5.f == 1) {
            var6 = var4 / 52;
            var4 -= var6 * 52;
            int var7 = var3.h(var6);
            int var8 = 0;
            int var9 = 0;
            double[] var10 = var5.a[0];

            for (int var11 = 0; var11 < var3.g; var11++) {
               int var12 = var3.k(var11);
               double[] var13 = var3.a[var11 * var3.b + var6];

               for (int var14 = 0; var14 < var12; var14++) {
                  if (var9 >= var10.length) {
                     var10 = var5.a[++var8];
                     var9 = 0;
                  }

                  var13[var14 * var7 + var4] = var10[var9++];
               }
            }
         } else {
            throw new tc(var5.e, var5.f, var6, 1);
         }
      } catch (ClassCastException var15) {
         super.b(var1, var2);
      }
   }

   private void b(int var1, sD var2) {
      td.b(this, var1);
      int var3 = this.e;
      if (var2.e == var3 && var2.f == 1) {
         var3 = var1 / 52;
         var1 -= var3 * 52;
         int var4 = this.h(var3);
         int var5 = 0;
         int var6 = 0;
         double[] var7 = var2.a[0];

         for (int var8 = 0; var8 < this.g; var8++) {
            int var9 = this.k(var8);
            double[] var10 = this.a[var8 * this.b + var3];

            for (int var11 = 0; var11 < var9; var11++) {
               if (var6 >= var7.length) {
                  var7 = var2.a[++var5];
                  var6 = 0;
               }

               var10[var11 * var4 + var1] = var7[var6++];
            }
         }
      } else {
         throw new tc(var2.e, var2.f, var3, 1);
      }
   }

   @Override
   public final sA d(int var1) {
      td.a(this, var1);
      double[] var2 = new double[this.f];
      int var3 = var1 / 52;
      var1 -= var3 * 52;
      int var4 = 0;

      for (int var5 = 0; var5 < this.b; var5++) {
         int var6 = this.h(var5);
         double[] var7;
         System.arraycopy(var7 = this.a[var3 * this.b + var5], var1 * var6, var2, var4, var6);
         var4 += var6;
      }

      return new sA(var2, false);
   }

   @Override
   public final void a(int var1, tu var2) {
      try {
         sA var6;
         double[] var5 = (var6 = (sA)var2).a;
         int var4 = var1;
         sD var3 = this;
         td.a(this, var4);
         int var13 = var3.f;
         if (var5.length != var13) {
            throw new tc(1, var5.length, 1, var13);
         }

         int var14 = var4 / 52;
         var4 -= var14 * 52;
         int var7 = 0;

         for (int var8 = 0; var8 < var3.b; var8++) {
            int var9 = var3.h(var8);
            double[] var10 = var3.a[var14 * var3.b + var8];
            System.arraycopy(var5, var7, var10, var4 * var9, var9);
            var7 += var9;
         }
      } catch (ClassCastException var11) {
         super.a(var1, var2);
      }
   }

   @Override
   public final sA e(int var1) {
      td.b(this, var1);
      double[] var2 = new double[this.e];
      int var3 = var1 / 52;
      var1 -= var3 * 52;
      int var4 = this.h(var3);
      int var5 = 0;

      for (int var6 = 0; var6 < this.g; var6++) {
         int var7 = this.k(var6);
         double[] var8 = this.a[var6 * this.b + var3];

         for (int var9 = 0; var9 < var7; var9++) {
            var2[var5++] = var8[var9 * var4 + var1];
         }
      }

      return new sA(var2, false);
   }

   @Override
   public final void b(int var1, tu var2) {
      try {
         sA var3;
         this.b(var1, (var3 = (sA)var2).a);
      } catch (ClassCastException var4) {
         super.b(var1, var2);
      }
   }

   @Override
   public final double[] f(int var1) {
      td.a(this, var1);
      double[] var2 = new double[this.f];
      int var3 = var1 / 52;
      var1 -= var3 * 52;
      int var4 = 0;

      for (int var5 = 0; var5 < this.b; var5++) {
         int var6 = this.h(var5);
         double[] var7;
         System.arraycopy(var7 = this.a[var3 * this.b + var5], var1 * var6, var2, var4, var6);
         var4 += var6;
      }

      return var2;
   }

   @Override
   public final void a(int var1, double[] var2) {
      td.a(this, var1);
      int var3 = this.f;
      if (var2.length != var3) {
         throw new tc(1, var2.length, 1, var3);
      }

      var3 = var1 / 52;
      var1 -= var3 * 52;
      int var4 = 0;

      for (int var5 = 0; var5 < this.b; var5++) {
         int var6 = this.h(var5);
         double[] var7 = this.a[var3 * this.b + var5];
         System.arraycopy(var2, var4, var7, var1 * var6, var6);
         var4 += var6;
      }
   }

   @Override
   public final double[] g(int var1) {
      td.b(this, var1);
      double[] var2 = new double[this.e];
      int var3 = var1 / 52;
      var1 -= var3 * 52;
      int var4 = this.h(var3);
      int var5 = 0;

      for (int var6 = 0; var6 < this.g; var6++) {
         int var7 = this.k(var6);
         double[] var8 = this.a[var6 * this.b + var3];

         for (int var9 = 0; var9 < var7; var9++) {
            var2[var5++] = var8[var9 * var4 + var1];
         }
      }

      return var2;
   }

   @Override
   public final void b(int var1, double[] var2) {
      td.b(this, var1);
      int var3 = this.e;
      if (var2.length != var3) {
         throw new tc(var2.length, 1, var3, 1);
      }

      var3 = var1 / 52;
      var1 -= var3 * 52;
      int var4 = this.h(var3);
      int var5 = 0;

      for (int var6 = 0; var6 < this.g; var6++) {
         int var7 = this.k(var6);
         double[] var8 = this.a[var6 * this.b + var3];

         for (int var9 = 0; var9 < var7; var9++) {
            var8[var9 * var4 + var1] = var2[var5++];
         }
      }
   }

   @Override
   public final double b(int var1, int var2) {
      td.a(this, var1, var2);
      int var3 = var1 / 52;
      int var4 = var2 / 52;
      var1 = (var1 - var3 * 52) * this.h(var4) + (var2 - var4 * 52);
      return this.a[var3 * this.b + var4][var1];
   }

   @Override
   public final void c(int var1, int var2, double var3) {
      td.a(this, var1, var2);
      int var5 = var1 / 52;
      int var6 = var2 / 52;
      var1 = (var1 - var5 * 52) * this.h(var6) + (var2 - var6 * 52);
      this.a[var5 * this.b + var6][var1] = var3;
   }

   @Override
   public final void a(int var1, int var2, double var3) {
      td.a(this, var1, var2);
      int var5 = var1 / 52;
      int var6 = var2 / 52;
      var1 = (var1 - var5 * 52) * this.h(var6) + (var2 - var6 * 52);
      this.a[var5 * this.b + var6][var1] = this.a[var5 * this.b + var6][var1] + var3;
   }

   @Override
   public final void b(int var1, int var2, double var3) {
      td.a(this, var1, var2);
      int var5 = var1 / 52;
      int var6 = var2 / 52;
      var1 = (var1 - var5 * 52) * this.h(var6) + (var2 - var6 * 52);
      this.a[var5 * this.b + var6][var1] = this.a[var5 * this.b + var6][var1] * var3;
   }

   private sD k() {
      int var1 = this.e;
      int var2 = this.f;
      sD var16 = new sD(var2, var1);
      var2 = 0;

      for (int var3 = 0; var3 < this.b; var3++) {
         for (int var4 = 0; var4 < this.g; var4++) {
            double[] var5 = var16.a[var2];
            double[] var6 = this.a[var4 * this.b + var3];
            int var7;
            int var8 = BY.a((var7 = var3 * 52) + 52, this.f);
            int var9;
            int var10 = BY.a((var9 = var4 * 52) + 52, this.e);
            int var11 = 0;

            for (int var12 = var7; var12 < var8; var12++) {
               int var13 = var8 - var7;
               int var14 = var12 - var7;

               for (int var15 = var9; var15 < var10; var15++) {
                  var5[var11] = var6[var14];
                  var11++;
                  var14 += var13;
               }
            }

            var2++;
         }
      }

      return var16;
   }

   @Override
   public final int f() {
      return this.e;
   }

   @Override
   public final int g() {
      return this.f;
   }

   @Override
   public final double[] a(double[] var1) {
      if (var1.length != this.f) {
         throw new oS(var1.length, this.f);
      }

      double[] var2 = new double[this.e];

      for (int var3 = 0; var3 < this.g; var3++) {
         int var4;
         int var5 = BY.a((var4 = var3 * 52) + 52, this.e);

         for (int var6 = 0; var6 < this.b; var6++) {
            double[] var7 = this.a[var3 * this.b + var6];
            int var8;
            int var9 = BY.a((var8 = var6 * 52) + 52, this.f);
            int var10 = 0;

            for (int var11 = var4; var11 < var5; var11++) {
               double var12 = 0.0;

               int var14;
               for (var14 = var8; var14 < var9 - 3; var14 += 4) {
                  var12 += var7[var10] * var1[var14]
                     + var7[var10 + 1] * var1[var14 + 1]
                     + var7[var10 + 2] * var1[var14 + 2]
                     + var7[var10 + 3] * var1[var14 + 3];
                  var10 += 4;
               }

               while (var14 < var9) {
                  var12 += var7[var10++] * var1[var14++];
               }

               var2[var11] += var12;
            }
         }
      }

      return var2;
   }

   @Override
   public final double[] b(double[] var1) {
      if (var1.length != this.e) {
         throw new oS(var1.length, this.e);
      }

      double[] var2 = new double[this.f];

      for (int var3 = 0; var3 < this.b; var3++) {
         int var4;
         int var5;
         int var6;
         int var7 = (var6 = (var5 = (var4 = this.h(var3)) + var4) + var4) + var4;
         int var8;
         int var9 = BY.a((var8 = var3 * 52) + 52, this.f);

         for (int var10 = 0; var10 < this.g; var10++) {
            double[] var11 = this.a[var10 * this.b + var3];
            int var12;
            int var13 = BY.a((var12 = var10 * 52) + 52, this.e);

            for (int var14 = var8; var14 < var9; var14++) {
               int var15 = var14 - var8;
               double var16 = 0.0;

               int var18;
               for (var18 = var12; var18 < var13 - 3; var18 += 4) {
                  var16 += var11[var15] * var1[var18]
                     + var11[var15 + var4] * var1[var18 + 1]
                     + var11[var15 + var5] * var1[var18 + 2]
                     + var11[var15 + var6] * var1[var18 + 3];
                  var15 += var7;
               }

               while (var18 < var13) {
                  var16 += var11[var15] * var1[var18++];
                  var15 += var4;
               }

               var2[var14] += var16;
            }
         }
      }

      return var2;
   }

   @Override
   public final double a(tr var1) {
      for (int var2 = 0; var2 < this.g; var2++) {
         int var3;
         int var4 = BY.a((var3 = var2 * 52) + 52, this.e);

         for (int var5 = var3; var5 < var4; var5++) {
            for (int var6 = 0; var6 < this.b; var6++) {
               int var7 = this.h(var6);
               int var8;
               int var9 = BY.a((var8 = var6 * 52) + 52, this.f);
               double[] var10 = this.a[var2 * this.b + var6];
               var7 = (var5 - var3) * var7;

               for (int var12 = var8; var12 < var9; var12++) {
                  var10[var7] = var1.a(var5, var12, var10[var7]);
                  var7++;
               }
            }
         }
      }

      return var1.b();
   }

   @Override
   public final double a(tt var1) {
      var1.a(0, this.e - 1, 0);

      for (int var2 = 0; var2 < this.g; var2++) {
         int var3;
         int var4 = BY.a((var3 = var2 * 52) + 52, this.e);

         for (int var5 = var3; var5 < var4; var5++) {
            for (int var6 = 0; var6 < this.b; var6++) {
               int var7 = this.h(var6);
               int var8;
               int var9 = BY.a((var8 = var6 * 52) + 52, this.f);
               double[] var10 = this.a[var2 * this.b + var6];
               var7 = (var5 - var3) * var7;

               for (int var12 = var8; var12 < var9; var12++) {
                  var1.a(var5, var12, var10[var7]);
                  var7++;
               }
            }
         }
      }

      return var1.a();
   }

   @Override
   public final double a(tr var1, int var2, int var3, int var4, int var5) {
      td.a(this, var2, var3, var4, var5);

      for (int var6 = var2 / 52; var6 < 1 + var3 / 52; var6++) {
         int var7 = var6 * 52;
         int var8 = BY.b(var2, var7);
         int var9 = BY.a((var6 + 1) * 52, var3 + 1);

         for (int var16 = var8; var16 < var9; var16++) {
            for (int var10 = var4 / 52; var10 < 1 + var5 / 52; var10++) {
               int var11 = this.h(var10);
               int var12 = var10 * 52;
               int var13 = BY.b(var4, var12);
               int var14 = BY.a((var10 + 1) * 52, var5 + 1);
               double[] var15 = this.a[var6 * this.b + var10];
               var11 = (var16 - var7) * var11 + var13 - var12;

               for (int var18 = var13; var18 < var14; var18++) {
                  var15[var11] = var1.a(var16, var18, var15[var11]);
                  var11++;
               }
            }
         }
      }

      return var1.b();
   }

   @Override
   public final double a(tt var1, int var2, int var3, int var4, int var5) {
      td.a(this, var2, var3, var4, var5);
      var1.a(var2, var3, var4);

      for (int var6 = var2 / 52; var6 < 1 + var3 / 52; var6++) {
         int var7 = var6 * 52;
         int var8 = BY.b(var2, var7);
         int var9 = BY.a((var6 + 1) * 52, var3 + 1);

         for (int var16 = var8; var16 < var9; var16++) {
            for (int var10 = var4 / 52; var10 < 1 + var5 / 52; var10++) {
               int var11 = this.h(var10);
               int var12 = var10 * 52;
               int var13 = BY.b(var4, var12);
               int var14 = BY.a((var10 + 1) * 52, var5 + 1);
               double[] var15 = this.a[var6 * this.b + var10];
               var11 = (var16 - var7) * var11 + var13 - var12;

               for (int var18 = var13; var18 < var14; var18++) {
                  var1.a(var16, var18, var15[var11]);
                  var11++;
               }
            }
         }
      }

      return var1.a();
   }

   @Override
   public final double a(<unrepresentable> var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < this.g; var3++) {
         int var4;
         int var5 = BY.a((var4 = var3 * 52) + 52, this.e);

         for (int var6 = 0; var6 < this.b; var6++) {
            int var7;
            int var8 = BY.a((var7 = var6 * 52) + 52, this.f);
            double[] var9 = this.a[var2];
            int var10 = 0;

            for (int var11 = var4; var11 < var5; var11++) {
               for (int var12 = var7; var12 < var8; var12++) {
                  var9[var10] = var1.a(var11, var12, var9[var10]);
                  var10++;
               }
            }

            var2++;
         }
      }

      return var1.b();
   }

   @Override
   public final double b(tt var1) {
      var1.a(0, this.e - 1, 0);
      int var2 = 0;

      for (int var3 = 0; var3 < this.g; var3++) {
         int var4;
         int var5 = BY.a((var4 = var3 * 52) + 52, this.e);

         for (int var6 = 0; var6 < this.b; var6++) {
            int var7;
            int var8 = BY.a((var7 = var6 * 52) + 52, this.f);
            double[] var9 = this.a[var2];
            int var10 = 0;

            for (int var11 = var4; var11 < var5; var11++) {
               for (int var12 = var7; var12 < var8; var12++) {
                  var1.a(var11, var12, var9[var10]);
                  var10++;
               }
            }

            var2++;
         }
      }

      return var1.a();
   }

   @Override
   public final double c(tr var1, int var2, int var3, int var4, int var5) {
      td.a(this, var2, var3, var4, var5);

      for (int var6 = var2 / 52; var6 < 1 + var3 / 52; var6++) {
         int var7 = var6 * 52;
         int var8 = BY.b(var2, var7);
         int var9 = BY.a((var6 + 1) * 52, var3 + 1);

         for (int var10 = var4 / 52; var10 < 1 + var5 / 52; var10++) {
            int var11 = this.h(var10);
            int var12 = var10 * 52;
            int var13 = BY.b(var4, var12);
            int var14 = BY.a((var10 + 1) * 52, var5 + 1);
            double[] var15 = this.a[var6 * this.b + var10];

            for (int var16 = var8; var16 < var9; var16++) {
               int var17 = (var16 - var7) * var11 + var13 - var12;

               for (int var18 = var13; var18 < var14; var18++) {
                  var15[var17] = var1.a(var16, var18, var15[var17]);
                  var17++;
               }
            }
         }
      }

      return var1.b();
   }

   @Override
   public final double a(sL var1, int var2, int var3, int var4, int var5) {
      td.a(this, var2, var3, var4, var5);
      var1.a(var2, var3, var4);

      for (int var6 = var2 / 52; var6 < 1 + var3 / 52; var6++) {
         int var7 = var6 * 52;
         int var8 = BY.b(var2, var7);
         int var9 = BY.a((var6 + 1) * 52, var3 + 1);

         for (int var10 = var4 / 52; var10 < 1 + var5 / 52; var10++) {
            int var11 = this.h(var10);
            int var12 = var10 * 52;
            int var13 = BY.b(var4, var12);
            int var14 = BY.a((var10 + 1) * 52, var5 + 1);
            double[] var15 = this.a[var6 * this.b + var10];

            for (int var16 = var8; var16 < var9; var16++) {
               int var17 = (var16 - var7) * var11 + var13 - var12;

               for (int var18 = var13; var18 < var14; var18++) {
                  var1.a(var16, var18, var15[var17]);
                  var17++;
               }
            }
         }
      }

      return var1.a();
   }

   private int k(int var1) {
      return var1 == this.g - 1 ? this.e - var1 * 52 : 52;
   }

   int h(int var1) {
      return var1 == this.b - 1 ? this.f - var1 * 52 : 52;
   }

   // $VF: synthetic method
   @Override
   public final sv i() {
      sD var1 = this;
      sD var2 = new sD(var1.e, var1.f);

      for (int var3 = 0; var3 < var1.a.length; var3++) {
         System.arraycopy(var1.a[var3], 0, var2.a[var3], 0, var1.a[var3].length);
      }

      return var2;
   }

   // $VF: synthetic method
   @Override
   public final sv a(int var1, int var2) {
      var2 = var2;
      var1 = var1;
      return new sD(var1, var2);
   }

   // $VF: synthetic method
   @Override
   public final tq d() {
      sD var1 = this;
      int var2 = this.e;
      int var3 = var1.f;
      sD var17 = new sD(var3, var2);
      var3 = 0;

      for (int var4 = 0; var4 < var1.b; var4++) {
         for (int var5 = 0; var5 < var1.g; var5++) {
            double[] var6 = var17.a[var3];
            double[] var7 = var1.a[var5 * var1.b + var4];
            int var8;
            int var9 = BY.a((var8 = var4 * 52) + 52, var1.f);
            int var10;
            int var11 = BY.a((var10 = var5 * 52) + 52, var1.e);
            int var12 = 0;

            for (int var13 = var8; var13 < var9; var13++) {
               int var14 = var9 - var8;
               int var15 = var13 - var8;

               for (int var16 = var10; var16 < var11; var16++) {
                  var6[var12] = var7[var15];
                  var12++;
                  var15 += var14;
               }
            }

            var3++;
         }
      }

      return var17;
   }

   // $VF: synthetic method
   @Override
   public final tq c(int var1) {
      int var2 = var1;
      sD var13 = this;
      td.b(this, var2);
      sD var3 = new sD(var13.e, 1);
      int var4 = var2 / 52;
      var2 -= var4 * 52;
      int var5 = var13.h(var4);
      int var6 = 0;
      int var7 = 0;
      double[] var8 = var3.a[0];

      for (int var9 = 0; var9 < var13.g; var9++) {
         int var10 = var13.k(var9);
         double[] var11 = var13.a[var9 * var13.b + var4];

         for (int var12 = 0; var12 < var10; var12++) {
            if (var7 >= var8.length) {
               var8 = var3.a[++var6];
               var7 = 0;
            }

            var8[var7++] = var11[var12 * var5 + var2];
         }
      }

      return var3;
   }

   // $VF: synthetic method
   @Override
   public final tq b(int var1) {
      int var2 = var1;
      sD var12 = this;
      td.a(this, var2);
      sD var3 = new sD(1, var12.f);
      int var4 = var2 / 52;
      var2 -= var4 * 52;
      int var5 = 0;
      int var6 = 0;
      double[] var7 = var3.a[0];

      for (int var8 = 0; var8 < var12.b; var8++) {
         int var9 = var12.h(var8);
         double[] var10 = var12.a[var4 * var12.b + var8];
         int var11 = var7.length - var6;
         if (var9 > var11) {
            System.arraycopy(var10, var2 * var9, var7, var6, var11);
            var7 = var3.a[++var5];
            System.arraycopy(var10, var2 * var9, var7, 0, var9 - var11);
            var6 = var9 - var11;
         } else {
            System.arraycopy(var10, var2 * var9, var7, var6, var9);
            var6 += var9;
         }
      }

      return var3;
   }

   // $VF: synthetic method
   @Override
   public final tq a(int var1, int var2, int var3, int var4) {
      int var5 = var4;
      var4 = var3;
      var3 = var2;
      var2 = var1;
      sD var18 = this;
      td.a(this, var2, var3, var4, var5);
      sD var22 = new sD(var3 - var2 + 1, var5 - var4 + 1);
      var5 = var2 / 52;
      var2 %= 52;
      int var6 = var4 / 52;
      var4 %= 52;
      var5 = var5;

      for (int var7 = 0; var7 < var22.g; var7++) {
         int var8 = var22.k(var7);
         int var9 = var6;

         for (int var10 = 0; var10 < var22.b; var10++) {
            int var11 = var22.h(var10);
            int var12 = var7 * var22.b + var10;
            double[] var27 = var22.a[var12];
            int var13 = var5 * var18.b + var9;
            int var14 = var18.h(var9);
            int var15 = var8 + var2 - 52;
            int var16 = var11 + var4 - 52;
            if (var15 > 0) {
               if (var16 > 0) {
                  int var17 = var18.h(var9 + 1);
                  a(var18.a[var13], var14, var2, 52, var4, 52, var27, var11, 0, 0);
                  a(var18.a[var13 + 1], var17, var2, 52, 0, var16, var27, var11, 0, var11 - var16);
                  a(var18.a[var13 + var18.b], var14, 0, var15, var4, 52, var27, var11, var8 - var15, 0);
                  a(var18.a[var13 + var18.b + 1], var17, 0, var15, 0, var16, var27, var11, var8 - var15, var11 - var16);
               } else {
                  a(var18.a[var13], var14, var2, 52, var4, var11 + var4, var27, var11, 0, 0);
                  a(var18.a[var13 + var18.b], var14, 0, var15, var4, var11 + var4, var27, var11, var8 - var15, 0);
               }
            } else if (var16 > 0) {
               int var28 = var18.h(var9 + 1);
               a(var18.a[var13], var14, var2, var8 + var2, var4, 52, var27, var11, 0, 0);
               a(var18.a[var13 + 1], var28, var2, var8 + var2, 0, var16, var27, var11, 0, var11 - var16);
            } else {
               a(var18.a[var13], var14, var2, var8 + var2, var4, var11 + var4, var27, var11, 0, 0);
            }

            var9++;
         }

         var5++;
      }

      return var22;
   }

   // $VF: synthetic method
   @Override
   public final tq c(tq var1) {
      return this.g(var1);
   }

   // $VF: synthetic method
   @Override
   public final tq a(double var1) {
      double var4 = var1;
      sD var9 = this;
      sD var2 = new sD(var9.e, var9.f);

      for (int var3 = 0; var3 < var2.a.length; var3++) {
         double[] var6 = var2.a[var3];
         double[] var7 = var9.a[var3];

         for (int var8 = 0; var8 < var6.length; var8++) {
            var6[var8] = var7[var8] + var4;
         }
      }

      return var2;
   }

   // $VF: synthetic method
   @Override
   public final tq b(tq var1) {
      return this.f(var1);
   }

   // $VF: synthetic method
   @Override
   public final tq a(tq var1) {
      return this.e(var1);
   }
}
