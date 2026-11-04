package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

public final class mK<T extends kN<T>> {
   private final List<T> a = new ArrayList<>();
   private final List<T[]> b = new ArrayList();
   private final List<T[]> c = new ArrayList();

   private void a(T var1, T[]... var2) {
      Ci.a(var1);
      kN var3 = var1.c().b();

      for (int var4 = 0; var4 < var2.length; var4++) {
         kN[] var5 = (kN[])var2[var4].clone();
         if (var4 > 1) {
            kN var6 = (var3 = var3.a(var4)).b();

            for (int var7 = 0; var7 < var5.length; var7++) {
               var5[var7] = var5[var7].c(var6);
            }
         }

         int var12 = this.a.size();
         this.c.add(var12 - var4, var5);
         kN[] var13 = var5;

         for (int var8 = var4; var8 < var12; var8++) {
            kN[] var9 = (kN[])this.c.get(var12 - (var8 + 1));
            if (var1.equals(this.a.get(var12 - (var8 + 1)))) {
               throw new pr(px.DUPLICATED_ABSCISSA_DIVISION_BY_ZERO, var1);
            }

            kN var10 = var1.b(this.a.get(var12 - (var8 + 1))).b();

            for (int var11 = 0; var11 < var5.length; var11++) {
               var9[var11] = var10.c(var13[var11].b(var9[var11]));
            }

            var13 = var9;
         }

         this.b.add(var13.clone());
         this.a.add((T)var1);
      }
   }

   private T[] a(T var1) {
      Ci.a(var1);
      if (this.a.isEmpty()) {
         throw new pf(px.EMPTY_INTERPOLATION_SAMPLE);
      }

      kN[] var2 = Ch.a(var1.c(), ((kN[])this.b.get(0)).length);
      kN var3 = var1.c().b();

      for (int var4 = 0; var4 < this.b.size(); var4++) {
         kN[] var5 = (kN[])this.b.get(var4);

         for (int var6 = 0; var6 < var2.length; var6++) {
            var2[var6] = var2[var6].a(var5[var6].c(var3));
         }

         kN var7 = var1.b(this.a.get(var4));
         var3 = var3.c(var7);
      }

      return (T[])var2;
   }

   private T[][] a(T var1, int var2) {
      Ci.a(var1);
      if (this.a.isEmpty()) {
         throw new pf(px.EMPTY_INTERPOLATION_SAMPLE);
      }

      kN var3 = var1.c().a();
      kN var4 = var1.c().b();
      kN[] var5;
      (var5 = Ch.a(var1.c(), var2 + 1))[0] = var3;

      for (int var11 = 0; var11 < var2; var11++) {
         var5[var11 + 1] = var5[var11].a(var4);
      }

      kN[][] var12 = Ch.a(var1.c(), var2 + 1, ((kN[])this.b.get(0)).length);
      kN[] var13;
      (var13 = Ch.a(var1.c(), var2 + 1))[0] = var1.c().b();

      for (int var6 = 0; var6 < this.b.size(); var6++) {
         kN[] var7 = (kN[])this.b.get(var6);
         kN var8 = var1.b(this.a.get(var6));

         for (int var9 = var2; var9 >= 0; var9--) {
            for (int var10 = 0; var10 < var12[var9].length; var10++) {
               var12[var9][var10] = var12[var9][var10].a(var7[var10].c(var13[var9]));
            }

            var13[var9] = var13[var9].c(var8);
            if (var9 > 0) {
               var13[var9] = var13[var9].a(var5[var9].c(var13[var9 - 1]));
            }
         }
      }

      return (T[][])var12;
   }
}
