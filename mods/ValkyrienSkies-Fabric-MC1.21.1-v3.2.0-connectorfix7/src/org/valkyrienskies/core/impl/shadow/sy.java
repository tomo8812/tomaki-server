package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public class sy extends sv implements Serializable {
   private static final long b = -1067294169172445528L;
   public double[][] a;

   public sy() {
   }

   public sy(int var1, int var2) {
      super(var1, var2);
      this.a = new double[var1][var2];
   }

   public sy(double[][] var1) {
      double[][] var2 = var1;
      this.a(var2, 0, 0);
   }

   public sy(double[][] var1, boolean var2) {
      if (var2) {
         double[][] var6 = var1;
         this.a(var6, 0, 0);
      } else {
         if (var1 == null) {
            throw new pl();
         }

         if ((var2 = var1.length) == 0) {
            throw new pf(px.AT_LEAST_ONE_ROW);
         }

         int var3;
         if ((var3 = var1[0].length) == 0) {
            throw new pf(px.AT_LEAST_ONE_COLUMN);
         }

         for (int var4 = 1; var4 < var2; var4++) {
            if (var1[var4].length != var3) {
               throw new oS(var1[var4].length, var3);
            }
         }

         this.a = var1;
      }
   }

   public sy(double[] var1) {
      int var2 = var1.length;
      this.a = new double[var2][1];

      for (int var3 = 0; var3 < var2; var3++) {
         this.a[var3][0] = var1[var3];
      }
   }

   @Override
   public final sv a(int var1, int var2) {
      return new sy(var1, var2);
   }

   @Override
   public final sv i() {
      return new sy(this.k(), false);
   }

   private sy b(sy var1) {
      td.a(this, var1);
      int var2 = this.f();
      int var3 = this.g();
      double[][] var4 = new double[var2][var3];

      for (int var5 = 0; var5 < var2; var5++) {
         double[] var6 = this.a[var5];
         double[] var7 = var1.a[var5];
         double[] var8 = var4[var5];

         for (int var9 = 0; var9 < var3; var9++) {
            var8[var9] = var6[var9] + var7[var9];
         }
      }

      return new sy(var4, false);
   }

   private sy c(sy var1) {
      td.a(this, var1);
      int var2 = this.f();
      int var3 = this.g();
      double[][] var4 = new double[var2][var3];

      for (int var5 = 0; var5 < var2; var5++) {
         double[] var6 = this.a[var5];
         double[] var7 = var1.a[var5];
         double[] var8 = var4[var5];

         for (int var9 = 0; var9 < var3; var9++) {
            var8[var9] = var6[var9] - var7[var9];
         }
      }

      return new sy(var4, false);
   }

   public final sy a(sy var1) {
      td.b(this, var1);
      int var2 = this.f();
      int var3 = var1.g();
      int var4 = this.g();
      double[][] var5 = new double[var2][var3];
      double[] var6 = new double[var4];
      double[][] var13 = var1.a;

      for (int var7 = 0; var7 < var3; var7++) {
         for (int var8 = 0; var8 < var4; var8++) {
            var6[var8] = var13[var8][var7];
         }

         for (int var14 = 0; var14 < var2; var14++) {
            double[] var9 = this.a[var14];
            double var11 = 0.0;

            for (int var10 = 0; var10 < var4; var10++) {
               var11 += var9[var10] * var6[var10];
            }

            var5[var14][var7] = var11;
         }
      }

      return new sy(var5, false);
   }

   @Override
   public final double[][] a() {
      return this.k();
   }

   public final double[][] j() {
      return this.a;
   }

   @Override
   public final void a(double[][] var1, int var2, int var3) {
      if (this.a == null) {
         if (var2 > 0) {
            throw new oX(px.FIRST_ROWS_NOT_INITIALIZED_YET, var2);
         }

         if (var3 > 0) {
            throw new oX(px.FIRST_COLUMNS_NOT_INITIALIZED_YET, var3);
         }

         Ci.a(var1);
         int var4;
         if ((var4 = var1.length) == 0) {
            throw new pf(px.AT_LEAST_ONE_ROW);
         }

         if ((var4 = var1[0].length) == 0) {
            throw new pf(px.AT_LEAST_ONE_COLUMN);
         }

         this.a = new double[var1.length][var4];

         for (int var5 = 0; var5 < this.a.length; var5++) {
            if (var1[var5].length != var4) {
               throw new oS(var1[var5].length, var4);
            }

            System.arraycopy(var1[var5], 0, this.a[var5 + var2], var3, var4);
         }
      } else {
         super.a(var1, var2, var3);
      }
   }

   @Override
   public final double b(int var1, int var2) {
      td.a(this, var1, var2);
      return this.a[var1][var2];
   }

   @Override
   public final void c(int var1, int var2, double var3) {
      td.a(this, var1, var2);
      this.a[var1][var2] = var3;
   }

   @Override
   public final void a(int var1, int var2, double var3) {
      td.a(this, var1, var2);
      this.a[var1][var2] = this.a[var1][var2] + var3;
   }

   @Override
   public final void b(int var1, int var2, double var3) {
      td.a(this, var1, var2);
      this.a[var1][var2] = this.a[var1][var2] * var3;
   }

   @Override
   public final int f() {
      return this.a == null ? 0 : this.a.length;
   }

   @Override
   public final int g() {
      return this.a != null && this.a[0] != null ? this.a[0].length : 0;
   }

   @Override
   public final double[] a(double[] var1) {
      int var2 = this.f();
      int var3 = this.g();
      if (var1.length != var3) {
         throw new oS(var1.length, var3);
      }

      double[] var4 = new double[var2];

      for (int var5 = 0; var5 < var2; var5++) {
         double[] var6 = this.a[var5];
         double var7 = 0.0;

         for (int var9 = 0; var9 < var3; var9++) {
            var7 += var6[var9] * var1[var9];
         }

         var4[var5] = var7;
      }

      return var4;
   }

   @Override
   public final double[] b(double[] var1) {
      int var2 = this.f();
      int var3 = this.g();
      if (var1.length != var2) {
         throw new oS(var1.length, var2);
      }

      double[] var4 = new double[var3];

      for (int var5 = 0; var5 < var3; var5++) {
         double var6 = 0.0;

         for (int var8 = 0; var8 < var2; var8++) {
            var6 += this.a[var8][var5] * var1[var8];
         }

         var4[var5] = var6;
      }

      return var4;
   }

   @Override
   public final double a(tr var1) {
      int var2 = this.f();
      int var3 = this.g();

      for (int var4 = 0; var4 < var2; var4++) {
         double[] var5 = this.a[var4];

         for (int var6 = 0; var6 < var3; var6++) {
            var5[var6] = var1.a(var4, var6, var5[var6]);
         }
      }

      return var1.b();
   }

   @Override
   public final double a(tt var1) {
      int var2 = this.f();
      int var3 = this.g();
      var1.a(0, var2 - 1, 0);

      for (int var4 = 0; var4 < var2; var4++) {
         double[] var5 = this.a[var4];

         for (int var6 = 0; var6 < var3; var6++) {
            var1.a(var4, var6, var5[var6]);
         }
      }

      return var1.a();
   }

   @Override
   public final double a(tr var1, int var2, int var3, int var4, int var5) {
      td.a(this, var2, var3, var4, var5);
      this.f();
      this.g();

      for (int var8 = var2; var8 <= var3; var8++) {
         double[] var6 = this.a[var8];

         for (int var7 = var4; var7 <= var5; var7++) {
            var6[var7] = var1.a(var8, var7, var6[var7]);
         }
      }

      return var1.b();
   }

   @Override
   public final double a(tt var1, int var2, int var3, int var4, int var5) {
      td.a(this, var2, var3, var4, var5);
      this.f();
      this.g();
      var1.a(var2, var3, var4);

      for (int var8 = var2; var8 <= var3; var8++) {
         double[] var6 = this.a[var8];

         for (int var7 = var4; var7 <= var5; var7++) {
            var1.a(var8, var7, var6[var7]);
         }
      }

      return var1.a();
   }

   @Override
   public final double b(tr var1) {
      int var2 = this.f();
      int var3 = this.g();

      for (int var4 = 0; var4 < var3; var4++) {
         for (int var5 = 0; var5 < var2; var5++) {
            double[] var6;
            (var6 = this.a[var5])[var4] = var1.a(var5, var4, var6[var4]);
         }
      }

      return var1.b();
   }

   @Override
   public final double a(<unrepresentable> var1) {
      int var2 = this.f();
      int var3 = this.g();
      var1.a(0, var2 - 1, 0);

      for (int var4 = 0; var4 < var3; var4++) {
         for (int var5 = 0; var5 < var2; var5++) {
            var1.a(var5, var4, this.a[var5][var4]);
         }
      }

      return var1.a();
   }

   @Override
   public final double b(tr var1, int var2, int var3, int var4, int var5) {
      td.a(this, var2, var3, var4, var5);
      this.f();
      this.g();

      for (int var8 = var4; var8 <= var5; var8++) {
         for (int var6 = var2; var6 <= var3; var6++) {
            double[] var7;
            (var7 = this.a[var6])[var8] = var1.a(var6, var8, var7[var8]);
         }
      }

      return var1.b();
   }

   @Override
   public final double b(tt var1, int var2, int var3, int var4, int var5) {
      td.a(this, var2, var3, var4, var5);
      this.f();
      this.g();
      var1.a(var2, var3, var4);

      for (int var7 = var4; var7 <= var5; var7++) {
         for (int var6 = var2; var6 <= var3; var6++) {
            var1.a(var6, var7, this.a[var6][var7]);
         }
      }

      return var1.a();
   }

   public double[][] k() {
      int var1;
      double[][] var2 = new double[var1 = this.f()][this.g()];

      for (int var3 = 0; var3 < var1; var3++) {
         System.arraycopy(this.a[var3], 0, var2[var3], 0, this.a[var3].length);
      }

      return var2;
   }

   private void a(double[][] var1) {
      this.a(var1, 0, 0);
   }
}
