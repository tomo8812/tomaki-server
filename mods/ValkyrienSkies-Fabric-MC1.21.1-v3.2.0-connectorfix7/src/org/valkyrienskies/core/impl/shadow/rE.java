package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class rE {
   private rE() {
   }

   private static Collection<rB> a(Collection<rB> var0) {
      int var1 = 0;
      rB var2 = null;
      rB var3 = null;
      rB var4 = null;
      rB var5 = null;

      for (rB var7 : var0) {
         if (var2 == null || var7.c < var2.c) {
            var2 = var7;
         }

         if (var3 == null || var7.c > var3.c) {
            var3 = var7;
         }

         if (var4 == null || var7.d < var4.d) {
            var4 = var7;
         }

         if (var5 == null || var7.d > var5.d) {
            var5 = var7;
         }

         var1++;
      }

      if (var1 < 4) {
         return var0;
      }

      rB[] var18 = new rB[]{var4, var3, var5, var2};
      ArrayList var20 = new ArrayList();
      rB[] var22 = var18;
      int var10000 = var18.length;

      for (int var14 = 0; var14 < 4; var14++) {
         rB var15 = var22[var14];
         if (!var20.contains(var15)) {
            var20.add(var15);
         }
      }

      ArrayList var26 = var20;
      if (var20.size() < 3) {
         return var0;
      }

      ArrayList var27 = new ArrayList(var26);
      Iterator var16 = var0.iterator();

      while (var16.hasNext()) {
         rB var17;
         rB var29 = var17 = (rB)var16.next();
         ArrayList var21 = var26;
         var2 = var29;
         var4 = (rB)var21.get(0);
         var5 = (rB)var21.get(1);
         boolean var30;
         if (!var2.equals(var4) && !var2.equals(var5)) {
            double var28 = var2.b(var4, var5);
            int var8 = var21.size();
            int var9 = 1;

            while (true) {
               if (var9 < var8) {
                  var4 = var5;
                  var5 = (rB)var21.get(var9 + 1 == var8 ? 0 : var9 + 1);
                  if (!var2.equals(var4) && !var2.equals(var5)) {
                     if (var28 * var2.b(var4, var5) < 0.0) {
                        var30 = false;
                        break;
                     }

                     var9++;
                     continue;
                  }

                  var30 = true;
                  break;
               }

               var30 = true;
               break;
            }
         } else {
            var30 = true;
         }

         if (!var30) {
            var27.add(var17);
         }
      }

      return var27;
   }

   private static List<rB> a(rB... var0) {
      ArrayList var1 = new ArrayList();
      rB[] var4;
      int var10000 = (var4 = var0).length;

      for (int var2 = 0; var2 < 4; var2++) {
         rB var3 = var4[var2];
         if (!var1.contains(var3)) {
            var1.add(var3);
         }
      }

      return var1;
   }

   private static boolean a(rB var0, List<rB> var1) {
      rB var2 = (rB)var1.get(0);
      rB var3 = (rB)var1.get(1);
      if (!var0.equals(var2) && !var0.equals(var3)) {
         double var4 = var0.b(var2, var3);
         int var6 = var1.size();

         for (int var7 = 1; var7 < var6; var7++) {
            var2 = var3;
            var3 = (rB)var1.get(var7 + 1 == var6 ? 0 : var7 + 1);
            if (var0.equals(var2) || var0.equals(var3)) {
               return true;
            }

            if (var4 * var0.b(var2, var3) < 0.0) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }
}
