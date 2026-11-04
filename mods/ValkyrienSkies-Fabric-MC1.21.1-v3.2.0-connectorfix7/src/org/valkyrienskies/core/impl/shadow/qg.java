package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.math.BigInteger;

public class qg extends Number implements Serializable, Comparable<qg>, kN<qg> {
   private static qg e;
   public static final qg a = new qg(1, 1);
   public static final qg b = new qg(0, 1);
   private static qg f;
   private static qg g;
   private static qg h;
   private static qg i;
   private static qg j;
   private static qg k;
   private static qg l;
   private static qg m;
   private static qg n;
   private static qg o;
   private static qg p;
   private static final long q = 3698073679419233275L;
   private static final double r = 1.0E-5;
   final int c;
   final int d;

   public qg(double var1) {
      this(var1, 1.0E-5, 100);
   }

   private qg(double var1, double var3, int var5) {
      this(var1, 1.0E-5, Integer.MAX_VALUE, 100);
   }

   private qg(double var1, int var3) {
      this(var1, 0.0, var3, 100);
   }

   private qg(double var1, double var3, int var5, int var6) {
      double var7 = var1;
      long var9;
      if (BY.a(var9 = (long)BY.y(var1)) > 2147483647L) {
         throw new qh(var1, var9, 1L);
      }

      if (BY.w(var9 - var1) < var3) {
         this.d = (int)var9;
         this.c = 1;
      } else {
         long var11 = 1L;
         long var13 = 0L;
         long var15 = var9;
         long var17 = 1L;
         int var23 = 0;
         boolean var24 = false;

         long var19;
         long var21;
         while (true) {
            var23++;
            double var25;
            long var27;
            var19 = (var27 = (long)BY.y(var25 = 1.0 / (var7 - var9))) * var15 + var11;
            var21 = var27 * var17 + var13;
            if (BY.a(var19) <= 2147483647L && BY.a(var21) <= 2147483647L) {
               double var29 = (double)var19 / var21;
               if (var23 < var6 && BY.w(var29 - var1) > var3 && var21 < var5) {
                  var11 = var15;
                  var15 = var19;
                  var13 = var17;
                  var17 = var21;
                  var9 = var27;
                  var7 = var25;
               } else {
                  var24 = true;
               }

               if (!var24) {
                  continue;
               }
               break;
            }

            if (var3 != 0.0 || BY.a(var17) >= var5) {
               throw new qh(var1, var19, var21);
            }
            break;
         }

         if (var23 >= var6) {
            throw new qh(var1, var6);
         }

         if (var21 < var5) {
            this.d = (int)var19;
            this.c = (int)var21;
         } else {
            this.d = (int)var15;
            this.c = (int)var17;
         }
      }
   }

   private qg(int var1) {
      this(var1, 1);
   }

   public qg(int var1, int var2) {
      if (var2 == 0) {
         throw new oU(px.ZERO_DENOMINATOR_IN_FRACTION, var1, var2);
      }

      if (var2 < 0) {
         if (var1 == Integer.MIN_VALUE || var2 == Integer.MIN_VALUE) {
            throw new oU(px.OVERFLOW_IN_FRACTION, var1, var2);
         }

         var1 = -var1;
         var2 = -var2;
      }

      int var3;
      if ((var3 = BM.b(var1, var2)) > 1) {
         var1 /= var3;
         var2 /= var3;
      }

      if (var2 < 0) {
         var1 = -var1;
         var2 = -var2;
      }

      this.d = var1;
      this.c = var2;
   }

   private qg f() {
      qg var1;
      if (this.d >= 0) {
         var1 = this;
      } else {
         var1 = this.g();
      }

      return var1;
   }

   private int a(qg var1) {
      long var2 = (long)this.d * var1.c;
      long var4 = (long)this.c * var1.d;
      if (var2 < var4) {
         return -1;
      } else {
         return var2 > var4 ? 1 : 0;
      }
   }

