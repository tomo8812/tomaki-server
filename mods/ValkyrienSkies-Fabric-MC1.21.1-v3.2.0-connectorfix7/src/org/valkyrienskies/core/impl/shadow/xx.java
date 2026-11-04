package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;

public final class xx extends xu {
   private static final double d = 2.0;
   private static final double e = 0.5;
   private final double f;
   private final double g;

   private xx(int var1) {
      this(var1, 1.0);
   }

   private xx(int var1, double var2) {
      this(var1, 1.0, 2.0, 0.5);
   }

   private xx(int var1, double var2, double var4) {
      this(var1, 1.0, var2, var4);
   }

   private xx(int var1, double var2, double var4, double var6) {
      super(var1, var2);
      this.f = var4;
      this.g = var6;
   }

   private xx(double[] var1) {
      this(var1, 2.0, 0.5);
   }

   private xx(double[] var1, double var2, double var4) {
      super(var1);
      this.f = 2.0;
      this.g = 0.5;
   }

   private xx(double[][] var1) {
      this(var1, 2.0, 0.5);
   }

   private xx(double[][] var1, double var2, double var4) {
      super(var1);
      this.f = 2.0;
      this.g = 0.5;
   }

   @Override
   public final void a(kW var1, Comparator<wQ> var2) {
      wQ[] var3;
      wQ var4 = (var3 = this.c())[0];
      wQ var5 = this.a(var1, var3, 1.0, var2);
      if (var2.compare(var5, var4) < 0) {
         wQ[] var7 = this.c();
         wQ var6 = this.a(var1, var3, this.f, var2);
         if (var2.compare(var5, var6) <= 0) {
            this.a(var7);
         }
      } else {
         this.a(var1, var3, this.g, var2);
      }
   }

   private wQ a(kW var1, wQ[] var2, double var3, Comparator<wQ> var5) {
      wQ var9;
      double[] var6 = (double[])(var9 = var2[0]).a;
      this.a(0, var2[0]);
      int var7 = this.a();

      for (int var8 = 1; var8 < this.b(); var8++) {
         double[] var13 = (double[])(var9 = var2[var8]).a;
         double[] var10 = new double[var7];

         for (int var11 = 0; var11 < var7; var11++) {
            var10[var11] = var6[var11] + var3 * (var6[var11] - var13[var11]);
         }

         this.a(var8, new wQ(var10, Double.NaN, false));
      }

      this.a(var1, var5);
      return this.a(0);
   }
}
