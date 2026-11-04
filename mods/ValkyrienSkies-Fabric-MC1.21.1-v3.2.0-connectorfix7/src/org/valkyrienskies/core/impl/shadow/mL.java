package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class mL implements lp {
   private final List<Double> a = new ArrayList<>();
   private final List<double[]> b = new ArrayList<>();
   private final List<double[]> c = new ArrayList<>();

   private void a(double var1, double[]... var3) {
      for (int var4 = 0; var4 < var3.length; var4++) {
         double[] var5 = (double[])var3[var4].clone();
         if (var4 > 1) {
            double var6 = 1.0 / BR.a(var4);

            for (int var8 = 0; var8 < var5.length; var8++) {
               var5[var8] *= var6;
            }
         }

         int var13 = this.a.size();
         this.c.add(var13 - var4, var5);
         double[] var7 = var5;

         for (int var14 = var4; var14 < var13; var14++) {
            double[] var9 = this.c.get(var13 - (var14 + 1));
            double var10;
            if (Double.isInfinite(var10 = 1.0 / (var1 - this.a.get(var13 - (var14 + 1))))) {
               throw new pr(px.DUPLICATED_ABSCISSA_DIVISION_BY_ZERO, var1);
            }

            for (int var12 = 0; var12 < var5.length; var12++) {
               var9[var12] = var10 * (var7[var12] - var9[var12]);
            }

            var7 = var9;
         }

         this.b.add((double[])var7.clone());
         this.a.add(var1);
      }
   }

   private ni[] a() {
      this.b();
      ni var1 = a(0.0);
      ni[] var2 = new ni[((double[])this.b.get(0)).length];

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = var1;
      }

      ni var13 = a(1.0);

      for (int var12 = 0; var12 < this.b.size(); var12++) {
         double[] var4 = this.b.get(var12);

         for (int var5 = 0; var5 < var2.length; var5++) {
            ni var10002 = var2[var5];
            ni var7 = var13.b(a(var4[var5]));
            ni var6 = var10002;
            int var8 = BY.a(var10002.a.length, var7.a.length);
            int var9;
            double[] var10 = new double[var9 = BY.b(var6.a.length, var7.a.length)];

            for (int var11 = 0; var11 < var8; var11++) {
               var10[var11] = var6.a[var11] + var7.a[var11];
            }

            System.arraycopy(var6.a.length < var7.a.length ? var7.a : var6.a, var8, var10, var8, var9 - var8);
            var2[var5] = new ni(var10);
         }

         var13 = var13.b(a(-this.a.get(var12), 1.0));
      }

      return var2;
   }

   @Override
   public final double[] a(double var1) {
      this.b();
      double[] var3 = new double[((double[])this.b.get(0)).length];
      double var4 = 1.0;

      for (int var6 = 0; var6 < this.b.size(); var6++) {
         double[] var7 = this.b.get(var6);

         for (int var8 = 0; var8 < var3.length; var8++) {
            var3[var8] += var7[var8] * var4;
         }

         double var10 = var1 - this.a.get(var6);
         var4 *= var10;
      }

      return var3;
   }

   @Override
   public final lg[] a(lg var1) {
      this.b();
      lg[] var2;
      Arrays.fill(var2 = new lg[((double[])this.b.get(0)).length], var1.c().a());
      lg var3 = var1.c().b();

      for (int var4 = 0; var4 < this.b.size(); var4++) {
         double[] var5 = this.b.get(var4);

         for (int var6 = 0; var6 < var2.length; var6++) {
            var2[var6] = var2[var6].a(var3.j(var5[var6]));
         }

         double var8 = this.a.get(var4);
         lg var10 = var1.h(-var8);
         var3 = var3.b(var10);
      }

      return var2;
   }

   private void b() {
      if (this.a.isEmpty()) {
         throw new pf(px.EMPTY_INTERPOLATION_SAMPLE);
      }
   }

   private static ni a(double... var0) {
      return new ni(var0);
   }
}
