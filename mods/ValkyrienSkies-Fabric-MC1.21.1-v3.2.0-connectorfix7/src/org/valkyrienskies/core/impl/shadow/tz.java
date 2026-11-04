package org.valkyrienskies.core.impl.shadow;

final class tz {
   private static final int a = 100;
   private final double[][] b;
   private final double[][] c;
   private tq d;
   private tq e;
   private tq f;
   private final double g = Cq.a;

   tz(tq var1) {
      if (!var1.e()) {
         throw new th(var1.f(), var1.g());
      }

      sW var43 = new sW(var1);
      sW var2 = var43;
      if (var43.b == null) {
         int var3;
         double[][] var5 = new double[var3 = var2.a.length][var3];

         for (int var6 = 0; var6 < var3; var6++) {
            if (var6 > 0) {
               var5[var6][var6 - 1] = var2.a[var6][var6 - 1];
            }

            for (int var4 = var6; var4 < var3; var4++) {
               var5[var6][var4] = var2.a[var6][var4];
            }
         }

         var2.b = td.a(var5);
      }

      this.c = var2.b.a();
      this.b = var43.a().a();
      this.e = null;
      this.d = null;
      this.f = null;
      tz var48 = this;
      int var49 = this.c.length;
      tz var9 = var48;
      double var25 = 0.0;

      for (int var27 = 0; var27 < var9.c.length; var27++) {
         for (int var10 = BY.b(var27 - 1, 0); var10 < var9.c.length; var10++) {
            var25 += BY.w(var9.c[var27][var10]);
         }
      }

      double var51 = var25;
      tz.a var50 = new tz.a((byte)0);
      int var44 = 0;
      int var7 = var49 - 1;

      while (var7 >= 0) {
         double var26 = var51;
         int var73 = var7;
         var9 = var48;

         int var59;
         for (var59 = var73; var59 > 0; var59--) {
            double var29;
            if ((var29 = BY.w(var9.c[var59 - 1][var59 - 1]) + BY.w(var9.c[var59][var59])) == 0.0) {
               var29 = var26;
            }

            if (BY.w(var9.c[var59][var59 - 1]) < var9.g * var29) {
               break;
            }
         }

         int var8 = var59;
         if (var59 == var7) {
            var48.c[var7][var7] = var48.c[var7][var7] + var50.d;
            var7--;
            var44 = 0;
         } else if (var8 == var7 - 1) {
            double var64;
            double var13 = (var64 = (var48.c[var7 - 1][var7 - 1] - var48.c[var7][var7]) / 2.0) * var64 + var48.c[var7][var7 - 1] * var48.c[var7 - 1][var7];
            var48.c[var7][var7] = var48.c[var7][var7] + var50.d;
            var48.c[var7 - 1][var7 - 1] = var48.c[var7 - 1][var7 - 1] + var50.d;
            if (var13 >= 0.0) {
               double var15 = BY.a(BY.w(var13));
               if (var64 >= 0.0) {
                  var15 = var64 + var15;
               } else {
                  var15 = var64 - var15;
               }

               double var17;
               double var19 = BY.w(var17 = var48.c[var7][var7 - 1]) + BY.w(var15);
               var64 = var17 / var19;
               var13 = var15 / var19;
               double var21 = BY.a(var64 * var64 + var13 * var13);
               var64 /= var21;
               var13 /= var21;

               for (int var45 = var7 - 1; var45 < var49; var45++) {
                  var15 = var48.c[var7 - 1][var45];
                  var48.c[var7 - 1][var45] = var13 * var15 + var64 * var48.c[var7][var45];
                  var48.c[var7][var45] = var13 * var48.c[var7][var45] - var64 * var15;
               }

               for (int var46 = 0; var46 <= var7; var46++) {
                  var15 = var48.c[var46][var7 - 1];
                  var48.c[var46][var7 - 1] = var13 * var15 + var64 * var48.c[var46][var7];
                  var48.c[var46][var7] = var13 * var48.c[var46][var7] - var64 * var15;
               }

               for (int var47 = 0; var47 <= var49 - 1; var47++) {
                  var15 = var48.b[var47][var7 - 1];
                  var48.b[var47][var7 - 1] = var13 * var15 + var64 * var48.b[var47][var7];
                  var48.b[var47][var7] = var13 * var48.b[var47][var7] - var64 * var15;
               }
            }

            var7 -= 2;
            var44 = 0;
         } else {
            tz.a var60 = var50;
            int var80 = var44;
            int var77 = var7;
            int var74 = var8;
            var9 = var48;
            var60.a = var9.c[var77][var77];
            var60.b = var60.c = 0.0;
            if (var74 < var77) {
               var60.b = var9.c[var77 - 1][var77 - 1];
               var60.c = var9.c[var77][var77 - 1] * var9.c[var77 - 1][var77];
            }

            if (var80 == 10) {
               var60.d = var60.d + var60.a;

               for (int var83 = 0; var83 <= var77; var83++) {
                  var9.c[var83][var83] = var9.c[var83][var83] - var60.a;
               }

               double var84 = BY.w(var9.c[var77][var77 - 1]) + BY.w(var9.c[var77 - 1][var77 - 2]);
               var60.a = var84 * 0.75;
               var60.b = var84 * 0.75;
               var60.c = var84 * -0.4375 * var84;
            }

            double var85;
            if (var80 == 30 && (var85 = (var85 = (var60.b - var60.a) / 2.0) * var85 + var60.c) > 0.0) {
               var85 = BY.a(var85);
               if (var60.b < var60.a) {
                  var85 = -var85;
               }

               var85 = var60.a - var60.c / ((var60.b - var60.a) / 2.0 + var85);

               for (int var31 = 0; var31 <= var77; var31++) {
                  var9.c[var31][var31] = var9.c[var31][var31] - var85;
               }

               var60.d += var85;
               var60.a = var60.b = var60.c = 0.964;
            }

            if (++var44 > 100) {
               throw new pc(px.CONVERGENCE_FAILED, 100);
            }

            double[] var11 = new double[3];
            double[] var61 = var11;
            tz.a var81 = var50;
            int var78 = var7;
            int var75 = var8;
            var9 = var48;

            for (var89 = var78 - 2; var89 >= var75; var89--) {
               double var30 = var9.c[var89][var89];
               double var32 = var81.a - var30;
               double var34 = var81.b - var30;
               var61[0] = (var32 * var34 - var81.c) / var9.c[var89 + 1][var89] + var9.c[var89][var89 + 1];
               var61[1] = var9.c[var89 + 1][var89 + 1] - var30 - var32 - var34;
               var61[2] = var9.c[var89 + 2][var89 + 1];
               if (var89 == var75) {
                  break;
               }

               double var36 = BY.w(var9.c[var89][var89 - 1]) * (BY.w(var61[1]) + BY.w(var61[2]));
               double var38 = BY.w(var61[0]) * (BY.w(var9.c[var89 - 1][var89 - 1]) + BY.w(var30) + BY.w(var9.c[var89 + 1][var89 + 1]));
               if (var36 < var9.g * var38) {
                  break;
               }
            }

            int var12 = var89;
            double[] var90 = var11;
            tz.a var62 = var50;
            var80 = var7;
            int var79 = var12;
            int var76 = var8;
            var9 = var48;
            int var91 = var48.c.length;
            double var92 = var90[0];
            double var33 = var90[1];
            double var35 = var90[2];

            for (int var37 = var79; var37 <= var80 - 1; var37++) {
               boolean var94 = var37 != var80 - 1;
               if (var37 != var79) {
                  var92 = var9.c[var37][var37 - 1];
                  var33 = var9.c[var37 + 1][var37 - 1];
                  var35 = var94 ? var9.c[var37 + 2][var37 - 1] : 0.0;
                  var62.a = BY.w(var92) + BY.w(var33) + BY.w(var35);
                  if (Cq.b(var62.a, 0.0, var9.g)) {
                     continue;
                  }

                  var92 /= var62.a;
                  var33 /= var62.a;
                  var35 /= var62.a;
               }

               double var39 = BY.a(var92 * var92 + var33 * var33 + var35 * var35);
               if (var92 < 0.0) {
                  var39 = -var39;
               }

               if (var39 != 0.0) {
                  if (var37 != var79) {
                     var9.c[var37][var37 - 1] = -var39 * var62.a;
                  } else if (var76 != var79) {
                     var9.c[var37][var37 - 1] = -var9.c[var37][var37 - 1];
                  }

                  var92 += var39;
                  var62.a = var92 / var39;
                  var62.b = var33 / var39;
                  double var41 = var35 / var39;
                  var33 /= var92;
                  var35 /= var92;

                  for (int var52 = var37; var52 < var91; var52++) {
                     var92 = var9.c[var37][var52] + var33 * var9.c[var37 + 1][var52];
                     if (var94) {
                        var92 += var35 * var9.c[var37 + 2][var52];
                        var9.c[var37 + 2][var52] = var9.c[var37 + 2][var52] - var92 * var41;
                     }

                     var9.c[var37][var52] = var9.c[var37][var52] - var92 * var62.a;
                     var9.c[var37 + 1][var52] = var9.c[var37 + 1][var52] - var92 * var62.b;
                  }

                  for (int var53 = 0; var53 <= BY.a(var80, var37 + 3); var53++) {
                     var92 = var62.a * var9.c[var53][var37] + var62.b * var9.c[var53][var37 + 1];
                     if (var94) {
                        var92 += var41 * var9.c[var53][var37 + 2];
                        var9.c[var53][var37 + 2] = var9.c[var53][var37 + 2] - var92 * var35;
                     }

                     var9.c[var53][var37] = var9.c[var53][var37] - var92;
                     var9.c[var53][var37 + 1] = var9.c[var53][var37 + 1] - var92 * var33;
                  }

                  var8 = var9.c.length - 1;

                  for (int var63 = 0; var63 <= var8; var63++) {
                     var92 = var62.a * var9.b[var63][var37] + var62.b * var9.b[var63][var37 + 1];
                     if (var94) {
                        var92 += var41 * var9.b[var63][var37 + 2];
                        var9.b[var63][var37 + 2] = var9.b[var63][var37 + 2] - var92 * var35;
                     }

                     var9.b[var63][var37] = var9.b[var63][var37] - var92;
                     var9.b[var63][var37 + 1] = var9.b[var63][var37 + 1] - var92 * var33;
                  }
               }
            }

            for (int var93 = var79 + 2; var93 <= var80; var93++) {
               var9.c[var93][var93 - 2] = 0.0;
               if (var93 > var79 + 2) {
                  var9.c[var93][var93 - 3] = 0.0;
               }
            }
         }
      }
   }

