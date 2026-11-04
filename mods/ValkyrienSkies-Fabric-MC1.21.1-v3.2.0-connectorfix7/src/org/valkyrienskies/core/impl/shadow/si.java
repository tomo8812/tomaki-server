package org.valkyrienskies.core.impl.shadow;

public final class si implements qM<sj> {
   public static final si a = new si(Double.NaN, rB.b);
   private static final long c = 20131218L;
   public final double b;
   private final rB d;

   public si(double var1) {
      this(Ci.b(var1, Math.PI), new rB(BY.o(var1), BY.n(var1)));
   }

   private si(double var1, rB var3) {
      this.b = var1;
      this.d = var3;
   }

   public final double c() {
      return this.b;
   }

   private rB d() {
      return this.d;
   }

   @Override
   public final qN a() {
      return sj.a.a();
   }

   @Override
   public final boolean b() {
      return Double.isNaN(this.b);
   }

   @Override
   public final double a(qM<sj> var1) {
      si var2 = (si)var1;
      return rB.a(this.d, var2.d);
   }

   private static double a(si var0, si var1) {
      return rB.a(var0.d, var1.d);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof si) {
         si var3;
         return Double.isNaN((var3 = (si)other).b) ? Double.isNaN(this.b) : this.b == var3.b;
      } else {
         return false;
      }
   }

   @Override
   public final int hashCode() {
      return Double.isNaN(this.b) ? 542 : 1759 * Ci.a(this.b);
   }
}
