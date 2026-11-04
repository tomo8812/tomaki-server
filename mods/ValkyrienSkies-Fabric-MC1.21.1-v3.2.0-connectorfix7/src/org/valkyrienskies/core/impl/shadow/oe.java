package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public abstract class oe implements Serializable, oI {
   private static double b;
   private static final long c = -38038050983108802L;
   @Deprecated
   private zE d = new zE();
   protected final zF a;
   private double e = 1.0E-6;

   @Deprecated
   protected oe() {
      this.a = null;
   }

   public oe(zF var1) {
      this.a = var1;
   }

   @Deprecated
   @Override
   public double a(double var1, double var3) {
      return this.b(var1, var3);
   }

   public double b(double var1, double var3) {
      if (var1 > var3) {
         throw new pm(px.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT, var1, var3, true);
      } else {
         return this.e(var3) - this.e(var1);
      }
   }

   @Override
   public double a(final double var1) {
      if (!(var1 < 0.0) && !(var1 > 1.0)) {
         double var3 = this.e();
         if (var1 == 0.0) {
            return var3;
         }

         double var5 = this.f();
         if (var1 == 1.0) {
            return var5;
         }

         double var7 = this.c();
         double var9 = BY.a(this.d());
         boolean var11 = !Double.isInfinite(var7) && !Double.isNaN(var7) && !Double.isInfinite(var9) && !Double.isNaN(var9);
         if (var3 == Double.NEGATIVE_INFINITY) {
            if (var11) {
               var3 = var7 - var9 * BY.a((1.0 - var1) / var1);
            } else {
               var3 = -1.0;

               while (this.e(var3) >= var1) {
                  var3 *= 2.0;
               }
            }
         }

         if (var5 == Double.POSITIVE_INFINITY) {
            if (var11) {
               var5 = var7 + var9 * BY.a(var1 / (1.0 - var1));
            } else {
               var5 = 1.0;

               while (this.e(var5) < var1) {
                  var5 *= 2.0;
               }
            }
         }

         lc var10000 = new lc() {
            @Override
            public final double a(double var1x) {
               return oe.this.e(var1x) - var1;
            }
         };
         double var20 = this.a();
         double var18 = var5;
         double var16 = var3;
         lc var22 = var10000;
         return new nz(var20).a(Integer.MAX_VALUE, var22, var16, var18);
      } else {
         throw new po(var1, 0, 1);
      }
   }

   protected double a() {
      return this.e;
   }

   @Override
   public void a(long var1) {
      this.a.setSeed(var1);
      this.d.a(var1);
   }

   @Override
   public double b() {
      return this.a(this.a.nextDouble());
   }

   @Override
   public final double[] a(int var1) {
      if (var1 <= 0) {
         throw new pk(px.NUMBER_OF_SAMPLES, var1);
      }

      double[] var2 = new double[var1];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = this.b();
      }

      return var2;
   }

   @Override
   public double b(double var1) {
      return 0.0;
   }

   public double c(double var1) {
      return BY.k(this.d(var1));
   }
}
