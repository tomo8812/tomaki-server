package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;

@Deprecated
public final class yD extends yx {
   private static final double d = 2.0;
   private static final double e = 0.5;
   private final double f;
   private final double g;

   private yD(int var1) {
      this(var1, 1.0);
   }

   private yD(int var1, double var2) {
      this(var1, 1.0, 2.0, 0.5);
   }

   private yD(int var1, double var2, double var4) {
      this(var1, 1.0, var2, var4);
   }

   private yD(int var1, double var2, double var4, double var6) {
      super(var1, var2);
      this.f = var4;
      this.g = var6;
   }

   private yD(double[] var1) {
      this(var1, 2.0, 0.5);
   }

   private yD(double[] var1, double var2, double var4) {
      super(var1);
      this.f = 2.0;
      this.g = 0.5;
   }

   private yD(double[][] var1) {
      this(var1, 2.0, 0.5);
   }

   private yD(double[][] var1, double var2, double var4) {
      super(var1);
      this.f = 2.0;
      this.g = 0.5;
   }

   @Override
   public final void a(kW var1, Comparator<yp> var2) {
      yp[] var3;
      yp var4 = (var3 = this.c())[0];
      yp var5 = this.a(var1, var3, 1.0, var2);
      if (var2.compare(var5, var4) < 0) {
         yp[] var7 = this.c();
         yp var6 = this.a(var1, var3, this.f, var2);
         if (var2.compare(var5, var6) <= 0) {
            this.a(var7);
         }
      } else {
         this.a(var1, var3, this.g, var2);
      }
   }

   private yp a(kW var1, yp[] var2, double var3, Comparator<yp> var5) {
      yp var9;
      double[] var6 = (double[])(var9 = var2[0]).a;
      this.a(0, var2[0]);
      int var7 = this.a();

      for (int var8 = 1; var8 < this.b(); var8++) {
         double[] var13 = (double[])(var9 = var2[var8]).a;
         double[] var10 = new double[var7];

         for (int var11 = 0; var11 < var7; var11++) {
            var10[var11] = var6[var11] + var3 * (var6[var11] - var13[var11]);
         }

         this.a(var8, new yp(var10, Double.NaN, false));
      }

      this.a(var1, var5);
      return this.a(0);
   }
}
