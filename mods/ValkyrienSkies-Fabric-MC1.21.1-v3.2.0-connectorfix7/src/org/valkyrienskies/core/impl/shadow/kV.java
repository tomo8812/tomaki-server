package org.valkyrienskies.core.impl.shadow;

public final class kV {
   private kV() {
   }

   private static <unrepresentable> a(final lc... var0) {
      return new lc() {
         @Override
         public final double a(double var1) {
            double var3 = var1;

            for (int var5 = var0.length - 1; var5 >= 0; var5--) {
               var3 = var0[var5].a(var3);
            }

            return var3;
         }
      };
   }

   private static <unrepresentable> a(final ln... var0) {
      return new ln() {
         @Override
         public final double a(double var1) {
            double var3 = var1;

            for (int var5 = var0.length - 1; var5 >= 0; var5--) {
               var3 = var0[var5].a(var3);
            }

            return var3;
         }

         @Override
         public final lg a(lg var1) {
            var1 = var1;

            for (int var2 = var0.length - 1; var2 >= 0; var2--) {
               var1 = var0[var2].a(var1);
            }

            return var1;
         }
      };
   }

   @Deprecated
   private static <unrepresentable> a(final kS... var0) {
      return new kS() {
         @Override
         public final double a(double var1) {
            double var3 = var1;

            for (int var5 = var0.length - 1; var5 >= 0; var5--) {
               var3 = var0[var5].a(var3);
            }

            return var3;
         }

         @Override
         public final lc a() {
            return new lc() {
               @Override
               public final double a(double var1) {
                  double var3 = 1.0;
                  double var5 = var1;

                  for (int var7 = var0.length - 1; var7 >= 0; var7--) {
                     var3 *= var0[var7].a().a(var5);
                     var5 = var0[var7].a(var5);
                  }

                  return var3;
               }
            };
         }
      };
   }

   private static <unrepresentable> b(final lc... var0) {
      return new lc() {
         @Override
         public final double a(double var1) {
            double var3 = var0[0].a(var1);

            for (int var5 = 1; var5 < var0.length; var5++) {
               var3 += var0[var5].a(var1);
            }

            return var3;
         }
      };
   }

   private static <unrepresentable> b(final ln... var0) {
      return new ln() {
         @Override
         public final double a(double var1) {
            double var3 = var0[0].a(var1);

            for (int var5 = 1; var5 < var0.length; var5++) {
               var3 += var0[var5].a(var1);
            }

            return var3;
         }

         @Override
         public final lg a(lg var1) {
            lg var2 = var0[0].a(var1);

            for (int var3 = 1; var3 < var0.length; var3++) {
               var2 = var2.a(var0[var3].a(var1));
            }

            return var2;
         }
      };
   }

   @Deprecated
   private static <unrepresentable> b(final kS... var0) {
      return new kS() {
         @Override
         public final double a(double var1) {
            double var3 = var0[0].a(var1);

            for (int var5 = 1; var5 < var0.length; var5++) {
               var3 += var0[var5].a(var1);
            }

            return var3;
         }

         @Override
         public final lc a() {
            return new lc() {
               @Override
               public final double a(double var1) {
                  double var3 = var0[0].a().a(var1);

                  for (int var5 = 1; var5 < var0.length; var5++) {
                     var3 += var0[var5].a().a(var1);
                  }

                  return var3;
               }
            };
         }
      };
   }

   private static <unrepresentable> c(final lc... var0) {
      return new lc() {
         @Override
         public final double a(double var1) {
            double var3 = var0[0].a(var1);

            for (int var5 = 1; var5 < var0.length; var5++) {
               var3 *= var0[var5].a(var1);
            }

            return var3;
         }
      };
   }

   private static <unrepresentable> c(final ln... var0) {
      return new ln() {
         @Override
         public final double a(double var1) {
            double var3 = var0[0].a(var1);

            for (int var5 = 1; var5 < var0.length; var5++) {
               var3 *= var0[var5].a(var1);
            }

            return var3;
         }

         @Override
         public final lg a(lg var1) {
            lg var2 = var0[0].a(var1);

            for (int var3 = 1; var3 < var0.length; var3++) {
               var2 = var2.b(var0[var3].a(var1));
            }

            return var2;
         }
      };
   }

   @Deprecated
   private static <unrepresentable> c(final kS... var0) {
      return new kS() {
         @Override
         public final double a(double var1) {
            double var3 = var0[0].a(var1);

            for (int var5 = 1; var5 < var0.length; var5++) {
               var3 *= var0[var5].a(var1);
            }

            return var3;
         }

         @Override
         public final lc a() {
            return new lc() {
               @Override
               public final double a(double var1) {
                  double var3 = 0.0;

                  for (int var5 = 0; var5 < var0.length; var5++) {
                     double var6 = var0[var5].a().a(var1);

                     for (int var8 = 0; var8 < var0.length; var8++) {
                        if (var5 != var8) {
                           var6 *= var0[var8].a(var1);
                        }
                     }

                     var3 += var6;
                  }

                  return var3;
               }
            };
         }
      };
   }

