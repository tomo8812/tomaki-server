package org.valkyrienskies.core.impl.shadow;

public final class mn extends mm {
   private static final mw a = new mw();
   private final int b;

   private mn(int var1, double var2, double var4, int var6, int var7) {
      super(var2, var4, var6, var7);
      if (var1 <= 0) {
         throw new pk(px.NUMBER_OF_POINTS, var1);
      }

      this.b = var1;
   }

   private mn(int var1, double var2, double var4) {
      this(var1, var2, var4, 3, Integer.MAX_VALUE);
   }

   private mn(int var1, int var2, int var3) {
      this(var1, 1.0E-6, 1.0E-15, var2, var3);
   }

   @Override
   protected final double j() {
      double var1 = this.a(1);
      int var3 = 2;

      while (true) {
         double var4;
         double var6 = BY.w((var4 = this.a(var3)) - var1);
         double var8 = BY.e(this.b(), this.a() * (BY.w(var1) + BY.w(var4)) * 0.5);
         if (this.f() + 1 >= this.c() && var6 <= var8) {
            return var4;
         }

         var3 = BY.b((int)(BY.d(4.0, BY.a(var6 / var8, 0.5 / this.b)) * var3), var3 + 1);
         var1 = var4;
         this.g();
      }
   }

   private double a(int var1) {
      lc var2 = new lc() {
         @Override
         public final double a(double var1) {
            return mn.this.a(var1);
         }
      };
      double var3 = this.h();
      double var7 = (this.i() - var3) / var1;
      double var9 = 0.0;

      for (int var5 = 0; var5 < var1; var5++) {
         double var12;
         double var14 = (var12 = var3 + var5 * var7) + var7;
         mv var6 = a.a(this.b, var12, var14);
         var9 += var6.a(var2);
      }

      return var9;
   }
}
