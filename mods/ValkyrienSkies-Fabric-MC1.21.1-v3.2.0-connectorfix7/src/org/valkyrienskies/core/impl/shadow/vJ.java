package org.valkyrienskies.core.impl.shadow;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

final class vJ extends wm {
   private static final long n = 20111120L;
   private static final double o = 0.054293734116568765;
   private static final double p = 4.450312892752409;
   private static final double q = 1.8915178993145003;
   private static final double r = -5.801203960010585;
   private static final double s = 0.3111643669578199;
   private static final double t = -0.1521609496625161;
   private static final double u = 0.20136540080403034;
   private static final double v = 0.04471061572777259;
   private static final double w = 0.1;
   private static final double x = 0.0018737681664791894;
   private static final double y = -4.450312892752409;
   private static final double z = -1.6380176890978755;
   private static final double A = 5.554964922539782;
   private static final double B = -0.4353557902216363;
   private static final double C = 0.30545274794128174;
   private static final double D = -0.19316434850839564;
   private static final double E = -0.03714271806722689;
   private static final double F = -0.008298;
   private static final double G = 0.2;
   private static final double H = -0.022459085953066622;
   private static final double I = -4.422011983080043;
   private static final double J = -1.8379759110070617;
   private static final double K = 5.746280211439194;
   private static final double L = -0.3111643669578199;
   private static final double M = 0.1521609496625161;
   private static final double N = -0.2014737481327276;
   private static final double O = -0.04432804463693693;
   private static final double P = -3.4046500868740456E-4;
   private static final double Q = 0.1413124436746325;
   private static final double R = 0.7777777777777778;
   private static final double S = -0.4831900357003607;
   private static final double T = -9.147934308113573;
   private static final double U = 5.791903296748099;
   private static final double V = 9.870193778407696;
   private static final double W = 0.04556282049746119;
   private static final double X = 0.1521609496625161;
   private static final double Y = -0.20136540080403034;
   private static final double Z = -0.04471061572777259;
   private static final double aa = -0.0013990241651590145;
   private static final double ab = 2.9475147891527724;
   private static final double ac = -9.15095847217987;
   private static final double[][] ad = new double[][]{
      {
            -8.428938276109013,
            0.5667149535193777,
            -3.0689499459498917,
            2.38466765651207,
            2.1170345824450285,
            -0.871391583777973,
            2.2404374302607883,
            0.6315787787694688,
            -0.08899033645133331,
            18.148505520854727,
            -9.194632392478356,
            -4.436036387594894
      },
      {
            10.427508642579134,
            242.28349177525817,
            165.20045171727028,
            -374.5467547226902,
            -22.113666853125302,
            7.733432668472264,
            -30.674084731089398,
            -9.332130526430229,
            15.697238121770845,
            -31.139403219565178,
            -9.35292435884448,
            35.81684148639408
      },
      {
            19.985053242002433,
            -387.0373087493518,
            -189.17813819516758,
            527.8081592054236,
            -11.573902539959631,
            6.8812326946963,
            -1.0006050966910838,
            0.7777137798053443,
            -2.778205752353508,
            -60.19669523126412,
            84.32040550667716,
            11.99229113618279
      },
      {
            -25.69393346270375,
            -154.18974869023643,
            -231.5293791760455,
            357.6391179106141,
            93.4053241836243,
            -37.45832313645163,
            104.0996495089623,
            29.8402934266605,
            -43.53345659001114,
            96.32455395918828,
            -39.17726167561544,
            -149.72683625798564
      }
   };
   private double[][] ae;
   private double[][] af;
   private boolean ag;

   public vJ() {
      this.ae = null;
      this.af = null;
      this.ag = false;
   }

   private vJ(vJ var1) {
      super(var1);
      if (var1.e == null) {
         this.ae = null;
         this.af = null;
         this.ag = false;
      } else {
         int var2 = var1.e.length;
         this.ae = new double[3][];

         for (int var3 = 0; var3 < this.ae.length; var3++) {
            this.ae[var3] = new double[var2];
            System.arraycopy(var1.ae[var3], 0, this.ae[var3], 0, var2);
         }

         this.af = new double[7][];

         for (int var4 = 0; var4 < this.af.length; var4++) {
            this.af[var4] = new double[var2];
            System.arraycopy(var1.af[var4], 0, this.af[var4], 0, var2);
         }

         this.ag = var1.ag;
      }
   }

