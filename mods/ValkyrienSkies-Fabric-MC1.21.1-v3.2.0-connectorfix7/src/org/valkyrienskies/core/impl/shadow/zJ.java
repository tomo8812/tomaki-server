package org.valkyrienskies.core.impl.shadow;

public final class zJ implements zA {
   private final zF a;
   private final double b;
   private final double c;
   private final double d;

   private zJ(zF var1, double var2, double var4) {
      if (var1 == null) {
         throw new pl();
      }

      if (!(var2 > 0.0) || !(var2 <= 2.0)) {
         throw new po(px.OUT_OF_RANGE_LEFT, var2, 0, 2);
      }

      if (var4 >= -1.0 && var4 <= 1.0) {
         this.a = var1;
         this.b = var2;
         this.c = var4;
         if (var2 < 2.0 && var4 != 0.0) {
            this.d = var4 * BY.p(var2 * Math.PI / 2.0);
         } else {
            this.d = 0.0;
         }
      } else {
         throw new po(px.OUT_OF_RANGE_SIMPLE, var4, -1, 1);
      }
   }

   @Override
   public final double a() {
      double var1 = -BY.k(this.a.nextDouble());
      double var3 = Math.PI * (this.a.nextDouble() - 0.5);
      if (this.b == 2.0) {
         return BY.a(var1 * 2.0) * BY.n(var3);
      }

      double var5;
      if (this.c == 0.0) {
         if (this.b == 1.0) {
            var5 = BY.p(var3);
         } else {
            var5 = BY.a(var1 * BY.o((1.0 - this.b) * var3), 1.0 / this.b - 1.0) * BY.n(this.b * var3) / BY.a(BY.o(var3), 1.0 / this.b);
         }
      } else {
         double var7 = BY.o(var3);
         if (BY.w(this.b - 1.0) > 1.0E-8) {
            double var9 = this.b * var3;
            double var11 = var3 - var9;
            var5 = (BY.n(var9) + this.d * BY.o(var9)) / var7 * (BY.o(var11) + this.d * BY.n(var11)) / BY.a(var1 * var7, (1.0 - this.b) / this.b);
         } else {
            double var13 = (Math.PI / 2) + this.c * var3;
            var5 = 0.6366197723675814 * (var13 * BY.p(var3) - this.c * BY.k(var1 * (Math.PI / 2) * var7 / var13));
            if (this.b != 1.0) {
               var5 += this.c * BY.p(Math.PI * this.b / 2.0);
            }
         }
      }

      return var5;
   }
}
