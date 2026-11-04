package org.valkyrienskies.core.impl.shadow;

public class BU extends Number implements Comparable<BU>, kO<BU> {
   public static final BU a = new BU(0.0);
   public static final BU b = new BU(1.0);
   private static BU c;
   private static BU d;
   private static BU e;
   private static final long f = 20120227L;
   private final double g;

   private BU(double var1) {
      this.g = var1;
   }

   @Override
   public final kM<BU> c() {
      return BV.d();
   }

   private BU a(BU var1) {
      return new BU(this.g + var1.g);
   }

   private BU b(BU var1) {
      return new BU(this.g - var1.g);
   }

   private BU C() {
      return new BU(-this.g);
   }

   private BU c(BU var1) {
      return new BU(this.g * var1.g);
   }

   private BU e(int var1) {
      return new BU(var1 * this.g);
   }

   private BU d(BU var1) {
      return new BU(this.g / var1.g);
   }

   private BU D() {
      return new BU(1.0 / this.g);
   }

   @Override
   public byte byteValue() {
      return (byte)this.g;
   }

   @Override
   public short shortValue() {
      return (short)this.g;
   }

   @Override
   public int intValue() {
      return (int)this.g;
   }

   @Override
   public long longValue() {
      return (long)this.g;
   }

   @Override
   public float floatValue() {
      return (float)this.g;
   }

   @Override
   public double doubleValue() {
      return this.g;
   }

   private int e(BU var1) {
      return Double.compare(this.g, var1.g);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof BU) {
         obj = (BU)obj;
         return Double.doubleToLongBits(this.g) == Double.doubleToLongBits(((BU)obj).g);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      long var1;
      return (int)((var1 = Double.doubleToLongBits(this.g)) ^ var1 >>> 32);
   }

   @Override
   public String toString() {
      return Double.toString(this.g);
   }

   private boolean E() {
      return Double.isInfinite(this.g);
   }

   private boolean F() {
      return Double.isNaN(this.g);
   }

   @Override
   public final double d() {
      return this.g;
   }

   private BU h(double var1) {
      return new BU(this.g + var1);
   }

   private BU i(double var1) {
      return new BU(this.g - var1);
   }

   private BU j(double var1) {
      return new BU(this.g * var1);
   }

   private BU k(double var1) {
      return new BU(this.g / var1);
   }

   private BU l(double var1) {
      return new BU(BY.g(this.g, var1));
   }

   private BU f(BU var1) {
      return new BU(BY.g(this.g, var1.g));
   }

   private BU G() {
      return new BU(BY.w(this.g));
   }

   private BU H() {
      return new BU(BY.z(this.g));
   }

   private BU I() {
      return new BU(BY.y(this.g));
   }

   private BU J() {
      return new BU(BY.A(this.g));
   }

   @Override
   public final long i() {
      return BY.B(this.g);
   }

   private BU K() {
      return new BU(BY.h(this.g));
   }

   private BU g(BU var1) {
      return new BU(BY.h(this.g, var1.g));
   }

   private BU m(double var1) {
      return new BU(BY.h(this.g, var1));
   }

   private BU f(int var1) {
      return new BU(BY.b(this.g, var1));
   }

   private BU h(BU var1) {
      return new BU(BY.f(this.g, var1.g));
   }

   private BU L() {
      return new BU(BY.a(this.g));
   }

   private BU M() {
      return new BU(BY.t(this.g));
   }

   private BU g(int var1) {
      return this.g < 0.0 ? new BU(-BY.a(-this.g, 1.0 / var1)) : new BU(BY.a(this.g, 1.0 / var1));
   }

   private BU n(double var1) {
      return new BU(BY.a(this.g, var1));
   }

   private BU h(int var1) {
      return new BU(BY.a(this.g, var1));
   }

   private BU i(BU var1) {
      return new BU(BY.a(this.g, var1.g));
   }

   private BU N() {
      return new BU(BY.i(this.g));
   }

   private BU O() {
      return new BU(BY.j(this.g));
   }

   private BU P() {
      return new BU(BY.k(this.g));
   }

   private BU Q() {
      return new BU(BY.l(this.g));
   }

   private BU R() {
      return new BU(BY.m(this.g));
   }

   private BU S() {
      return new BU(BY.o(this.g));
   }

