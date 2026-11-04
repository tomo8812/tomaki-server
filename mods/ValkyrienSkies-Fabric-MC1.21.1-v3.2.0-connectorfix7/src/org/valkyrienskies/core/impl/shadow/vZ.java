package org.valkyrienskies.core.impl.shadow;

public final class vZ extends vL {
   private static final String f = "Higham-Hall 5(4)";
   private static final double[] g = new double[]{0.2222222222222222, 0.3333333333333333, 0.5, 0.6, 1.0, 1.0};
   private static final double[][] m = new double[][]{
      {0.2222222222222222},
      {0.08333333333333333, 0.25},
      {0.125, 0.0, 0.375},
      {0.182, -0.27, 0.624, 0.064},
      {-0.55, 1.35, 2.4, -7.2, 5.0},
      {0.08333333333333333, 0.0, 0.84375, -1.3333333333333333, 1.3020833333333333, 0.10416666666666667}
   };
   private static final double[] n = new double[]{0.08333333333333333, 0.0, 0.84375, -1.3333333333333333, 1.3020833333333333, 0.10416666666666667, 0.0};
   private static final double[] o = new double[]{-0.05, 0.0, 0.50625, -1.2, 0.78125, 0.0625, -0.1};

   private vZ(double var1, double var3, double var5, double var7) {
      super("Higham-Hall 5(4)", false, g, m, n, new wa(), var1, var3, var5, var7);
   }

   private vZ(double var1, double var3, double[] var5, double[] var6) {
      super("Higham-Hall 5(4)", false, g, m, n, new wa(), var1, var3, var5, var6);
   }

   @Override
   public final int n() {
      return 5;
   }

   @Override
   protected final double a(double[][] var1, double[] var2, double[] var3, double var4) {
      double var6 = 0.0;

      for (int var8 = 0; var8 < super.l; var8++) {
         double var9 = o[0] * var1[0][var8];

         for (int var11 = 1; var11 < o.length; var11++) {
            var9 += o[var11] * var1[var11][var8];
         }

         double var17 = BY.e(BY.w(var2[var8]), BY.w(var3[var8]));
         double var13 = super.j == null ? super.h + super.i * var17 : super.j[var8] + super.k[var8] * var17;
         double var15 = var4 * var9 / var13;
         var6 += var15 * var15;
      }

      return BY.a(var6 / super.l);
   }
}
