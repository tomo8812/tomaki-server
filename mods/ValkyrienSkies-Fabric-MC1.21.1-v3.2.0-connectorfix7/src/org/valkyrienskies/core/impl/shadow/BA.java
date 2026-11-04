package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class BA implements Serializable, BB {
   private static final long a = -3004689053607543335L;
   private double b = 0.0;
   private double c = 0.0;
   private double d = 0.0;
   private double e = 0.0;
   private double f = 0.0;
   private long g = 0L;
   private double h = 0.0;
   private double i = 0.0;
   private final boolean j = true;

   public BA() {
      this(true);
   }

   private BA(boolean var1) {
   }

   public final void a(double var1, double var3) {
      if (this.g == 0L) {
         this.h = var1;
         this.i = var3;
      } else if (this.j) {
         double var5 = 1.0 + this.g;
         double var7 = this.g / (1.0 + this.g);
         double var9 = var1 - this.h;
         double var11 = var3 - this.i;
         this.c += var9 * var9 * var7;
         this.e += var11 * var11 * var7;
         this.f += var9 * var11 * var7;
         this.h += var9 / var5;
         this.i += var11 / var5;
      }

      if (!this.j) {
         this.c += var1 * var1;
         this.e += var3 * var3;
         this.f += var1 * var3;
      }

      this.b += var1;
      this.d += var3;
      this.g++;
   }

   private void a(BA var1) {
      if (this.g == 0L) {
         this.h = var1.h;
         this.i = var1.i;
         this.c = var1.c;
         this.e = var1.e;
         this.f = var1.f;
      } else if (this.j) {
         double var2 = (double)var1.g / (var1.g + this.g);
         double var4 = (double)(this.g * var1.g) / (var1.g + this.g);
         double var6 = var1.h - this.h;
         double var8 = var1.i - this.i;
         this.c = this.c + (var1.c + var6 * var6 * var4);
         this.e = this.e + (var1.e + var8 * var8 * var4);
         this.f = this.f + (var1.f + var6 * var8 * var4);
         this.h += var6 * var2;
         this.i += var8 * var2;
      } else {
         this.c = this.c + var1.c;
         this.e = this.e + var1.e;
         this.f = this.f + var1.f;
      }

      this.b = this.b + var1.b;
      this.d = this.d + var1.d;
      this.g = this.g + var1.g;
   }

   private void b(double var1, double var3) {
      if (this.g > 0L) {
         if (this.j) {
            double var5 = this.g - 1.0;
            double var7 = this.g / (this.g - 1.0);
            double var9 = var1 - this.h;
            double var11 = var3 - this.i;
            this.c -= var9 * var9 * var7;
            this.e -= var11 * var11 * var7;
            this.f -= var9 * var11 * var7;
            this.h -= var9 / var5;
            this.i -= var11 / var5;
         } else {
            double var13 = this.g - 1.0;
            this.c -= var1 * var1;
            this.e -= var3 * var3;
            this.f -= var1 * var3;
            this.h -= var1 / var13;
            this.i -= var3 / var13;
         }

         this.b -= var1;
         this.d -= var3;
         this.g--;
      }
   }

   private void a(double[][] var1) {
      for (int var2 = 0; var2 < var1.length; var2++) {
         if (var1[var2].length < 2) {
            throw new Bw(px.INVALID_REGRESSION_OBSERVATION, var1[var2].length, 2);
         }

         this.a(var1[var2][0], var1[var2][1]);
      }
   }

   @Override
   public final void a(double[] var1, double var2) {
      if (var1 != null && var1.length != 0) {
         this.a(var1[0], var2);
      } else {
         throw new Bw(px.INVALID_REGRESSION_OBSERVATION, var1 != null ? var1.length : 0, 1);
      }
   }

   @Override
   public final void a(double[][] var1, double[] var2) {
      if (var1 != null && var2 != null && var1.length == var2.length) {
         boolean var3 = true;

         for (int var4 = 0; var4 < var1.length; var4++) {
            if (var1[var4] == null || var1[var4].length == 0) {
               var3 = false;
            }
         }

         if (!var3) {
            throw new Bw(px.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS, 0, 1);
         }

         for (int var5 = 0; var5 < var1.length; var5++) {
            this.a(var1[var5][0], var2[var5]);
         }
      } else {
         throw new Bw(px.DIMENSIONS_MISMATCH_SIMPLE, var1 == null ? 0 : var1.length, var2 == null ? 0 : var2.length);
      }
   }

   private void b(double[][] var1) {
      for (int var2 = 0; var2 < var1.length && this.g > 0L; var2++) {
         double var10001 = var1[var2][0];
         double var6 = var1[var2][1];
         double var4 = var10001;
         BA var3 = this;
         if (this.g > 0L) {
            if (var3.j) {
               double var8 = var3.g - 1.0;
               double var10 = var3.g / (var3.g - 1.0);
               double var12 = var4 - var3.h;
               double var14 = var6 - var3.i;
               var3.c -= var12 * var12 * var10;
               var3.e -= var14 * var14 * var10;
               var3.f -= var12 * var14 * var10;
               var3.h -= var12 / var8;
               var3.i -= var14 / var8;
            } else {
               double var16 = var3.g - 1.0;
               var3.c -= var4 * var4;
               var3.e -= var6 * var6;
               var3.f -= var4 * var6;
               var3.h -= var4 / var16;
               var3.i -= var6 / var16;
            }

            var3.b -= var4;
            var3.d -= var6;
            var3.g--;
         }
      }
   }

   @Override
   public final void c() {
      this.b = 0.0;
      this.c = 0.0;
      this.d = 0.0;
      this.e = 0.0;
      this.f = 0.0;
      this.g = 0L;
   }

   @Override
   public final long b() {
      return this.g;
   }

   private double a(double var1) {
      double var3 = this.g();
      return this.j ? this.c(var3) + var3 * var1 : var3 * var1;
   }

   private double f() {
      return this.j ? this.c(this.g()) : 0.0;
   }

   @Override
   public final boolean a() {
      return this.j;
   }

   private double g() {
      if (this.g < 2L) {
         return Double.NaN;
      } else {
         return BY.w(this.c) < 5.0E-323 ? Double.NaN : this.f / this.c;
      }
   }

   private double h() {
      return BY.e(0.0, this.e - this.f * this.f / this.c);
   }

   private double i() {
      return this.g < 2L ? Double.NaN : this.e;
   }

   private double j() {
      return this.g < 2L ? Double.NaN : this.c;
   }

   private double k() {
      return this.f;
   }

   private double l() {
      double var2 = this.g();
      BA var1 = this;
      return var2 * var2 * var1.c;
   }

   private double m() {
      if (this.g < 3L) {
         return Double.NaN;
      } else {
         return this.j ? this.h() / (this.g - 2L) : this.h() / (this.g - 1L);
      }
   }

   public final double e() {
      double var1 = this.g();
      double var3 = BY.a(this.n());
      if (var1 < 0.0) {
         var3 = -var3;
      }

      return var3;
   }

   private double n() {
      double var1;
      return ((var1 = this.i()) - this.h()) / var1;
   }

   private double o() {
      return !this.j ? Double.NaN : BY.a(this.m() * (1.0 / this.g + this.h * this.h / this.c));
   }

   private double p() {
      return BY.a(this.m() / this.c);
   }

   private double q() {
      double var2 = 0.05;
      BA var1 = this;
      if (this.g < 3L) {
         return Double.NaN;
      }

      oK var4 = new oK(var1.g - 2L);
      double var10000 = var1.p();
      double var10002 = 0.05 / 2.0;
      return var10000 * var4.a(0.975);
   }

   private double b(double var1) {
      if (this.g < 3L) {
         return Double.NaN;
      }

      oK var3 = new oK(this.g - 2L);
      double var10000 = this.p();
      double var10002 = 0.05 / 2.0;
      return var10000 * var3.a(0.975);
   }

   private double r() {
      if (this.g < 3L) {
         return Double.NaN;
      }

      oK var1 = new oK(this.g - 2L);
      return 2.0 * (1.0 - var1.e(BY.w(this.g()) / this.p()));
   }

   private double c(double var1) {
      return this.j ? (this.d - var1 * this.b) / this.g : 0.0;
   }

   private double d(double var1) {
      return var1 * var1 * this.c;
   }

   @Override
   public final Bz d() {
      if (this.j) {
         if (this.g < 3L) {
            throw new pf(px.NOT_ENOUGH_DATA_REGRESSION);
         } else if (BY.w(this.c) > Cq.b) {
            double[] var10000 = new double[2];
            BA var8 = this;
            var10000[0] = this.j ? var8.c(var8.g()) : 0.0;
            var10000[1] = this.g();
            double[] var9 = var10000;
            double var12 = this.m();
            double var4 = this.e + this.d * this.d / this.g;
            double[] var13 = new double[]{var12 * (this.h * this.h / this.c + 1.0 / this.g), -this.h * var12 / this.c, var12 / this.c};
            return new Bz(var9, new double[][]{var13}, true, this.g, 2, this.d, var4, this.h(), true, false);
         } else {
            double[] var7 = new double[]{this.d / this.g, Double.NaN};
            double[] var11 = new double[]{this.i / (this.g - 1.0), Double.NaN, Double.NaN};
            return new Bz(var7, new double[][]{var11}, true, this.g, 1, this.d, this.e, this.h(), true, false);
         }
      } else if (this.g < 2L) {
         throw new pf(px.NOT_ENOUGH_DATA_REGRESSION);
      } else if (!Double.isNaN(this.c)) {
         double[] var6 = new double[]{this.m() / this.c};
         double[] var10 = new double[]{this.f / this.c};
         return new Bz(var10, new double[][]{var6}, true, this.g, 1, this.d, this.e, this.h(), false, false);
      } else {
         double[] var1 = new double[]{Double.NaN};
         double[] var2 = new double[]{Double.NaN};
         return new Bz(var2, new double[][]{var1}, true, this.g, 1, Double.NaN, Double.NaN, Double.NaN, false, false);
      }
   }

   @Override
   public final Bz a(int[] var1) {
      if (var1 != null && var1.length != 0) {
         if (var1.length > 2 || var1.length > 1 && !this.j) {
            throw new Bw(px.ARRAY_SIZE_EXCEEDS_MAX_VARIABLES, var1.length > 1 && !this.j ? 1 : 2);
         }

         if (this.j) {
            if (var1.length == 2) {
               if (var1[0] == 1) {
                  throw new Bw(px.NOT_INCREASING_SEQUENCE);
               } else if (var1[0] != 0) {
                  throw new po(var1[0], 0, 1);
               } else if (var1[1] != 1) {
                  throw new po(var1[0], 0, 1);
               } else {
                  return this.d();
               }
            } else {
               if (var1[0] != 1 && var1[0] != 0) {
                  throw new po(var1[0], 0, 1);
               }

               double var2 = this.d * this.d / this.g;
               double var4 = this.e + var2;
               if (var1[0] == 0) {
                  double[] var18 = new double[]{this.e / ((this.g - 1L) * this.g)};
                  double[] var7 = new double[]{this.i};
                  return new Bz(var7, new double[][]{var18}, true, this.g, 1, this.d, var4 + var2, this.e, true, false);
               }

               if (var1[0] == 1) {
                  double var6 = this.c + this.b * this.b / this.g;
                  double var8 = this.f + this.b * this.d / this.g;
                  double var10;
                  double var12 = (var10 = BY.e(0.0, var4 - var8 * var8 / var6)) / (this.g - 1L);
                  if (!Double.isNaN(var6)) {
                     double[] var15 = new double[]{var12 / var6};
                     double[] var17 = new double[]{var8 / var6};
                     return new Bz(var17, new double[][]{var15}, true, this.g, 1, this.d, var4, var10, false, false);
                  } else {
                     double[] var14 = new double[]{Double.NaN};
                     double[] var16 = new double[]{Double.NaN};
                     return new Bz(var16, new double[][]{var14}, true, this.g, 1, Double.NaN, Double.NaN, Double.NaN, false, false);
                  }
               } else {
                  return null;
               }
            }
         } else if (var1[0] != 0) {
            throw new po(var1[0], 0, 0);
         } else {
            return this.d();
         }
      } else {
         throw new oV(px.ARRAY_ZERO_LENGTH_OR_NULL_NOT_ALLOWED);
      }
   }
}
