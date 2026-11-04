package org.valkyrienskies.core.impl.shadow;

public final class xz extends xp {
   private static final double b = 2.0 * BY.x(1.0);
   private final double c;
   private final double d;
   private final xl e;

   private xz(double var1, double var3, wK<wQ> var5) {
      this(var1, var3, BY.a(var1), BY.a(var3), null);
   }

   private xz(double var1, double var3, double var5, double var7, wK<wQ> var9) {
      super(var9);
      if (var1 < b) {
         throw new pn(var1, b, true);
      }

      if (var3 <= 0.0) {
         throw new pk(var3);
      }

      this.c = var1;
      this.d = var3;
      this.e = new xl(this, var5, var7, 1.0);
   }

   private xz(double var1, double var3) {
      this(var1, var3, null);
   }

   private xz(double var1, double var3, double var5, double var7) {
      this(var1, var3, var5, var7, null);
   }

   private wQ b() {
      xz var1 = this;
      if (this.f() == null && var1.g() == null) {
         xi var28 = this.n();
         double[] var2;
         int var3;
         double[][] var4 = new double[var3 = (var2 = this.e()).length][var3];

         for (int var5 = 0; var5 < var3; var5++) {
            var4[var5][var5] = 1.0;
         }

         wK var30 = this.j();
         var2 = var2;
         double var7 = this.b(var2);
         double[] var6 = (double[])var2.clone();

         while (true) {
            this.l();
            double var10 = var7;
            double var14 = 0.0;
            int var9 = 0;

            for (int var16 = 0; var16 < var3; var16++) {
               double[] var18;
               double[] var19 = Ch.a(var18 = var4[var16], var18.length);
               double var12 = var7;
               xS var17;
               var7 = (var17 = this.e.a(var2, var19)).b;
               double var36 = var17.a;
               var2 = a(var2, var19, var36)[0];
               if (var12 - var7 > var14) {
                  var14 = var12 - var7;
                  var9 = var16;
               }
            }

            boolean var35 = 2.0 * (var10 - var7) <= this.c * (BY.w(var10) + BY.w(var7)) + this.d;
            wQ var39 = new wQ(var6, var10);
            wQ var37 = new wQ(var2, var7);
            if (!var35 && var30 != null) {
               var35 = var30.a(this.i(), var39, var37);
            }

            if (var35) {
               if (var28 == xi.MINIMIZE) {
                  if (var7 < var10) {
                     return var37;
                  }

                  return var39;
               }

               if (var7 > var10) {
                  return var37;
               }

               return var39;
            }

            double[] var40 = new double[var3];
            double[] var33 = new double[var3];

            for (int var24 = 0; var24 < var3; var24++) {
               var40[var24] = var2[var24] - var6[var24];
               var33[var24] = 2.0 * var2[var24] - var6[var24];
            }

            var6 = (double[])var2.clone();
            double var34 = this.b(var33);
            if (var10 > var34) {
               double var41 = 2.0 * (var10 + var34 - var7 * 2.0);
               double var26 = var10 - var7 - var14;
               var41 *= var26 * var26;
               var26 = var10 - var34;
               if ((var41 = var41 - var14 * var26 * var26) < 0.0) {
                  xS var31;
                  var7 = (var31 = this.e.a(var2, var40)).b;
                  double var38 = var31.a;
                  double[][] var32;
                  var2 = (var32 = a(var2, var40, var38))[0];
                  int var11 = var3 - 1;
                  var4[var9] = var4[var11];
                  var4[var11] = var32[1];
               }
            }
         }
      } else {
         throw new pb(px.CONSTRAINT);
      }
   }

   private static double[][] a(double[] var0, double[] var1, double var2) {
      int var4;
      double[] var5 = new double[var4 = var0.length];
      double[] var6 = new double[var4];

      for (int var7 = 0; var7 < var4; var7++) {
         var6[var7] = var1[var7] * var2;
         var5[var7] = var0[var7] + var6[var7];
      }

      double[][] var8;
      (var8 = new double[2][])[0] = var5;
      var8[1] = var6;
      return var8;
   }

   private void d() {
      if (this.f() != null || this.g() != null) {
         throw new pb(px.CONSTRAINT);
      }
   }

   // $VF: synthetic method
   @Override
   public final Object c() {
      xz var1 = this;
      xz var2 = this;
      if (this.f() == null && var2.g() == null) {
         xi var29 = var1.n();
         double[] var3;
         int var4;
         double[][] var5 = new double[var4 = (var3 = var1.e()).length][var4];

         for (int var6 = 0; var6 < var4; var6++) {
            var5[var6][var6] = 1.0;
         }

         wK var31 = var1.j();
         var3 = var3;
         double var8 = var1.b(var3);
         double[] var7 = (double[])var3.clone();

         while (true) {
            var1.l();
            double var11 = var8;
            double var15 = 0.0;
            int var10 = 0;

            for (int var17 = 0; var17 < var4; var17++) {
               double[] var19;
               double[] var20 = Ch.a(var19 = var5[var17], var19.length);
               double var13 = var8;
               xS var18;
               var8 = (var18 = var1.e.a(var3, var20)).b;
               double var37 = var18.a;
               var3 = a(var3, var20, var37)[0];
               if (var13 - var8 > var15) {
                  var15 = var13 - var8;
                  var10 = var17;
               }
            }

            boolean var36 = 2.0 * (var11 - var8) <= var1.c * (BY.w(var11) + BY.w(var8)) + var1.d;
            wQ var40 = new wQ(var7, var11);
            wQ var38 = new wQ(var3, var8);
            if (!var36 && var31 != null) {
               var36 = var31.a(var1.i(), var40, var38);
            }

            if (var36) {
               wQ var10000;
               if (var29 == xi.MINIMIZE) {
                  if (!(var8 < var11)) {
                     return var40;
                  }

                  var10000 = var38;
               } else {
                  if (!(var8 > var11)) {
                     return var40;
                  }

                  var10000 = var38;
               }

               return var10000;
            }

            double[] var41 = new double[var4];
            double[] var34 = new double[var4];

            for (int var25 = 0; var25 < var4; var25++) {
               var41[var25] = var3[var25] - var7[var25];
               var34[var25] = 2.0 * var3[var25] - var7[var25];
            }

            var7 = (double[])var3.clone();
            double var35 = var1.b(var34);
            if (var11 > var35) {
               double var42 = 2.0 * (var11 + var35 - var8 * 2.0);
               double var27 = var11 - var8 - var15;
               var42 *= var27 * var27;
               var27 = var11 - var35;
               if ((var42 = var42 - var15 * var27 * var27) < 0.0) {
                  xS var32;
                  var8 = (var32 = var1.e.a(var3, var41)).b;
                  double var39 = var32.a;
                  double[][] var33;
                  var3 = (var33 = a(var3, var41, var39))[0];
                  int var12 = var4 - 1;
                  var5[var10] = var5[var12];
                  var5[var12] = var33[1];
               }
            }
         }
      } else {
         throw new pb(px.CONSTRAINT);
      }
   }
}