   private static <unrepresentable> a(final kP var0, final lc var1, final lc var2) {
      return new lc() {
         @Override
         public final double a(double var1x) {
            return var0.a(var1.a(var1x), var2.a(var1x));
         }
      };
   }

   private static <unrepresentable> a(final kP var0, lN var1, double var2) {
      return new kW(var2, var1) {
         // $VF: synthetic field
         private double b;
         // $VF: synthetic field
         private lc c;

         {
            this.b = var2x;
            this.c = var4;
         }

         @Override
         public final double a(double[] var1) {
            double var2x = var0.a(this.b, this.c.a(var1[0]));

            for (int var4 = 1; var4 < var1.length; var4++) {
               var2x = var0.a(var2x, this.c.a(var1[var4]));
            }

            return var2x;
         }
      };
   }

   private static kW b(kP var0, double var1) {
      lN var10001 = new lN();
      double var5 = var1;
      lN var8 = var10001;
      var0 = var0;
      return new kW(var5, var8) {
         // $VF: synthetic field
         private double b;
         // $VF: synthetic field
         private lc c;

         {
            this.b = var2x;
            this.c = var4;
         }

         @Override
         public final double a(double[] var1) {
            double var2x = var0.a(this.b, this.c.a(var1[0]));

            for (int var4 = 1; var4 < var1.length; var4++) {
               var2x = var0.a(var2x, this.c.a(var1[var4]));
            }

            return var2x;
         }
      };
   }

   private static <unrepresentable> c(final kP var0, final double var1) {
      return new lc() {
         @Override
         public final double a(double var1x) {
            return var0.a(var1, var1x);
         }
      };
   }

   public static <unrepresentable> a(final kP var0, final double var1) {
      return new lc() {
         @Override
         public final double a(double var1x) {
            return var0.a(var1x, var1);
         }
      };
   }

   public static double[] a(lc var0, double var1, double var3, int var5) {
      if (var5 <= 0) {
         throw new pk(px.NOT_POSITIVE_NUMBER_OF_SAMPLES, var5);
      }

      if (var1 >= var3) {
         throw new pm(var1, var3, false);
      }

      double[] var6 = new double[var5];
      double var7 = (var3 - var1) / var5;

      for (int var9 = 0; var9 < var5; var9++) {
         var6[var9] = var0.a(var1 + var9 * var7);
      }

      return var6;
   }

   @Deprecated
   public static <unrepresentable> a(final ln var0) {
      return new kS() {
         @Override
         public final double a(double var1) {
            return var0.a(var1);
         }

         @Override
         public final lc a() {
            return new lc() {
               @Override
               public final double a(double var1) {
                  return var0.a(new lg(1, 1, 0, var1)).a(1);
               }
            };
         }
      };
   }

   @Deprecated
   private static <unrepresentable> a(final kS var0) {
      return new ln() {
         @Override
         public final double a(double var1) {
            return var0.a(var1);
         }

         @Override
         public final lg a(lg var1) {
            lf var2 = var1.a;
            switch (var1.a.b) {
               case 0:
                  var2 = var1.a;
                  return new lg(var1.a.a, 0, var0.a(var1.b[0]));
               case 1:
                  var2 = var1.a;
                  int var10 = var1.a.a;
                  double[] var3;
                  (var3 = new double[var1.a.a + 1])[0] = var0.a(var1.b[0]);
                  double var4 = var0.a().a(var1.b[0]);
                  int[] var6 = new int[var10];

                  for (int var7 = 0; var7 < var10; var7++) {
                     var6[var7] = 1;
                     var3[var7 + 1] = var4 * var1.a(var6);
                     var6[var7] = 0;
                  }

                  return new lg(var10, 1, var3);
               default:
                  var2 = var1.a;
                  throw new pm(var1.a.b, 1, true);
            }
         }
      };
   }

   @Deprecated
   public static <unrepresentable> a(final lk var0) {
      return new kQ() {
         @Override
         public final double a(double[] var1) {
            return var0.a(var1);
         }

         @Override
         public final <unrepresentable> a(final int var1) {
            return new kW() {
               @Override
               public final double a(double[] var1x) {
                  int var2;
                  lg[] var3 = new lg[var2 = var1x.length];

                  for (int var4 = 0; var4 < var2; var4++) {
                     if (var4 == var1) {
                        var3[var4] = new lg(1, 1, 0, var1x[var4]);
                     } else {
                        var3[var4] = new lg(1, 1, var1x[var4]);
                     }
                  }

                  return var0.a(var3).a(1);
               }
            };
         }

         @Override
         public final <unrepresentable> a() {
            return new kY() {
               @Override
               public final double[] a(double[] var1) {
                  int var2x;
                  lg[] var3x = new lg[var2x = var1.length];

                  for (int var4x = 0; var4x < var2x; var4x++) {
                     var3x[var4x] = new lg(var2x, 1, var4x, var1[var4x]);
                  }

                  lg var8 = var0.a(var3x);
                  var1 = new double[var2x];
                  int[] var7 = new int[var2x];

                  for (int var5x = 0; var5x < var2x; var5x++) {
                     var7[var5x] = 1;
                     var1[var5x] = var8.a(var7);
                     var7[var5x] = 0;
                  }

                  return var1;
               }
            };
         }
      };
   }

