package org.valkyrienskies.core.impl.shadow;

import java.math.BigInteger;

public final class BM {
   private BM() {
   }

   public static int a(int var0, int var1) {
      long var2;
      if ((var2 = (long)var0 + var1) >= -2147483648L && var2 <= 2147483647L) {
         return (int)var2;
      } else {
         throw new oU(px.OVERFLOW_IN_ADDITION, var0, var1);
      }
   }

   private static long b(long var0, long var2) {
      return a(var0, var2, px.OVERFLOW_IN_ADDITION);
   }

   @Deprecated
   private static long f(int var0, int var1) {
      return BR.a(var0, var1);
   }

   @Deprecated
   private static double g(int var0, int var1) {
      return BR.b(var0, var1);
   }

   @Deprecated
   private static double h(int var0, int var1) {
      return BR.c(var0, var1);
   }

   @Deprecated
   private static long a(int var0) {
      return BR.a(var0);
   }

   @Deprecated
   private static double b(int var0) {
      return BR.b(var0);
   }

   @Deprecated
   private static double c(int var0) {
      return BR.c(var0);
   }

   public static int b(int var0, int var1) {
      int var2 = var0;
      int var3 = var1;
      if (var2 != 0 && var3 != 0) {
         long var4 = var2;
         long var6 = var3;
         boolean var8 = false;
         if (var2 < 0) {
            if (Integer.MIN_VALUE == var2) {
               var8 = true;
            } else {
               var2 = -var2;
            }

            var4 = -var4;
         }

         if (var3 < 0) {
            if (Integer.MIN_VALUE == var3) {
               var8 = true;
            } else {
               var3 = -var3;
            }

            var6 = -var6;
         }

         if (var8) {
            if (var4 == var6) {
               throw new oU(px.GCD_OVERFLOW_32_BITS, var0, var1);
            }

            long var9 = var6;
            var6 = var4;
            if ((var4 = var9 % var4) == 0L) {
               if (var6 > 2147483647L) {
                  throw new oU(px.GCD_OVERFLOW_32_BITS, var0, var1);
               }

               return (int)var6;
            }

            var9 = var6;
            var3 = (int)var4;
            var2 = (int)(var9 % var4);
         }

         return i(var2, var3);
      } else if (var2 != Integer.MIN_VALUE && var3 != Integer.MIN_VALUE) {
         return BY.a(var2 + var3);
      } else {
         throw new oU(px.GCD_OVERFLOW_32_BITS, var0, var1);
      }
   }

   private static int i(int var0, int var1) {
      if (var0 == 0) {
         return var1;
      }

      if (var1 == 0) {
         return var0;
      }

      int var2 = Integer.numberOfTrailingZeros(var0);
      var0 >>= var2;
      int var3 = Integer.numberOfTrailingZeros(var1);
      var1 >>= var3;
      var2 = BY.a(var2, var3);

      while (var0 != var1) {
         var3 = var0 - var1;
         var1 = Math.min(var0, var1);
         var0 = (var0 = Math.abs(var3)) >> Integer.numberOfTrailingZeros(var0);
      }

      return var0 << var2;
   }

   private static long c(long var0, long var2) {
      long var4 = var0;
      long var6 = var2;
      if (var4 != 0L && var6 != 0L) {
         if (var4 > 0L) {
            var4 = -var4;
         }

         if (var6 > 0L) {
            var6 = -var6;
         }

         int var8;
         for (var8 = 0; (var4 & 1L) == 0L && (var6 & 1L) == 0L && var8 < 63; var8++) {
            var4 /= 2L;
            var6 /= 2L;
         }

         if (var8 == 63) {
            throw new oU(px.GCD_OVERFLOW_64_BITS, var0, var2);
         }

         long var10000 = (var4 & 1L) == 1L ? var6 : -(var4 / 2L);

         while (true) {
            long var9 = var10000;

            while ((var9 & 1L) != 0L) {
               if (var9 > 0L) {
                  var4 = -var9;
               } else {
                  var6 = var9;
               }

               if ((var9 = (var6 - var4) / 2L) == 0L) {
                  return -var4 * (1L << var8);
               }
            }

            var10000 = var9 / 2L;
         }
      } else if (var4 != Long.MIN_VALUE && var6 != Long.MIN_VALUE) {
         return BY.a(var4) + BY.a(var6);
      } else {
         throw new oU(px.GCD_OVERFLOW_64_BITS, var0, var2);
      }
   }

