package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public final class Be {
   public final double a(Collection<double[]> var1) {
      return this.c(var1).c;
   }

   public final double b(Collection<double[]> var1) {
      Be.a var3 = this.c(var1);
      oo var2 = new oo(null, var3.a, var3.b);
      return 1.0 - var2.e(var3.c);
   }

   private double a(Collection<Aw> var1, boolean var2) {
      Be.a var3 = b(var1, var2);
      oo var4 = new oo(null, var3.a, var3.b);
      return 1.0 - var4.e(var3.c);
   }

   Be.a c(Collection<double[]> var1) {
      Ci.a(var1);
      ArrayList var2 = new ArrayList(var1.size());

      for (double[] var3 : var1) {
         Aw var4 = new Aw();
         var2.add(var4);
         double[] var12;
         int var5 = (var12 = var3).length;

         for (int var6 = 0; var6 < var5; var6++) {
            double var9 = var12[var6];
            var4.a(var9);
         }
      }

      return b(var2, false);
   }

   public final boolean a(Collection<double[]> var1, double var2) {
      if (!(var2 <= 0.0) && !(var2 > 0.5)) {
         return this.b(var1) < var2;
      } else {
         throw new po(px.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, var2, 0, 0.5);
      }
   }

   private static Be.a b(Collection<Aw> var0, boolean var1) {
      Ci.a(var0);
      if (!var1) {
         if (var0.size() < 2) {
            throw new oS(px.TWO_OR_MORE_CATEGORIES_REQUIRED, var0.size(), 2);
         }

         Iterator var21 = var0.iterator();

         while (var21.hasNext()) {
            Aw var3;
            if ((var3 = (Aw)var21.next()).d() <= 1L) {
               throw new oS(px.TWO_OR_MORE_VALUES_IN_CATEGORY_REQUIRED, (int)var3.d(), 2);
            }
         }
      }

      var1 = 0;
      double var23 = 0.0;
      double var5 = 0.0;
      double var7 = 0.0;
      int var2 = 0;
      Iterator var10 = var0.iterator();

      while (var10.hasNext()) {
         Aw var11;
         double var12 = (var11 = (Aw)var10.next()).f();
         double var14 = var11.i();
         int var16 = (int)var11.d();
         var2 += var16;
         var5 += var12;
         var7 += var14;
         var1 += var16 - 1;
         double var17 = var14 - var12 * var12 / var16;
         var23 += var17;
      }

      double var24;
      double var25 = (var24 = var7 - var5 * var5 / var2) - var23;
      int var26 = var0.size() - 1;
      double var15 = var25 / var26;
      double var27 = var23 / var1;
      double var19 = var15 / var27;
      return new Be.a(var26, var1, var19, (byte)0);
   }

   static class a {
      final int a;
      final int b;
      final double c;

      private a(int var1, int var2, double var3) {
         this.a = var1;
         this.b = var2;
         this.c = var3;
      }

      // $VF: synthetic method
      static double a(Be.a var0) {
         return var0.c;
      }

      // $VF: synthetic method
      static int b(Be.a var0) {
         return var0.a;
      }

      // $VF: synthetic method
      static int c(Be.a var0) {
         return var0.b;
      }

      // $VF: synthetic method
      a(int var1, int var2, double var3, byte var5) {
         this(var1, var2, var3);
      }
   }
}
