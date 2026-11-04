package org.valkyrienskies.core.impl.shadow;

public final class lS implements kS, ln {
   private final double a;
   private final double b;
   private final double c;
   private final double d;
   private final double e;
   private final double f;

   public lS(double var1, double var3, double var5, double var7, double var9, double var11) {
      this.b = var1;
      this.f = var3;
      this.c = var5;
      this.e = 1.0;
      this.a = 0.0;
      this.d = 1.0;
   }

   @Override
   public final double a(double var1) {
      return b(this.f - var1, this.b, this.c, this.e, this.a, this.d);
   }

   @Deprecated
   @Override
   public final lc a() {
      return kV.a(this).a();
   }

   private static double b(double var0, double var2, double var4, double var6, double var8, double var10) {
      return var8 + (var2 - var8) / BY.a(1.0 + var6 * BY.i(var4 * var0), var10);
   }

   @Override
   public final lg a(lg var1) {
      return var1.G().h(this.f).j(this.c).K().j(this.e).h(1.0).k(this.d).H().j(this.b - this.a).h(this.a);
   }

   public static class a implements kZ {
      @Override
      public final double a(double var1, double... var3) {
         a(var3);
         return lS.b(var3[1] - var1, var3[0], var3[2], var3[3], var3[4], 1.0 / var3[5]);
      }

      @Override
      public final double[] b(double var1, double... var3) {
         a(var3);
         double var4 = var3[2];
         double var6 = var3[3];
         double var8 = var3[1] - var1;
         double var10 = 1.0 / var3[5];
         double var12 = BY.i(var4 * var8);
         double var14;
         double var16 = (var14 = var6 * var12) + 1.0;
         double var18;
         double var20 = -(var18 = (var3[0] - var3[4]) * var10 / BY.a(var16, var10)) / var16;
         double var22 = lS.b(var8, 1.0, var4, var6, 0.0, var10);
         double var24 = var20 * var4 * var14;
         double var26 = var20 * var8 * var14;
         double var28 = var20 * var12;
         double var30 = lS.b(var8, 0.0, var4, var6, 1.0, var10);
         double var32 = var18 * BY.k(var16) * var10;
         return new double[]{var22, var24, var26, var28, var30, var32};
      }

      private static void a(double[] var0) {
         if (var0 == null) {
            throw new pl();
         }

         if (var0.length != 6) {
            throw new oS(var0.length, 6);
         }

         if (var0[5] <= 0.0) {
            throw new pk(var0[5]);
         }
      }
   }
}