   @Override
   public final ws a() {
      return new vJ(this);
   }

   @Override
   public final void a(uA var1, double[] var2, double[][] var3, boolean var4, uE var5, uE[] var6) {
      super.a(var1, var2, var3, var4, var5, var6);
      int var7 = super.e.length;
      this.ae = new double[3][];

      for (int var8 = 0; var8 < this.ae.length; var8++) {
         this.ae[var8] = new double[var7];
      }

      this.af = new double[7][];

      for (int var9 = 0; var9 < this.af.length; var9++) {
         this.af[var9] = new double[var7];
      }

      this.ag = false;
   }

   @Override
   public final void a(double var1) {
      super.a(var1);
      this.ag = false;
   }

   @Override
   public final void a(double var1, double var3) {
      if (!this.ag) {
         if (this.af == null) {
            this.af = new double[7][];

            for (int var5 = 0; var5 < 7; var5++) {
               this.af[var5] = new double[super.g.length];
            }
         }

         this.m();

         for (int var31 = 0; var31 < super.g.length; var31++) {
            double var6 = super.b[0][var31];
            double var8 = super.b[5][var31];
            double var10 = super.b[6][var31];
            double var12 = super.b[7][var31];
            double var14 = super.b[8][var31];
            double var16 = super.b[9][var31];
            double var18 = super.b[10][var31];
            double var20 = super.b[11][var31];
            double var22 = super.b[12][var31];
            double var24 = this.ae[0][var31];
            double var26 = this.ae[1][var31];
            double var28 = this.ae[2][var31];
            this.af[0][var31] = var6 * 0.054293734116568765
               + var8 * 4.450312892752409
               + var10 * 1.8915178993145003
               + var12 * -5.801203960010585
               + var14 * 0.3111643669578199
               + var16 * -0.1521609496625161
               + var18 * 0.20136540080403034
               + var20 * 0.04471061572777259;
            this.af[1][var31] = var6 - this.af[0][var31];
            this.af[2][var31] = this.af[0][var31] - this.af[1][var31] - super.b[12][var31];

            for (int var30 = 0; var30 < ad.length; var30++) {
               this.af[var30 + 3][var31] = ad[var30][0] * var6
                  + ad[var30][1] * var8
                  + ad[var30][2] * var10
                  + ad[var30][3] * var12
                  + ad[var30][4] * var14
                  + ad[var30][5] * var16
                  + ad[var30][6] * var18
                  + ad[var30][7] * var20
                  + ad[var30][8] * var22
                  + ad[var30][9] * var24
                  + ad[var30][10] * var26
                  + ad[var30][11] * var28;
            }
         }

         this.ag = true;
      }

      double var32 = 1.0 - var1;
      double var7 = var1 * 2.0;
      double var9 = var1 * var1;
      double var11 = 1.0 - var7;
      double var13 = var1 * (2.0 - var1 * 3.0);
      double var15 = var7 * (1.0 + var1 * (var7 - 3.0));
      double var17 = var9 * (3.0 + var1 * (5.0 * var1 - 8.0));
      double var19 = var9 * (3.0 + var1 * (-12.0 + var1 * (15.0 - var1 * 6.0)));
      double var21 = var9 * var1 * (4.0 + var1 * (-15.0 + var1 * (18.0 - var1 * 7.0)));
      if (super.a != null && var1 <= 0.5) {
         for (int var33 = 0; var33 < super.g.length; var33++) {
            super.g[var33] = super.a[var33]
               + var1
                  * super.d
                  * (
                     this.af[0][var33]
                        + var32
                           * (
                              this.af[1][var33]
                                 + var1
                                    * (
                                       this.af[2][var33]
                                          + var32 * (this.af[3][var33] + var1 * (this.af[4][var33] + var32 * (this.af[5][var33] + var1 * this.af[6][var33])))
                                    )
                           )
                  );
            super.h[var33] = this.af[0][var33]
               + var11 * this.af[1][var33]
               + var13 * this.af[2][var33]
               + var15 * this.af[3][var33]
               + var17 * this.af[4][var33]
               + var19 * this.af[5][var33]
               + var21 * this.af[6][var33];
         }
      } else {
         for (int var23 = 0; var23 < super.g.length; var23++) {
            super.g[var23] = super.e[var23]
               - var3
                  * (
                     this.af[0][var23]
                        - var1
                           * (
                              this.af[1][var23]
                                 + var1
                                    * (
                                       this.af[2][var23]
                                          + var32 * (this.af[3][var23] + var1 * (this.af[4][var23] + var32 * (this.af[5][var23] + var1 * this.af[6][var23])))
                                    )
                           )
                  );
            super.h[var23] = this.af[0][var23]
               + var11 * this.af[1][var23]
               + var13 * this.af[2][var23]
               + var15 * this.af[3][var23]
               + var17 * this.af[4][var23]
               + var19 * this.af[5][var23]
               + var21 * this.af[6][var23];
         }
      }
   }