   @Deprecated
   private static <unrepresentable> a(final kQ var0) {
      return new lk() {
         @Override
         public final double a(double[] var1) {
            return var0.a(var1);
         }

         @Override
         public final lg a(lg[] var1) {
            lg var7;
            int var2 = (var7 = var1[0]).a.a;
            int var3 = (var7 = var1[0]).a.b;
            int var4 = var1.length;
            if (var3 > 1) {
               throw new pm(var3, 1, true);
            }

            for (int var5 = 0; var5 < var4; var5++) {
               if ((var7 = var1[var5]).a.a != var2) {
                  throw new oS((var7 = var1[var5]).a.a, var2);
               }

               if ((var7 = var1[var5]).a.b != var3) {
                  throw new oS((var7 = var1[var5]).a.b, var3);
               }
            }

            double[] var10 = new double[var4];

            for (int var6 = 0; var6 < var4; var6++) {
               var10[var6] = (var7 = var1[var6]).b[0];
            }

            double var12 = var0.a(var10);
            var10 = var0.a().a(var10);
            double[] var8;
            (var8 = new double[var2 + 1])[0] = var12;
            int[] var13 = new int[var2];

            for (int var20 = 0; var20 < var2; var20++) {
               var13[var20] = 1;

               for (int var9 = 0; var9 < var4; var9++) {
                  var8[var20 + 1] = var8[var20 + 1] + var10[var9] * var1[var9].a(var13);
               }

               var13[var20] = 0;
            }

            return new lg(var2, var3, var8);
         }
      };
   }

   @Deprecated
   public static <unrepresentable> a(final ll var0) {
      return new kR() {
         @Override
         public final double[] a(double[] var1) {
            return var0.a(var1);
         }

         @Override
         public final kX a() {
            return new kX() {
               @Override
               public final double[][] a(double[] var1) {
                  int var2;
                  lg[] var3 = new lg[var2 = var1.length];

                  for (int var4 = 0; var4 < var2; var4++) {
                     var3[var4] = new lg(var2, 1, var4, var1[var4]);
                  }

                  lg[] var9;
                  double[][] var7 = new double[(var9 = var0.a(var3)).length][var2];
                  int[] var8 = new int[var2];

                  for (int var5 = 0; var5 < var9.length; var5++) {
                     for (int var6 = 0; var6 < var2; var6++) {
                        var8[var6] = 1;
                        var7[var5][var6] = var9[var5].a(var8);
                        var8[var6] = 0;
                     }
                  }

                  return var7;
               }
            };
         }
      };
   }

   @Deprecated
   public static <unrepresentable> a(final kR var0) {
      return new ll() {
         @Override
         public final double[] a(double[] var1) {
            return var0.a(var1);
         }

         @Override
         public final lg[] a(lg[] var1) {
            lg var7;
            int var2 = (var7 = var1[0]).a.a;
            int var3 = (var7 = var1[0]).a.b;
            int var4 = var1.length;
            if (var3 > 1) {
               throw new pm(var3, 1, true);
            }

            for (int var5 = 0; var5 < var4; var5++) {
               if ((var7 = var1[var5]).a.a != var2) {
                  throw new oS((var7 = var1[var5]).a.a, var2);
               }

               if ((var7 = var1[var5]).a.b != var3) {
                  throw new oS((var7 = var1[var5]).a.b, var3);
               }
            }

            double[] var13 = new double[var4];

            for (int var6 = 0; var6 < var4; var6++) {
               var13[var6] = (var7 = var1[var6]).b[0];
            }

            double[] var15 = var0.a(var13);
            double[][] var14 = var0.a().a(var13);
            lg[] var22 = new lg[var15.length];

            for (int var8 = 0; var8 < var22.length; var8++) {
               double[] var9;
               (var9 = new double[var2 + 1])[0] = var15[var8];
               int[] var10 = new int[var2];

               for (int var11 = 0; var11 < var2; var11++) {
                  var10[var11] = 1;

                  for (int var12 = 0; var12 < var4; var12++) {
                     var9[var11 + 1] = var9[var11 + 1] + var14[var8][var12] * var1[var12].a(var10);
                  }

                  var10[var11] = 0;
               }

               var22[var8] = new lg(var2, var3, var9);
            }

            return var22;
         }
      };
   }
}