   private static int j(int var0, int var1) {
      if (var0 != 0 && var1 != 0) {
         int var2;
         if ((var2 = BY.a(c(var0 / b(var0, var1), var1))) == Integer.MIN_VALUE) {
            throw new oU(px.LCM_OVERFLOW_32_BITS, var0, var1);
         } else {
            return var2;
         }
      } else {
         return 0;
      }
   }

   private static long d(long var0, long var2) {
      if (var0 != 0L && var2 != 0L) {
         long var8 = var2;
         long var6 = var0;
         long var10 = var0;
         long var12 = var8;
         long var10001;
         if (var10 != 0L && var12 != 0L) {
            if (var10 > 0L) {
               var10 = -var10;
            }

            if (var12 > 0L) {
               var12 = -var12;
            }

            int var4;
            for (var4 = 0; (var10 & 1L) == 0L && (var12 & 1L) == 0L && var4 < 63; var4++) {
               var10 /= 2L;
               var12 /= 2L;
            }

            if (var4 == 63) {
               throw new oU(px.GCD_OVERFLOW_64_BITS, var6, var8);
            }

            var10001 = (var10 & 1L) == 1L ? var12 : -(var10 / 2L);

            label57:
            while (true) {
               long var15 = var10001;

               while ((var15 & 1L) != 0L) {
                  if (var15 > 0L) {
                     var10 = -var15;
                  } else {
                     var12 = var15;
                  }

                  if ((var15 = (var12 - var10) / 2L) == 0L) {
                     var10001 = -var10 * (1L << var4);
                     break label57;
                  }
               }

               var10001 = var15 / 2L;
            }
         } else {
            if (var10 == Long.MIN_VALUE || var12 == Long.MIN_VALUE) {
               throw new oU(px.GCD_OVERFLOW_64_BITS, var6, var8);
            }

            var10001 = BY.a(var10) + BY.a(var12);
         }

         long var17;
         if ((var17 = BY.a(a(var0 / var10001, var2))) == Long.MIN_VALUE) {
            throw new oU(px.LCM_OVERFLOW_64_BITS, var0, var2);
         } else {
            return var17;
         }
      } else {
         return 0L;
      }
   }

   public static int c(int var0, int var1) {
      long var2;
      if ((var2 = (long)var0 * var1) >= -2147483648L && var2 <= 2147483647L) {
         return (int)var2;
      } else {
         throw new oU();
      }
   }

   public static long a(long var0, long var2) {
      long var4;
      if (var0 > var2) {
         var4 = a(var2, var0);
      } else if (var0 < 0L) {
         if (var2 < 0L) {
            if (var0 < Long.MAX_VALUE / var2) {
               throw new oU();
            }

            var4 = var0 * var2;
         } else if (var2 > 0L) {
            if (Long.MIN_VALUE / var2 > var0) {
               throw new oU();
            }

            var4 = var0 * var2;
         } else {
            var4 = 0L;
         }
      } else if (var0 > 0L) {
         if (var0 > Long.MAX_VALUE / var2) {
            throw new oU();
         }

         var4 = var0 * var2;
      } else {
         var4 = 0L;
      }

      return var4;
   }

   public static int d(int var0, int var1) {
      long var2;
      if ((var2 = (long)var0 - var1) >= -2147483648L && var2 <= 2147483647L) {
         return (int)var2;
      } else {
         throw new oU(px.OVERFLOW_IN_SUBTRACTION, var0, var1);
      }
   }

