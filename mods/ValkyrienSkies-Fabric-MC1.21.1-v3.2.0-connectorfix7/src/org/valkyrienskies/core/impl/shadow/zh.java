package org.valkyrienskies.core.impl.shadow;

@Deprecated
public final class zh {
   private static final double d = 1.0E-21;
   private static final double e = 1.618034;
   private final double f;
   private final Cb g = new Cb();
   public double a;
   public double b;
   public double c;
   private double h;
   private double i;
   private double j;

   public zh() {
      this(100.0, 50);
   }

   private zh(double var1, int var3) {
      this.f = 100.0;
      this.g.a(50);
   }

   public final void a(<unrepresentable> var1, yf var2, double var3, double var5) {
      this.g.d();
      boolean var30 = var2 == yf.MINIMIZE;
      double var8 = this.a(var1, 0.0);
      double var10 = this.a(var1, 1.0);
      if (var30 ? var8 < var10 : var8 > var10) {
         var3 = 1.0;
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
         double var26 = var5 + this.f * (var31 - var5);
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

      this.a = var3;
      this.h = var8;
      this.c = var5;
      this.j = var10;
      this.b = var31;
      this.i = var14;
      if (this.a > this.b) {
         double var32 = this.a;
         this.a = this.b;
         this.b = var32;
         var32 = this.h;
         this.h = this.i;
         this.i = var32;
      }
   }

   private int d() {
      Cb var1 = this.g;
      return this.g.a;
   }

   private int e() {
      Cb var1 = this.g;
      return this.g.b;
   }

   public final double a() {
      return this.a;
   }

   private double f() {
      return this.h;
   }

   public final double b() {
      return this.b;
   }

   private double g() {
      return this.i;
   }

   public final double c() {
      return this.c;
   }

   private double h() {
      return this.j;
   }

   private double a(lc var1, double var2) {
      try {
         this.g.c();
      } catch (pc var4) {
         throw new pp(var4.a);
      }

      return var1.a(var2);
   }
}
