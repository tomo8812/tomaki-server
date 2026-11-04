package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class nU implements Serializable {
   private static nU a;
   private static nU b;
   private static nU c;
   private static nU d;
   private static nU e;
   private static final long f = 20092012L;
   private final double g;
   private final double h;
   private final double i;
   private final double j;

   private nU(double var1, double var3, double var5, double var7) {
      this.g = var1;
      this.h = var3;
      this.i = var5;
      this.j = var7;
   }

   private nU(double var1, double[] var3) {
      if (var3.length != 3) {
         throw new oS(var3.length, 3);
      }

      this.g = 0.0;
      this.h = var3[0];
      this.i = var3[1];
      this.j = var3[2];
   }

   private nU(double[] var1) {
      this(0.0, var1);
   }

   private nU a() {
      return new nU(this.g, -this.h, -this.i, -this.j);
   }

   private static nU a(nU var0, nU var1) {
      double var2 = var0.g;
      double var4 = var0.h;
      double var6 = var0.i;
      double var8 = var0.j;
      double var10 = var1.g;
      double var12 = var1.h;
      double var14 = var1.i;
      double var16 = var1.j;
      double var18 = var2 * var10 - var4 * var12 - var6 * var14 - var8 * var16;
      double var20 = var2 * var12 + var4 * var10 + var6 * var16 - var8 * var14;
      double var22 = var2 * var14 - var4 * var16 + var6 * var10 + var8 * var12;
      double var24 = var2 * var16 + var4 * var14 - var6 * var12 + var8 * var10;
      return new nU(var18, var20, var22, var24);
   }

   private nU a(nU var1) {
      nU var2 = var1;
      var1 = this;
      double var4 = this.g;
      double var6 = var1.h;
      double var8 = var1.i;
      double var10 = var1.j;
      double var12 = var2.g;
      double var14 = var2.h;
      double var16 = var2.i;
      double var18 = var2.j;
      double var20 = var4 * var12 - var6 * var14 - var8 * var16 - var10 * var18;
      double var22 = var4 * var14 + var6 * var12 + var8 * var18 - var10 * var16;
      double var24 = var4 * var16 - var6 * var18 + var8 * var12 + var10 * var14;
      double var26 = var4 * var18 + var6 * var16 - var8 * var14 + var10 * var12;
      return new nU(var20, var22, var24, var26);
   }

   private static nU b(nU var0, nU var1) {
      return new nU(var0.g + var1.g, var0.h + var1.h, var0.i + var1.i, var0.j + var1.j);
   }

   private nU b(nU var1) {
      nU var2 = var1;
      var1 = this;
      return new nU(var1.g + var2.g, var1.h + var2.h, var1.i + var2.i, var1.j + var2.j);
   }

   private static nU c(nU var0, nU var1) {
      return new nU(var0.g - var1.g, var0.h - var1.h, var0.i - var1.i, var0.j - var1.j);
   }

   private nU c(nU var1) {
      nU var2 = var1;
      var1 = this;
      return new nU(var1.g - var2.g, var1.h - var2.h, var1.i - var2.i, var1.j - var2.j);
   }

   private static double d(nU var0, nU var1) {
      return var0.g * var1.g + var0.h * var1.h + var0.i * var1.i + var0.j * var1.j;
   }

   private double d(nU var1) {
      nU var2 = var1;
      var1 = this;
      return this.g * var2.g + var1.h * var2.h + var1.i * var2.i + var1.j * var2.j;
   }

   private double b() {
      return BY.a(this.g * this.g + this.h * this.h + this.i * this.i + this.j * this.j);
   }

   private nU c() {
      double var1;
      if ((var1 = this.b()) < Cq.b) {
         throw new pr(px.NORM, var1);
      } else {
         return new nU(this.g / var1, this.h / var1, this.i / var1, this.j / var1);
      }
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof nU) {
         other = (nU)other;
         return this.g == ((nU)other).g && this.h == ((nU)other).h && this.i == ((nU)other).i && this.j == ((nU)other).j;
      } else {
         return false;
      }
   }

   @Override
   public final int hashCode() {
      int var1 = 17;
      double[] var2;
      int var10000 = (var2 = new double[]{this.g, this.h, this.i, this.j}).length;

      for (int var3 = 0; var3 < 4; var3++) {
         double var4;
         int var6 = Ci.a(var4 = var2[var3]);
         var1 = var1 * 31 + var6;
      }

      return var1;
   }

   private boolean a(nU var1, double var2) {
      return Cq.b(this.g, var1.g, var2) && Cq.b(this.h, var1.h, var2) && Cq.b(this.i, var1.i, var2) && Cq.b(this.j, var1.j, var2);
   }

   private boolean a(double var1) {
      return Cq.b(this.b(), 1.0, var1);
   }

   private boolean b(double var1) {
      return BY.w(this.g) <= var1;
   }

   private nU d() {
      if (this.g < 0.0) {
         nU var1 = this.c();
         return new nU(-var1.g, -var1.h, -var1.i, -var1.j);
      } else {
         return this.c();
      }
   }

   private nU e() {
      double var1;
      if ((var1 = this.g * this.g + this.h * this.h + this.i * this.i + this.j * this.j) < Cq.b) {
         throw new pr(px.NORM, var1);
      } else {
         return new nU(this.g / var1, -this.h / var1, -this.i / var1, -this.j / var1);
      }
   }

   private double f() {
      return this.g;
   }

   private double g() {
      return this.h;
   }

   private double h() {
      return this.i;
   }

   private double i() {
      return this.j;
   }

   private double j() {
      return this.g;
   }

   private double[] k() {
      return new double[]{this.h, this.i, this.j};
   }

   private nU c(double var1) {
      return new nU(var1 * this.g, var1 * this.h, var1 * this.i, var1 * this.j);
   }

   @Override
   public final String toString() {
      StringBuilder var1;
      (var1 = new StringBuilder()).append("[").append(this.g).append(" ").append(this.h).append(" ").append(this.i).append(" ").append(this.j).append("]");
      return var1.toString();
   }

   static {
      new nU(1.0, 0.0, 0.0, 0.0);
      new nU(0.0, 0.0, 0.0, 0.0);
      new nU(0.0, 1.0, 0.0, 0.0);
      new nU(0.0, 0.0, 1.0, 0.0);
      new nU(0.0, 0.0, 0.0, 1.0);
   }
}