   private BU T() {
      return new BU(BY.n(this.g));
   }

   private BU U() {
      return new BU(BY.p(this.g));
   }

   private BU V() {
      return new BU(BY.s(this.g));
   }

   private BU W() {
      return new BU(BY.r(this.g));
   }

   private BU X() {
      return new BU(BY.q(this.g));
   }

   private BU j(BU var1) {
      return new BU(BY.b(this.g, var1.g));
   }

   private BU Y() {
      return new BU(BY.b(this.g));
   }

   private BU Z() {
      return new BU(BY.c(this.g));
   }

   private BU aa() {
      return new BU(BY.d(this.g));
   }

   private BU ab() {
      return new BU(BY.e(this.g));
   }

   private BU ac() {
      return new BU(BY.f(this.g));
   }

   private BU ad() {
      return new BU(BY.g(this.g));
   }

   private static BU a(BU[] var0, BU[] var1) {
      if (var0.length != var1.length) {
         throw new oS(var0.length, var1.length);
      }

      double[] var2 = new double[var0.length];
      double[] var3 = new double[var1.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var2[var4] = var0[var4].g;
         var3[var4] = var1[var4].g;
      }

      return new BU(Ch.h(var2, var3));
   }

   private static BU a(double[] var0, BU[] var1) {
      if (var0.length != var1.length) {
         throw new oS(var0.length, var1.length);
      }

      double[] var2 = new double[var1.length];

      for (int var3 = 0; var3 < var0.length; var3++) {
         var2[var3] = var1[var3].g;
      }

      return new BU(Ch.h(var0, var2));
   }

   private static BU a(BU var0, BU var1, BU var2, BU var3) {
      return new BU(Ch.a(var0.g, var1.g, var2.g, var3.g));
   }

   private static BU a(double var0, BU var2, double var3, BU var5) {
      return new BU(Ch.a(var0, var2.g, var3, var5.g));
   }

   private static BU a(BU var0, BU var1, BU var2, BU var3, BU var4, BU var5) {
      return new BU(Ch.a(var0.g, var1.g, var2.g, var3.g, var4.g, var5.g));
   }

   private static BU a(double var0, BU var2, double var3, BU var5, double var6, BU var8) {
      return new BU(Ch.a(var0, var2.g, var3, var5.g, var6, var8.g));
   }

   private static BU a(BU var0, BU var1, BU var2, BU var3, BU var4, BU var5, BU var6, BU var7) {
      return new BU(Ch.a(var0.g, var1.g, var2.g, var3.g, var4.g, var5.g, var6.g, var7.g));
   }

   private static BU a(double var0, BU var2, double var3, BU var5, double var6, BU var8, double var9, BU var11) {
      return new BU(Ch.a(var0, var2.g, var3, var5.g, var6, var8.g, var9, var11.g));
   }

   // $VF: synthetic method
   @Override
   public final kO a(double var1, kO var3, double var4, kO var6, double var7, kO var9, double var10, kO var12) {
      BU var10001 = var3;
      BU var10003 = (BU)var6;
      BU var10005 = (BU)var9;
      BU var26 = (BU)var12;
      double var22 = var10;
      var3 = var10005;
      double var19 = var7;
      BU var2 = var10003;
      double var16 = var4;
      BU var24 = var10001;
      double var13 = var1;
      return new BU(Ch.a(var13, var24.g, var16, var2.g, var19, var3.g, var22, var26.g));
   }

   // $VF: synthetic method
   @Override
   public final kO a(kO var1, kO var2, kO var3, kO var4, kO var5, kO var6, kO var7, kO var8) {
      BU var10000 = var1;
      BU var10001 = var2;
      BU var10002 = var3;
      BU var10003 = var4;
      BU var10004 = var5;
      BU var10005 = var6;
      BU var10006 = var7;
      var8 = var8;
      var7 = var10006;
      var6 = var10005;
      var5 = var10004;
      var4 = var10003;
      var3 = var10002;
      var2 = var10001;
      var1 = var10000;
      return new BU(Ch.a(var1.g, var2.g, var3.g, var4.g, var5.g, var6.g, var7.g, var8.g));
   }

