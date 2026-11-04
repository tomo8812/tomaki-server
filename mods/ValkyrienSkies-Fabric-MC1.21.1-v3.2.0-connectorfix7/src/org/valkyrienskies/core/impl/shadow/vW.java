package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInput;
import java.io.ObjectOutput;

final class vW extends ws {
   private static final long n = 20110928L;
   private double[] o;
   private double[] p;
   private double[] q;
   private double[][] r;
   double[][] a;
   double[] b;
   int c;

   public vW() {
      this.o = null;
      this.p = null;
      this.q = null;
      this.r = null;
      this.c(-1);
   }

   vW(double[] var1, double[] var2, double[] var3, double[] var4, double[][] var5, boolean var6, uE var7, uE[] var8) {
      super(var1, var6, var7, var8);
      this.o = var2;
      this.p = var3;
      this.q = var4;
      this.r = var5;
      this.c(var5.length + 4);
   }

   private vW(vW var1) {
      super(var1);
      int var2 = super.e.length;
      this.o = null;
      this.p = null;
      this.q = null;
      this.r = null;
      if (var1.a == null) {
         this.a = null;
         this.c = -1;
      } else {
         this.c(var1.c);

         for (int var3 = 0; var3 < this.a.length; var3++) {
            this.a[var3] = new double[var2];
            System.arraycopy(var1.a[var3], 0, this.a[var3], 0, var2);
         }

         this.c = var1.c;
      }
   }

   private void c(int var1) {
      if (var1 < 0) {
         this.a = null;
         this.b = null;
         this.c = -1;
      } else {
         double[][] var2 = new double[var1 + 1][];
         if (this.a != null) {
            System.arraycopy(this.a, 0, var2, 0, this.a.length);

            for (int var3 = this.a.length; var3 < var2.length; var3++) {
               var2[var3] = new double[super.e.length];
            }
         } else {
            for (int var9 = 0; var9 < var2.length; var9++) {
               var2[var9] = new double[super.e.length];
            }
         }

         this.a = var2;
         if (var1 <= 4) {
            this.b = null;
         } else {
            this.b = new double[var1 - 4];

            for (int var10 = 0; var10 < this.b.length; var10++) {
               var1 = var10 + 5;
               this.b[var10] = 1.0 / (var1 * var1);
               double var5 = 0.5 * BY.a((double)(var10 + 1) / var1);

               for (int var8 = 0; var8 <= var10; var8++) {
                  this.b[var10] = this.b[var10] * (var5 / (var8 + 1));
               }
            }
         }

         this.c = 0;
      }
   }

   @Override
   public final ws a() {
      return new vW(this);
   }

   public final void a(int var1, double var2) {
      if (this.a == null || this.a.length <= var1 + 4) {
         this.c(var1 + 4);
      }

      this.c = var1 + 4;

      for (int var4 = 0; var4 < super.e.length; var4++) {
         double var5 = var2 * this.o[var4];
         double var7 = var2 * this.q[var4];
         double var9;
         double var11 = (var9 = this.p[var4] - super.e[var4]) - var7;
         double var13 = var5 - var9;
         this.a[0][var4] = super.e[var4];
         this.a[1][var4] = var9;
         this.a[2][var4] = var11;
         this.a[3][var4] = var13;
         if (var1 < 0) {
            return;
         }

         double var15 = 0.5 * (super.e[var4] + this.p[var4]) + 0.125 * (var11 + var13);
         this.a[4][var4] = 16.0 * (this.r[0][var4] - var15);
         if (var1 > 0) {
            double var17 = var9 + 0.25 * (var11 - var13);
            this.a[5][var4] = 16.0 * (this.r[1][var4] - var17);
            if (var1 > 1) {
               double var19 = var7 - var5;
               this.a[6][var4] = 16.0 * (this.r[2][var4] - var19 + this.a[4][var4]);
               if (var1 > 2) {
                  double var21 = 6.0 * (var13 - var11);
                  this.a[7][var4] = 16.0 * (this.r[3][var4] - var21 + 3.0 * this.a[5][var4]);

                  for (int var28 = 4; var28 <= var1; var28++) {
                     double var24 = 0.5 * var28 * (var28 - 1);
                     double var26 = var24 * 2.0 * (var28 - 2) * (var28 - 3);
                     this.a[var28 + 4][var4] = 16.0 * (this.r[var28][var4] + var24 * this.a[var28 + 2][var4] - var26 * this.a[var28][var4]);
                  }
               }
            }
         }
      }
   }

   public final double a(double[] var1) {
      double var2 = 0.0;
      if (this.c >= 5) {
         for (int var4 = 0; var4 < var1.length; var4++) {
            double var5 = this.a[this.c][var4] / var1[var4];
            var2 += var5 * var5;
         }

         var2 = BY.a(var2 / var1.length) * this.b[this.c - 5];
      }

      return var2;
   }

   @Override
   public final void a(double var1, double var3) {
      int var38 = super.e.length;
      double var6 = 1.0 - var1;
      double var8 = var1 - 0.5;
      double var10;
      double var12 = (var10 = var1 * var6) * var10;
      double var14 = var10 * 2.0 * (1.0 - var1 * 2.0);
      double var16 = 1.0 / super.d;
      double var18 = var1 * (2.0 - var1 * 3.0) / super.d;
      double var20 = ((var1 * 3.0 - 4.0) * var1 + 1.0) / super.d;

      for (int var4 = 0; var4 < var38; var4++) {
         double var23 = this.a[0][var4];
         double var25 = this.a[1][var4];
         double var27 = this.a[2][var4];
         double var29 = this.a[3][var4];
         super.g[var4] = var23 + var1 * (var25 + var6 * (var27 * var1 + var29 * var6));
         super.h[var4] = var16 * var25 + var18 * var27 + var20 * var29;
         if (this.c > 3) {
            double var31 = 0.0;
            double var33 = this.a[this.c][var4];

            for (int var5 = this.c - 1; var5 > 3; var5--) {
               double var36;
               var31 = (var36 = 1.0 / (var5 - 3)) * (var8 * var31 + var33);
               var33 = this.a[var5][var4] + var33 * var36 * var8;
            }

            super.g[var4] = super.g[var4] + var12 * var33;
            super.h[var4] = super.h[var4] + (var12 * var31 + var14 * var33) / super.d;
         }
      }

      if (super.d == 0.0) {
         System.arraycopy(this.r[1], 0, super.h, 0, var38);
      }
   }

   @Override
   public final void writeExternal(ObjectOutput out) {
      int var2 = super.e == null ? -1 : super.e.length;
      this.a(out);
      out.writeInt(this.c);

      for (int var3 = 0; var3 <= this.c; var3++) {
         for (int var4 = 0; var4 < var2; var4++) {
            out.writeDouble(this.a[var3][var4]);
         }
      }
   }

   @Override
   public final void readExternal(ObjectInput in) {
      double var2 = this.a(in);
      int var4 = super.e == null ? -1 : super.e.length;
      int var5 = in.readInt();
      this.c(var5);
      this.c = var5;

      for (int var7 = 0; var7 <= this.c; var7++) {
         for (int var6 = 0; var6 < var4; var6++) {
            this.a[var7][var6] = in.readDouble();
         }
      }

      this.d(var2);
   }
}
