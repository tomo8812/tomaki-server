package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;

public final class Bz implements Serializable {
   private static final int a = 0;
   private static final int b = 1;
   private static final int c = 2;
   private static final int d = 3;
   private static final int e = 4;
   private static final long f = 1L;
   private final double[] g;
   private final double[][] h;
   private final boolean i;
   private final int j;
   private final long k;
   private final boolean l;
   private final double[] m;

   private Bz() {
      this.g = null;
      this.h = null;
      this.j = -1;
      this.k = -1L;
      this.l = false;
      this.i = false;
      this.m = null;
   }

   public Bz(double[] var1, double[][] var2, boolean var3, long var4, int var6, double var7, double var9, double var11, boolean var13, boolean var14) {
      this.g = var1;
      this.h = var2;
      this.i = true;
      this.k = var4;
      this.j = var6;
      this.l = var13;
      this.m = new double[5];
      Arrays.fill(this.m, Double.NaN);
      if (var6 > 0) {
         this.m[1] = var13 ? var9 - var7 * var7 / var4 : var9;
      }

      this.m[0] = var11;
      this.m[3] = this.m[0] / (var4 - var6);
      this.m[2] = 1.0 - this.m[0] / this.m[1];
      if (!var13) {
         this.m[4] = 1.0 - (1.0 - this.m[2]) * ((double)var4 / (var4 - var6));
      } else {
         this.m[4] = 1.0 - var11 * (var4 - 1.0) / (this.m[1] * (var4 - var6));
      }
   }

   private double a(int var1) {
      if (this.g == null) {
         return Double.NaN;
      } else if (var1 >= 0 && var1 < this.g.length) {
         return this.g[var1];
      } else {
         throw new po(var1, 0, this.g.length - 1);
      }
   }

   private double[] a() {
      if (this.g == null) {
         return null;
      }

      double[] var1 = this.g;
      return Ch.a(this.g, var1.length);
   }

   private double b(int var1) {
      if (this.g == null) {
         return Double.NaN;
      } else if (var1 >= 0 && var1 < this.g.length) {
         double var2;
         return !Double.isNaN(var2 = this.b(var1, var1)) && var2 > Double.MIN_VALUE ? BY.a(var2) : Double.NaN;
      } else {
         throw new po(var1, 0, this.g.length - 1);
      }
   }

   private double[] b() {
      if (this.g == null) {
         return null;
      }

      double[] var1 = new double[this.g.length];

      for (int var2 = 0; var2 < this.g.length; var2++) {
         double var3;
         if (!Double.isNaN(var3 = this.b(var2, var2)) && var3 > Double.MIN_VALUE) {
            var1[var2] = BY.a(var3);
         } else {
            var1[var2] = Double.NaN;
         }
      }

      return var1;
   }

   private double a(int var1, int var2) {
      if (this.g == null) {
         return Double.NaN;
      } else if (var1 < 0 || var1 >= this.g.length) {
         throw new po(var1, 0, this.g.length - 1);
      } else if (var2 >= 0 && var2 < this.g.length) {
         return this.b(var1, var2);
      } else {
         throw new po(var2, 0, this.g.length - 1);
      }
   }

   private int c() {
      return this.g == null ? -1 : this.g.length;
   }

   private long d() {
      return this.k;
   }

   private double e() {
      return this.m[1];
   }

   private double f() {
      return this.m[1] - this.m[0];
   }

   private double g() {
      return this.m[0];
   }

   private double h() {
      return this.m[3];
   }

   private double i() {
      return this.m[2];
   }

   private double j() {
      return this.m[4];
   }

   private boolean k() {
      return this.l;
   }

   private double b(int var1, int var2) {
      if (this.i) {
         if (this.h.length > 1) {
            if (var1 == var2) {
               return this.h[var1][var1];
            } else {
               return var1 >= this.h[var2].length ? this.h[var1][var2] : this.h[var2][var1];
            }
         } else {
            return var1 > var2 ? this.h[0][(var1 + 1) * var1 / 2 + var2] : this.h[0][(var2 + 1) * var2 / 2 + var1];
         }
      } else {
         return this.h[var1][var2];
      }
   }
}