   // $VF: synthetic method
   @Override
   public final kO a(double var1, kO var3, double var4, kO var6, double var7, kO var9) {
      BU var10001 = var3;
      BU var10003 = (BU)var6;
      var3 = (BU)var9;
      double var16 = var7;
      BU var2 = var10003;
      double var13 = var4;
      BU var18 = var10001;
      double var10 = var1;
      return new BU(Ch.a(var10, var18.g, var13, var2.g, var16, var3.g));
   }

   // $VF: synthetic method
   @Override
   public final kO a(kO var1, kO var2, kO var3, kO var4, kO var5, kO var6) {
      BU var10000 = var1;
      BU var10001 = var2;
      BU var10002 = var3;
      BU var10003 = var4;
      BU var10004 = var5;
      var6 = var6;
      var5 = var10004;
      var4 = var10003;
      var3 = var10002;
      var2 = var10001;
      var1 = var10000;
      return new BU(Ch.a(var1.g, var2.g, var3.g, var4.g, var5.g, var6.g));
   }

   // $VF: synthetic method
   @Override
   public final kO a(double var1, kO var3, double var4, kO var6) {
      BU var10001 = (BU)var3;
      BU var2 = (BU)var6;
      double var10 = var4;
      BU var12 = var10001;
      double var7 = var1;
      return new BU(Ch.a(var7, var12.g, var10, var2.g));
   }

   // $VF: synthetic method
   @Override
   public final kO a(kO var1, kO var2, Object var3, kO var4) {
      BU var10000 = var1;
      BU var10001 = var2;
      BU var10002 = var3;
      var4 = var4;
      var3 = var10002;
      var2 = var10001;
      var1 = var10000;
      return new BU(Ch.a(var1.g, var2.g, var3.g, var4.g));
   }

   // $VF: synthetic method
   @Override
   public final kO a(double[] var1, Object[] var2) {
      var2 = var2;
      var1 = var1;
      if (var1.length != var2.length) {
         throw new oS(var1.length, var2.length);
      }

      double[] var3 = new double[var2.length];

      for (int var4 = 0; var4 < var1.length; var4++) {
         var3[var4] = var2[var4].g;
      }

      return new BU(Ch.h(var1, var3));
   }

   // $VF: synthetic method
   @Override
   public final kO a(Object[] var1, Object[] var2) {
      BU[] var10000 = var1;
      var2 = var2;
      var1 = var10000;
      if (var10000.length != var2.length) {
         throw new oS(var1.length, var2.length);
      }

      double[] var3 = new double[var1.length];
      double[] var4 = new double[var2.length];

      for (int var5 = 0; var5 < var1.length; var5++) {
         var3[var5] = var1[var5].g;
         var4[var5] = var2[var5].g;
      }

      return new BU(Ch.h(var3, var4));
   }

