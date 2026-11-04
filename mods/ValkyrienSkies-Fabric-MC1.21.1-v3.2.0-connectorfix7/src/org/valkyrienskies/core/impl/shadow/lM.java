package org.valkyrienskies.core.impl.shadow;

public final class lM implements kS, ln {
   private final double a;
   private final double b;
   private final double c;

   private lM(double var1, double var3, double var5) {
      this.a = var1;
      this.b = var3;
      this.c = var5;
   }

   @Override
   public final double a(double var1) {
      double var10000 = this.b * var1 + this.c;
      double var5 = this.a;
      double var3 = var10000;
      return var5 * BY.o(var3);
   }

   @Deprecated
   @Override
   public final lc a() {
      return kV.a(this).a();
   }

   private static double b(double var0, double var2) {
      return var2 * BY.o(var0);
   }

   @Override
   public final lg a(lg var1) {
      double var2 = var1.b[0];
      double[] var4 = new double[var1.D() + 1];
      double var5 = this.b * var2 + this.c;
      var4[0] = this.a * BY.o(var5);
      if (var4.length > 1) {
         var4[1] = -this.a * this.b * BY.n(var5);
         double var7 = -this.b * this.b;

         for (int var9 = 2; var9 < var4.length; var9++) {
            var4[var9] = var7 * var4[var9 - 2];
         }
      }

      return var1.a(var4);
   }

   // $VF: synthetic method
   static double a(double var0, double var2) {
      double var6 = var2;
      double var4 = var0;
      return var6 * BY.o(var4);
   }

   public static class a implements kZ {
      @Override
      public final double a(double var1, double... var3) {
         a(var3);
         return lM.a(var1 * var3[1] + var3[2], var3[0]);
      }

      @Override
      public final double[] b(double var1, double... var3) {
         a(var3);
         double var4 = var3[0];
         double var6 = var3[1];
         double var8 = var3[2];
         double var10;
         double var12 = lM.a(var10 = var6 * var1 + var8, 1.0);
         double var14;
         double var16 = (var14 = -var4 * BY.n(var10)) * var1;
         return new double[]{var12, var16, var14};
      }

      private static void a(double[] var0) {
         if (var0 == null) {
            throw new pl();
         }

         if (var0.length != 3) {
            throw new oS(var0.length, 3);
         }
      }
   }
}