   public final tq a() {
      if (this.d == null) {
         this.d = td.a(this.b);
      }

      return this.d;
   }

   private tq c() {
      if (this.f == null) {
         this.f = this.a().d();
      }

      return this.f;
   }

   public final tq b() {
      if (this.e == null) {
         this.e = td.a(this.c);
      }

      return this.e;
   }

   private void d() {
      int var1 = this.c.length;
      double var2 = this.e();
      tz.a var4 = new tz.a((byte)0);
      int var5 = 0;
      int var6 = var1 - 1;

      while (var6 >= 0) {
         int var7;
         if ((var7 = this.a(var6, var2)) == var6) {
            this.c[var6][var6] = this.c[var6][var6] + var4.d;
            var6--;
            var5 = 0;
         } else if (var7 != var6 - 1) {
            this.a(var7, var6, var5, var4);
            if (++var5 > 100) {
               throw new pc(px.CONVERGENCE_FAILED, 100);
            }

            double[] var25 = new double[3];
            int var9 = this.a(var7, var6, var4, var25);
            this.a(var7, var9, var6, var4, var25);
         } else {
            double var8;
            double var10 = (var8 = (this.c[var6 - 1][var6 - 1] - this.c[var6][var6]) / 2.0) * var8 + this.c[var6][var6 - 1] * this.c[var6 - 1][var6];
            this.c[var6][var6] = this.c[var6][var6] + var4.d;
            this.c[var6 - 1][var6 - 1] = this.c[var6 - 1][var6 - 1] + var4.d;
            if (var10 >= 0.0) {
               double var12 = BY.a(BY.w(var10));
               if (var8 >= 0.0) {
                  var12 = var8 + var12;
               } else {
                  var12 = var8 - var12;
               }

               double var14;
               double var16 = BY.w(var14 = this.c[var6][var6 - 1]) + BY.w(var12);
               var8 = var14 / var16;
               var10 = var12 / var16;
               double var18 = BY.a(var8 * var8 + var10 * var10);
               var8 /= var18;
               var10 /= var18;

               for (int var20 = var6 - 1; var20 < var1; var20++) {
                  var12 = this.c[var6 - 1][var20];
                  this.c[var6 - 1][var20] = var10 * var12 + var8 * this.c[var6][var20];
                  this.c[var6][var20] = var10 * this.c[var6][var20] - var8 * var12;
               }

               for (int var21 = 0; var21 <= var6; var21++) {
                  var12 = this.c[var21][var6 - 1];
                  this.c[var21][var6 - 1] = var10 * var12 + var8 * this.c[var21][var6];
                  this.c[var21][var6] = var10 * this.c[var21][var6] - var8 * var12;
               }

               for (int var22 = 0; var22 <= var1 - 1; var22++) {
                  var12 = this.b[var22][var6 - 1];
                  this.b[var22][var6 - 1] = var10 * var12 + var8 * this.b[var22][var6];
                  this.b[var22][var6] = var10 * this.b[var22][var6] - var8 * var12;
               }
            }

            var6 -= 2;
            var5 = 0;
         }
      }
   }

