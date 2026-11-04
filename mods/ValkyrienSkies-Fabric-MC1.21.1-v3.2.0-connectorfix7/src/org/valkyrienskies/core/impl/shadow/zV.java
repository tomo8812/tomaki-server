package org.valkyrienskies.core.impl.shadow;

public final class zV implements lc {
   private static final double a = 0.6366197723675814;
   private static final double b = 6.28125;
   private static final double c = 0.001935307179586477;
   private static final double d = Math.PI * 2;
   private static final double e = 1.0E308;
   private static final double f = 1.0E16;
   private static final double g = 1.0E-4;
   private static final double h = 8.9E-308;
   private static final double i = 0.0;
   private static final double j = 10000.0;
   private static final double[] k = new double[]{
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
      1.21645100408832E17,
      2.43290200817664E18,
      5.109094217170944E19,
      1.1240007277776077E21,
      2.585201673888498E22,
      6.204484017332394E23
   };
   private final double l;

   private zV(double var1) {
      this.l = var1;
   }

   @Override
   public final double a(double var1) {
      int var2;
      double var3;
      double var5;
      zV.a var10000;
      label331: {
         var5 = var1;
         var3 = this.l;
         var65 = (int)this.l;
         double var8 = var3 - var65;
         var2 = var65 + 1;
         int var7 = var2;
         double var14 = var8;
         double var12 = var5;
         double[] var67 = new double[var7];
         int var10 = (int)var12;
         int var9;
         if (var7 > 0 && var12 >= 0.0 && var12 <= 10000.0 && var14 >= 0.0 && var14 < 1.0) {
            var9 = var7;

            for (int var24 = 0; var24 < var7; var24++) {
               var67[var24] = 0.0;
            }

            if (var12 < 1.0E-4) {
               double var79 = 1.0;
               double var19 = var14 + 1.0;
               double var32 = 0.0;
               if (var12 > 8.9E-308) {
                  var32 = var12 * 0.5;
               }

               if (var14 != 0.0) {
                  var79 = BY.a(var32, var14) / (var14 * zY.d(var14));
               }

               double var26 = 0.0;
               if (var12 + 1.0 > 1.0) {
                  var26 = -var32 * var32;
               }

               var67[0] = var79 + var79 * var26 / var19;
               if (var12 != 0.0 && var67[0] == 0.0) {
                  var9 = 0;
               }

               if (var7 != 1) {
                  if (var12 <= 0.0) {
                     for (int var34 = 1; var34 < var7; var34++) {
                        var67[var34] = 0.0;
                     }
                  } else {
                     double var28 = var32;
                     double var30 = var26 != 0.0 ? 8.9E-308 / var26 : 1.78E-307 / var12;

                     for (int var90 = 1; var90 < var7; var90++) {
                        var79 /= var19;
                        if ((var79 = var79 * var28) <= var30 * ++var19) {
                           var79 = 0.0;
                        }

                        var67[var90] = var79 + var79 * var26 / var19;
                        if (var67[var90] == 0.0 && var9 > var90) {
                           var9 = var90;
                        }
                     }
                  }
               }
            } else if (!(var12 > 25.0) || var7 > var10 + 1) {
               int var87 = var7 - var10;
               int var33 = var10 + 1;
               double var96 = 2.0 * (var33 + var14);
               double var98 = 1.0;
               double var40 = var96 / var12;
               double var44 = 2.0E16;
               boolean var46 = false;
               if (var87 >= 3) {
                  int var92 = var10 + 2;
                  int var35 = var7 - 1;
                  var96 = 2.0 * (var92 - 1 + var14);

                  for (int var112 = var92; var112 <= var35; var112++) {
                     var33 = var112;
                     var96 += 2.0;
                     double var42 = var98;
                     var98 = var40;
                     if ((var40 = var96 * var98 / var12 - var42) > 1.0E292) {
                        var40 /= 1.0E308;
                        var98 /= 1.0E308;
                        double var106 = var40;
                        double var109 = var98;
                        var92 = var33 + 1;

                        do {
                           var33++;
                           var96 += 2.0;
                           var42 = var98;
                           var98 = var40;
                        } while ((var40 = var96 * var98 / var12 - var42) <= 1.0);

                        double var83 = var96 / var12;
                        var44 = var42 * var98 * (0.5 - 0.5 / (var83 * var83)) / 1.0E16;
                        var40 = var98 * 1.0E308;
                        var33--;
                        var96 -= 2.0;
                        var35 = BY.a(var7, var33);

                        for (int var52 = var92; var52 <= var35; var52++) {
                           var42 = var109;
                           var109 = var106;
                           if ((var106 = var96 * var109 / var12 - var42) * var109 > var44) {
                              break;
                           }
                        }

                        var9 = var35;
                        var46 = true;
                        break;
                     }
                  }

                  if (!var46) {
                     var33 = var35;
                     var96 = 2.0 * (var33 + var14);
                     var44 = BY.e(var44, BY.a(var98 * 1.0E16) * BY.a(var40 * 2.0));
                  }
               }

               double var105;
               if (!var46) {
                  do {
                     var33++;
                     var96 += 2.0;
                     var105 = var98;
                     var98 = var40;
                  } while ((var40 = var96 * var98 / var12 - var105) < var44);
               }

               var33++;
               var96 += 2.0;
               double var84 = 0.0;
               double var81 = 1.0 / var40;
               int var107 = 2 * var33 - 4 * (var33 / 2);
               double var48 = 0.0;
               double var50;
               double var70 = (var50 = var33 / 2) - 1.0 + var14;
               double var21 = var50 * 2.0 + var14;
               if (var107 != 0) {
                  var48 = var81 * var70 * var21 / var50;
               }

               int var94 = var33 - var7;
               boolean var113 = false;
               boolean var115 = false;

               for (int var54 = 1; var54 <= var94; var54++) {
                  var33--;
                  var96 -= 2.0;
                  double var85 = var84;
                  var84 = var81;
                  var81 = var96 * var84 / var12 - var85;
                  if ((var107 = 2 - var107) != 0) {
                     var21 = --var50 * 2.0 + var14;
                     if (var33 == 1) {
                        break;
                     }

                     if ((var70 = var50 - 1.0 + var14) == 0.0) {
                        var70 = 1.0;
                     }

                     var48 = (var48 + var81 * var21) * var70 / var50;
                  }
               }

               var67[var33 - 1] = var81;
               if (var94 >= 0) {
                  if (var7 <= 1) {
                     var21 = var14;
                     if (var14 + 1.0 == 1.0) {
                        var21 = 1.0;
                     }

                     var48 += var67[0] * var21;
                     var113 = true;
                  } else {
                     var33--;
                     var96 -= 2.0;
                     var67[var33 - 1] = var96 * var81 / var12 - var84;
                     if (var33 == 1) {
                        var115 = true;
                     } else if ((var107 = 2 - var107) != 0) {
                        var21 = --var50 * 2.0 + var14;
                        if ((var70 = var50 - 1.0 + var14) == 0.0) {
                           var70 = 1.0;
                        }

                        var48 = (var48 + var67[var33 - 1] * var21) * var70 / var50;
                     }
                  }
               }

               if (!var113 && !var115 && (var94 = var33 - 2) != 0) {
                  for (int var116 = 1; var116 <= var94; var116++) {
                     var33--;
                     var96 -= 2.0;
                     var67[var33 - 1] = var96 * var67[var33] / var12 - var67[var33 + 1];
                     if ((var107 = 2 - var107) != 0) {
                        var21 = --var50 * 2.0 + var14;
                        if ((var70 = var50 - 1.0 + var14) == 0.0) {
                           var70 = 1.0;
                        }

                        var48 = (var48 + var67[var33 - 1] * var21) * var70 / var50;
                     }
                  }
               }

               if (!var113) {
                  if (!var115) {
                     var67[0] = 2.0 * (var14 + 1.0) * var67[1] / var12 - var67[2];
                  }

                  if ((var21 = --var50 * 2.0 + var14) == 0.0) {
                     var21 = 1.0;
                  }

                  var48 += var67[0] * var21;
               }

               if (BY.w(var14) > 1.0E-16) {
                  var48 *= zY.d(var14) * BY.a(var12 * 0.5, -var14);
               }

               var81 = 8.9E-308;
               if (var48 > 1.0) {
                  var81 = var48 * 8.9E-308;
               }

               for (int var89 = 0; var89 < var7; var89++) {
                  if (BY.w(var67[var89]) < var81) {
                     var67[var89] = 0.0;
                  }

                  var67[var89] /= var48;
               }
            } else {
               double var86 = BY.a(0.6366197723675814 / var12);
               double var91;
               double var36 = (var91 = 0.125 / var12) * var91;
               byte var38;
               if (var12 >= 130.0) {
                  var38 = 4;
               } else if (var12 >= 35.0) {
                  var38 = 8;
               } else {
                  var38 = 11;
               }

               double var39 = 4.0 * var38;
               double var41 = (int)(var12 / (Math.PI * 2) + 0.5);
               double var43;
               double var45 = BY.n(var43 = var12 - var41 * 6.28125 - var41 * 0.001935307179586477 - (var14 + 0.5) / 0.6366197723675814);
               double var47 = BY.o(var43);
               double var49 = var14 * 2.0;

               for (int var68 = 1; var68 <= 2; var68++) {
                  double var55 = (var39 - 1.0 - var49) * (var39 - 1.0 + var49) * var36 * 0.5;
                  var41 = (var49 - (var39 - 3.0)) * (var49 + (var39 - 3.0));
                  double var53 = var55 * var41 / k[2 * var38];
                  double var57 = (var49 - (var39 + 1.0)) * (var49 + (var39 + 1.0));
                  double var51 = var55 * var57 / k[2 * var38 + 1];
                  double var59 = var39;
                  int var11 = 2 * var38;
                  var57 = var41;

                  for (int var16 = 2; var16 <= var38; var16++) {
                     var55 = ((var59 -= 4.0) - 1.0 - var49) * (var59 - 1.0 + var49);
                     var41 = (var49 - (var59 - 3.0)) * (var49 + (var59 - 3.0));
                     var53 = (var53 + 1.0 / k[var11 - 2]) * var55 * var41 * var36;
                     var51 = (var51 + 1.0 / k[var11 - 1]) * var55 * var57 * var36;
                     var11 -= 2;
                     var57 = var41;
                  }

                  var53++;
                  var51 = (var51 + 1.0) * (var49 * var49 - 1.0) * (0.125 / var12);
                  var67[var68 - 1] = var86 * (var53 * var47 - var51 * var45);
                  if (var7 == 1) {
                     var10000 = new zV.a(Ch.a(var67, var67.length), var9);
                     break label331;
                  }

                  var41 = var45;
                  var45 = -var47;
                  var47 = var41;
                  var49 += 2.0;
               }

               if (var7 > 2) {
                  var49 = var14 * 2.0 + 2.0;

                  for (int var69 = 2; var69 < var7; var69++) {
                     var67[var69] = var49 * var67[var69 - 1] / var12 - var67[var69 - 2];
                     var49 += 2.0;
                  }
               }
            }
         } else {
            if (var67.length > 0) {
               var67[0] = 0.0;
            }

            var9 = BY.a(var7, 0) - 1;
         }

         var10000 = new zV.a(Ch.a(var67, var67.length), var9);
      }

      zV.a var66 = var10000;
      if (var10000.b >= var2) {
         return var66.a[var65];
      } else if (var66.b < 0) {
         throw new oV(px.BESSEL_FUNCTION_BAD_ARGUMENT, var3, var5);
      } else if (BY.w(var66.a[var66.b - 1]) < 1.0E-100) {
         return var66.a[var65];
      } else {
         throw new oR(px.BESSEL_FUNCTION_FAILED_CONVERGENCE, var3, var5);
      }
   }