   @Override
   public final void b() {
      if (super.e != null) {
         double[] var3 = new double[super.e.length];
         double var4 = this.e();

         for (int var6 = 0; var6 < super.e.length; var6++) {
            double var1 = 0.0018737681664791894 * super.b[0][var6]
               + -4.450312892752409 * super.b[5][var6]
               + -1.6380176890978755 * super.b[6][var6]
               + 5.554964922539782 * super.b[7][var6]
               + -0.4353557902216363 * super.b[8][var6]
               + 0.30545274794128174 * super.b[9][var6]
               + -0.19316434850839564 * super.b[10][var6]
               + -0.03714271806722689 * super.b[11][var6]
               + -0.008298 * super.b[12][var6];
            var3[var6] = super.e[var6] + super.d * var1;
         }

         super.c.a(var4 + 0.1 * super.d, var3, this.ae[0]);

         for (int var9 = 0; var9 < super.e.length; var9++) {
            double var7 = -0.022459085953066622 * super.b[0][var9]
               + -4.422011983080043 * super.b[5][var9]
               + -1.8379759110070617 * super.b[6][var9]
               + 5.746280211439194 * super.b[7][var9]
               + -0.3111643669578199 * super.b[8][var9]
               + 0.1521609496625161 * super.b[9][var9]
               + -0.2014737481327276 * super.b[10][var9]
               + -0.04432804463693693 * super.b[11][var9]
               + -3.4046500868740456E-4 * super.b[12][var9]
               + 0.1413124436746325 * this.ae[0][var9];
            var3[var9] = super.e[var9] + super.d * var7;
         }

         super.c.a(var4 + 0.2 * super.d, var3, this.ae[1]);

         for (int var10 = 0; var10 < super.e.length; var10++) {
            double var8 = -0.4831900357003607 * super.b[0][var10]
               + -9.147934308113573 * super.b[5][var10]
               + 5.791903296748099 * super.b[6][var10]
               + 9.870193778407696 * super.b[7][var10]
               + 0.04556282049746119 * super.b[8][var10]
               + 0.1521609496625161 * super.b[9][var10]
               + -0.20136540080403034 * super.b[10][var10]
               + -0.04471061572777259 * super.b[11][var10]
               + -0.0013990241651590145 * super.b[12][var10]
               + 2.9475147891527724 * this.ae[0][var10]
               + -9.15095847217987 * this.ae[1][var10];
            var3[var10] = super.e[var10] + super.d * var8;
         }

         super.c.a(var4 + 0.7777777777777778 * super.d, var3, this.ae[2]);
      }
   }

   @Override
   public final void writeExternal(ObjectOutput out) {
      try {
         this.m();
      } catch (pc var4) {
         IOException var3;
         (var3 = new IOException(var4.getLocalizedMessage())).initCause(var4);
         throw var3;
      }

      int var2 = super.e == null ? -1 : super.e.length;
      out.writeInt(var2);

      for (int var5 = 0; var5 < var2; var5++) {
         out.writeDouble(this.ae[0][var5]);
         out.writeDouble(this.ae[1][var5]);
         out.writeDouble(this.ae[2][var5]);
      }

      super.writeExternal(out);
   }

   @Override
   public final void readExternal(ObjectInput in) {
      this.ae = new double[3][];
      int var2 = in.readInt();
      this.ae[0] = var2 < 0 ? null : new double[var2];
      this.ae[1] = var2 < 0 ? null : new double[var2];
      this.ae[2] = var2 < 0 ? null : new double[var2];

      for (int var3 = 0; var3 < var2; var3++) {
         this.ae[0][var3] = in.readDouble();
         this.ae[1][var3] = in.readDouble();
         this.ae[2][var3] = in.readDouble();
      }

      super.readExternal(in);
   }
}
