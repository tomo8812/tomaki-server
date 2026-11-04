package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class rk implements Serializable {
   private static rk e;
   private static final long f = -2153622329907944313L;
   final double a;
   final double b;
   final double c;
   final double d;

   public rk(double var1, double var3, double var5, double var7, boolean var9) {
      this.a = var1;
      this.b = var3;
      this.c = var5;
      this.d = var7;
   }

   @Deprecated
   private rk(rs var1, double var2) {
      this(var1, var2, rl.VECTOR_OPERATOR$59e4743c);
   }

   public rk(rs var1, double var2, int var4) {
      double var5;
      if ((var5 = var1.e()) == 0.0) {
         throw new oV(px.ZERO_NORM_FOR_ROTATION_AXIS);
      }

      double var7;
      double var9 = BY.n(var7 = var4 == rl.VECTOR_OPERATOR$59e4743c ? var2 * -0.5 : var2 * 0.5) / var5;
      this.a = BY.o(var7);
      this.b = var9 * var1.i;
      this.c = var9 * var1.j;
      this.d = var9 * var1.k;
   }

   private rk(double[][] var1, double var2) {
      if (var1.length == 3 && var1[0].length == 3 && var1[1].length == 3 && var1[2].length == 3) {
         double var9 = var2;
         double[][] var77 = var1;
         double[] var81 = var1[0];
         double[] var3 = var77[1];
         double[] var78 = var77[2];
         double var14 = var81[0];
         double var16 = var81[1];
         double var18 = var81[2];
         double var20 = var3[0];
         double var22 = var3[1];
         double var24 = var3[2];
         double var26 = var78[0];
         double var28 = var78[1];
         double var30 = var78[2];
         double var32 = 0.0;
         double[][] var4;
         double[] var5 = (var4 = new double[3][3])[0];
         double[] var6 = var4[1];
         double[] var7 = var4[2];
         int var8 = 0;

         while (++var8 < 11) {
            double var41 = var81[0] * var14 + var3[0] * var20 + var78[0] * var26;
            double var43 = var81[1] * var14 + var3[1] * var20 + var78[1] * var26;
            double var45 = var81[2] * var14 + var3[2] * var20 + var78[2] * var26;
            double var47 = var81[0] * var16 + var3[0] * var22 + var78[0] * var28;
            double var49 = var81[1] * var16 + var3[1] * var22 + var78[1] * var28;
            double var51 = var81[2] * var16 + var3[2] * var22 + var78[2] * var28;
            double var53 = var81[0] * var18 + var3[0] * var24 + var78[0] * var30;
            double var55 = var81[1] * var18 + var3[1] * var24 + var78[1] * var30;
            double var57 = var81[2] * var18 + var3[2] * var24 + var78[2] * var30;
            var5[0] = var14 - 0.5 * (var14 * var41 + var16 * var43 + var18 * var45 - var81[0]);
            var5[1] = var16 - 0.5 * (var14 * var47 + var16 * var49 + var18 * var51 - var81[1]);
            var5[2] = var18 - 0.5 * (var14 * var53 + var16 * var55 + var18 * var57 - var81[2]);
            var6[0] = var20 - 0.5 * (var20 * var41 + var22 * var43 + var24 * var45 - var3[0]);
            var6[1] = var22 - 0.5 * (var20 * var47 + var22 * var49 + var24 * var51 - var3[1]);
            var6[2] = var24 - 0.5 * (var20 * var53 + var22 * var55 + var24 * var57 - var3[2]);
            var7[0] = var26 - 0.5 * (var26 * var41 + var28 * var43 + var30 * var45 - var78[0]);
            var7[1] = var28 - 0.5 * (var26 * var47 + var28 * var49 + var30 * var51 - var78[1]);
            var7[2] = var30 - 0.5 * (var26 * var53 + var28 * var55 + var30 * var57 - var78[2]);
            double var59 = var5[0] - var81[0];
            double var61 = var5[1] - var81[1];
            double var63 = var5[2] - var81[2];
            double var65 = var6[0] - var3[0];
            double var67 = var6[1] - var3[1];
            double var69 = var6[2] - var3[2];
            double var71 = var7[0] - var78[0];
            double var73 = var7[1] - var78[1];
            double var75 = var7[2] - var78[2];
            double var34;
            if (BY.w(
                  (
                        var34 = var59 * var59
                           + var61 * var61
                           + var63 * var63
                           + var65 * var65
                           + var67 * var67
                           + var69 * var69
                           + var71 * var71
                           + var73 * var73
                           + var75 * var75
                     )
                     - var32
               )
               <= var9) {
               var1 = var4;
               double var82;
               if ((
                     var82 = var4[0][0] * (var1[1][1] * var1[2][2] - var1[2][1] * var1[1][2])
                        - var1[1][0] * (var1[0][1] * var1[2][2] - var1[2][1] * var1[0][2])
                        + var1[2][0] * (var1[0][1] * var1[1][2] - var1[1][1] * var1[0][2])
                  )
                  < 0.0) {
                  throw new rg(px.CLOSEST_ORTHOGONAL_MATRIX_HAS_NEGATIVE_DETERMINANT, var82);
               }

               double[] var80 = a(var1);
               this.a = var80[0];
               this.b = var80[1];
               this.c = var80[2];
               this.d = var80[3];
               return;
            }

            var14 = var5[0];
            var16 = var5[1];
            var18 = var5[2];
            var20 = var6[0];
            var22 = var6[1];
            var24 = var6[2];
            var26 = var7[0];
            var28 = var7[1];
            var30 = var7[2];
            var32 = var34;
         }

         throw new rg(px.UNABLE_TO_ORTHOGONOLIZE_MATRIX, var8 - 1);
      } else {
         throw new rg(px.ROTATION_MATRIX_DIMENSIONS, var1.length, var1[0].length);
      }
   }

   private rk(rs var1, rs var2, rs var3, rs var4) {
      rs var5;
      var2 = (var5 = var1.b(var2).o()).b(var1).o();
      var1 = var1.o();
      rs var6;
      var4 = (var6 = var3.b(var4).o()).b(var3).o();
      var3 = var3.o();
      double[] var9 = a(
         new double[][]{
            {
                  Ch.a(var1.i, var3.i, var2.i, var4.i, var5.i, var6.i),
                  Ch.a(var1.j, var3.i, var2.j, var4.i, var5.j, var6.i),
                  Ch.a(var1.k, var3.i, var2.k, var4.i, var5.k, var6.i)
            },
            {
                  Ch.a(var1.i, var3.j, var2.i, var4.j, var5.i, var6.j),
                  Ch.a(var1.j, var3.j, var2.j, var4.j, var5.j, var6.j),
                  Ch.a(var1.k, var3.j, var2.k, var4.j, var5.k, var6.j)
            },
            {
                  Ch.a(var1.i, var3.k, var2.i, var4.k, var5.i, var6.k),
                  Ch.a(var1.j, var3.k, var2.j, var4.k, var5.j, var6.k),
                  Ch.a(var1.k, var3.k, var2.k, var4.k, var5.k, var6.k)
            }
         }
      );
      this.a = var9[0];
      this.b = var9[1];
      this.c = var9[2];
      this.d = var9[3];
   }

   private rk(rs var1, rs var2) {
      double var3;
      if ((var3 = var1.e() * var2.e()) == 0.0) {
         throw new oU(px.ZERO_NORM_FOR_ROTATION_DEFINING_VECTOR);
      }

      double var5;
      if ((var5 = var1.f(var2)) < var3 * -0.999999999999998) {
         rs var10 = var1.p();
         this.a = 0.0;
         this.b = -var10.i;
         this.c = -var10.j;
         this.d = -var10.k;
      } else {
         this.a = BY.a(0.5 * (1.0 + var5 / var3));
         double var7 = 1.0 / (2.0 * this.a * var3);
         var1 = var2.b(var1);
         this.b = var7 * var1.i;
         this.c = var7 * var1.j;
         this.d = var7 * var1.k;
      }
   }

   @Deprecated
   private rk(rm var1, double var2, double var4, double var6) {
      this(var1, rl.VECTOR_OPERATOR$59e4743c, var2, var4, var6);
   }

   private rk(rm var1, int var2, double var3, double var5, double var7) {
      rk var12 = new rk(var1.l, var3, var2);
      rk var4 = new rk(var1.m, var5, var2);
      rk var10 = new rk(var1.n, var7, var2);
      rk var11 = var12.a(var4.a(var10, var2), var2);
      this.a = var11.a;
      this.b = var11.b;
      this.c = var11.c;
      this.d = var11.d;
   }

   private static double[] a(double[][] var0) {
      double[] var1 = new double[4];
      double var2;
      if ((var2 = var0[0][0] + var0[1][1] + var0[2][2]) > -0.19) {
         var1[0] = 0.5 * BY.a(var2 + 1.0);
         double var4 = 0.25 / var1[0];
         var1[1] = var4 * (var0[1][2] - var0[2][1]);
         var1[2] = var4 * (var0[2][0] - var0[0][2]);
         var1[3] = var4 * (var0[0][1] - var0[1][0]);
      } else if ((var2 = var0[0][0] - var0[1][1] - var0[2][2]) > -0.19) {
         var1[1] = 0.5 * BY.a(var2 + 1.0);
         double var9 = 0.25 / var1[1];
         var1[0] = var9 * (var0[1][2] - var0[2][1]);
         var1[2] = var9 * (var0[0][1] + var0[1][0]);
         var1[3] = var9 * (var0[0][2] + var0[2][0]);
      } else if ((var2 = var0[1][1] - var0[0][0] - var0[2][2]) > -0.19) {
         var1[2] = 0.5 * BY.a(var2 + 1.0);
         double var10 = 0.25 / var1[2];
         var1[0] = var10 * (var0[2][0] - var0[0][2]);
         var1[1] = var10 * (var0[0][1] + var0[1][0]);
         var1[3] = var10 * (var0[2][1] + var0[1][2]);
      } else {
         var2 = var0[2][2] - var0[0][0] - var0[1][1];
         var1[3] = 0.5 * BY.a(var2 + 1.0);
         double var11 = 0.25 / var1[3];
         var1[0] = var11 * (var0[0][1] - var0[1][0]);
         var1[1] = var11 * (var0[0][2] + var0[2][0]);
         var1[2] = var11 * (var0[2][1] + var0[1][2]);
      }

      return var1;
   }

   private rk e() {
      return new rk(-this.a, this.b, this.c, this.d, false);
   }

   public final double a() {
      return this.a;
   }

   public final double b() {
      return this.b;
   }

   public final double c() {
      return this.c;
   }

   public final double d() {
      return this.d;
   }

   @Deprecated
   private rs f() {
      int var2 = rl.VECTOR_OPERATOR$59e4743c;
      rk var1 = this;
      double var3;
      rs var10000;
      if ((var3 = this.b * var1.b + var1.c * var1.c + var1.d * var1.d) == 0.0) {
         if (var2 != rl.VECTOR_OPERATOR$59e4743c) {
            return rs.c;
         }

         var10000 = rs.b;
      } else {
         double var5 = var2 == rl.VECTOR_OPERATOR$59e4743c ? 1.0 : -1.0;
         if (var1.a < 0.0) {
            double var9 = var5 / BY.a(var3);
            return new rs(var1.b * var9, var1.c * var9, var1.d * var9);
         }

         double var7 = -var5 / BY.a(var3);
         var10000 = new rs(var1.b * var7, var1.c * var7, var1.d * var7);
      }

      return var10000;
   }

   private rs a(int var1) {
      double var2;
      if ((var2 = this.b * this.b + this.c * this.c + this.d * this.d) == 0.0) {
         return var1 == rl.VECTOR_OPERATOR$59e4743c ? rs.b : rs.c;
      } else {
         double var4 = var1 == rl.VECTOR_OPERATOR$59e4743c ? 1.0 : -1.0;
         if (this.a < 0.0) {
            double var8 = var4 / BY.a(var2);
            return new rs(this.b * var8, this.c * var8, this.d * var8);
         } else {
            double var6 = -var4 / BY.a(var2);
            return new rs(this.b * var6, this.c * var6, this.d * var6);
         }
      }
   }

   private double g() {
      if (this.a < -0.1 || this.a > 0.1) {
         return 2.0 * BY.r(BY.a(this.b * this.b + this.c * this.c + this.d * this.d));
      } else {
         return this.a < 0.0 ? 2.0 * BY.s(-this.a) : 2.0 * BY.s(this.a);
      }
   }

   @Deprecated
   private double[] a(rm var1) {
      int var3 = rl.VECTOR_OPERATOR$59e4743c;
      rm var2 = var1;
      rk var4 = this;
      if (var3 == rl.VECTOR_OPERATOR$59e4743c) {
         if (var2 == rm.a) {
            rs var52 = var4.a(rs.f);
            rs var28;
            if (!((var28 = var4.b(rs.b)).k < -0.9999999999) && !(var28.k > 0.9999999999)) {
               return new double[]{BY.b(-var52.j, var52.k), BY.r(var28.k), BY.b(-var28.j, var28.i)};
            } else {
               throw new rb(true);
            }
         } else if (var2 == rm.b) {
            rs var51 = var4.a(rs.d);
            rs var27;
            if (!((var27 = var4.b(rs.b)).j < -0.9999999999) && !(var27.j > 0.9999999999)) {
               return new double[]{BY.b(var51.k, var51.j), -BY.r(var27.j), BY.b(var27.k, var27.i)};
            } else {
               throw new rb(true);
            }
         } else if (var2 == rm.c) {
            rs var50 = var4.a(rs.f);
            rs var26;
            if (!((var26 = var4.b(rs.d)).k < -0.9999999999) && !(var26.k > 0.9999999999)) {
               return new double[]{BY.b(var50.i, var50.k), -BY.r(var26.k), BY.b(var26.i, var26.j)};
            } else {
               throw new rb(true);
            }
         } else if (var2 == rm.d) {
            rs var49 = var4.a(rs.b);
            rs var25;
            if (!((var25 = var4.b(rs.d)).i < -0.9999999999) && !(var25.i > 0.9999999999)) {
               return new double[]{BY.b(-var49.k, var49.i), BY.r(var25.i), BY.b(-var25.k, var25.j)};
            } else {
               throw new rb(true);
            }
         } else if (var2 == rm.e) {
            rs var48 = var4.a(rs.d);
            rs var24;
            if (!((var24 = var4.b(rs.f)).j < -0.9999999999) && !(var24.j > 0.9999999999)) {
               return new double[]{BY.b(-var48.i, var48.j), BY.r(var24.j), BY.b(-var24.i, var24.k)};
            } else {
               throw new rb(true);
            }
         } else if (var2 == rm.f) {
            rs var47 = var4.a(rs.b);
            rs var23;
            if (!((var23 = var4.b(rs.f)).i < -0.9999999999) && !(var23.i > 0.9999999999)) {
               return new double[]{BY.b(var47.j, var47.i), -BY.r(var23.i), BY.b(var23.j, var23.k)};
            } else {
               throw new rb(true);
            }
         } else if (var2 == rm.g) {
            rs var46 = var4.a(rs.b);
            rs var22;
            if (!((var22 = var4.b(rs.b)).i < -0.9999999999) && !(var22.i > 0.9999999999)) {
               return new double[]{BY.b(var46.j, -var46.k), BY.s(var22.i), BY.b(var22.j, var22.k)};
            } else {
               throw new rb(false);
            }
         } else if (var2 == rm.h) {
            rs var45 = var4.a(rs.b);
            rs var21;
            if (!((var21 = var4.b(rs.b)).i < -0.9999999999) && !(var21.i > 0.9999999999)) {
               return new double[]{BY.b(var45.k, var45.j), BY.s(var21.i), BY.b(var21.k, -var21.j)};
            } else {
               throw new rb(false);
            }
         } else if (var2 == rm.i) {
            rs var44 = var4.a(rs.d);
            rs var20;
            if (!((var20 = var4.b(rs.d)).j < -0.9999999999) && !(var20.j > 0.9999999999)) {
               return new double[]{BY.b(var44.i, var44.k), BY.s(var20.j), BY.b(var20.i, -var20.k)};
            } else {
               throw new rb(false);
            }
         } else if (var2 == rm.j) {
            rs var43 = var4.a(rs.d);
            rs var19;
            if (!((var19 = var4.b(rs.d)).j < -0.9999999999) && !(var19.j > 0.9999999999)) {
               return new double[]{BY.b(var43.k, -var43.i), BY.s(var19.j), BY.b(var19.k, var19.i)};
            } else {
               throw new rb(false);
            }
         } else if (var2 == rm.k) {
            rs var42 = var4.a(rs.f);
            rs var18;
            if (!((var18 = var4.b(rs.f)).k < -0.9999999999) && !(var18.k > 0.9999999999)) {
               return new double[]{BY.b(var42.i, -var42.j), BY.s(var18.k), BY.b(var18.i, var18.j)};
            } else {
               throw new rb(false);
            }
         } else {
            rs var41 = var4.a(rs.f);
            rs var17;
            if (!((var17 = var4.b(rs.f)).k < -0.9999999999) && !(var17.k > 0.9999999999)) {
               return new double[]{BY.b(var41.j, var41.i), BY.s(var17.k), BY.b(var17.j, -var17.i)};
            } else {
               throw new rb(false);
            }
         }
      } else if (var2 == rm.a) {
         rs var40 = var4.a(rs.b);
         rs var16;
         if (!((var16 = var4.b(rs.f)).i < -0.9999999999) && !(var16.i > 0.9999999999)) {
            return new double[]{BY.b(-var16.j, var16.k), BY.r(var16.i), BY.b(-var40.j, var40.i)};
         } else {
            throw new rb(true);
         }
      } else if (var2 == rm.b) {
         rs var39 = var4.a(rs.b);
         rs var15;
         if (!((var15 = var4.b(rs.d)).i < -0.9999999999) && !(var15.i > 0.9999999999)) {
            return new double[]{BY.b(var15.k, var15.j), -BY.r(var15.i), BY.b(var39.k, var39.i)};
         } else {
            throw new rb(true);
         }
      } else if (var2 == rm.c) {
         rs var38 = var4.a(rs.d);
         rs var14;
         if (!((var14 = var4.b(rs.f)).j < -0.9999999999) && !(var14.j > 0.9999999999)) {
            return new double[]{BY.b(var14.i, var14.k), -BY.r(var14.j), BY.b(var38.i, var38.j)};
         } else {
            throw new rb(true);
         }
      } else if (var2 == rm.d) {
         rs var37 = var4.a(rs.d);
         rs var13;
         if (!((var13 = var4.b(rs.b)).j < -0.9999999999) && !(var13.j > 0.9999999999)) {
            return new double[]{BY.b(-var13.k, var13.i), BY.r(var13.j), BY.b(-var37.k, var37.j)};
         } else {
            throw new rb(true);
         }
      } else if (var2 == rm.e) {
         rs var36 = var4.a(rs.f);
         rs var12;
         if (!((var12 = var4.b(rs.d)).k < -0.9999999999) && !(var12.k > 0.9999999999)) {
            return new double[]{BY.b(-var12.i, var12.j), BY.r(var12.k), BY.b(-var36.i, var36.k)};
         } else {
            throw new rb(true);
         }
      } else if (var2 == rm.f) {
         rs var35 = var4.a(rs.f);
         rs var11;
         if (!((var11 = var4.b(rs.b)).k < -0.9999999999) && !(var11.k > 0.9999999999)) {
            return new double[]{BY.b(var11.j, var11.i), -BY.r(var11.k), BY.b(var35.j, var35.k)};
         } else {
            throw new rb(true);
         }
      } else if (var2 == rm.g) {
         rs var34 = var4.a(rs.b);
         rs var10;
         if (!((var10 = var4.b(rs.b)).i < -0.9999999999) && !(var10.i > 0.9999999999)) {
            return new double[]{BY.b(var10.j, -var10.k), BY.s(var10.i), BY.b(var34.j, var34.k)};
         } else {
            throw new rb(false);
         }
      } else if (var2 == rm.h) {
         rs var33 = var4.a(rs.b);
         rs var9;
         if (!((var9 = var4.b(rs.b)).i < -0.9999999999) && !(var9.i > 0.9999999999)) {
            return new double[]{BY.b(var9.k, var9.j), BY.s(var9.i), BY.b(var33.k, -var33.j)};
         } else {
            throw new rb(false);
         }
      } else if (var2 == rm.i) {
         rs var32 = var4.a(rs.d);
         rs var8;
         if (!((var8 = var4.b(rs.d)).j < -0.9999999999) && !(var8.j > 0.9999999999)) {
            return new double[]{BY.b(var8.i, var8.k), BY.s(var8.j), BY.b(var32.i, -var32.k)};
         } else {
            throw new rb(false);
         }
      } else if (var2 == rm.j) {
         rs var31 = var4.a(rs.d);
         rs var7;
         if (!((var7 = var4.b(rs.d)).j < -0.9999999999) && !(var7.j > 0.9999999999)) {
            return new double[]{BY.b(var7.k, -var7.i), BY.s(var7.j), BY.b(var31.k, var31.i)};
         } else {
            throw new rb(false);
         }
      } else if (var2 == rm.k) {
         rs var30 = var4.a(rs.f);
         rs var6;
         if (!((var6 = var4.b(rs.f)).k < -0.9999999999) && !(var6.k > 0.9999999999)) {
            return new double[]{BY.b(var6.i, -var6.j), BY.s(var6.k), BY.b(var30.i, var30.j)};
         } else {
            throw new rb(false);
         }
      } else {
         rs var29 = var4.a(rs.f);
         rs var5;
         if (!((var5 = var4.b(rs.f)).k < -0.9999999999) && !(var5.k > 0.9999999999)) {
            return new double[]{BY.b(var5.j, var5.i), BY.s(var5.k), BY.b(var29.j, -var29.i)};
         } else {
            throw new rb(false);
         }
      }
   }

   private double[] a(rm var1, int var2) {
      if (var2 == rl.VECTOR_OPERATOR$59e4743c) {
         if (var1 == rm.a) {
            rs var27 = this.a(rs.f);
            rs var51;
            if (!((var51 = this.b(rs.b)).k < -0.9999999999) && !(var51.k > 0.9999999999)) {
               return new double[]{BY.b(-var27.j, var27.k), BY.r(var51.k), BY.b(-var51.j, var51.i)};
            } else {
               throw new rb(true);
            }
         } else if (var1 == rm.b) {
            rs var26 = this.a(rs.d);
            rs var50;
            if (!((var50 = this.b(rs.b)).j < -0.9999999999) && !(var50.j > 0.9999999999)) {
               return new double[]{BY.b(var26.k, var26.j), -BY.r(var50.j), BY.b(var50.k, var50.i)};
            } else {
               throw new rb(true);
            }
         } else if (var1 == rm.c) {
            rs var25 = this.a(rs.f);
            rs var49;
            if (!((var49 = this.b(rs.d)).k < -0.9999999999) && !(var49.k > 0.9999999999)) {
               return new double[]{BY.b(var25.i, var25.k), -BY.r(var49.k), BY.b(var49.i, var49.j)};
            } else {
               throw new rb(true);
            }
         } else if (var1 == rm.d) {
            rs var24 = this.a(rs.b);
            rs var48;
            if (!((var48 = this.b(rs.d)).i < -0.9999999999) && !(var48.i > 0.9999999999)) {
               return new double[]{BY.b(-var24.k, var24.i), BY.r(var48.i), BY.b(-var48.k, var48.j)};
            } else {
               throw new rb(true);
            }
         } else if (var1 == rm.e) {
            rs var23 = this.a(rs.d);
            rs var47;
            if (!((var47 = this.b(rs.f)).j < -0.9999999999) && !(var47.j > 0.9999999999)) {
               return new double[]{BY.b(-var23.i, var23.j), BY.r(var47.j), BY.b(-var47.i, var47.k)};
            } else {
               throw new rb(true);
            }
         } else if (var1 == rm.f) {
            rs var22 = this.a(rs.b);
            rs var46;
            if (!((var46 = this.b(rs.f)).i < -0.9999999999) && !(var46.i > 0.9999999999)) {
               return new double[]{BY.b(var22.j, var22.i), -BY.r(var46.i), BY.b(var46.j, var46.k)};
            } else {
               throw new rb(true);
            }
         } else if (var1 == rm.g) {
            rs var21 = this.a(rs.b);
            rs var45;
            if (!((var45 = this.b(rs.b)).i < -0.9999999999) && !(var45.i > 0.9999999999)) {
               return new double[]{BY.b(var21.j, -var21.k), BY.s(var45.i), BY.b(var45.j, var45.k)};
            } else {
               throw new rb(false);
            }
         } else if (var1 == rm.h) {
            rs var20 = this.a(rs.b);
            rs var44;
            if (!((var44 = this.b(rs.b)).i < -0.9999999999) && !(var44.i > 0.9999999999)) {
               return new double[]{BY.b(var20.k, var20.j), BY.s(var44.i), BY.b(var44.k, -var44.j)};
            } else {
               throw new rb(false);
            }
         } else if (var1 == rm.i) {
            rs var19 = this.a(rs.d);
            rs var43;
            if (!((var43 = this.b(rs.d)).j < -0.9999999999) && !(var43.j > 0.9999999999)) {
               return new double[]{BY.b(var19.i, var19.k), BY.s(var43.j), BY.b(var43.i, -var43.k)};
            } else {
               throw new rb(false);
            }
         } else if (var1 == rm.j) {
            rs var18 = this.a(rs.d);
            rs var42;
            if (!((var42 = this.b(rs.d)).j < -0.9999999999) && !(var42.j > 0.9999999999)) {
               return new double[]{BY.b(var18.k, -var18.i), BY.s(var42.j), BY.b(var42.k, var42.i)};
            } else {
               throw new rb(false);
            }
         } else if (var1 == rm.k) {
            rs var17 = this.a(rs.f);
            rs var41;
            if (!((var41 = this.b(rs.f)).k < -0.9999999999) && !(var41.k > 0.9999999999)) {
               return new double[]{BY.b(var17.i, -var17.j), BY.s(var41.k), BY.b(var41.i, var41.j)};
            } else {
               throw new rb(false);
            }
         } else {
            rs var16 = this.a(rs.f);
            rs var40;
            if (!((var40 = this.b(rs.f)).k < -0.9999999999) && !(var40.k > 0.9999999999)) {
               return new double[]{BY.b(var16.j, var16.i), BY.s(var40.k), BY.b(var40.j, -var40.i)};
            } else {
               throw new rb(false);
            }
         }
      } else if (var1 == rm.a) {
         rs var15 = this.a(rs.b);
         rs var39;
         if (!((var39 = this.b(rs.f)).i < -0.9999999999) && !(var39.i > 0.9999999999)) {
            return new double[]{BY.b(-var39.j, var39.k), BY.r(var39.i), BY.b(-var15.j, var15.i)};
         } else {
            throw new rb(true);
         }
      } else if (var1 == rm.b) {
         rs var14 = this.a(rs.b);
         rs var38;
         if (!((var38 = this.b(rs.d)).i < -0.9999999999) && !(var38.i > 0.9999999999)) {
            return new double[]{BY.b(var38.k, var38.j), -BY.r(var38.i), BY.b(var14.k, var14.i)};
         } else {
            throw new rb(true);
         }
      } else if (var1 == rm.c) {
         rs var13 = this.a(rs.d);
         rs var37;
         if (!((var37 = this.b(rs.f)).j < -0.9999999999) && !(var37.j > 0.9999999999)) {
            return new double[]{BY.b(var37.i, var37.k), -BY.r(var37.j), BY.b(var13.i, var13.j)};
         } else {
            throw new rb(true);
         }
      } else if (var1 == rm.d) {
         rs var12 = this.a(rs.d);
         rs var36;
         if (!((var36 = this.b(rs.b)).j < -0.9999999999) && !(var36.j > 0.9999999999)) {
            return new double[]{BY.b(-var36.k, var36.i), BY.r(var36.j), BY.b(-var12.k, var12.j)};
         } else {
            throw new rb(true);
         }
      } else if (var1 == rm.e) {
         rs var11 = this.a(rs.f);
         rs var35;
         if (!((var35 = this.b(rs.d)).k < -0.9999999999) && !(var35.k > 0.9999999999)) {
            return new double[]{BY.b(-var35.i, var35.j), BY.r(var35.k), BY.b(-var11.i, var11.k)};
         } else {
            throw new rb(true);
         }
      } else if (var1 == rm.f) {
         rs var10 = this.a(rs.f);
         rs var34;
         if (!((var34 = this.b(rs.b)).k < -0.9999999999) && !(var34.k > 0.9999999999)) {
            return new double[]{BY.b(var34.j, var34.i), -BY.r(var34.k), BY.b(var10.j, var10.k)};
         } else {
            throw new rb(true);
         }
      } else if (var1 == rm.g) {
         rs var9 = this.a(rs.b);
         rs var33;
         if (!((var33 = this.b(rs.b)).i < -0.9999999999) && !(var33.i > 0.9999999999)) {
            return new double[]{BY.b(var33.j, -var33.k), BY.s(var33.i), BY.b(var9.j, var9.k)};
         } else {
            throw new rb(false);
         }
      } else if (var1 == rm.h) {
         rs var8 = this.a(rs.b);
         rs var32;
         if (!((var32 = this.b(rs.b)).i < -0.9999999999) && !(var32.i > 0.9999999999)) {
            return new double[]{BY.b(var32.k, var32.j), BY.s(var32.i), BY.b(var8.k, -var8.j)};
         } else {
            throw new rb(false);
         }
      } else if (var1 == rm.i) {
         rs var7 = this.a(rs.d);
         rs var31;
         if (!((var31 = this.b(rs.d)).j < -0.9999999999) && !(var31.j > 0.9999999999)) {
            return new double[]{BY.b(var31.i, var31.k), BY.s(var31.j), BY.b(var7.i, -var7.k)};
         } else {
            throw new rb(false);
         }
      } else if (var1 == rm.j) {
         rs var6 = this.a(rs.d);
         rs var30;
         if (!((var30 = this.b(rs.d)).j < -0.9999999999) && !(var30.j > 0.9999999999)) {
            return new double[]{BY.b(var30.k, -var30.i), BY.s(var30.j), BY.b(var6.k, var6.i)};
         } else {
            throw new rb(false);
         }
      } else if (var1 == rm.k) {
         rs var5 = this.a(rs.f);
         rs var29;
         if (!((var29 = this.b(rs.f)).k < -0.9999999999) && !(var29.k > 0.9999999999)) {
            return new double[]{BY.b(var29.i, -var29.j), BY.s(var29.k), BY.b(var5.i, var5.j)};
         } else {
            throw new rb(false);
         }
      } else {
         rs var4 = this.a(rs.f);
         rs var28;
         if (!((var28 = this.b(rs.f)).k < -0.9999999999) && !(var28.k > 0.9999999999)) {
            return new double[]{BY.b(var28.j, var28.i), BY.s(var28.k), BY.b(var4.j, -var4.i)};
         } else {
            throw new rb(false);
         }
      }
   }

   private double[][] h() {
      double var1 = this.a * this.a;
      double var3 = this.a * this.b;
      double var5 = this.a * this.c;
      double var7 = this.a * this.d;
      double var9 = this.b * this.b;
      double var11 = this.b * this.c;
      double var13 = this.b * this.d;
      double var15 = this.c * this.c;
      double var17 = this.c * this.d;
      double var19 = this.d * this.d;
      double[][] var21;
      (var21 = new double[3][])[0] = new double[3];
      var21[1] = new double[3];
      var21[2] = new double[3];
      var21[0][0] = 2.0 * (var1 + var9) - 1.0;
      var21[1][0] = 2.0 * (var11 - var7);
      var21[2][0] = 2.0 * (var13 + var5);
      var21[0][1] = 2.0 * (var11 + var7);
      var21[1][1] = 2.0 * (var1 + var15) - 1.0;
      var21[2][1] = 2.0 * (var17 - var3);
      var21[0][2] = 2.0 * (var13 - var5);
      var21[1][2] = 2.0 * (var17 + var3);
      var21[2][2] = 2.0 * (var1 + var19) - 1.0;
      return var21;
   }

   public final rs a(rs var1) {
      double var2 = var1.i;
      double var4 = var1.j;
      double var6 = var1.k;
      double var8 = this.b * var2 + this.c * var4 + this.d * var6;
      return new rs(
         2.0 * (this.a * (var2 * this.a - (this.c * var6 - this.d * var4)) + var8 * this.b) - var2,
         2.0 * (this.a * (var4 * this.a - (this.d * var2 - this.b * var6)) + var8 * this.c) - var4,
         2.0 * (this.a * (var6 * this.a - (this.b * var4 - this.c * var2)) + var8 * this.d) - var6
      );
   }

   private void a(double[] var1, double[] var2) {
      double var3 = var1[0];
      double var5 = var1[1];
      double var7 = var1[2];
      double var9 = this.b * var3 + this.c * var5 + this.d * var7;
      var2[0] = 2.0 * (this.a * (var3 * this.a - (this.c * var7 - this.d * var5)) + var9 * this.b) - var3;
      var2[1] = 2.0 * (this.a * (var5 * this.a - (this.d * var3 - this.b * var7)) + var9 * this.c) - var5;
      var2[2] = 2.0 * (this.a * (var7 * this.a - (this.b * var5 - this.c * var3)) + var9 * this.d) - var7;
   }

   private rs b(rs var1) {
      double var2 = var1.i;
      double var4 = var1.j;
      double var6 = var1.k;
      double var8 = this.b * var2 + this.c * var4 + this.d * var6;
      double var10 = -this.a;
      return new rs(
         2.0 * (var10 * (var2 * var10 - (this.c * var6 - this.d * var4)) + var8 * this.b) - var2,
         2.0 * (var10 * (var4 * var10 - (this.d * var2 - this.b * var6)) + var8 * this.c) - var4,
         2.0 * (var10 * (var6 * var10 - (this.b * var4 - this.c * var2)) + var8 * this.d) - var6
      );
   }

   private void b(double[] var1, double[] var2) {
      double var3 = var1[0];
      double var5 = var1[1];
      double var7 = var1[2];
      double var9 = this.b * var3 + this.c * var5 + this.d * var7;
      double var11 = -this.a;
      var2[0] = 2.0 * (var11 * (var3 * var11 - (this.c * var7 - this.d * var5)) + var9 * this.b) - var3;
      var2[1] = 2.0 * (var11 * (var5 * var11 - (this.d * var3 - this.b * var7)) + var9 * this.c) - var5;
      var2[2] = 2.0 * (var11 * (var7 * var11 - (this.b * var5 - this.c * var3)) + var9 * this.d) - var7;
   }

   private rk a(rk var1) {
      return this.a(var1, rl.VECTOR_OPERATOR$59e4743c);
   }

   private rk a(rk var1, int var2) {
      return var2 == rl.VECTOR_OPERATOR$59e4743c ? this.b(var1) : var1.b(this);
   }

   private rk b(rk var1) {
      return new rk(
         var1.a * this.a - (var1.b * this.b + var1.c * this.c + var1.d * this.d),
         var1.b * this.a + var1.a * this.b + (var1.c * this.d - var1.d * this.c),
         var1.c * this.a + var1.a * this.c + (var1.d * this.b - var1.b * this.d),
         var1.d * this.a + var1.a * this.d + (var1.b * this.c - var1.c * this.b),
         false
      );
   }

   private rk c(rk var1) {
      int var3 = rl.VECTOR_OPERATOR$59e4743c;
      rk var2 = var1;
      var1 = this;
      if (var3 == rl.VECTOR_OPERATOR$59e4743c) {
         return var1.d(var2);
      }

      var1 = var1;
      return var2.b(new rk(-var1.a, var1.b, var1.c, var1.d, false));
   }

   private rk b(rk var1, int var2) {
      return var2 == rl.VECTOR_OPERATOR$59e4743c ? this.d(var1) : var1.b(this.e());
   }

   private rk d(rk var1) {
      return new rk(
         -var1.a * this.a - (var1.b * this.b + var1.c * this.c + var1.d * this.d),
         -var1.b * this.a + var1.a * this.b + (var1.c * this.d - var1.d * this.c),
         -var1.c * this.a + var1.a * this.c + (var1.d * this.b - var1.b * this.d),
         -var1.d * this.a + var1.a * this.d + (var1.b * this.c - var1.c * this.b),
         false
      );
   }

   private static double[][] a(double[][] var0, double var1) {
      double[] var3 = var0[0];
      double[] var4 = var0[1];
      double[] var69 = var0[2];
      double var6 = var3[0];
      double var8 = var3[1];
      double var10 = var3[2];
      double var12 = var4[0];
      double var14 = var4[1];
      double var16 = var4[2];
      double var18 = var69[0];
      double var20 = var69[1];
      double var22 = var69[2];
      double var24 = 0.0;
      double[][] var5;
      double[] var28 = (var5 = new double[3][3])[0];
      double[] var29 = var5[1];
      double[] var30 = var5[2];
      int var31 = 0;

      while (++var31 < 11) {
         double var33 = var3[0] * var6 + var4[0] * var12 + var69[0] * var18;
         double var35 = var3[1] * var6 + var4[1] * var12 + var69[1] * var18;
         double var37 = var3[2] * var6 + var4[2] * var12 + var69[2] * var18;
         double var39 = var3[0] * var8 + var4[0] * var14 + var69[0] * var20;
         double var41 = var3[1] * var8 + var4[1] * var14 + var69[1] * var20;
         double var43 = var3[2] * var8 + var4[2] * var14 + var69[2] * var20;
         double var45 = var3[0] * var10 + var4[0] * var16 + var69[0] * var22;
         double var47 = var3[1] * var10 + var4[1] * var16 + var69[1] * var22;
         double var49 = var3[2] * var10 + var4[2] * var16 + var69[2] * var22;
         var28[0] = var6 - 0.5 * (var6 * var33 + var8 * var35 + var10 * var37 - var3[0]);
         var28[1] = var8 - 0.5 * (var6 * var39 + var8 * var41 + var10 * var43 - var3[1]);
         var28[2] = var10 - 0.5 * (var6 * var45 + var8 * var47 + var10 * var49 - var3[2]);
         var29[0] = var12 - 0.5 * (var12 * var33 + var14 * var35 + var16 * var37 - var4[0]);
         var29[1] = var14 - 0.5 * (var12 * var39 + var14 * var41 + var16 * var43 - var4[1]);
         var29[2] = var16 - 0.5 * (var12 * var45 + var14 * var47 + var16 * var49 - var4[2]);
         var30[0] = var18 - 0.5 * (var18 * var33 + var20 * var35 + var22 * var37 - var69[0]);
         var30[1] = var20 - 0.5 * (var18 * var39 + var20 * var41 + var22 * var43 - var69[1]);
         var30[2] = var22 - 0.5 * (var18 * var45 + var20 * var47 + var22 * var49 - var69[2]);
         double var51 = var28[0] - var3[0];
         double var53 = var28[1] - var3[1];
         double var55 = var28[2] - var3[2];
         double var57 = var29[0] - var4[0];
         double var59 = var29[1] - var4[1];
         double var61 = var29[2] - var4[2];
         double var63 = var30[0] - var69[0];
         double var65 = var30[1] - var69[1];
         double var67 = var30[2] - var69[2];
         double var26;
         if (BY.w(
               (
                     var26 = var51 * var51
                        + var53 * var53
                        + var55 * var55
                        + var57 * var57
                        + var59 * var59
                        + var61 * var61
                        + var63 * var63
                        + var65 * var65
                        + var67 * var67
                  )
                  - var24
            )
            <= var1) {
            return var5;
         }

         var6 = var28[0];
         var8 = var28[1];
         var10 = var28[2];
         var12 = var29[0];
         var14 = var29[1];
         var16 = var29[2];
         var18 = var30[0];
         var20 = var30[1];
         var22 = var30[2];
         var24 = var26;
      }

      throw new rg(px.UNABLE_TO_ORTHOGONOLIZE_MATRIX, var31 - 1);
   }

   private static double a(rk var0, rk var1) {
      if ((var0 = var0.d(var1)).a < -0.1 || var0.a > 0.1) {
         return 2.0 * BY.r(BY.a(var0.b * var0.b + var0.c * var0.c + var0.d * var0.d));
      } else {
         return var0.a < 0.0 ? 2.0 * BY.s(-var0.a) : 2.0 * BY.s(var0.a);
      }
   }

   static {
      new rk(1.0, 0.0, 0.0, 0.0, false);
   }
}