   private static double a(double var0, double var2) {
      int var4 = (int)var0;
      double var5 = var0 - var4;
      int var7 = var4 + 1;
      zV.a var8;
      if ((var8 = a(var2, var5, var7)).b >= var7) {
         return var8.a[var4];
      } else if (var8.b < 0) {
         throw new oV(px.BESSEL_FUNCTION_BAD_ARGUMENT, var0, var2);
      } else if (BY.w(var8.a[var8.b - 1]) < 1.0E-100) {
         return var8.a[var4];
      } else {
         throw new oR(px.BESSEL_FUNCTION_FAILED_CONVERGENCE, var0, var2);
      }
   }

   private static zV.a a(double var0, double var2, int var4) {
      double[] var5 = new double[var4];
      int var7 = (int)var0;
      int var6;
      if (var4 > 0 && var0 >= 0.0 && var0 <= 10000.0 && var2 >= 0.0 && var2 < 1.0) {
         var6 = var4;

         for (int var12 = 0; var12 < var4; var12++) {
            var5[var12] = 0.0;
         }

         if (var0 < 1.0E-4) {
            double var62 = 1.0;
            double var49 = var2 + 1.0;
            double var20 = 0.0;
            if (var0 > 8.9E-308) {
               var20 = var0 * 0.5;
            }

            if (var2 != 0.0) {
               var62 = BY.a(var20, var2) / (var2 * zY.d(var2));
            }

            double var14 = 0.0;
            if (var0 + 1.0 > 1.0) {
               var14 = -var20 * var20;
            }

            var5[0] = var62 + var62 * var14 / var49;
            if (var0 != 0.0 && var5[0] == 0.0) {
               var6 = 0;
            }

            if (var4 != 1) {
               if (var0 <= 0.0) {
                  for (int var22 = 1; var22 < var4; var22++) {
                     var5[var22] = 0.0;
                  }
               } else {
                  double var16 = var20;
                  double var18 = var14 != 0.0 ? 8.9E-308 / var14 : 1.78E-307 / var0;

                  for (int var73 = 1; var73 < var4; var73++) {
                     var62 /= var49;
                     if ((var62 = var62 * var16) <= var18 * ++var49) {
                        var62 = 0.0;
                     }

                     var5[var73] = var62 + var62 * var14 / var49;
                     if (var5[var73] == 0.0 && var6 > var73) {
                        var6 = var73;
                     }
                  }
               }
            }
         } else if (!(var0 > 25.0) || var4 > var7 + 1) {
            int var70 = var4 - var7;
            int var21 = var7 + 1;
            double var79 = 2.0 * (var21 + var2);
            double var81 = 1.0;
            double var28 = var79 / var0;
            double var32 = 2.0E16;
            boolean var34 = false;
            if (var70 >= 3) {
               int var75 = var7 + 2;
               int var23 = var4 - 1;
               var79 = 2.0 * (var75 - 1 + var2);

               for (int var95 = var75; var95 <= var23; var95++) {
                  var21 = var95;
                  var79 += 2.0;
                  double var30 = var81;
                  var81 = var28;
                  if ((var28 = var79 * var81 / var0 - var30) > 1.0E292) {
                     var28 /= 1.0E308;
                     var81 /= 1.0E308;
                     double var89 = var28;
                     double var92 = var81;
                     var75 = var21 + 1;

                     do {
                        var21++;
                        var79 += 2.0;
                        var30 = var81;
                        var81 = var28;
                     } while ((var28 = var79 * var81 / var0 - var30) <= 1.0);

                     double var66 = var79 / var0;
                     var32 = var30 * var81 * (0.5 - 0.5 / (var66 * var66)) / 1.0E16;
                     var28 = var81 * 1.0E308;
                     var21--;
                     var79 -= 2.0;
                     var23 = BY.a(var4, var21);

                     for (int var40 = var75; var40 <= var23; var40++) {
                        var30 = var92;
                        var92 = var89;
                        if ((var89 = var79 * var92 / var0 - var30) * var92 > var32) {
                           break;
                        }
                     }

                     var6 = var23;
                     var34 = true;
                     break;
                  }
               }

               if (!var34) {
                  var21 = var23;
                  var79 = 2.0 * (var21 + var2);
                  var32 = BY.e(var32, BY.a(var81 * 1.0E16) * BY.a(var28 * 2.0));
               }
            }

            double var88;
            if (!var34) {
               do {
                  var21++;
                  var79 += 2.0;
                  var88 = var81;
                  var81 = var28;
               } while ((var28 = var79 * var81 / var0 - var88) < var32);
            }

            var21++;
            var79 += 2.0;
            double var67 = 0.0;
            double var64 = 1.0 / var28;
            int var90 = 2 * var21 - 4 * (var21 / 2);
            double var36 = 0.0;
            double var38;
            double var52 = (var38 = var21 / 2) - 1.0 + var2;
            double var56 = var38 * 2.0 + var2;
            if (var90 != 0) {
               var36 = var64 * var52 * var56 / var38;
            }

            int var77 = var21 - var4;
            boolean var96 = false;
            boolean var98 = false;

            for (int var42 = 1; var42 <= var77; var42++) {
               var21--;
               var79 -= 2.0;
               double var68 = var67;
               var67 = var64;
               var64 = var79 * var67 / var0 - var68;
               if ((var90 = 2 - var90) != 0) {
                  var56 = --var38 * 2.0 + var2;
                  if (var21 == 1) {
                     break;
                  }

                  double var53;
                  if ((var53 = var38 - 1.0 + var2) == 0.0) {
                     var53 = 1.0;
                  }

                  var36 = (var36 + var64 * var56) * var53 / var38;
               }
            }

            var5[var21 - 1] = var64;
            if (var77 >= 0) {
               if (var4 <= 1) {
                  var56 = var2;
                  if (var2 + 1.0 == 1.0) {
                     var56 = 1.0;
                  }

                  var36 += var5[0] * var56;
                  var96 = true;
               } else {
                  var21--;
                  var79 -= 2.0;
                  var5[var21 - 1] = var79 * var64 / var0 - var67;
                  if (var21 == 1) {
                     var98 = true;
                  } else if ((var90 = 2 - var90) != 0) {
                     var56 = --var38 * 2.0 + var2;
                     double var54;
                     if ((var54 = var38 - 1.0 + var2) == 0.0) {
                        var54 = 1.0;
                     }

                     var36 = (var36 + var5[var21 - 1] * var56) * var54 / var38;
                  }
               }
            }

            if (!var96 && !var98 && (var77 = var21 - 2) != 0) {
               for (int var99 = 1; var99 <= var77; var99++) {
                  var21--;
                  var79 -= 2.0;
                  var5[var21 - 1] = var79 * var5[var21] / var0 - var5[var21 + 1];
                  if ((var90 = 2 - var90) != 0) {
                     var56 = --var38 * 2.0 + var2;
                     double var55;
                     if ((var55 = var38 - 1.0 + var2) == 0.0) {
                        var55 = 1.0;
                     }

                     var36 = (var36 + var5[var21 - 1] * var56) * var55 / var38;
                  }
               }
            }

            if (!var96) {
               if (!var98) {
                  var5[0] = 2.0 * (var2 + 1.0) * var5[1] / var0 - var5[2];
               }

               if ((var56 = --var38 * 2.0 + var2) == 0.0) {
                  var56 = 1.0;
               }

               var36 += var5[0] * var56;
            }

            if (BY.w(var2) > 1.0E-16) {
               var36 *= zY.d(var2) * BY.a(var0 * 0.5, -var2);
            }

            var64 = 8.9E-308;
            if (var36 > 1.0) {
               var64 = var36 * 8.9E-308;
            }

            for (int var72 = 0; var72 < var4; var72++) {
               if (BY.w(var5[var72]) < var64) {
                  var5[var72] = 0.0;
               }

               var5[var72] /= var36;
            }
         } else {
            double var69 = BY.a(0.6366197723675814 / var0);
            double var74;
            double var24 = (var74 = 0.125 / var0) * var74;
            byte var26;
            if (var0 >= 130.0) {
               var26 = 4;
            } else if (var0 >= 35.0) {
               var26 = 8;
            } else {
               var26 = 11;
            }

            double var27 = 4.0 * var26;
            double var29 = (int)(var0 / (Math.PI * 2) + 0.5);
            double var31;
            double var33 = BY.n(var31 = var0 - var29 * 6.28125 - var29 * 0.001935307179586477 - (var2 + 0.5) / 0.6366197723675814);
            double var35 = BY.o(var31);
            double var37 = var2 * 2.0;

            for (int var50 = 1; var50 <= 2; var50++) {
               double var43 = (var27 - 1.0 - var37) * (var27 - 1.0 + var37) * var24 * 0.5;
               var29 = (var37 - (var27 - 3.0)) * (var37 + (var27 - 3.0));
               double var41 = var43 * var29 / k[2 * var26];
               double var45 = (var37 - (var27 + 1.0)) * (var37 + (var27 + 1.0));
               double var39 = var43 * var45 / k[2 * var26 + 1];
               double var47 = var27;
               int var8 = 2 * var26;
               var45 = var29;

               for (int var9 = 2; var9 <= var26; var9++) {
                  var43 = ((var47 -= 4.0) - 1.0 - var37) * (var47 - 1.0 + var37);
                  var29 = (var37 - (var47 - 3.0)) * (var37 + (var47 - 3.0));
                  var41 = (var41 + 1.0 / k[var8 - 2]) * var43 * var29 * var24;
                  var39 = (var39 + 1.0 / k[var8 - 1]) * var43 * var45 * var24;
                  var8 -= 2;
                  var45 = var29;
               }

               var41++;
               var39 = (var39 + 1.0) * (var37 * var37 - 1.0) * (0.125 / var0);
               var5[var50 - 1] = var69 * (var41 * var35 - var39 * var33);
               if (var4 == 1) {
                  return new zV.a(Ch.a(var5, var5.length), var6);
               }

               var29 = var33;
               var33 = -var35;
               var35 = var29;
               var37 += 2.0;
            }

            if (var4 > 2) {
               var37 = var2 * 2.0 + 2.0;

               for (int var51 = 2; var51 < var4; var51++) {
                  var5[var51] = var37 * var5[var51 - 1] / var0 - var5[var51 - 2];
                  var37 += 2.0;
               }
            }
         }
      } else {
         if (var5.length > 0) {
            var5[0] = 0.0;
         }

         var6 = BY.a(var4, 0) - 1;
      }

      return new zV.a(Ch.a(var5, var5.length), var6);
   }

   public static class a {
      final double[] a;
      final int b;

      public a(double[] var1, int var2) {
         this.a = Ch.a(var1, var1.length);
         this.b = var2;
      }

      private double[] a() {
         return Ch.a(this.a, this.a.length);
      }

      private int b() {
         return this.b;
      }

      // $VF: synthetic method
      static int a(zV.a var0) {
         return var0.b;
      }

      // $VF: synthetic method
      static double[] b(zV.a var0) {
         return var0.a;
      }
   }
}
