package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;
import java.util.Comparator;

@Deprecated
public final class pG extends pE<lL.a> {
   private pG(xF var1) {
      super(var1);
   }

   private double[] a(double[] var1) {
      lL.a var2 = new lL.a(this) {
         // $VF: synthetic field
         private pG a;

         {
            this.a = var1;
         }

         @Override
         public final double a(double var1, double... var3x) {
            double var4x = Double.POSITIVE_INFINITY;

            try {
               var4x = super.a(var1, var3x);
            } catch (pk var6) {
            }

            return var4x;
         }

         @Override
         public final double[] b(double var1, double... var3x) {
            double[] var4x = new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};

            try {
               var4x = super.b(var1, var3x);
            } catch (pk var5x) {
            }

            return var4x;
         }
      };
      return this.a(var2, var1);
   }

   private double[] c() {
      pG.a var1 = new pG.a(this.a());
      double[] var4 = new double[]{var1.a, var1.b, var1.c};
      double[] var2 = var4;
      pG var5 = this;
      lL.a var3 = new lL.a(var5) {
         // $VF: synthetic field
         private pG a;

         {
            this.a = var1;
         }

         @Override
         public final double a(double var1, double... var3x) {
            double var4x = Double.POSITIVE_INFINITY;

            try {
               var4x = super.a(var1, var3x);
            } catch (pk var6) {
            }

            return var4x;
         }

         @Override
         public final double[] b(double var1, double... var3x) {
            double[] var4x = new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};

            try {
               var4x = super.b(var1, var3x);
            } catch (pk var5x) {
            }

            return var4x;
         }
      };
      return var5.a(var3, var2);
   }

   public static class a {
      final double a;
      final double b;
      final double c;

      public a(pM[] var1) {
         if (var1 == null) {
            throw new pl(px.INPUT_ARRAY);
         }

         if (var1.length < 3) {
            throw new pn(var1.length, 3, true);
         }

         pM[] var2 = var1;
         pG.a var3 = this;
         var2 = (pM[])var2.clone();
         Comparator var4 = new Comparator<pM>(var3) {
            // $VF: synthetic field
            private pG.a a;

            {
               this.a = var1;
            }

            private static int a(pM var0, pM var1x) {
               if (var0 == null && var1x == null) {
                  return 0;
               } else if (var0 == null) {
                  return -1;
               } else if (var1x == null) {
                  return 1;
               } else {
                  int var2x;
                  if ((var2x = Double.compare(var0.b, var1x.b)) < 0) {
                     return -1;
                  } else if (var2x > 0) {
                     return 1;
                  } else if ((var2x = Double.compare(var0.c, var1x.c)) < 0) {
                     return -1;
                  } else if (var2x > 0) {
                     return 1;
                  } else {
                     int var3x;
                     if ((var3x = Double.compare(var0.a, var1x.a)) < 0) {
                        return -1;
                     } else {
                        return var3x > 0 ? 1 : 0;
                     }
                  }
               }
            }

            // $VF: synthetic method
            // $VF: bridge method
            @Override
            public final int compare(Object var1, Object var2) {
               pM var10000 = var1;
               var2 = var2;
               var1 = var10000;
               if (var10000 != null || var2 != null) {
                  if (var1 == null) {
                     return -1;
                  }

                  if (var2 == null) {
                     return 1;
                  }

                  int var3;
                  if ((var3 = Double.compare(var1.b, var2.b)) < 0) {
                     return -1;
                  }

                  if (var3 > 0) {
                     return 1;
                  }

                  if ((var3 = Double.compare(var1.c, var2.c)) < 0) {
                     return -1;
                  }

                  if (var3 > 0) {
                     return 1;
                  }

                  int var5;
                  if ((var5 = Double.compare(var1.a, var2.a)) < 0) {
                     return -1;
                  }

                  if (var5 > 0) {
                     return 1;
                  }
               }

               return 0;
            }
         };
         Arrays.sort(var2, var4);
         var1 = var2;
         double[] var6 = this.b(var1);
         this.a = var6[0];
         this.b = var6[1];
         this.c = var6[2];
      }

      public final double[] a() {
         return new double[]{this.a, this.b, this.c};
      }

      private pM[] a(pM[] var1) {
         var1 = (pM[])var1.clone();
         Comparator var2 = new Comparator<pM>(this) {
            // $VF: synthetic field
            private pG.a a;

            {
               this.a = var1;
            }

            private static int a(pM var0, pM var1x) {
               if (var0 == null && var1x == null) {
                  return 0;
               } else if (var0 == null) {
                  return -1;
               } else if (var1x == null) {
                  return 1;
               } else {
                  int var2x;
                  if ((var2x = Double.compare(var0.b, var1x.b)) < 0) {
                     return -1;
                  } else if (var2x > 0) {
                     return 1;
                  } else if ((var2x = Double.compare(var0.c, var1x.c)) < 0) {
                     return -1;
                  } else if (var2x > 0) {
                     return 1;
                  } else {
                     int var3x;
                     if ((var3x = Double.compare(var0.a, var1x.a)) < 0) {
                        return -1;
                     } else {
                        return var3x > 0 ? 1 : 0;
                     }
                  }
               }
            }

            // $VF: synthetic method
            // $VF: bridge method
            @Override
            public final int compare(Object var1, Object var2) {
               pM var10000 = var1;
               var2 = var2;
               var1 = var10000;
               if (var10000 != null || var2 != null) {
                  if (var1 == null) {
                     return -1;
                  }

                  if (var2 == null) {
                     return 1;
                  }

                  int var3;
                  if ((var3 = Double.compare(var1.b, var2.b)) < 0) {
                     return -1;
                  }

                  if (var3 > 0) {
                     return 1;
                  }

                  if ((var3 = Double.compare(var1.c, var2.c)) < 0) {
                     return -1;
                  }

                  if (var3 > 0) {
                     return 1;
                  }

                  int var5;
                  if ((var5 = Double.compare(var1.a, var2.a)) < 0) {
                     return -1;
                  }

                  if (var5 > 0) {
                     return 1;
                  }
               }

               return 0;
            }
         };
         Arrays.sort(var1, var2);
         return var1;
      }

      private double[] b(pM[] var1) {
         pM[] var2 = var1;
         int var3 = 0;

         for (int var4 = 1; var4 < var2.length; var4++) {
            pM var7;
            if ((var7 = var2[var4]).c > (var7 = var2[var3]).c) {
               var3 = var4;
            }
         }

         int var16 = var3;
         pM var19;
         double var17 = (var19 = var1[var16]).c;
         double var5 = (var19 = var1[var16]).b;

         try {
            double var9 = var17 + (var5 - var17) / 2.0;
            double var11 = this.a(var1, var16, -1, var9);
            var23 = this.a(var1, var16, 1, var9) - var11;
         } catch (po var15) {
            pM var22;
            var23 = (var19 = var1[var1.length - 1]).b - (var22 = var1[0]).b;
         }

         double var24 = var23 / (2.0 * BY.a(2.0 * BY.k(2.0)));
         return new double[]{var17, var5, var24};
      }

      private static int c(pM[] var0) {
         int var1 = 0;

         for (int var2 = 1; var2 < var0.length; var2++) {
            pM var3;
            if ((var3 = var0[var2]).c > (var3 = var0[var1]).c) {
               var1 = var2;
            }
         }

         return var1;
      }

      private double a(pM[] var1, int var2, int var3, double var4) {
         if (var3 == 0) {
            throw new pr();
         } else {
            pM[] var6;
            pM var8 = (var6 = this.b(var1, var2, var3, var4))[0];
            pM var7 = var6[1];
            if (var8.c == var4) {
               return var8.b;
            } else {
               return var7.c == var4 ? var7.b : var8.b + (var4 - var8.c) * (var7.b - var8.b) / (var7.c - var8.c);
            }
         }
      }

      private pM[] b(pM[] var1, int var2, int var3, double var4) {
         if (var3 == 0) {
            throw new pr();
         }

         for (int var9 = var2; var3 < 0 ? var9 + var3 >= 0 : var9 + var3 < var1.length; var9 += var3) {
            pM var6 = var1[var9];
            pM var7 = var1[var9 + var3];
            if (a(var4, var6.c, var7.c)) {
               if (var3 < 0) {
                  return new pM[]{var7, var6};
               }

               return new pM[]{var6, var7};
            }
         }

         throw new po(var4, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
      }

      private static boolean a(double var0, double var2, double var4) {
         return var0 >= var2 && var0 <= var4 || var0 >= var4 && var0 <= var2;
      }
   }
}
