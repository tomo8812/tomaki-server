package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class Au implements Serializable, At {
   private static final long a = -5108854841843722536L;
   private final double b;
   private final double c;
   private final long d;
   private final double e;
   private final double f;
   private final double g;

   public Au(double var1, double var3, long var5, double var7, double var9, double var11) {
      this.b = var1;
      this.c = var3;
      this.d = var5;
      this.e = var7;
      this.f = var9;
      this.g = var11;
   }

   @Override
   public final double a() {
      return this.e;
   }

   @Override
   public final double b() {
      return this.b;
   }

   @Override
   public final double c() {
      return this.f;
   }

   @Override
   public final long d() {
      return this.d;
   }

   @Override
   public final double f() {
      return this.g;
   }

   @Override
   public final double e() {
      return BY.a(this.c);
   }

   @Override
   public final double g() {
      return this.c;
   }

   @Override
   public final boolean equals(Object object) {
      if (object == this) {
         return true;
      }

      Au var2;
      return !(object instanceof Au)
         ? false
         : Cq.b((var2 = (Au)object).a(), this.a())
            && Cq.b(var2.b(), this.b())
            && Cq.b(var2.c(), this.c())
            && Cq.b((float)var2.d(), (float)this.d())
            && Cq.b(var2.f(), this.f())
            && Cq.b(var2.g(), this.g());
   }

   @Override
   public final int hashCode() {
      return (((((31 + Ci.a(this.a())) * 31 + Ci.a(this.b())) * 31 + Ci.a(this.c())) * 31 + Ci.a(this.d())) * 31 + Ci.a(this.f())) * 31 + Ci.a(this.g());
   }

   @Override
   public final String toString() {
      StringBuffer var1 = new StringBuffer();
      String var2 = "\n";
      var1.append("StatisticalSummaryValues:").append(var2);
      var1.append("n: ").append(this.d()).append(var2);
      var1.append("min: ").append(this.c()).append(var2);
      var1.append("max: ").append(this.a()).append(var2);
      var1.append("mean: ").append(this.b()).append(var2);
      var1.append("std dev: ").append(this.e()).append(var2);
      var1.append("variance: ").append(this.g()).append(var2);
      var1.append("sum: ").append(this.f()).append(var2);
      return var1.toString();
   }
}
