package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public final class mb implements kS, ln {
   private final double a;
   private final double b;

   public mb() {
      this(0.0, 1.0);
   }

   public mb(double var1, double var3) {
      this.a = var1;
      this.b = var3;
   }

   @Deprecated
   @Override
   public final lc a() {
      return kV.a(this).a();
   }

   @Override
   public final double a(double var1) {
      return a(var1, this.a, this.b);
   }

   static double a(double var0, double var2, double var4) {
      return var2 + (var4 - var2) / (1.0 + BY.i(-var0));
   }

   @Override
   public final lg a(lg var1) {
      double[] var2 = new double[var1.D() + 1];
      double var3;
      if (Double.isInfinite(var3 = BY.i(-var1.b[0]))) {
         var2[0] = this.a;
         Arrays.fill(var2, 1, var2.length, 0.0);
      } else {
         double[] var5 = new double[var2.length];
         double var6 = 1.0 / (var3 + 1.0);
         double var8 = this.b - this.a;

         for (int var10 = 0; var10 < var2.length; var10++) {
            double var11 = 0.0;
            var5[var10] = 1.0;

            for (int var13 = var10; var13 >= 0; var13--) {
               var11 = var11 * var3 + var5[var13];
               if (var13 > 1) {
                  var5[var13 - 1] = (var10 - var13 + 2) * var5[var13 - 2] - (var13 - 1) * var5[var13 - 1];
               } else {
                  var5[0] = 0.0;
               }
            }

            var8 *= var6;
            var2[var10] = var8 * var11;
         }

         var2[0] += this.a;
      }

      return var1.a(var2);
   }

   // $VF: synthetic method
   static double b(double var0, double var2, double var4) {
      return a(var0, var2, var4);
   }

   public static class a implements kZ {
      @Override
      public final double a(double var1, double... var3) {
         a(var3);
         double var10001 = var3[0];
         double var8 = var3[1];
         double var6 = var10001;
         return mb.a(var1, var6, var8);
      }

      @Override
      public final double[] b(double var1, double... var3) {
         a(var3);
         double var4 = 1.0 / (1.0 + BY.i(-var1));
         return new double[]{1.0 - var4, var4};
      }

      private static void a(double[] var0) {
         if (var0 == null) {
            throw new pl();
         }

         if (var0.length != 2) {
            throw new oS(var0.length, 2);
         }
      }
   }
}
