package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;
import java.util.Comparator;

public final class Ai {
   private final tq a;

   public Ai() {
      this.a = null;
   }

   private Ai(double[][] var1) {
      this(td.a(var1));
   }

   private Ai(tq var1) {
      this.a = this.a(var1);
   }

   private tq a() {
      return this.a;
   }

   private sD a(tq var1) {
      int var2 = var1.g();
      sD var3 = new sD(var2, var2);

      for (int var4 = 0; var4 < var2; var4++) {
         for (int var5 = 0; var5 < var4; var5++) {
            double var6 = this.a(var1.g(var4), var1.g(var5));
            var3.c(var4, var5, var6);
            var3.c(var5, var4, var6);
         }

         var3.c(var4, var4, 1.0);
      }

      return var3;
   }

   private tq a(double[][] var1) {
      return this.a(new sD(var1));
   }

   private double a(double[] var1, double[] var2) {
      if (var1.length != var2.length) {
         throw new oS(var1.length, var2.length);
      }

      int var3;
      long var4 = a((var3 = var1.length) - 1);
      Co[] var6 = new Co[var3];

      for (int var7 = 0; var7 < var3; var7++) {
         var6[var7] = new Co<>(var1[var7], var2[var7]);
      }

      Arrays.sort(var6, new Comparator<Co<Double, Double>>() {
         private static int a(Co<Double, Double> var0, Co<Double, Double> var1x) {
            int var2x;
            return (var2x = ((Double)var0.a).compareTo((Double)var1x.a)) != 0 ? var2x : ((Double)var0.b).compareTo((Double)var1x.b);
         }

         // $VF: synthetic method
         @Override
         public final int compare(Object var1, Object var2) {
            Co var10000 = var1;
            var2 = var2;
            var1 = var10000;
            int var3;
            return (var3 = ((Double)var10000.a).compareTo((Double)var2.a)) != 0 ? var3 : ((Double)var1.b).compareTo((Double)var2.b);
         }
      });
      long var32 = 0L;
      long var9 = 0L;
      long var11 = 1L;
      long var13 = 1L;
      Co var27 = var6[0];

      for (int var16 = 1; var16 < var3; var16++) {
         Co var17;
         if (((Double)(var17 = var6[var16]).a).equals(var27.a)) {
            var11++;
            if (((Double)var17.b).equals(var27.b)) {
               var13++;
            } else {
               var9 += a(var13 - 1L);
               var13 = 1L;
            }
         } else {
            var32 += a(var11 - 1L);
            var11 = 1L;
            var9 += a(var13 - 1L);
            var13 = 1L;
         }

         var27 = var17;
      }

      var32 += a(var11 - 1L);
      var9 += a(var13 - 1L);
      long var35 = 0L;
      Co[] var28 = new Co[var3];

      for (byte var19 = 1; var19 < var3; var19 <<= 1) {
         for (int var20 = 0; var20 < var3; var20 += 2 * var19) {
            int var21 = var20;
            int var22;
            int var23;
            int var24 = BY.a((var23 = var22 = BY.a(var20 + var19, var3)) + var19, var3);

            for (int var25 = var20; var21 < var22 || var23 < var24; var25++) {
               if (var21 < var22) {
                  if (var23 < var24) {
                     Co var30;
                     Co var31;
                     if (((Double)(var30 = var6[var21]).b).compareTo((Double)(var31 = var6[var23]).b) <= 0) {
                        var28[var25] = var6[var21];
                        var21++;
                     } else {
                        var28[var25] = var6[var23];
                        var23++;
                        var35 += var22 - var21;
                     }
                  } else {
                     var28[var25] = var6[var21];
                     var21++;
                  }
               } else {
                  var28[var25] = var6[var23];
                  var23++;
               }
            }
         }

         Co[] var38 = var6;
         var6 = var28;
         var28 = var38;
      }

      long var36 = 0L;
      long var39 = 1L;
      Co var29 = var6[0];

      for (int var40 = 1; var40 < var3; var40++) {
         Co var42;
         if (((Double)(var42 = var6[var40]).b).equals(var29.b)) {
            var39++;
         } else {
            var36 += a(var39 - 1L);
            var39 = 1L;
         }

         var29 = var42;
      }

      var36 += a(var39 - 1L);
      long var41 = var4 - var32 - var36 + var9 - 2L * var35;
      double var43 = (double)(var4 - var32) * (var4 - var36);
      return var41 / BY.a(var43);
   }

   private static long a(long var0) {
      return var0 * (var0 + 1L) / 2L;
   }
}
