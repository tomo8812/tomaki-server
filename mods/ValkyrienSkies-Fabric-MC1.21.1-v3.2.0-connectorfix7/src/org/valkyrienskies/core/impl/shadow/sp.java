package org.valkyrienskies.core.impl.shadow;

public final class sp implements qM<sq> {
   private static sp d;
   private static sp e;
   public static final sp a = new sp(0.0, 0.0, rs.f);
   private static sp f;
   private static sp g;
   private static sp h;
   public static final sp b = new sp(Double.NaN, Double.NaN, rs.h);
   private static final long i = 20131218L;
   private final double j;
   private final double k;
   final rs c;

   public sp(double var1, double var3) {
      double var7 = 0.0;
      double var5 = 0.0;
      if (!(var7 < 0.0) && !(var7 > Math.PI)) {
         double var9 = BY.o(var5);
         double var11 = BY.n(var5);
         double var13 = BY.o(var7);
         double var15 = BY.n(var7);
         this(0.0, 0.0, new rs(var9 * var15, var11 * var15, var13));
      } else {
         throw new po(var7, 0, Math.PI);
      }
   }

   public sp(rs var1) {
      this(BY.b(var1.j, var1.i), rs.a(rs.f, var1), var1.o());
   }

   private sp(double var1, double var3, rs var5) {
      this.j = var1;
      this.k = var3;
      this.c = var5;
   }

   private static rs a(double var0, double var2) {
      if (!(var2 < 0.0) && !(var2 > Math.PI)) {
         double var4 = BY.o(var0);
         double var6 = BY.n(var0);
         double var8 = BY.o(var2);
         double var10 = BY.n(var2);
         return new rs(var4 * var10, var6 * var10, var8);
      } else {
         throw new po(var2, 0, Math.PI);
      }
   }

   private double e() {
      return this.j;
   }

   private double f() {
      return this.k;
   }

   public final rs c() {
      return this.c;
   }

   @Override
   public final qN a() {
      return sq.a.a();
   }

   @Override
   public final boolean b() {
      return Double.isNaN(this.j) || Double.isNaN(this.k);
   }

   public final sp d() {
      return new sp(-this.j, Math.PI - this.k, this.c.q());
   }

   @Override
   public final double a(qM<sq> var1) {
      sp var2 = (sp)var1;
      return rs.a(this.c, var2.c);
   }

   private static double a(sp var0, sp var1) {
      return rs.a(var0.c, var1.c);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof sp) {
         sp var2;
         return (var2 = (sp)other).b() ? this.b() : this.j == var2.j && this.k == var2.k;
      } else {
         return false;
      }
   }

   @Override
   public final int hashCode() {
      return this.b() ? 542 : 134 * (37 * Ci.a(this.j) + Ci.a(this.k));
   }

   static {
      new sp(0.0, Math.PI / 2, rs.b);
      new sp(Math.PI / 2, Math.PI / 2, rs.d);
      new sp(Math.PI, Math.PI / 2, rs.c);
      new sp(Math.PI * 3.0 / 2.0, Math.PI / 2, rs.e);
      new sp(0.0, Math.PI, rs.g);
   }
}
