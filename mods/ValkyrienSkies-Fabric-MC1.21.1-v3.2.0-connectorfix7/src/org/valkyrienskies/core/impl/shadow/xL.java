package org.valkyrienskies.core.impl.shadow;

public final class xL {
   private static final double i = 1.0E-21;
   private static final double j = 1.618034;
   public final double a = 100.0;
   public Cc.a b = Cc.a.a().b(500);
   public double c;
   public double d;
   public double e;
   public double f;
   public double g;
   public double h;

   public xL() {
      this(100.0, 500);
   }

   private xL(double var1, int var3) {
   }

   public final void a(<unrepresentable> var1, xi var2, double var3, double var5) {
      this.b = this.b.a(0);
      boolean var30 = var2 == xi.MINIMIZE;
      double var8 = this.a(var1, 0.0);
      double var10 = this.a(var1, var5);
      if (var30 ? var8 < var10 : var8 > var10) {
         var3 = var5;
         var5 = 0.0;
         double var12 = var8;
         var8 = var10;
         var10 = var12;
      }

      double var31 = var5 + 1.618034 * (var5 - var3);
      double var14 = this.a(var1, var31);

      while (var30 ? var14 < var10 : var14 > var10) {
         double var16 = (var5 - var3) * (var10 - var14);
         double var18;
         double var20;
         double var22 = BY.w(var20 = (var18 = (var5 - var31) * (var10 - var8)) - var16) < 1.0E-21 ? 2.0E-21 : var20 * 2.0;
         double var24 = var5 - ((var5 - var31) * var18 - (var5 - var3) * var16) / var22;
         double var26 = var5 + this.a * (var31 - var5);
         double var34;
         if ((var24 - var31) * (var5 - var24) > 0.0) {
            var34 = this.a(var1, var24);
            if (var30 ? var34 < var14 : var34 > var14) {
               var3 = var5;
               var5 = var24;
               var8 = var10;
               var10 = var34;
               break;
            }

            if (var30 ? var34 > var10 : var34 < var10) {
               var31 = var24;
               var14 = var34;
               break;
            }

            var24 = var31 + 1.618034 * (var31 - var5);
            var34 = this.a(var1, var24);
         } else if ((var24 - var26) * (var26 - var31) >= 0.0) {
            var24 = var26;
            var34 = this.a(var1, var24);
         } else if ((var24 - var26) * (var31 - var24) > 0.0) {
            var34 = this.a(var1, var24);
            if (var30 ? var34 < var14 : var34 > var14) {
               var5 = var31;
               var31 = var24;
               var24 += 1.618034 * (var31 - var5);
               var10 = var14;
               var14 = var34;
               var34 = this.a(var1, var24);
            }
         } else {
            var24 = var31 + 1.618034 * (var31 - var5);
            var34 = this.a(var1, var24);
         }

         var3 = var5;
         var8 = var10;
         var5 = var31;
         var10 = var14;
         var31 = var24;
         var14 = var34;
      }

      this.c = var3;
      this.f = var8;
      this.e = var5;
      this.h = var10;
      this.d = var31;
      this.g = var14;
      if (this.c > this.d) {
         double var32 = this.c;
         this.c = this.d;
         this.d = var32;
         var32 = this.f;
         this.f = this.g;
         this.g = var32;
      }
   }

   private int d() {
      Cc.a var1 = this.b;
      return this.b.b;
   }

   private int e() {
      Cc.a var1 = this.b;
      return this.b.e;
   }

   public final double a() {
      return this.c;
   }

   private double f() {
      return this.f;
   }

   public final double b() {
      return this.d;
   }

   private double g() {
      return this.g;
   }

   public final double c() {
      return this.e;
   }

   private double h() {
      return this.h;
   }

   public double a(lc var1, double var2) {
      try {
         Cc.a var4 = this.b;
         this.b.d(1);
      } catch (pc var5) {
         throw new pp(var5.a);
      }

      return var1.a(var2);
   }
}
