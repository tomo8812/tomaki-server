package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

@Deprecated
public final class se implements Comparable<se> {
   private static final long a = Long.MIN_VALUE;
   private static final long b = 9218868437227405312L;
   private static final long c = 4503599627370495L;
   private static final long d = 4503599627370496L;
   private double[] e;
   private int f;
   private int g;
   private long[] h;
   private boolean i;
   private boolean j;
   private boolean k;

   private se(double... var1) {
      this.e = (double[])var1.clone();
      int var2 = Integer.MIN_VALUE;
      this.g = Integer.MAX_VALUE;
      this.i = false;
      this.j = false;
      this.k = false;

      for (int var3 = 0; var3 < var1.length; var3++) {
         if (Double.isInfinite(var1[var3])) {
            if (var1[var3] < 0.0) {
               this.j = true;
            } else {
               this.i = true;
            }
         } else if (Double.isNaN(var1[var3])) {
            this.k = true;
         } else {
            long var4;
            long var6;
            if ((var6 = c(var4 = Double.doubleToLongBits(var1[var3]))) != 0L) {
               int var15 = b(var4);
               long var11 = var6;
               long var13 = 4294967295L;
               long var5 = (long)32;
               long var8 = (long)0;

               while (var5 != 0) {
                  if ((var11 & var13) != var11) {
                     var8 |= var5;
                     var11 >>= var5;
                  }

                  var5 >>= 1;
                  var13 >>= var5;
               }

               var2 = BY.b(var2, (int)(var15 + var8));
               var11 = var6;
               var13 = -4294967296L;
               var5 = (long)32;
               var8 = (long)0;

               while (var5 != 0) {
                  if ((var11 & var13) == var11) {
                     var8 |= var5;
                     var11 >>= var5;
                  }

                  var5 >>= 1;
                  var13 >>= var5;
               }

               this.g = BY.a(this.g, (int)(var15 + var8));
            }
         }
      }

      if (this.i && this.j) {
         this.i = false;
         this.j = false;
         this.k = true;
      }

      if (this.g <= var2) {
         this.a(var2 + 16);
      } else {
         this.h = new long[]{0L};
      }
   }

   private void a(int var1) {
      this.f = var1 + 31;
      this.f = this.f - this.f % 32;
      if (this.h == null || this.h.length != 1 || this.h[0] != 0L) {
         var1 = (this.f + 1 - this.g + 62) / 63;
         this.h = new long[this.e.length * var1];
         var1 = 0;
         int var2 = 62;
         long var6 = 0L;

         for (int var3 = this.f; var1 < this.h.length; var3--) {
            for (int var4 = 0; var4 < this.e.length; var4++) {
               if (this.a(var4, var3) != 0) {
                  var6 |= 1L << var2;
               }

               if (var2-- == 0) {
                  this.h[var1++] = var6;
                  var6 = 0L;
                  var2 = 62;
               }
            }
         }
      }
   }

   private int a(se var1) {
      if (this.e.length == var1.e.length) {
         if (this.k) {
            return 1;
         }

         if (var1.k) {
            return -1;
         }

         if (!this.j && !var1.i) {
            if (!this.i && !var1.j) {
               if (this.f < var1.f) {
                  this.a(var1.f);
               } else if (this.f > var1.f) {
                  var1.a(this.f);
               }

               int var2 = BY.a(this.h.length, var1.h.length);

               for (int var3 = 0; var3 < var2; var3++) {
                  if (this.h[var3] < var1.h[var3]) {
                     return -1;
                  }

                  if (this.h[var3] > var1.h[var3]) {
                     return 1;
                  }
               }

               if (this.h.length < var1.h.length) {
                  return -1;
               } else {
                  return this.h.length > var1.h.length ? 1 : 0;
               }
            } else {
               return 1;
            }
         } else {
            return -1;
         }
      } else {
         return this.e.length - var1.e.length;
      }
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return other instanceof se ? this.a((se)other) == 0 : false;
      }
   }

   @Override
   public final int hashCode() {
      return ((((Arrays.hashCode(this.e) * 37 + this.f) * 37 + this.g) * 37 + (this.i ? 97 : 71)) * 37 + (this.j ? 97 : 71)) * 37 + (this.k ? 97 : 71);
   }

   private double[] a() {
      return (double[])this.e.clone();
   }

   private static long a(long var0) {
      return var0 & Long.MIN_VALUE;
   }

   private static int b(long var0) {
      return (int)((var0 & 9218868437227405312L) >> 52) - 1075;
   }

   private static long c(long var0) {
      return (var0 & 9218868437227405312L) == 0L ? (var0 & 4503599627370495L) << 1 : 4503599627370496L | var0 & 4503599627370495L;
   }

   private static int d(long var0) {
      long var2 = var0;
      long var4 = 4294967295L;
      var0 = (long)32;
      long var1 = (long)0;

      while (var0 != 0) {
         if ((var2 & var4) != var2) {
            var1 |= var0;
            var2 >>= var0;
         }

         var0 >>= 1;
         var4 >>= var0;
      }

      return (int)var1;
   }

   private static int e(long var0) {
      long var2 = var0;
      long var4 = -4294967296L;
      var0 = (long)32;
      long var1 = (long)0;

      while (var0 != 0) {
         if ((var2 & var4) == var2) {
            var1 |= var0;
            var2 >>= var0;
         }

         var0 >>= 1;
         var4 >>= var0;
      }

      return (int)var1;
   }

   private int a(int var1, int var2) {
      long var3;
      var1 = b(var3 = Double.doubleToLongBits(this.e[var1]));
      if (var2 < var1 || var2 > this.f) {
         return 0;
      } else if (var2 == this.f) {
         return (var3 & Long.MIN_VALUE) == 0L ? 1 : 0;
      } else if (var2 > var1 + 52) {
         return (var3 & Long.MIN_VALUE) == 0L ? 0 : 1;
      } else {
         return (int)(((var3 & Long.MIN_VALUE) == 0L ? c(var3) : -c(var3)) >> var2 - var1 & 1L);
      }
   }

   // $VF: synthetic method
   @Override
   public final int compareTo(Object var1) {
      return this.a((se)var1);
   }
}