   private double e() {
      double var1 = 0.0;

      for (int var3 = 0; var3 < this.c.length; var3++) {
         for (int var4 = BY.b(var3 - 1, 0); var4 < this.c.length; var4++) {
            var1 += BY.w(this.c[var3][var4]);
         }
      }

      return var1;
   }

   private int a(int var1, double var2) {
      for (var1 = var1; var1 > 0; var1--) {
         double var5;
         if ((var5 = BY.w(this.c[var1 - 1][var1 - 1]) + BY.w(this.c[var1][var1])) == 0.0) {
            var5 = var2;
         }

         if (BY.w(this.c[var1][var1 - 1]) < this.g * var5) {
            break;
         }
      }

      return var1;
   }

   private void a(int var1, int var2, int var3, tz.a var4) {
      var4.a = this.c[var2][var2];
      var4.b = var4.c = 0.0;
      if (var1 < var2) {
         var4.b = this.c[var2 - 1][var2 - 1];
         var4.c = this.c[var2][var2 - 1] * this.c[var2 - 1][var2];
      }

      if (var3 == 10) {
         var4.d = var4.d + var4.a;

         for (int var5 = 0; var5 <= var2; var5++) {
            this.c[var5][var5] = this.c[var5][var5] - var4.a;
         }

         double var8 = BY.w(this.c[var2][var2 - 1]) + BY.w(this.c[var2 - 1][var2 - 2]);
         var4.a = var8 * 0.75;
         var4.b = var8 * 0.75;
         var4.c = var8 * -0.4375 * var8;
      }

      double var9;
      if (var3 == 30 && (var9 = (var9 = (var4.b - var4.a) / 2.0) * var9 + var4.c) > 0.0) {
         var9 = BY.a(var9);
         if (var4.b < var4.a) {
            var9 = -var9;
         }

         var9 = var4.a - var4.c / ((var4.b - var4.a) / 2.0 + var9);

         for (int var7 = 0; var7 <= var2; var7++) {
            this.c[var7][var7] = this.c[var7][var7] - var9;
         }

         var4.d += var9;
         var4.a = var4.b = var4.c = 0.964;
      }
   }

