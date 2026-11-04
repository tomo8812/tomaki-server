package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class rp implements Serializable {
   private static final long a = 20130206L;
   private final rs b;
   private final double c;
   private final double d;
   private final double e;
   private double[][] f;
   private double[][] g;
   private double[][] h;
   private double[][] i;

   public rp(rs var1) {
      this.b = var1;
      this.c = var1.e();
      rs var2 = var1;
      this.d = BY.b(var1.j, var2.i);
      this.e = BY.s(var1.k / this.c);
   }

   private rp(double var1, double var3, double var5) {
      double var7 = BY.o(var3);
      double var9 = BY.n(var3);
      double var11 = BY.o(var5);
      double var13 = BY.n(var5);
      this.c = var1;
      this.d = var3;
      this.e = var5;
      this.b = new rs(var1 * var7 * var13, var1 * var9 * var13, var1 * var11);
   }

   private rs a() {
      return this.b;
   }

   private double b() {
      return this.c;
   }

   private double c() {
      return this.d;
   }

   private double d() {
      return this.e;
   }

   private double[] a(double[] var1) {
      this.e();
      return new double[]{
         var1[0] * this.f[0][0] + var1[1] * this.f[1][0] + var1[2] * this.f[2][0],
         var1[0] * this.f[0][1] + var1[1] * this.f[1][1] + var1[2] * this.f[2][1],
         var1[0] * this.f[0][2] + var1[2] * this.f[2][2]
      };
   }

   private double[][] a(double[][] var1, double[] var2) {
      this.e();
      rp var3 = this;
      if (this.g == null) {
         rs var4 = var3.b;
         double var6 = var3.b.i;
         var4 = var3.b;
         double var8 = var3.b.j;
         var4 = var3.b;
         double var10 = var3.b.k;
         double var12 = var6 * var6;
         double var14 = var8 * var8;
         double var16 = var10 * var10;
         double var18;
         double var20 = BY.a(var18 = var12 + var14);
         double var22 = var18 + var16;
         double var24 = var6 / var3.c;
         double var26 = var8 / var3.c;
         double var28 = var10 / var3.c;
         double var30 = var6 / var18;
         double var32 = var8 / var18;
         double var34 = var24 / var22;
         double var36 = var26 / var22;
         double var38 = var28 / var22;
         var3.g = new double[3][3];
         var3.g[0][0] = var8 * var36 + var10 * var38;
         var3.g[1][0] = -var6 * var36;
         var3.g[2][0] = -var10 * var34;
         var3.g[1][1] = var6 * var34 + var10 * var38;
         var3.g[2][1] = -var8 * var38;
         var3.g[2][2] = var6 * var34 + var8 * var36;
         var3.g[0][1] = var3.g[1][0];
         var3.g[0][2] = var3.g[2][0];
         var3.g[1][2] = var3.g[2][1];
         var3.h = new double[2][2];
         var3.h[0][0] = var30 * 2.0 * var32;
         var3.h[1][0] = var32 * var32 - var30 * var30;
         var3.h[1][1] = var30 * -2.0 * var32;
         var3.h[0][1] = var3.h[1][0];
         double var40 = var20 * var22;
         double var42 = var20 * var40;
         double var44;
         double var46 = (var44 = var40 * var22) * var18;
         double var48 = var18 * 3.0 + var16;
         var3.i = new double[3][3];
         var3.i[0][0] = var10 * (var42 - var12 * var48) / var46;
         var3.i[1][0] = -var6 * var8 * var10 * var48 / var46;
         var3.i[2][0] = var6 * (var18 - var16) / var44;
         var3.i[1][1] = var10 * (var42 - var14 * var48) / var46;
         var3.i[2][1] = var8 * (var18 - var16) / var44;
         var3.i[2][2] = var20 * 2.0 * var38 / var3.c;
         var3.i[0][1] = var3.i[1][0];
         var3.i[0][2] = var3.i[2][0];
         var3.i[1][2] = var3.i[2][1];
      }

      double[][] var50 = new double[3][3];
      double[][] var53 = new double[3][3];
      var50[0][0] = var1[0][0] * this.f[0][0] + var1[1][0] * this.f[1][0] + var1[2][0] * this.f[2][0];
      var50[0][1] = var1[0][0] * this.f[0][1] + var1[1][0] * this.f[1][1] + var1[2][0] * this.f[2][1];
      var50[0][2] = var1[0][0] * this.f[0][2] + var1[2][0] * this.f[2][2];
      var50[1][0] = var1[1][0] * this.f[0][0] + var1[1][1] * this.f[1][0] + var1[2][1] * this.f[2][0];
      var50[1][1] = var1[1][0] * this.f[0][1] + var1[1][1] * this.f[1][1] + var1[2][1] * this.f[2][1];
      var50[2][0] = var1[2][0] * this.f[0][0] + var1[2][1] * this.f[1][0] + var1[2][2] * this.f[2][0];
      var50[2][1] = var1[2][0] * this.f[0][1] + var1[2][1] * this.f[1][1] + var1[2][2] * this.f[2][1];
      var50[2][2] = var1[2][0] * this.f[0][2] + var1[2][2] * this.f[2][2];
      var53[0][0] = this.f[0][0] * var50[0][0] + this.f[1][0] * var50[1][0] + this.f[2][0] * var50[2][0];
      var53[1][0] = this.f[0][1] * var50[0][0] + this.f[1][1] * var50[1][0] + this.f[2][1] * var50[2][0];
      var53[2][0] = this.f[0][2] * var50[0][0] + this.f[2][2] * var50[2][0];
      var53[1][1] = this.f[0][1] * var50[0][1] + this.f[1][1] * var50[1][1] + this.f[2][1] * var50[2][1];
      var53[2][1] = this.f[0][2] * var50[0][1] + this.f[2][2] * var50[2][1];
      var53[2][2] = this.f[0][2] * var50[0][2] + this.f[2][2] * var50[2][2];
      var53[0][0] = var53[0][0] + (var2[0] * this.g[0][0] + var2[1] * this.h[0][0] + var2[2] * this.i[0][0]);
      var53[1][0] = var53[1][0] + (var2[0] * this.g[1][0] + var2[1] * this.h[1][0] + var2[2] * this.i[1][0]);
      var53[2][0] = var53[2][0] + (var2[0] * this.g[2][0] + var2[2] * this.i[2][0]);
      var53[1][1] = var53[1][1] + (var2[0] * this.g[1][1] + var2[1] * this.h[1][1] + var2[2] * this.i[1][1]);
      var53[2][1] = var53[2][1] + (var2[0] * this.g[2][1] + var2[2] * this.i[2][1]);
      var53[2][2] = var53[2][2] + (var2[0] * this.g[2][2] + var2[2] * this.i[2][2]);
      var53[0][1] = var53[1][0];
      var53[0][2] = var53[2][0];
      var53[1][2] = var53[2][1];
      return var53;
   }

   private void e() {
      if (this.f == null) {
         rs var5 = this.b;
         double var1 = this.b.i;
         var5 = this.b;
         double var3 = this.b.j;
         var5 = this.b;
         double var15 = this.b.k;
         double var7;
         double var9 = BY.a(var7 = var1 * var1 + var3 * var3);
         double var11 = var7 + var15 * var15;
         this.f = new double[3][3];
         this.f[0][0] = var1 / this.c;
         this.f[0][1] = var3 / this.c;
         this.f[0][2] = var15 / this.c;
         this.f[1][0] = -var3 / var7;
         this.f[1][1] = var1 / var7;
         this.f[2][0] = var1 * var15 / (var9 * var11);
         this.f[2][1] = var3 * var15 / (var9 * var11);
         this.f[2][2] = -var9 / var11;
      }
   }

   private void f() {
      if (this.g == null) {
         rs var5 = this.b;
         double var1 = this.b.i;
         var5 = this.b;
         double var3 = this.b.j;
         var5 = this.b;
         double var47 = this.b.k;
         double var7 = var1 * var1;
         double var9 = var3 * var3;
         double var11 = var47 * var47;
         double var13;
         double var15 = BY.a(var13 = var7 + var9);
         double var17 = var13 + var11;
         double var19 = var1 / this.c;
         double var21 = var3 / this.c;
         double var23 = var47 / this.c;
         double var25 = var1 / var13;
         double var27 = var3 / var13;
         double var29 = var19 / var17;
         double var31 = var21 / var17;
         double var33 = var23 / var17;
         this.g = new double[3][3];
         this.g[0][0] = var3 * var31 + var47 * var33;
         this.g[1][0] = -var1 * var31;
         this.g[2][0] = -var47 * var29;
         this.g[1][1] = var1 * var29 + var47 * var33;
         this.g[2][1] = -var3 * var33;
         this.g[2][2] = var1 * var29 + var3 * var31;
         this.g[0][1] = this.g[1][0];
         this.g[0][2] = this.g[2][0];
         this.g[1][2] = this.g[2][1];
         this.h = new double[2][2];
         this.h[0][0] = var25 * 2.0 * var27;
         this.h[1][0] = var27 * var27 - var25 * var25;
         this.h[1][1] = var25 * -2.0 * var27;
         this.h[0][1] = this.h[1][0];
         double var35 = var15 * var17;
         double var37 = var15 * var35;
         double var39;
         double var41 = (var39 = var35 * var17) * var13;
         double var43 = var13 * 3.0 + var11;
         this.i = new double[3][3];
         this.i[0][0] = var47 * (var37 - var7 * var43) / var41;
         this.i[1][0] = -var1 * var3 * var47 * var43 / var41;
         this.i[2][0] = var1 * (var13 - var11) / var39;
         this.i[1][1] = var47 * (var37 - var9 * var43) / var41;
         this.i[2][1] = var3 * (var13 - var11) / var39;
         this.i[2][2] = var15 * 2.0 * var33 / this.c;
         this.i[0][1] = this.i[1][0];
         this.i[0][2] = this.i[2][0];
         this.i[1][2] = this.i[2][1];
      }
   }

   private rp.a g() {
      rs var1 = this.b;
      var1 = this.b;
      var1 = this.b;
      return new rp.a(this.b.i, this.b.j, this.b.k);
   }

   static class a implements Serializable {
      private static final long a = 20130206L;
      private final double b;
      private final double c;
      private final double d;

      a(double var1, double var3, double var5) {
         this.b = var1;
         this.c = var3;
         this.d = var5;
      }

      private rp a() {
         return new rp(new rs(this.b, this.c, this.d));
      }
   }
}
