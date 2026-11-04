package org.valkyrienskies.core.impl.shadow;

public final class vE extends vL {
   private static final String f = "Dormand-Prince 5(4)";
   private static final double[] g = new double[]{0.2, 0.3, 0.8, 0.8888888888888888, 1.0, 1.0};
   private static final double[][] m = new double[][]{
      {0.2},
      {0.075, 0.225},
      {0.9777777777777777, -3.7333333333333334, 3.5555555555555554},
      {2.9525986892242035, -11.595793324188385, 9.822892851699436, -0.2908093278463649},
      {2.8462752525252526, -10.757575757575758, 8.906422717743473, 0.2784090909090909, -0.2735313036020583},
      {0.09114583333333333, 0.0, 0.44923629829290207, 0.6510416666666666, -0.322376179245283, 0.13095238095238096}
   };
   private static final double[] n = new double[]{
      0.09114583333333333, 0.0, 0.44923629829290207, 0.6510416666666666, -0.322376179245283, 0.13095238095238096, 0.0
   };
   private static final double o = 0.0012326388888888888;
   private static final double p = -0.0042527702905061394;
   private static final double q = 0.03697916666666667;
   private static final double r = -0.05086379716981132;
   private static final double s = 0.0419047619047619;
   private static final double t = -0.025;

   private vE(double var1, double var3, double var5, double var7) {
      super("Dormand-Prince 5(4)", true, g, m, n, new vF(), var1, var3, var5, var7);
   }

   private vE(double var1, double var3, double[] var5, double[] var6) {
      super("Dormand-Prince 5(4)", true, g, m, n, new vF(), var1, var3, var5, var6);
   }

   @Override
   public final int n() {
      return 5;
   }

   @Override
   protected final double a(double[][] var1, double[] var2, double[] var3, double var4) {
      double var6 = 0.0;

      for (int var8 = 0; var8 < super.l; var8++) {
         double var9 = 0.0012326388888888888 * var1[0][var8]
            + -0.0042527702905061394 * var1[2][var8]
            + 0.03697916666666667 * var1[3][var8]
            + -0.05086379716981132 * var1[4][var8]
            + 0.0419047619047619 * var1[5][var8]
            + -0.025 * var1[6][var8];
         double var11 = BY.e(BY.w(var2[var8]), BY.w(var3[var8]));
         double var13 = super.j == null ? super.h + super.i * var11 : super.j[var8] + super.k[var8] * var11;
         double var15 = var4 * var9 / var13;
         var6 += var15 * var15;
      }

      return BY.a(var6 / super.l);
   }
}
