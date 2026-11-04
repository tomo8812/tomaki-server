package org.valkyrienskies.core.impl.shadow;

public final class xl {
   private static final double b = 1.0E-15;
   private static final double c = Double.MIN_VALUE;
   private final xM d;
   private final xL e = new xL();
   private final double f;
   final xp a;

   public xl(xp var1, double var2, double var4, double var6) {
      this.a = var1;
      this.d = new xM(1.0E-15, Double.MIN_VALUE, new xP(var2, var4));
      this.f = var6;
   }

   public final xS a(final double[] var1, final double[] var2) {
      final int var3 = var1.length;
      lc var36 = new lc() {
         @Override
         public final double a(double var1x) {
            double[] var3x = new double[var3];

            for (int var4 = 0; var4 < var3; var4++) {
               var3x[var4] = var1[var4] + var1x * var2[var4];
            }

            xl var6 = xl.this;
            return xl.this.a.b(var3x);
         }
      };
      xp var37 = this.a;
      xi var38 = this.a.a;
      double var11 = this.f;
      double var9 = 0.0;
      xi var5 = var38;
      lc var4 = var36;
      xL var39 = this.e;
      this.e.b = var39.b.a(0);
      boolean var43 = var5 == xi.MINIMIZE;
      double var14 = var39.a(var4, 0.0);
      double var16 = var39.a(var4, var11);
      if (var43 ? var14 < var16 : var14 > var16) {
         var9 = var11;
         var11 = 0.0;
         double var18 = var14;
         var14 = var16;
         var16 = var18;
      }

      double var44 = var11 + 1.618034 * (var11 - var9);
      double var20 = var39.a(var4, var44);

      while (var43 ? var20 < var16 : var20 > var16) {
         double var22 = (var11 - var9) * (var16 - var20);
         double var24;
         double var26;
         double var28 = BY.w(var26 = (var24 = (var11 - var44) * (var16 - var14)) - var22) < 1.0E-21 ? 2.0E-21 : var26 * 2.0;
         double var30 = var11 - ((var11 - var44) * var24 - (var11 - var9) * var22) / var28;
         double var32 = var11 + var39.a * (var44 - var11);
         double var47;
         if ((var30 - var44) * (var11 - var30) > 0.0) {
            var47 = var39.a(var4, var30);
            if (var43 ? var47 < var20 : var47 > var20) {
               var9 = var11;
               var11 = var30;
               var14 = var16;
               var16 = var47;
               break;
            }

            if (var43 ? var47 > var16 : var47 < var16) {
               var44 = var30;
               var20 = var47;
               break;
            }

            var30 = var44 + 1.618034 * (var44 - var11);
            var47 = var39.a(var4, var30);
         } else if ((var30 - var32) * (var32 - var44) >= 0.0) {
            var30 = var32;
            var47 = var39.a(var4, var30);
         } else if ((var30 - var32) * (var44 - var30) > 0.0) {
            var47 = var39.a(var4, var30);
            if (var43 ? var47 < var20 : var47 > var20) {
               var11 = var44;
               var44 = var30;
               var30 += 1.618034 * (var44 - var11);
               var16 = var20;
               var20 = var47;
               var47 = var39.a(var4, var30);
            }
         } else {
            var30 = var44 + 1.618034 * (var44 - var11);
            var47 = var39.a(var4, var30);
         }

         var9 = var11;
         var14 = var16;
         var11 = var44;
         var16 = var20;
         var44 = var30;
         var20 = var47;
      }

      var39.c = var9;
      var39.f = var14;
      var39.e = var11;
      var39.h = var16;
      var39.d = var44;
      var39.g = var20;
      if (var39.c > var39.d) {
         double var45 = var39.c;
         var39.c = var39.d;
         var39.d = var45;
         var45 = var39.f;
         var39.f = var39.g;
         var39.g = var45;
      }

      xM var10000 = this.d;
      wO[] var10001 = new wO[]{new wM(Integer.MAX_VALUE), new xQ(var36), var38, null};
      xL var40 = this.e;
      xL var41 = this.e;
      xL var42 = this.e;
      var10001[3] = new xO(this.e.c, this.e.d, this.e.e);
      return var10000.c(var10001);
   }

   // $VF: synthetic method
   static xp a(xl var0) {
      return var0.a;
   }
}