   private static long e(long var0, long var2) {
      long var4;
      if (var2 == Long.MIN_VALUE) {
         if (var0 >= 0L) {
            throw new oU(px.OVERFLOW_IN_ADDITION, var0, -var2);
         }

         var4 = var0 - var2;
      } else {
         var4 = a(var0, -var2, px.OVERFLOW_IN_ADDITION);
      }

      return var4;
   }

   public static int e(int var0, int var1) {
      if (var1 < 0) {
         throw new pj(px.EXPONENT, var1);
      }

      try {
         int var2 = var1;
         int var3 = 1;
         int var4 = var0;

         while (true) {
            if ((var2 & 1) != 0) {
               var3 = c(var3, var4);
            }

            if ((var2 >>= 1) == 0) {
               return var3;
            }

            var4 = c(var4, var4);
         }
      } catch (oU var5) {
         var5.a.a(px.OVERFLOW);
         var5.a.a(px.BASE, new Object[]{var0});
         var5.a.a(px.EXPONENT, new Object[]{var1});
         throw var5;
      }
   }

   @Deprecated
   private static int a(int var0, long var1) {
      if (var1 < 0L) {
         throw new pj(px.EXPONENT, var1);
      }

      int var3 = 1;
      var0 = var0;

      while (var1 != 0L) {
         if ((var1 & 1L) != 0L) {
            var3 *= var0;
         }

         var0 *= var0;
         var1 >>= 1;
      }

      return var3;
   }

   private static long a(long var0, int var2) {
      if (var2 < 0) {
         throw new pj(px.EXPONENT, var2);
      }

      try {
         int var3 = var2;
         long var4 = 1L;
         long var6 = var0;

         while (true) {
            if ((var3 & 1) != 0) {
               var4 = a(var4, var6);
            }

            if ((var3 >>= 1) == 0) {
               return var4;
            }

            var6 = a(var6, var6);
         }
      } catch (oU var8) {
         var8.a.a(px.OVERFLOW);
         var8.a.a(px.BASE, new Object[]{var0});
         var8.a.a(px.EXPONENT, new Object[]{var2});
         throw var8;
      }
   }

   @Deprecated
   private static long f(long var0, long var2) {
      if (var2 < 0L) {
         throw new pj(px.EXPONENT, var2);
      }

      long var4 = 1L;
      long var6 = var0;

      while (var2 != 0L) {
         if ((var2 & 1L) != 0L) {
            var4 *= var6;
         }

         var6 *= var6;
         var2 >>= 1;
      }

      return var4;
   }

   private static BigInteger a(BigInteger var0, int var1) {
      if (var1 < 0) {
         throw new pj(px.EXPONENT, var1);
      } else {
         return var0.pow(var1);
      }
   }

   public static BigInteger a(BigInteger var0, long var1) {
      if (var1 < 0L) {
         throw new pj(px.EXPONENT, var1);
      }

      BigInteger var3 = BigInteger.ONE;
      var0 = var0;

      while (var1 != 0L) {
         if ((var1 & 1L) != 0L) {
            var3 = var3.multiply(var0);
         }

         var0 = var0.multiply(var0);
         var1 >>= 1;
      }

      return var3;
   }

   public static BigInteger a(BigInteger var0, BigInteger var1) {
      if (var1.compareTo(BigInteger.ZERO) < 0) {
         throw new pj(px.EXPONENT, var1);
      }

      BigInteger var2 = BigInteger.ONE;
      var0 = var0;

      while (!BigInteger.ZERO.equals(var1)) {
         if (var1.testBit(0)) {
            var2 = var2.multiply(var0);
         }

         var0 = var0.multiply(var0);
         var1 = var1.shiftRight(1);
      }

      return var2;
   }

   @Deprecated
   private static long k(int var0, int var1) {
      return BR.d(var0, var1);
   }

   private static long a(long var0, long var2, px var4) {
      long var5 = var0 + var2;
      if (!((var0 ^ var2) < 0L | (var0 ^ var5) >= 0L)) {
         throw new oU(var4, var0, var2);
      } else {
         return var5;
      }
   }

   public static boolean a(long var0) {
      return var0 > 0L && (var0 & var0 - 1L) == 0L;
   }
}
