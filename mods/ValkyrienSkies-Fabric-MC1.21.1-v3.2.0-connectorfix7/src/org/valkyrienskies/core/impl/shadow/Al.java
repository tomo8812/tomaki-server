package org.valkyrienskies.core.impl.shadow;

final class Al {
   double a;
   double b;
   double c;
   double d;
   private boolean e;

   Al() {
      this(true);
   }

   Al(boolean var1) {
      this.a = this.b = 0.0;
      this.c = 0.0;
      this.d = 0.0;
      this.e = var1;
   }

   public final void a(double var1, double var3) {
      this.c++;
      double var5 = var1 - this.a;
      double var7 = var3 - this.b;
      this.a = this.a + var5 / this.c;
      this.b = this.b + var7 / this.c;
      this.d = this.d + (this.c - 1.0) / this.c * var5 * var7;
   }

   public final void a(Al var1) {
      double var2 = this.c;
      this.c = this.c + var1.c;
      double var4 = var1.a - this.a;
      double var6 = var1.b - this.b;
      this.a = this.a + var4 * var1.c / this.c;
      this.b = this.b + var6 * var1.c / this.c;
      this.d = this.d + (var1.d + var2 * var1.c / this.c * var4 * var6);
   }

   private double b() {
      return this.c;
   }

   public final double a() {
      if (this.c < 2.0) {
         throw new pn(px.INSUFFICIENT_DIMENSION, this.c, 2, true);
      } else {
         return this.e ? this.d / (this.c - 1.0) : this.d / this.c;
      }
   }
}
