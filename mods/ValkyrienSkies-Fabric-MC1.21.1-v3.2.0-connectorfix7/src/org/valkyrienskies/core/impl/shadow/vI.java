package org.valkyrienskies.core.impl.shadow;

public final class vI extends vL {
   private static final String f = "Dormand-Prince 8 (5, 3)";
   private static final double[] g = new double[]{
      (12.0 - 2.0 * BY.a(6.0)) / 135.0,
      (6.0 - BY.a(6.0)) / 45.0,
      (6.0 - BY.a(6.0)) / 30.0,
      (6.0 + BY.a(6.0)) / 30.0,
      0.3333333333333333,
      0.25,
      0.3076923076923077,
      0.6512820512820513,
      0.6,
      0.8571428571428571,
      1.0,
      1.0
   };
   private static final double[][] m = new double[][]{
      {(12.0 - 2.0 * BY.a(6.0)) / 135.0},
      {(6.0 - BY.a(6.0)) / 180.0, (6.0 - BY.a(6.0)) / 60.0},
      {(6.0 - BY.a(6.0)) / 120.0, 0.0, (6.0 - BY.a(6.0)) / 40.0},
      {(462.0 + 107.0 * BY.a(6.0)) / 3000.0, 0.0, (-402.0 - 197.0 * BY.a(6.0)) / 1000.0, (168.0 + 73.0 * BY.a(6.0)) / 375.0},
      {0.037037037037037035, 0.0, 0.0, (16.0 + BY.a(6.0)) / 108.0, (16.0 - BY.a(6.0)) / 108.0},
      {0.037109375, 0.0, 0.0, (118.0 + 23.0 * BY.a(6.0)) / 1024.0, (118.0 - 23.0 * BY.a(6.0)) / 1024.0, -0.017578125},
      {
            0.03709200011850479,
            0.0,
            0.0,
            (51544.0 + 4784.0 * BY.a(6.0)) / 371293.0,
            (51544.0 - 4784.0 * BY.a(6.0)) / 371293.0,
            -0.015319437748624402,
            0.008273789163814023
      },
      {
            0.6241109587160757,
            0.0,
            0.0,
            (-1.324889724104E12 - 3.18801444819E11 * BY.a(6.0)) / 6.265569375E11,
            (-1.324889724104E12 + 3.18801444819E11 * BY.a(6.0)) / 6.265569375E11,
            27.59209969944671,
            20.154067550477894,
            -43.48988418106996
      },
      {
            0.47766253643826434,
            0.0,
            0.0,
            (-4521408.0 - 1137963.0 * BY.a(6.0)) / 2937500.0,
            (-4521408.0 + 1137963.0 * BY.a(6.0)) / 2937500.0,
            21.230051448181193,
            15.279233632882423,
            -33.28821096898486,
            -0.020331201708508627
      },
      {
            -0.9371424300859873,
            0.0,
            0.0,
            (354216.0 + 94326.0 * BY.a(6.0)) / 112847.0,
            (354216.0 - 94326.0 * BY.a(6.0)) / 112847.0,
            -8.149787010746927,
            -18.52006565999696,
            22.739487099350505,
            2.4936055526796523,
            -3.0467644718982196
      },
      {
            2.273310147516538,
            0.0,
            0.0,
            (-3457480.0 - 960905.0 * BY.a(6.0)) / 551636.0,
            (-3457480.0 + 960905.0 * BY.a(6.0)) / 551636.0,
            -17.9589318631188,
            27.94888452941996,
            -2.8589982771350235,
            -8.87285693353063,
            12.360567175794303,
            0.6433927460157636
      },
      {
            0.054293734116568765,
            0.0,
            0.0,
            0.0,
            0.0,
            4.450312892752409,
            1.8915178993145003,
            -5.801203960010585,
            0.3111643669578199,
            -0.1521609496625161,
            0.20136540080403034,
            0.04471061572777259
      }
   };
   private static final double[] n = new double[]{
      0.054293734116568765,
      0.0,
      0.0,
      0.0,
      0.0,
      4.450312892752409,
      1.8915178993145003,
      -5.801203960010585,
      0.3111643669578199,
      -0.1521609496625161,
      0.20136540080403034,
      0.04471061572777259,
      0.0
   };
   private static final double o = 0.01312004499419488;
   private static final double p = -1.2251564463762044;
   private static final double q = -0.4957589496572502;
   private static final double r = 1.6643771824549864;
   private static final double s = -0.35032884874997366;
   private static final double t = 0.3341791187130175;
   private static final double u = 0.08192320648511571;
   private static final double v = -0.022355307863886294;
   private static final double w = -0.18980075407240762;
   private static final double x = 4.450312892752409;
   private static final double y = 1.8915178993145003;
   private static final double z = -5.801203960010585;
   private static final double A = -0.42268232132379197;
   private static final double B = -0.1521609496625161;
   private static final double C = 0.20136540080403034;
   private static final double D = 0.022651792198360825;

   public vI(double var1, double var3, double var5, double var7) {
      super("Dormand-Prince 8 (5, 3)", true, g, m, n, new vJ(), var1, var3, var5, var7);
   }

   public vI(double var1, double var3, double[] var5, double[] var6) {
      super("Dormand-Prince 8 (5, 3)", true, g, m, n, new vJ(), var1, var3, var5, var6);
   }

   @Override
   public final int n() {
      return 8;
   }

   @Override
   protected final double a(double[][] var1, double[] var2, double[] var3, double var4) {
      double var6 = 0.0;
      double var8 = 0.0;

      for (int var10 = 0; var10 < super.l; var10++) {
         double var11 = 0.01312004499419488 * var1[0][var10]
            + -1.2251564463762044 * var1[5][var10]
            + -0.4957589496572502 * var1[6][var10]
            + 1.6643771824549864 * var1[7][var10]
            + -0.35032884874997366 * var1[8][var10]
            + 0.3341791187130175 * var1[9][var10]
            + 0.08192320648511571 * var1[10][var10]
            + -0.022355307863886294 * var1[11][var10];
         double var13 = -0.18980075407240762 * var1[0][var10]
            + 4.450312892752409 * var1[5][var10]
            + 1.8915178993145003 * var1[6][var10]
            + -5.801203960010585 * var1[7][var10]
            + -0.42268232132379197 * var1[8][var10]
            + -0.1521609496625161 * var1[9][var10]
            + 0.20136540080403034 * var1[10][var10]
            + 0.022651792198360825 * var1[11][var10];
         double var15 = BY.e(BY.w(var2[var10]), BY.w(var3[var10]));
         double var17 = super.j == null ? super.h + super.i * var15 : super.j[var10] + super.k[var10] * var15;
         double var19 = var11 / var17;
         var6 += var19 * var19;
         double var21 = var13 / var17;
         var8 += var21 * var21;
      }

      double var23;
      if ((var23 = var6 + var8 * 0.01) <= 0.0) {
         var23 = 1.0;
      }

      return BY.w(var4) * var6 / BY.a(super.l * var23);
   }
}
