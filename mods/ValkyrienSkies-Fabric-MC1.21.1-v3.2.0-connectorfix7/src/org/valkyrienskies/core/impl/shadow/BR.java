package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

public final class BR {
   private static long[] a = new long[]{
      1L,
      1L,
      2L,
      6L,
      24L,
      120L,
      720L,
      5040L,
      40320L,
      362880L,
      3628800L,
      39916800L,
      479001600L,
      6227020800L,
      87178291200L,
      1307674368000L,
      20922789888000L,
      355687428096000L,
      6402373705728000L,
      121645100408832000L,
      2432902008176640000L
   };
   private static AtomicReference<long[][]> b = new AtomicReference<>(null);

   private BR() {
   }

   public static long a(int var0, int var1) {
      while (true) {
         e(var0, var1);
         if (var0 != var1 && var1 != 0) {
            if (var1 != 1 && var1 != var0 - 1) {
               if (var1 > var0 / 2) {
                  var1 = var0 - var1;
                  var0 = var0;
                  continue;
               }

               long var2 = 1L;
               if (var0 <= 61) {
                  var0 = var0 - var1 + 1;

                  for (int var4 = 1; var4 <= var1; var4++) {
                     var2 = var2 * var0 / var4;
                     var0++;
                  }
               } else if (var0 <= 66) {
                  var0 = var0 - var1 + 1;

                  for (int var11 = 1; var11 <= var1; var11++) {
                     long var6 = BM.b(var0, var11);
                     var2 = var2 / (var11 / var6) * (var0 / var6);
                     var0++;
                  }
               } else {
                  var0 = var0 - var1 + 1;

                  for (int var12 = 1; var12 <= var1; var12++) {
                     long var13 = BM.b(var0, var12);
                     var2 = BM.a(var2 / (var12 / var13), var0 / var13);
                     var0++;
                  }
               }

               return var2;
            }

            return var0;
         }

         return 1L;
      }
   }

   public static double b(int var0, int var1) {
      while (true) {
         e(var0, var1);
         if (var0 != var1 && var1 != 0) {
            if (var1 != 1 && var1 != var0 - 1) {
               if (var1 > var0 / 2) {
                  var1 = var0 - var1;
                  var0 = var0;
                  continue;
               }

               if (var0 < 67) {
                  return a(var0, var1);
               }

               double var2 = 1.0;

               for (int var4 = 1; var4 <= var1; var4++) {
                  var2 *= (double)(var0 - var1 + var4) / var4;
               }

               return BY.y(var2 + 0.5);
            }

            return var0;
         }

         return 1.0;
      }
   }

   public static double c(int var0, int var1) {
      while (true) {
         e(var0, var1);
         if (var0 != var1 && var1 != 0) {
            if (var1 != 1 && var1 != var0 - 1) {
               if (var0 < 67) {
                  return BY.k(a(var0, var1));
               }

               if (var0 < 1030) {
                  return BY.k(b(var0, var1));
               }

               if (var1 > var0 / 2) {
                  var1 = var0 - var1;
                  var0 = var0;
                  continue;
               }

               double var2 = 0.0;

               for (int var4 = var0 - var1 + 1; var4 <= var0; var4++) {
                  var2 += BY.k(var4);
               }

               for (int var5 = 2; var5 <= var1; var5++) {
                  var2 -= BY.k(var5);
               }

               return var2;
            }

            return BY.k(var0);
         }

         return 0.0;
      }
   }

   public static long a(int var0) {
      if (var0 < 0) {
         throw new pj(px.FACTORIAL_NEGATIVE_PARAMETER, var0);
      } else if (var0 > 20) {
         throw new oU();
      } else {
         return a[var0];
      }
   }

   public static double b(int var0) {
      if (var0 < 0) {
         throw new pj(px.FACTORIAL_NEGATIVE_PARAMETER, var0);
      } else {
         return var0 < 21 ? a[var0] : BY.y(BY.i(c(var0)) + 0.5);
      }
   }

   public static double c(int var0) {
      if (var0 < 0) {
         throw new pj(px.FACTORIAL_NEGATIVE_PARAMETER, var0);
      }

      if (var0 < 21) {
         return BY.k(a[var0]);
      }

      double var1 = 0.0;

      for (int var3 = 2; var3 <= var0; var3++) {
         var1 += BY.k(var3);
      }

      return var1;
   }

   public static long d(int var0, int var1) {
      if (var1 < 0) {
         throw new pj(var1);
      }

      if (var1 > var0) {
         throw new pm(var1, var0, true);
      }

      long[][] var2;
      if ((var2 = b.get()) == null) {
         (var2 = new long[26][])[0] = new long[]{1L};

         for (int var4 = 1; var4 < var2.length; var4++) {
            var2[var4] = new long[var4 + 1];
            var2[var4][0] = 0L;
            var2[var4][1] = 1L;
            var2[var4][var4] = 1L;

            for (int var5 = 2; var5 < var4; var5++) {
               var2[var4][var5] = var5 * var2[var4 - 1][var5] + var2[var4 - 1][var5 - 1];
            }
         }

         b.compareAndSet(null, var2);
      }

      if (var0 < var2.length) {
         return var2[var0][var1];
      }

      if (var1 == 0) {
         return 0L;
      }

      if (var1 != 1 && var1 != var0) {
         if (var1 == 2) {
            return (1L << var0 - 1) - 1L;
         }

         if (var1 == var0 - 1) {
            return a(var0, 2);
         }

         long var3 = 0L;
         long var8 = (var1 & 1) == 0 ? 1L : -1L;

         for (int var7 = 1; var7 <= var1; var7++) {
            var8 = -var8;
            if ((var3 += var8 * a(var1, var7) * BM.e(var7, var0)) < 0L) {
               throw new oU(px.ARGUMENT_OUTSIDE_DOMAIN, var0, 0, var2.length - 1);
            }
         }

         return var3 / a(var1);
      } else {
         return 1L;
      }
   }

   private static Iterator<int[]> f(int var0, int var1) {
      return new BQ(var0, var1).iterator();
   }

   public static void e(int var0, int var1) {
      if (var0 < var1) {
         throw new pm(px.BINOMIAL_INVALID_PARAMETERS_ORDER, var1, var0, true);
      }

      if (var0 < 0) {
         throw new pj(px.BINOMIAL_NEGATIVE_PARAMETER, var0);
      }
   }
}
