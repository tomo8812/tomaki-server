package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

public final class uc {
   private uc() {
   }

   public static ue a(double[] var0, Iterable<ue> var1, tW var2) {
      ue var3 = null;
      double var4 = Double.POSITIVE_INFINITY;

      for (ue var6 : var1) {
         double var8;
         if ((var8 = var2.a(var6.c(), var0)) < var4) {
            var4 = var8;
            var3 = var6;
         }
      }

      return var3;
   }

   public static Co<ue, ue> b(double[] var0, Iterable<ue> var1, tW var2) {
      ue[] var3 = new ue[]{null, null};
      double[] var4 = new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};

      for (ue var5 : var1) {
         double var7;
         if ((var7 = var2.a(var5.c(), var0)) < var4[0]) {
            var4[1] = var4[0];
            var3[1] = var3[0];
            var4[0] = var7;
            var3[0] = var5;
         } else if (var7 < var4[1]) {
            var4[1] = var7;
            var3[1] = var5;
         }
      }

      return new Co<>(var3[0], var3[1]);
   }

   public static ue[] a(double[] var0, Iterable<ue> var1, tW var2) {
      ArrayList var3 = new ArrayList();

      for (ue var4 : var1) {
         double var6 = var2.a(var4.c(), var0);
         var3.add(new uc.a(var4, var6));
      }

      Collections.sort(var3, uc.a.a);
      int var9;
      ue[] var10 = new ue[var9 = var3.size()];

      for (int var11 = 0; var11 < var9; var11++) {
         var10[var11] = ((uc.a)var3.get(var11)).b;
      }

      return var10;
   }

   private static double[][] a(uq var0, tW var1) {
      int var2 = var0.b;
      int var3 = var0.c;
      double[][] var4 = new double[var2][var3];
      ud var5 = var0.a;

      for (int var6 = 0; var6 < var2; var6++) {
         for (int var7 = 0; var7 < var3; var7++) {
            ue var8 = var0.a(var6, var7);
            ue var10 = var8;
            Collection var9 = var5.a(var10, (Iterable<ue>)null);
            double[] var14 = var8.c();
            double var11 = 0.0;
            int var16 = 0;

            for (ue var13 : var9) {
               var16++;
               var11 += var1.a(var14, var13.c());
            }

            var4[var6][var7] = var11 / var16;
         }
      }

      return var4;
   }

   private static int[][] a(Iterable<double[]> var0, uq var1, tW var2) {
      HashMap var3 = new HashMap();
      ud var4 = var1.a;
      Iterator var8 = var0.iterator();

      while (var8.hasNext()) {
         ue var6 = a((double[])var8.next(), (Iterable<ue>)var4, var2);
         Integer var7;
         if ((var7 = (Integer)var3.get(var6)) == null) {
            var3.put(var6, 1);
         } else {
            var3.put(var6, var7 + 1);
         }
      }

      int var9 = var1.b;
      int var5 = var1.c;
      int[][] var13 = new int[var9][var5];

      for (int var14 = 0; var14 < var9; var14++) {
         for (int var10 = 0; var10 < var5; var10++) {
            ue var11 = var1.a(var14, var10);
            Integer var12;
            if ((var12 = (Integer)var3.get(var11)) == null) {
               var13[var14][var10] = 0;
            } else {
               var13[var14][var10] = var12;
            }
         }
      }

      return var13;
   }

   private static double a(Iterable<double[]> var0, Iterable<ue> var1, tW var2) {
      double var3 = 0.0;
      int var5 = 0;

      for (double[] var6 : var0) {
         var5++;
         var3 += var2.a(var6, a(var6, var1, var2).c());
      }

      if (var5 == 0) {
         throw new pf();
      } else {
         return var3 / var5;
      }
   }

   private static double a(Iterable<double[]> var0, ud var1, tW var2) {
      int var3 = 0;
      int var4 = 0;

      for (double[] var5 : var0) {
         var4++;
         Co var9 = b(var5, var1, var2);
         ue var7 = (ue)var9.a;
         if (!var1.a(var7, (Iterable<ue>)null).contains(var9.b)) {
            var3++;
         }
      }

      if (var4 == 0) {
         throw new pf();
      } else {
         return (double)var3 / var4;
      }
   }

   public static class a {
      public static final <unrepresentable> a = new Comparator<uc.a>() {
         private static int a(uc.a var0, uc.a var1) {
            return Double.compare(var0.c, var1.c);
         }

         // $VF: synthetic method
         @Override
         public final int compare(Object var1, Object var2) {
            uc.a var10000 = (uc.a)var1;
            var2 = var2;
            return Double.compare(var10000.c, var2.c);
         }
      };
      public final ue b;
      private final double c;

      public a(ue var1, double var2) {
         this.b = var1;
         this.c = var2;
      }

      public final ue a() {
         return this.b;
      }
   }
}
