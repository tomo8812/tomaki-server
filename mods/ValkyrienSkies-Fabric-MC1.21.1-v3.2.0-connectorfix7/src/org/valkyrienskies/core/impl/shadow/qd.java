package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public class qd extends Number implements Serializable, Comparable<qd>, kN<qd> {
   public static final qd a = new qd(2);
   public static final qd b = new qd(1);
   public static final qd c = new qd(0);
   public static final qd d = new qd(-1);
   private static qd h;
   private static qd i;
   public static final qd e = new qd(1, 2);
   private static qd j;
   private static qd k;
   private static qd l;
   private static qd m;
   private static qd n;
   private static qd o;
   private static qd p;
   private static final long q = -5630213147331578515L;
   private static final BigInteger r = BigInteger.valueOf(100L);
   final BigInteger f;
   final BigInteger g;

   public qd(BigInteger var1) {
      this(var1, BigInteger.ONE);
   }

   public qd(BigInteger var1, BigInteger var2) {
      Ci.a(var1, px.NUMERATOR);
      Ci.a(var2, px.DENOMINATOR);
      if (var2.signum() == 0) {
         throw new pr(px.ZERO_DENOMINATOR);
      }

      if (var1.signum() == 0) {
         this.f = BigInteger.ZERO;
         this.g = BigInteger.ONE;
      } else {
         BigInteger var3 = var1.gcd(var2);
         if (BigInteger.ONE.compareTo(var3) < 0) {
            var1 = var1.divide(var3);
            var2 = var2.divide(var3);
         }

         if (var2.signum() == -1) {
            var1 = var1.negate();
            var2 = var2.negate();
         }

         this.f = var1;
         this.g = var2;
      }
   }

   public qd(double var1) {
      if (Double.isNaN(var1)) {
         throw new oV(px.NAN_VALUE_CONVERSION);
      }

      if (Double.isInfinite(var1)) {
         throw new oV(px.INFINITE_VALUE_CONVERSION);
      }

      long var3;
      long var5 = (var3 = Double.doubleToLongBits(var1)) & Long.MIN_VALUE;
      long var7 = var3 & 9218868437227405312L;
      long var9 = var3 & 4503599627370495L;
      if (var7 != 0L) {
         var9 |= 4503599627370496L;
      }

      if (var5 != 0L) {
         var9 = -var9;
      }

      for (var11 = (int)(var7 >> 52) - 1075; (var9 & 9007199254740990L) != 0L && (var9 & 1L) == 0L; var11++) {
         var9 >>= 1;
      }

      if (var11 < 0) {
         this.f = BigInteger.valueOf(var9);
         this.g = BigInteger.ZERO.flipBit(-var11);
      } else {
         this.f = BigInteger.valueOf(var9).multiply(BigInteger.ZERO.flipBit(var11));
         this.g = BigInteger.ONE;
      }
   }

   public qd(double var1, double var3, int var5) {
      this(var1, var3, Integer.MAX_VALUE, 10000);
   }

   private qd(double var1, double var3, int var5, int var6) {
      double var7 = var1;
      long var9;
      if (BY.a(var9 = (long)BY.y(var1)) > 2147483647L) {
         throw new qh(var1, var9, 1L);
      }

      if (BY.w(var9 - var1) < var3) {
         this.f = BigInteger.valueOf(var9);
         this.g = BigInteger.ONE;
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
            if (var19 <= 2147483647L && var21 <= 2147483647L) {
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
            this.f = BigInteger.valueOf(var19);
            this.g = BigInteger.valueOf(var21);
         } else {
            this.f = BigInteger.valueOf(var15);
            this.g = BigInteger.valueOf(var17);
         }
      }
   }

   private qd(double var1, int var3) {
      this(var1, 0.0, var3, 100);
   }

   public qd(int var1) {
      this(BigInteger.valueOf(var1), BigInteger.ONE);
   }

   public qd(int var1, int var2) {
      this(BigInteger.valueOf(var1), BigInteger.valueOf(var2));
   }

   private qd(long var1) {
      this(BigInteger.valueOf(var1), BigInteger.ONE);
   }

   private qd(long var1, long var3) {
      this(BigInteger.valueOf(var1), BigInteger.valueOf(var3));
   }

   private static qd b(int var0, int var1) {
      return var0 == 0 ? c : new qd(var0, var1);
   }

   private qd g() {
      return this.f.signum() == 1 ? this : this.f();
   }

   private qd a(BigInteger var1) {
      Ci.a(var1);
      if (this.f.signum() == 0) {
         return new qd(var1);
      } else {
         return var1.signum() == 0 ? this : new qd(this.f.add(this.g.multiply(var1)), this.g);
      }
   }

   private qd f(int var1) {
      return this.a(BigInteger.valueOf(var1));
   }

   private qd a(long var1) {
      return this.a(BigInteger.valueOf(var1));
   }

   public final qd a(qd var1) {
      if (var1 == null) {
         throw new pl(px.FRACTION);
      }

      if (var1.f.signum() == 0) {
         return this;
      }

      if (this.f.signum() == 0) {
         return var1;
      }

      BigInteger var2;
      BigInteger var3;
      if (this.g.equals(var1.g)) {
         var2 = this.f.add(var1.f);
         var3 = this.g;
      } else {
         var2 = this.f.multiply(var1.g).add(var1.f.multiply(this.g));
         var3 = this.g.multiply(var1.g);
      }

      return var2.signum() == 0 ? c : new qd(var2, var3);
   }

   private BigDecimal h() {
      return new BigDecimal(this.f).divide(new BigDecimal(this.g));
   }

   private BigDecimal g(int var1) {
      return new BigDecimal(this.f).divide(new BigDecimal(this.g), var1);
   }

   public final BigDecimal a(int var1, int var2) {
      return new BigDecimal(this.f).divide(new BigDecimal(this.g), 20, 4);
   }

   public final int b(qd var1) {
      int var2 = this.f.signum();
      int var3 = var1.f.signum();
      if (var2 != var3) {
         return var2 > var3 ? 1 : -1;
      }

      if (var2 == 0) {
         return 0;
      }

      BigInteger var5 = this.f.multiply(var1.g);
      BigInteger var4 = this.g.multiply(var1.f);
      return var5.compareTo(var4);
   }

   private qd b(BigInteger var1) {
      if (var1 == null) {
         throw new pl(px.FRACTION);
      } else if (var1.signum() == 0) {
         throw new oU(px.ZERO_DENOMINATOR);
      } else {
         return this.f.signum() == 0 ? c : new qd(this.f, this.g.multiply(var1));
      }
   }

   public final qd b(int var1) {
      return this.b(BigInteger.valueOf(var1));
   }

   private qd b(long var1) {
      return this.b(BigInteger.valueOf(var1));
   }

   public final qd c(qd var1) {
      if (var1 == null) {
         throw new pl(px.FRACTION);
      } else if (var1.f.signum() == 0) {
         throw new oU(px.ZERO_DENOMINATOR);
      } else {
         return this.f.signum() == 0 ? c : this.d(var1.n());
      }
   }

   @Override
   public double doubleValue() {
      double var1;
      if (Double.isNaN(var1 = this.f.doubleValue() / this.g.doubleValue())) {
         int var3 = BY.b(this.f.bitLength(), this.g.bitLength()) - BY.C(Double.MAX_VALUE);
         var1 = this.f.shiftRight(var3).doubleValue() / this.g.shiftRight(var3).doubleValue();
      }

      return var1;
   }

   @Override
   public boolean equals(Object other) {
      boolean var2 = false;
      boolean var10000;
      if (this == other) {
         var10000 = true;
      } else {
         if (!(other instanceof qd)) {
            return var2;
         }

         other = ((qd)other).o();
         qd var4;
         var10000 = (var4 = this.o()).f.equals(((qd)other).f) && var4.g.equals(((qd)other).g);
      }

      return var10000;
   }

   @Override
   public float floatValue() {
      float var1;
      if (Double.isNaN(var1 = this.f.floatValue() / this.g.floatValue())) {
         int var2 = BY.b(this.f.bitLength(), this.g.bitLength()) - BY.b(Float.MAX_VALUE);
         var1 = this.f.shiftRight(var2).floatValue() / this.g.shiftRight(var2).floatValue();
      }

      return var1;
   }

   public final BigInteger d() {
      return this.g;
   }

   private int i() {
      return this.g.intValue();
   }

   private long j() {
      return this.g.longValue();
   }

   public final BigInteger e() {
      return this.f;
   }

   private int k() {
      return this.f.intValue();
   }

   private long l() {
      return this.f.longValue();
   }

   @Override
   public int hashCode() {
      return 37 * (629 + this.f.hashCode()) + this.g.hashCode();
   }

   @Override
   public int intValue() {
      return this.f.divide(this.g).intValue();
   }

   @Override
   public long longValue() {
      return this.f.divide(this.g).longValue();
   }

   private qd c(BigInteger var1) {
      if (var1 == null) {
         throw new pl();
      } else {
         return this.f.signum() != 0 && var1.signum() != 0 ? new qd(var1.multiply(this.f), this.g) : c;
      }
   }

   public final qd c(int var1) {
      return var1 != 0 && this.f.signum() != 0 ? this.c(BigInteger.valueOf(var1)) : c;
   }

   private qd c(long var1) {
      return var1 != 0L && this.f.signum() != 0 ? this.c(BigInteger.valueOf(var1)) : c;
   }

   public final qd d(qd var1) {
      if (var1 == null) {
         throw new pl(px.FRACTION);
      } else {
         return this.f.signum() != 0 && var1.f.signum() != 0 ? new qd(this.f.multiply(var1.f), this.g.multiply(var1.g)) : c;
      }
   }

   public final qd f() {
      return new qd(this.f.negate(), this.g);
   }

   private double m() {
      return this.c(r).doubleValue();
   }

   public final qd d(int var1) {
      if (var1 == 0) {
         return b;
      } else if (this.f.signum() == 0) {
         return this;
      } else {
         return var1 < 0 ? new qd(this.g.pow(-var1), this.f.pow(-var1)) : new qd(this.f.pow(var1), this.g.pow(var1));
      }
   }

   private qd d(long var1) {
      if (var1 == 0L) {
         return b;
      } else if (this.f.signum() == 0) {
         return this;
      } else {
         return var1 < 0L ? new qd(BM.a(this.g, -var1), BM.a(this.f, -var1)) : new qd(BM.a(this.f, var1), BM.a(this.g, var1));
      }
   }

   private qd d(BigInteger var1) {
      if (var1.signum() == 0) {
         return b;
      } else if (this.f.signum() == 0) {
         return this;
      } else if (var1.signum() == -1) {
         var1 = var1.negate();
         return new qd(BM.a(this.g, var1), BM.a(this.f, var1));
      } else {
         return new qd(BM.a(this.f, var1), BM.a(this.g, var1));
      }
   }

   private double a(double var1) {
      return BY.a(this.f.doubleValue(), var1) / BY.a(this.g.doubleValue(), var1);
   }

   private qd n() {
      return new qd(this.g, this.f);
   }

   private qd o() {
      BigInteger var1 = this.f.gcd(this.g);
      return BigInteger.ONE.compareTo(var1) < 0 ? new qd(this.f.divide(var1), this.g.divide(var1)) : this;
   }

   private qd e(BigInteger var1) {
      if (var1 == null) {
         throw new pl();
      } else if (var1.signum() == 0) {
         return this;
      } else {
         return this.f.signum() == 0 ? new qd(var1.negate()) : new qd(this.f.subtract(this.g.multiply(var1)), this.g);
      }
   }

   public final qd e(int var1) {
      return this.e(BigInteger.valueOf(1L));
   }

   private qd e(long var1) {
      return this.e(BigInteger.valueOf(var1));
   }

   public final qd e(qd var1) {
      if (var1 == null) {
         throw new pl(px.FRACTION);
      }

      if (var1.f.signum() == 0) {
         return this;
      }

      if (this.f.signum() == 0) {
         return var1.f();
      }

      BigInteger var2;
      BigInteger var3;
      if (this.g.equals(var1.g)) {
         var2 = this.f.subtract(var1.f);
         var3 = this.g;
      } else {
         var2 = this.f.multiply(var1.g).subtract(var1.f.multiply(this.g));
         var3 = this.g.multiply(var1.g);
      }

      return new qd(var2, var3);
   }

   @Override
   public String toString() {
      String var1;
      if (BigInteger.ONE.equals(this.g)) {
         var1 = this.f.toString();
      } else if (BigInteger.ZERO.equals(this.f)) {
         var1 = "0";
      } else {
         var1 = this.f + " / " + this.g;
      }

      return var1;
   }

   private static qe p() {
      return qe.a.a();
   }

   // $VF: synthetic method
   @Override
   public final kM c() {
      return qe.a.a();
   }

   // $VF: synthetic method
   @Override
   public final kN b() {
      return this.n();
   }

   // $VF: synthetic method
   @Override
   public final kN d(Object var1) {
      return this.c((qd)var1);
   }

   // $VF: synthetic method
   @Override
   public final kN c(Object var1) {
      return this.d((qd)var1);
   }

   // $VF: synthetic method
   @Override
   public final kN a(int var1) {
      return this.c(var1);
   }

   // $VF: synthetic method
   @Override
   public final kN a() {
      return this.f();
   }

   // $VF: synthetic method
   @Override
   public final kN b(Object var1) {
      return this.e((qd)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final kN a(Object var1) {
      return this.a((qd)var1);
   }

   // $VF: synthetic method
   @Override
   public int compareTo(Object var1) {
      return this.b((qd)var1);
   }

   static {
      new qd(4, 5);
      new qd(1, 5);
      new qd(1, 4);
      new qd(1, 3);
      new qd(3, 5);
      new qd(3, 4);
      new qd(2, 5);
      new qd(2, 4);
      new qd(2, 3);
   }
}
