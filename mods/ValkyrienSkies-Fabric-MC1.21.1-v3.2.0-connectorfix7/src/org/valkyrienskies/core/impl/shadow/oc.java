package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public abstract class oc implements Serializable, ot {
   private static final long b = -1146319659338487221L;
   @Deprecated
   private zE c = new zE();
   protected final zF a;

   @Deprecated
   protected oc() {
      this.a = null;
   }

   protected oc(zF var1) {
      this.a = var1;
   }

   @Override
   public final double a(int var1, int var2) {
      if (var2 < var1) {
         throw new pm(px.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT, var1, var2, true);
      } else {
         return this.d(var2) - this.d(var1);
      }
   }

   @Override
   public int a(double var1) {
      if (!(var1 < 0.0) && !(var1 > 1.0)) {
         int var3 = this.d();
         if (var1 == 0.0) {
            return var3;
         }

         if (var3 == Integer.MIN_VALUE) {
            if (this.e(var3) >= var1) {
               return var3;
            }
         } else {
            var3--;
         }

         int var4 = this.e();
         if (var1 == 1.0) {
            return var4;
         }

         double var5 = this.b();
         double var7 = BY.a(this.c());
         if (!Double.isInfinite(var5) && !Double.isNaN(var5) && !Double.isInfinite(var7) && !Double.isNaN(var7) && var7 != 0.0) {
            double var10 = BY.a((1.0 - var1) / var1);
            double var12;
            if ((var12 = var5 - var10 * var7) > var3) {
               var3 = (int)BY.z(var12) - 1;
            }

            var10 = 1.0 / var10;
            if ((var12 = var5 + var10 * var7) < var4) {
               var4 = (int)BY.z(var12) - 1;
            }
         }

         int var23 = var4;
         int var2 = var3;
         double var15 = var1;
         oc var22 = this;

         while (var2 + 1 < var23) {
            if ((var4 = (var2 + var23) / 2) < var2 || var4 > var23) {
               var4 = var2 + (var23 - var2) / 2;
            }

            if (var22.e(var4) >= var15) {
               var23 = var4;
            } else {
               var2 = var4;
            }
         }

         return var23;
      } else {
         throw new po(var1, 0, 1);
      }
   }

   private int a(double var1, int var3, int var4) {
      while (var3 + 1 < var4) {
         int var5;
         if ((var5 = (var3 + var4) / 2) < var3 || var5 > var4) {
            var5 = var3 + (var4 - var3) / 2;
         }

         if (this.e(var5) >= var1) {
            var4 = var5;
         } else {
            var3 = var5;
         }
      }

      return var4;
   }

   @Override
   public final void a(long var1) {
      this.a.setSeed(var1);
      this.c.a(var1);
   }

   @Override
   public int a() {
      return this.a(this.a.nextDouble());
   }

   @Override
   public final int[] a(int var1) {
      if (var1 <= 0) {
         throw new pk(px.NUMBER_OF_SAMPLES, var1);
      }

      int[] var2 = new int[var1];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = this.a();
      }

      return var2;
   }

   private double e(int var1) {
      double var2;
      if (Double.isNaN(var2 = this.d(var1))) {
         throw new oY(px.DISCRETE_CUMULATIVE_PROBABILITY_RETURNED_NAN, var1);
      } else {
         return var2;
      }
   }

   public double b(int var1) {
      return BY.k(this.c(var1));
   }
}