   // $VF: synthetic method
   @Override
   public final kO B() {
      BU var1 = this;
      return new BU(BY.g(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO A() {
      BU var1 = this;
      return new BU(BY.f(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO z() {
      BU var1 = this;
      return new BU(BY.e(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO y() {
      BU var1 = this;
      return new BU(BY.d(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO x() {
      BU var1 = this;
      return new BU(BY.c(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO w() {
      BU var1 = this;
      return new BU(BY.b(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO h(Object var1) {
      BU var2 = var1;
      var1 = this;
      return new BU(BY.b(var1.g, var2.g));
   }

   // $VF: synthetic method
   @Override
   public final kO v() {
      BU var1 = this;
      return new BU(BY.q(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO u() {
      BU var1 = this;
      return new BU(BY.r(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO t() {
      BU var1 = this;
      return new BU(BY.s(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO s() {
      BU var1 = this;
      return new BU(BY.p(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO r() {
      BU var1 = this;
      return new BU(BY.n(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO q() {
      BU var1 = this;
      return new BU(BY.o(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO p() {
      BU var1 = this;
      return new BU(BY.l(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO o() {
      BU var1 = this;
      return new BU(BY.k(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO n() {
      BU var1 = this;
      return new BU(BY.j(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO m() {
      BU var1 = this;
      return new BU(BY.i(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO a(kO var1) {
      BU var2 = var1;
      var1 = this;
      return new BU(BY.a(var1.g, var2.g));
   }

   // $VF: synthetic method
   @Override
   public final kO d(int var1) {
      int var2 = var1;
      BU var3 = this;
      return new BU(BY.a(var3.g, var2));
   }

   // $VF: synthetic method
   @Override
   public final kO g(double var1) {
      double var4 = var1;
      BU var6 = this;
      return new BU(BY.a(var6.g, var4));
   }

   // $VF: synthetic method
   @Override
   public final kO c(int var1) {
      int var2 = var1;
      BU var3 = this;
      return this.g < 0.0 ? new BU(-BY.a(-var3.g, 1.0 / var2)) : new BU(BY.a(var3.g, 1.0 / var2));
   }

   // $VF: synthetic method
   @Override
   public final kO l() {
      BU var1 = this;
      return new BU(BY.t(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO k() {
      BU var1 = this;
      return new BU(BY.a(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kN b() {
      BU var1 = this;
      return new BU(1.0 / var1.g);
   }

   // $VF: synthetic method
   @Override
   public final kO g(Object var1) {
      BU var2 = var1;
      var1 = this;
      return new BU(BY.f(var1.g, var2.g));
   }

   // $VF: synthetic method
   @Override
   public final kO b(int var1) {
      int var2 = var1;
      BU var3 = this;
      return new BU(BY.b(var3.g, var2));
   }

   // $VF: synthetic method
   @Override
   public final kO f(double var1) {
      double var4 = var1;
      BU var6 = this;
      return new BU(BY.h(var6.g, var4));
   }

   // $VF: synthetic method
   @Override
   public final kO f(Object var1) {
      BU var2 = var1;
      var1 = this;
      return new BU(BY.h(var1.g, var2.g));
   }

   // $VF: synthetic method
   @Override
   public final kO j() {
      BU var1 = this;
      return new BU(BY.h(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO h() {
      BU var1 = this;
      return new BU(BY.A(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO g() {
      BU var1 = this;
      return new BU(BY.y(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO f() {
      BU var1 = this;
      return new BU(BY.z(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO e() {
      BU var1 = this;
      return new BU(BY.w(var1.g));
   }

   // $VF: synthetic method
   @Override
   public final kO e(Object var1) {
      BU var2 = var1;
      var1 = this;
      return new BU(BY.g(var1.g, var2.g));
   }

   // $VF: synthetic method
   @Override
   public final kO e(double var1) {
      double var4 = var1;
      BU var6 = this;
      return new BU(BY.g(var6.g, var4));
   }

   // $VF: synthetic method
   @Override
   public final kO d(double var1) {
      double var4 = var1;
      BU var6 = this;
      return new BU(var6.g / var4);
   }

   // $VF: synthetic method
   @Override
   public final kO c(double var1) {
      double var4 = var1;
      BU var6 = this;
      return new BU(var6.g * var4);
   }

   // $VF: synthetic method
   @Override
   public final kO b(double var1) {
      double var4 = var1;
      BU var6 = this;
      return new BU(var6.g - var4);
   }

   // $VF: synthetic method
   @Override
   public final kO a(double var1) {
      double var4 = var1;
      BU var6 = this;
      return new BU(var6.g + var4);
   }

   // $VF: synthetic method
   @Override
   public final kN d(Object var1) {
      BU var2 = var1;
      var1 = this;
      return new BU(var1.g / var2.g);
   }

   // $VF: synthetic method
   @Override
   public final kN c(Object var1) {
      BU var2 = var1;
      var1 = this;
      return new BU(var1.g * var2.g);
   }

   // $VF: synthetic method
   @Override
   public final kN a(int var1) {
      int var2 = var1;
      BU var3 = this;
      return new BU(var2 * var3.g);
   }

   // $VF: synthetic method
   @Override
   public final kN a() {
      BU var1 = this;
      return new BU(-var1.g);
   }

   // $VF: synthetic method
   @Override
   public final kN b(Object var1) {
      BU var2 = var1;
      var1 = this;
      return new BU(var1.g - var2.g);
   }

   // $VF: synthetic method
   @Override
   public final kN a(Object var1) {
      BU var2 = var1;
      var1 = this;
      return new BU(var1.g + var2.g);
   }

   // $VF: synthetic method
   @Override
   public int compareTo(Object var1) {
      BU var2 = (BU)var1;
      return Double.compare(this.g, var2.g);
   }

   static {
      new BU(Double.NEGATIVE_INFINITY);
      new BU(Double.POSITIVE_INFINITY);
      new BU(Double.NaN);
   }
}
