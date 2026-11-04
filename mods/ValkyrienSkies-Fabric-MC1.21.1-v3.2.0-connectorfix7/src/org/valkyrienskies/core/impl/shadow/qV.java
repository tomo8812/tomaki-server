package org.valkyrienskies.core.impl.shadow;

public final class qV {
   public final double a;
   public final double b;

   public qV(double var1, double var3) {
      if (var3 < var1) {
         throw new pn(px.ENDPOINTS_NOT_AN_INTERVAL, var3, var1, true);
      }

      this.a = var1;
      this.b = var3;
   }

   public final double a() {
      return this.a;
   }

   @Deprecated
   private double e() {
      return this.a;
   }

   public final double b() {
      return this.b;
   }

   @Deprecated
   private double f() {
      return this.b;
   }

   public final double c() {
      return this.b - this.a;
   }

   @Deprecated
   private double g() {
      return this.c();
   }

   public final double d() {
      return 0.5 * (this.a + this.b);
   }

   @Deprecated
   private double h() {
      return this.d();
   }

   private int a(double var1, double var3) {
      if (var1 < this.a - var3 || var1 > this.b + var3) {
         return rY.a.OUTSIDE$3ad793e5;
      } else {
         return var1 > this.a + var3 && var1 < this.b - var3 ? rY.a.INSIDE$3ad793e5 : rY.a.BOUNDARY$3ad793e5;
      }
   }
}
