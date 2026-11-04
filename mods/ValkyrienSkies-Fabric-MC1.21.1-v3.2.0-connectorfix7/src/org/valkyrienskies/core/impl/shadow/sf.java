package org.valkyrienskies.core.impl.shadow;

public final class sf {
   public final double a;
   public final double b;
   private final double c;
   private final double d;

   public sf(double var1, double var3, double var5) {
      this.d = var5;
      if (Cq.b(var1, var3, 0) || var3 - var1 >= Math.PI * 2) {
         this.a = 0.0;
         this.b = Math.PI * 2;
         this.c = Math.PI;
      } else if (var1 <= var3) {
         this.a = Ci.b(var1, Math.PI);
         this.b = this.a + (var3 - var1);
         this.c = 0.5 * (this.a + this.b);
      } else {
         throw new pm(px.ENDPOINTS_NOT_AN_INTERVAL, var1, var3, true);
      }
   }

   public final double a() {
      return this.a;
   }

   public final double b() {
      return this.b;
   }

   public final double c() {
      return this.b - this.a;
   }

   private double d() {
      return this.c;
   }

   private double e() {
      return this.d;
   }

   private int a(double var1) {
      double var3;
      if ((var3 = Ci.b(var1, this.c)) < this.a - this.d || var3 > this.b + this.d) {
         return rY.a.OUTSIDE$3ad793e5;
      } else if (var3 > this.a + this.d && var3 < this.b - this.d) {
         return rY.a.INSIDE$3ad793e5;
      } else {
         return this.c() >= (Math.PI * 2) - this.d ? rY.a.INSIDE$3ad793e5 : rY.a.BOUNDARY$3ad793e5;
      }
   }
}
