package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class BN implements Serializable, Comparable<BN>, kN<BN> {
   public static final BN a = new BN(BigDecimal.ZERO);
   public static final BN b = new BN(BigDecimal.ONE);
   private static final long c = 4984534880991310382L;
   private final BigDecimal d;
   private RoundingMode e = RoundingMode.HALF_UP;
   private int f = 64;

   private BN(BigDecimal var1) {
      this.d = var1;
   }

   private BN(BigInteger var1) {
      this.d = new BigDecimal(var1);
   }

   private BN(BigInteger var1, int var2) {
      this.d = new BigDecimal(var1, var2);
   }

   private BN(BigInteger var1, int var2, MathContext var3) {
      this.d = new BigDecimal(var1, var2, var3);
   }

   private BN(BigInteger var1, MathContext var2) {
      this.d = new BigDecimal(var1, var2);
   }

   private BN(char[] var1) {
      this.d = new BigDecimal(var1);
   }

   private BN(char[] var1, int var2, int var3) {
      this.d = new BigDecimal(var1, var2, var3);
   }

   private BN(char[] var1, int var2, int var3, MathContext var4) {
      this.d = new BigDecimal(var1, var2, var3, var4);
   }

   private BN(char[] var1, MathContext var2) {
      this.d = new BigDecimal(var1, var2);
   }

   private BN(double var1) {
      this.d = new BigDecimal(var1);
   }

   private BN(double var1, MathContext var3) {
      this.d = new BigDecimal(var1, var3);
   }

   private BN(int var1) {
      this.d = new BigDecimal(var1);
   }

   private BN(int var1, MathContext var2) {
      this.d = new BigDecimal(var1, var2);
   }

   private BN(long var1) {
      this.d = new BigDecimal(var1);
   }

   private BN(long var1, MathContext var3) {
      this.d = new BigDecimal(var1, var3);
   }

   private BN(String var1) {
      this.d = new BigDecimal(var1);
   }

   private BN(String var1, MathContext var2) {
      this.d = new BigDecimal(var1, var2);
   }

   private RoundingMode d() {
      return this.e;
   }

   private void a(RoundingMode var1) {
      this.e = var1;
   }

   private int e() {
      return this.f;
   }

   private void b(int var1) {
      this.f = var1;
   }

   private BN a(BN var1) {
      return new BN(this.d.add(var1.d));
   }

   private BN b(BN var1) {
      return new BN(this.d.subtract(var1.d));
   }

   private BN f() {
      return new BN(this.d.negate());
   }

   private BN c(BN var1) {
      try {
         return new BN(this.d.divide(var1.d, this.f, this.e));
      } catch (ArithmeticException var2) {
         throw new oU(px.ZERO_NOT_ALLOWED);
      }
   }

   private BN g() {
      try {
         return new BN(BigDecimal.ONE.divide(this.d, this.f, this.e));
      } catch (ArithmeticException var1) {
         throw new oU(px.ZERO_NOT_ALLOWED);
      }
   }

   private BN d(BN var1) {
      return new BN(this.d.multiply(var1.d));
   }

   private BN c(int var1) {
      return new BN(this.d.multiply(new BigDecimal(var1)));
   }

   private int e(BN var1) {
      return this.d.compareTo(var1.d);
   }

   private double h() {
      return this.d.doubleValue();
   }

   private BigDecimal i() {
      return this.d;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return other instanceof BN ? this.d.equals(((BN)other).d) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.d.hashCode();
   }

   @Override
   public final kM<BN> c() {
      return BO.a.a();
   }

   // $VF: synthetic method
   @Override
   public final kN b() {
      return this.g();
   }

   // $VF: synthetic method
   @Override
   public final kN d(Object var1) {
      return this.c((BN)var1);
   }

   // $VF: synthetic method
   @Override
   public final kN c(Object var1) {
      BN var2 = var1;
      var1 = this;
      return new BN(var1.d.multiply(var2.d));
   }

   // $VF: synthetic method
   @Override
   public final kN a(int var1) {
      int var2 = var1;
      BN var3 = this;
      return new BN(var3.d.multiply(new BigDecimal(var2)));
   }

   // $VF: synthetic method
   @Override
   public final kN a() {
      BN var1 = this;
      return new BN(var1.d.negate());
   }

   // $VF: synthetic method
   @Override
   public final kN b(Object var1) {
      BN var2 = var1;
      var1 = this;
      return new BN(var1.d.subtract(var2.d));
   }

   // $VF: synthetic method
   @Override
   public final kN a(Object var1) {
      BN var2 = var1;
      var1 = this;
      return new BN(var1.d.add(var2.d));
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public int compareTo(Object var1) {
      BN var2 = (BN)var1;
      return this.d.compareTo(var2.d);
   }
}
