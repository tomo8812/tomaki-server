package org.valkyrienskies.core.impl.shadow;

final class vF extends wm {
   private static final double n = 0.09114583333333333;
   private static final double o = 0.44923629829290207;
   private static final double p = 0.6510416666666666;
   private static final double q = -0.322376179245283;
   private static final double r = 0.13095238095238096;
   private static final double s = -1.1270175653862835;
   private static final double t = 2.675424484351598;
   private static final double u = -5.685526961588504;
   private static final double v = 3.5219323679207912;
   private static final double w = -1.7672812570757455;
   private static final double x = 2.382468931778144;
   private static final long y = 20111120L;
   private double[] z;
   private double[] A;
   private double[] B;
   private double[] C;
   private boolean D;

   public vF() {
      this.z = null;
      this.A = null;
      this.B = null;
      this.C = null;
      this.D = false;
   }

   private vF(vF var1) {
      super(var1);
      if (var1.z == null) {
         this.z = null;
         this.A = null;
         this.B = null;
         this.C = null;
         this.D = false;
      } else {
         this.z = (double[])var1.z.clone();
         this.A = (double[])var1.A.clone();
         this.B = (double[])var1.B.clone();
         this.C = (double[])var1.C.clone();
         this.D = var1.D;
      }
   }

   @Override
   public final ws a() {
      return new vF(this);
   }

   @Override
   public final void a(uA var1, double[] var2, double[][] var3, boolean var4, uE var5, uE[] var6) {
      super.a(var1, var2, var3, var4, var5, var6);
      this.z = null;
      this.A = null;
      this.B = null;
      this.C = null;
      this.D = false;
   }

   @Override
   public final void a(double var1) {
      super.a(var1);
      this.D = false;
   }

   @Override
   public final void a(double var1, double var3) {
      if (!this.D) {
         if (this.z == null) {
            this.z = new double[super.g.length];
            this.A = new double[super.g.length];
            this.B = new double[super.g.length];
            this.C = new double[super.g.length];
         }

         for (int var5 = 0; var5 < super.g.length; var5++) {
            double var6 = super.b[0][var5];
            double var8 = super.b[2][var5];
            double var10 = super.b[3][var5];
            double var12 = super.b[4][var5];
            double var14 = super.b[5][var5];
            double var16 = super.b[6][var5];
            this.z[var5] = var6 * 0.09114583333333333
               + var8 * 0.44923629829290207
               + var10 * 0.6510416666666666
               + var12 * -0.322376179245283
               + var14 * 0.13095238095238096;
            this.A[var5] = var6 - this.z[var5];
            this.B[var5] = this.z[var5] - this.A[var5] - var16;
            this.C[var5] = var6 * -1.1270175653862835
               + var8 * 2.675424484351598
               + var10 * -5.685526961588504
               + var12 * 3.5219323679207912
               + var14 * -1.7672812570757455
               + var16 * 2.382468931778144;
         }

         this.D = true;
      }

      double var18 = 1.0 - var1;
      double var7 = var1 * 2.0;
      double var9 = 1.0 - var7;
      double var11 = var1 * (2.0 - var1 * 3.0);
      double var13 = var7 * (1.0 + var1 * (var7 - 3.0));
      if (super.a != null && var1 <= 0.5) {
         for (int var19 = 0; var19 < super.g.length; var19++) {
            super.g[var19] = super.a[var19] + var1 * super.d * (this.z[var19] + var18 * (this.A[var19] + var1 * (this.B[var19] + var18 * this.C[var19])));
            super.h[var19] = this.z[var19] + var9 * this.A[var19] + var11 * this.B[var19] + var13 * this.C[var19];
         }
      } else {
         for (int var15 = 0; var15 < super.g.length; var15++) {
            super.g[var15] = super.e[var15] - var3 * (this.z[var15] - var1 * (this.A[var15] + var1 * (this.B[var15] + var18 * this.C[var15])));
            super.h[var15] = this.z[var15] + var9 * this.A[var15] + var11 * this.B[var15] + var13 * this.C[var15];
         }
      }
   }
}