   private int a(int var1, int var2, tz.a var3, double[] var4) {
      for (var2 -= 2; var2 >= var1; var2--) {
         double var6 = this.c[var2][var2];
         double var8 = var3.a - var6;
         double var10 = var3.b - var6;
         var4[0] = (var8 * var10 - var3.c) / this.c[var2 + 1][var2] + this.c[var2][var2 + 1];
         var4[1] = this.c[var2 + 1][var2 + 1] - var6 - var8 - var10;
         var4[2] = this.c[var2 + 2][var2 + 1];
         if (var2 == var1) {
            break;
         }

         double var12 = BY.w(this.c[var2][var2 - 1]) * (BY.w(var4[1]) + BY.w(var4[2]));
         double var14 = BY.w(var4[0]) * (BY.w(this.c[var2 - 1][var2 - 1]) + BY.w(var6) + BY.w(this.c[var2 + 1][var2 + 1]));
         if (var12 < this.g * var14) {
            break;
         }
      }

      return var2;
   }

   private void a(int var1, int var2, int var3, tz.a var4, double[] var5) {
      int var6 = this.c.length;
      double var7 = var5[0];
      double var9 = var5[1];
      double var11 = var5[2];

      for (int var19 = var2; var19 <= var3 - 1; var19++) {
         boolean var13 = var19 != var3 - 1;
         if (var19 != var2) {
            var7 = this.c[var19][var19 - 1];
            var9 = this.c[var19 + 1][var19 - 1];
            var11 = var13 ? this.c[var19 + 2][var19 - 1] : 0.0;
            var4.a = BY.w(var7) + BY.w(var9) + BY.w(var11);
            if (Cq.b(var4.a, 0.0, this.g)) {
               continue;
            }

            var7 /= var4.a;
            var9 /= var4.a;
            var11 /= var4.a;
         }

         double var15 = BY.a(var7 * var7 + var9 * var9 + var11 * var11);
         if (var7 < 0.0) {
            var15 = -var15;
         }

         if (var15 != 0.0) {
            if (var19 != var2) {
               this.c[var19][var19 - 1] = -var15 * var4.a;
            } else if (var1 != var2) {
               this.c[var19][var19 - 1] = -this.c[var19][var19 - 1];
            }

            var7 += var15;
            var4.a = var7 / var15;
            var4.b = var9 / var15;
            double var17 = var11 / var15;
            var9 /= var7;
            var11 /= var7;

            for (int var14 = var19; var14 < var6; var14++) {
               var7 = this.c[var19][var14] + var9 * this.c[var19 + 1][var14];
               if (var13) {
                  var7 += var11 * this.c[var19 + 2][var14];
                  this.c[var19 + 2][var14] = this.c[var19 + 2][var14] - var7 * var17;
               }

               this.c[var19][var14] = this.c[var19][var14] - var7 * var4.a;
               this.c[var19 + 1][var14] = this.c[var19 + 1][var14] - var7 * var4.b;
            }

            for (int var21 = 0; var21 <= BY.a(var3, var19 + 3); var21++) {
               var7 = var4.a * this.c[var21][var19] + var4.b * this.c[var21][var19 + 1];
               if (var13) {
                  var7 += var17 * this.c[var21][var19 + 2];
                  this.c[var21][var19 + 2] = this.c[var21][var19 + 2] - var7 * var11;
               }

               this.c[var21][var19] = this.c[var21][var19] - var7;
               this.c[var21][var19 + 1] = this.c[var21][var19 + 1] - var7 * var9;
            }

            int var22 = this.c.length - 1;

            for (int var23 = 0; var23 <= var22; var23++) {
               var7 = var4.a * this.b[var23][var19] + var4.b * this.b[var23][var19 + 1];
               if (var13) {
                  var7 += var17 * this.b[var23][var19 + 2];
                  this.b[var23][var19 + 2] = this.b[var23][var19 + 2] - var7 * var11;
               }

               this.b[var23][var19] = this.b[var23][var19] - var7;
               this.b[var23][var19 + 1] = this.b[var23][var19 + 1] - var7 * var9;
            }
         }
      }

      for (int var20 = var2 + 2; var20 <= var3; var20++) {
         this.c[var20][var20 - 2] = 0.0;
         if (var20 > var2 + 2) {
            this.c[var20][var20 - 3] = 0.0;
         }
      }
   }

   static class a {
      double a;
      double b;
      double c;
      double d;

      private a() {
      }

      // $VF: synthetic method
      a(byte var1) {
         this();
      }
   }
}