   @Override
   public double doubleValue() {
      return (double)this.d / this.c;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof qg) {
         other = (qg)other;
         return this.d == ((qg)other).d && this.c == ((qg)other).c;
      } else {
         return false;
      }
   }

   @Override
   public float floatValue() {
      return (float)this.doubleValue();
   }

   public final int d() {
      return this.c;
   }

   public final int e() {
      return this.d;
   }

   @Override
   public int hashCode() {
      return 37 * (629 + this.d) + this.c;
   }

   @Override
   public int intValue() {
      return (int)this.doubleValue();
   }

   @Override
   public long longValue() {
      return (long)this.doubleValue();
   }

   private qg g() {
      if (this.d == Integer.MIN_VALUE) {
         throw new oU(px.OVERFLOW_IN_FRACTION, this.d, this.c);
      } else {
         return new qg(-this.d, this.c);
      }
   }

   private qg h() {
      return new qg(this.c, this.d);
   }

   private qg b(qg var1) {
      return this.a(var1, true);
   }

   private qg b(int var1) {
      return new qg(this.d + var1 * this.c, this.c);
   }

   private qg c(qg var1) {
      return this.a(var1, false);
   }

   private qg c(int var1) {
      return new qg(this.d - var1 * this.c, this.c);
   }

   private qg a(qg var1, boolean var2) {
      if (var1 == null) {
         throw new pl(px.FRACTION);
      } else if (this.d == 0) {
         return var2 ? var1 : var1.g();
      } else if (var1.d == 0) {
         return this;
      } else {
         int var3;
         if ((var3 = BM.b(this.c, var1.c)) == 1) {
            int var10 = BM.c(this.d, var1.c);
            int var11 = BM.c(var1.d, this.c);
            return new qg(var2 ? BM.a(var10, var11) : BM.d(var10, var11), BM.c(this.c, var1.c));
         } else {
            BigInteger var4 = BigInteger.valueOf(this.d).multiply(BigInteger.valueOf(var1.c / var3));
            BigInteger var5 = BigInteger.valueOf(var1.d).multiply(BigInteger.valueOf(this.c / var3));
            BigInteger var6;
            int var8;
            int var9 = (var8 = (var6 = var2 ? var4.add(var5) : var4.subtract(var5)).mod(BigInteger.valueOf(var3)).intValue()) == 0 ? var3 : BM.b(var8, var3);
            BigInteger var7;
            if ((var7 = var6.divide(BigInteger.valueOf(var9))).bitLength() > 31) {
               throw new oU(px.NUMERATOR_OVERFLOW_AFTER_MULTIPLY, var7);
            } else {
               return new qg(var7.intValue(), BM.c(this.c / var3, var1.c / var9));
            }
         }
      }
   }

   private qg d(qg var1) {
      if (var1 == null) {
         throw new pl(px.FRACTION);
      }

      if (this.d != 0 && var1.d != 0) {
         int var2 = BM.b(this.d, var1.c);
         int var3 = BM.b(var1.d, this.c);
         int var10000 = BM.c(this.d / var2, var1.d / var3);
         var2 = BM.c(this.c / var3, var1.c / var2);
         int var4 = var10000;
         if (var2 == 0) {
            throw new oU(px.ZERO_DENOMINATOR_IN_FRACTION, var4, var2);
         }

         if (var4 == 0) {
            return b;
         }

         if (var2 == Integer.MIN_VALUE && (var4 & 1) == 0) {
            var4 /= 2;
            var2 /= 2;
         }

         if (var2 < 0) {
            if (var4 == Integer.MIN_VALUE || var2 == Integer.MIN_VALUE) {
               throw new oU(px.OVERFLOW_IN_FRACTION, var4, var2);
            }

            var4 = -var4;
            var2 = -var2;
         }

         var3 = BM.b(var4, var2);
         int var5 = var4 / var3;
         var2 /= var3;
         return new qg(var5, var2);
      } else {
         return b;
      }
   }

   private qg d(int var1) {
      return this.d(new qg(var1));
   }

   private qg e(qg var1) {
      if (var1 == null) {
         throw new pl(px.FRACTION);
      } else if (var1.d == 0) {
         throw new oU(px.ZERO_FRACTION_TO_DIVIDE_BY, var1.d, var1.c);
      } else {
         return this.d(var1.h());
      }
   }

   private qg e(int var1) {
      return this.e(new qg(var1));
   }

   private double i() {
      return 100.0 * this.doubleValue();
   }

   private static qg a(int var0, int var1) {
      if (var1 == 0) {
         throw new oU(px.ZERO_DENOMINATOR_IN_FRACTION, var0, var1);
      }

      if (var0 == 0) {
         return b;
      }

      if (var1 == Integer.MIN_VALUE && (var0 & 1) == 0) {
         var0 /= 2;
         var1 /= 2;
      }

      if (var1 < 0) {
         if (var0 == Integer.MIN_VALUE || var1 == Integer.MIN_VALUE) {
            throw new oU(px.OVERFLOW_IN_FRACTION, var0, var1);
         }

         var0 = -var0;
         var1 = -var1;
      }

      int var2 = BM.b(var0, var1);
      var0 /= var2;
      var1 /= var2;
      return new qg(var0, var1);
   }

   @Override
   public String toString() {
      String var1;
      if (this.c == 1) {
         var1 = Integer.toString(this.d);
      } else if (this.d == 0) {
         var1 = "0";
      } else {
         var1 = this.d + " / " + this.c;
      }

      return var1;
   }

   private static qi j() {
      return qi.a.a();
   }

   // $VF: synthetic method
   @Override
   public final kM c() {
      return qi.a.a();
   }

   // $VF: synthetic method
   @Override
   public final kN b() {
      return this.h();
   }

   // $VF: synthetic method
   @Override
   public final kN d(Object var1) {
      return this.e((qg)var1);
   }

   // $VF: synthetic method
   @Override
   public final kN c(Object var1) {
      return this.d((qg)var1);
   }

   // $VF: synthetic method
   @Override
   public final kN a(int var1) {
      int var2 = var1;
      return this.d(new qg(var2));
   }

   // $VF: synthetic method
   @Override
   public final kN a() {
      return this.g();
   }

   // $VF: synthetic method
   @Override
   public final kN b(Object var1) {
      qg var2 = (qg)var1;
      return this.a(var2, false);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final kN a(Object var1) {
      qg var2 = (qg)var1;
      return this.a(var2, true);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public int compareTo(Object var1) {
      qg var2 = var1;
      var1 = this;
      long var4 = (long)this.d * var2.c;
      long var6 = (long)var1.c * var2.d;
      if (var4 < var6) {
         return -1;
      } else {
         return var4 > var6 ? 1 : 0;
      }
   }

   static {
      new qg(2, 1);
      new qg(4, 5);
      new qg(1, 5);
      new qg(1, 2);
      new qg(1, 4);
      new qg(1, 3);
      new qg(3, 5);
      new qg(3, 4);
      new qg(2, 5);
      new qg(2, 4);
      new qg(2, 3);
      new qg(-1, 1);
   }
}
