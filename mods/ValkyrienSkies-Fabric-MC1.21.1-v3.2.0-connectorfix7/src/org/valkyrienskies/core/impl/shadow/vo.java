package org.valkyrienskies.core.impl.shadow;

public final class vo extends vr {
   private static final String m = "Adams-Bashforth";

   private vo(int var1, double var2, double var4, double var6, double var8) {
      super("Adams-Bashforth", var1, var1, var2, var4, var6, var8);
   }

   private vo(int var1, double var2, double var4, double[] var6, double[] var7) {
      super("Adams-Bashforth", var1, var1, var2, var4, var6, var7);
   }

   private double a(double[] var1, double[] var2, double[] var3, sy var4) {
      double var5 = 0.0;

      for (int var7 = 0; var7 < super.l; var7++) {
         double var8 = BY.w(var2[var7]);
         double var10 = super.j == null ? super.h + super.i * var8 : super.j[var7] + super.k[var7] * var8;
         double var12 = 0.0;
         int var17 = var4.f() % 2 == 0 ? -1 : 1;

         for (int var15 = var4.f() - 1; var15 >= 0; var15--) {
            var12 += var17 * var4.b(var15, var7);
            var17 = -var17;
         }

         var12 -= var3[var7];
         double var19 = (var2[var7] - var1[var7] + var12) / var10;
         var5 += var19 * var19;
      }

      return BY.a(var5 / super.l);
   }

   @Override
   public final void a(uF var1, double var2) {
      this.b(var1, var2);
      this.a(var1);
      boolean var4 = var2 > var1.c;
      double[] var5;
      double[] var6 = new double[(var5 = var1.g()).length];
      wz var7;
      (var7 = new wz()).a(var5, var4, var1.b, var1.d());
      this.a(var1.c, var5, var2);
      this.b(var1.c, var5, var2);
      var7.a(super.a, super.b, super.f, super.g);
      var7.a(super.a);
      double var8 = super.b;
      var7.e(var8);
      super.c = false;

      do {
         var7.c();
         double[] var10 = new double[var5.length];
         double[] var11 = new double[var5.length];
         sy var12 = null;
         double var13 = 10.0;

         while (var13 >= 1.0) {
            double var15 = super.a + var8;
            var7.a(var15);
            uF var17;
            (var17 = this.j()).b.b(var7.k(), var10);
            int var19 = 0;
            uE[] var20;
            int var21 = (var20 = var17.d()).length;

            for (int var22 = 0; var22 < var21; var22++) {
               uE var47;
               (var47 = var20[var22]).b(var7.a(var19), var10);
               var19++;
            }

            this.a(var15, var10, var6);

            for (int var56 = 0; var56 < var11.length; var56++) {
               var11[var56] = var8 * var6[var56];
            }

            var12 = this.a(super.g);
            this.a(super.f, var11, var12);
            sy var52 = var12;
            double[] var16 = var11;
            double[] var50 = var10;
            double[] var14 = var5;
            vo var49 = this;
            double var32 = 0.0;

            for (int var18 = 0; var18 < var49.l; var18++) {
               double var35 = BY.w(var50[var18]);
               double var37 = var49.j == null ? var49.h + var49.i * var35 : var49.j[var18] + var49.k[var18] * var35;
               double var39 = 0.0;
               var19 = var52.f() % 2 == 0 ? -1 : 1;

               for (int var42 = var52.f() - 1; var42 >= 0; var42--) {
                  var39 += var19 * var52.b(var42, var18);
                  var19 = -var19;
               }

               var39 -= var16[var18];
               double var60 = (var50[var18] - var14[var18] + var39) / var37;
               var32 += var60 * var60;
            }

            if ((var13 = BY.a(var32 / var49.l)) >= 1.0) {
               double var57 = this.a(var13);
               var8 = this.a(var8 * var57, var4, false);
               var7.e(var8);
            }
         }

         super.b = var8;
         double var51 = super.a + super.b;
         var7.a(var51, super.b, var11, var12);
         var7.a(var51);
         System.arraycopy(var10, 0, var5, 0, var5.length);
         super.a = this.a(var7, var5, var6, var2);
         super.f = var11;
         super.g = var12;
         var7.a(var51, super.b, super.f, super.g);
         if (!super.c) {
            var7.a(super.a);
            if (super.d) {
               this.b(super.a, var5, var2);
               var7.a(super.a, super.b, super.f, super.g);
            }

            double var53 = this.a(var13);
            double var55 = super.b * var53;
            double var58 = super.a + var55;
            boolean var48 = var4 ? var58 >= var2 : var58 <= var2;
            var8 = this.a(var55, var4, var48);
            double var24 = super.a + var8;
            if (var4 ? var24 >= var2 : var24 <= var2) {
               var8 = var2 - super.a;
            }

            var7.e(var8);
         }
      } while (!super.c);

      double var45 = super.a;
      var1.c = var45;
      var1.b(var5);
      this.l();
   }
}
