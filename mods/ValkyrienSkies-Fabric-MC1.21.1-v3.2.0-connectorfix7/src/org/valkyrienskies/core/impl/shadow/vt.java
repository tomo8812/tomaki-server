package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public final class vt extends vr {
   private static final String m = "Adams-Moulton";

   private vt(int var1, double var2, double var4, double var6, double var8) {
      super("Adams-Moulton", var1, var1 + 1, var2, var4, var6, var8);
   }

   private vt(int var1, double var2, double var4, double[] var6, double[] var7) {
      super("Adams-Moulton", var1, var1 + 1, var2, var4, var6, var7);
   }

   @Override
   public final void a(uF var1, double var2) {
      this.b(var1, var2);
      this.a(var1);
      boolean var4 = var2 > var1.c;
      double[] var5;
      double[] var6;
      double[] var7 = new double[(var6 = (double[])(var5 = var1.g()).clone()).length];
      double[] var8 = new double[var6.length];
      double[] var9 = new double[var6.length];
      sy var10 = null;
      wz var11;
      (var11 = new wz()).a(var6, var4, var1.b, var1.d());
      this.a(var1.c, var5, var2);
      this.b(var1.c, var6, var2);
      var11.a(super.a, super.b, super.f, super.g);
      var11.a(super.a);
      double var12 = super.b;
      var11.e(var12);
      super.c = false;

      do {
         double var14 = 10.0;

         while (var14 >= 1.0) {
            super.b = var12;
            double var16 = super.a + super.b;
            var11.d(var16);
            uF var18;
            (var18 = this.j()).b.b(var11.k(), var8);
            int var20 = 0;
            uE[] var21;
            int var22 = (var21 = var18.d()).length;

            for (int var23 = 0; var23 < var22; var23++) {
               uE var24;
               (var24 = var21[var23]).b(var11.a(var20), var8);
               var20++;
            }

            this.a(var16, var8, var7);

            for (int var36 = 0; var36 < var5.length; var36++) {
               var9[var36] = super.b * var7[var36];
            }

            var10 = this.a(super.g);
            this.a(super.f, var9, var10);
            if ((var14 = var10.b(new vt.a(var6, var9, var8))) >= 1.0) {
               double var37 = this.a(var14);
               var12 = this.a(super.b * var37, var4, false);
               var11.e(var12);
            }
         }

         double var33 = super.a + super.b;
         this.a(var33, var8, var7);
         double[] var34 = new double[var5.length];

         for (int var19 = 0; var19 < var5.length; var19++) {
            var34[var19] = super.b * var7[var19];
         }

         this.a(var9, var34, var10);
         System.arraycopy(var8, 0, var6, 0, var6.length);
         var11.a(var33, super.b, var34, var10);
         var11.a(super.a);
         var11.c();
         var11.a(var33);
         super.a = this.a(var11, var6, var7, var2);
         super.f = var34;
         super.g = var10;
         if (!super.c) {
            var11.a(super.a);
            if (super.d) {
               this.b(super.a, var6, var2);
               var11.a(super.a, super.b, super.f, super.g);
            }

            double var35 = this.a(var14);
            double var38 = super.b * var35;
            double var39 = super.a + var38;
            boolean var32 = var4 ? var39 >= var2 : var39 <= var2;
            var12 = this.a(var38, var4, var32);
            double var26 = super.a + var12;
            if (var4 ? var26 >= var2 : var26 <= var2) {
               var12 = var2 - super.a;
            }

            var11.e(var12);
         }
      } while (!super.c);

      double var30 = super.a;
      var1.c = var30;
      var1.b(var6);
      this.l();
   }

   class a implements tt {
      private final double[] a;
      private final double[] b;
      private final double[] c;
      private final double[] d;

      a(double[] var2, double[] var3, double[] var4) {
         this.a = var2;
         this.b = var3;
         this.d = var4;
         this.c = (double[])var4.clone();
      }

      @Override
      public final void a(int var1, int var2, int var3) {
         Arrays.fill(this.d, 0.0);
      }

      @Override
      public final void a(int var1, int var2, double var3) {
         if ((var1 & 1) == 0) {
            this.d[var2] = this.d[var2] - var3;
         } else {
            this.d[var2] = this.d[var2] + var3;
         }
      }

      @Override
      public final double a() {
         double var1 = 0.0;

         for (int var3 = 0; var3 < this.d.length; var3++) {
            this.d[var3] = this.d[var3] + (this.a[var3] + this.b[var3]);
            if (var3 < vt.super.l) {
               double var4 = BY.e(BY.w(this.a[var3]), BY.w(this.d[var3]));
               double var6 = vt.super.j == null ? vt.super.h + vt.super.i * var4 : vt.super.j[var3] + vt.super.k[var3] * var4;
               double var8 = (this.d[var3] - this.c[var3]) / var6;
               var1 += var8 * var8;
            }
         }

         return BY.a(var1 / vt.super.l);
      }
   }
}
