package org.valkyrienskies.core.impl.shadow;

public final class lT implements kS, ln {
   private final double a;
   private final double b;

   public lT() {
      this(0.0, 1.0);
   }

   public lT(double var1, double var3) {
      this.a = var1;
      this.b = var3;
   }

   @Override
   public final double a(double var1) {
      return a(var1, this.a, this.b);
   }

   @Deprecated
   @Override
   public final lc a() {
      return kV.a(this).a();
   }

   static double a(double var0, double var2, double var4) {
      if (!(var0 < var2) && !(var0 > var4)) {
         return BY.k((var0 - var2) / (var4 - var0));
      } else {
         throw new po(var0, var2, var4);
      }
   }

   @Override
   public final lg a(lg var1) {
      double var2;
      if (!((var2 = var1.b[0]) < this.a) && !(var2 > this.b)) {
         double[] var4;
         (var4 = new double[var1.D() + 1])[0] = BY.k((var2 - this.a) / (this.b - var2));
         if (Double.isInfinite(var4[0])) {
            if (var4.length > 1) {
               var4[1] = Double.POSITIVE_INFINITY;
            }

            for (int var5 = 2; var5 < var4.length; var5++) {
               var4[var5] = var4[var5 - 2];
            }
         } else {
            double var14;
            double var7 = var14 = 1.0 / (var2 - this.a);
            double var9;
            double var11 = var9 = 1.0 / (this.b - var2);

            for (int var13 = 1; var13 < var4.length; var13++) {
               var4[var13] = var7 + var11;
               var7 *= -var13 * var14;
               var11 *= var13 * var9;
            }
         }

         return var1.a(var4);
      } else {
         throw new po(var2, this.a, this.b);
      }
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
         return lT.a(var1, var6, var8);
      }

      @Override
      public final double[] b(double var1, double... var3) {
         a(var3);
         double var4 = var3[0];
         double var6 = var3[1];
         return new double[]{1.0 / (var4 - var1), 1.0 / (var6 - var1)};
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
