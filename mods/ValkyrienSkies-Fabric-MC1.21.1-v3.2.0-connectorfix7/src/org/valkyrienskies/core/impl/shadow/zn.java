package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

final class zn {
   private zn() {
   }

   private static List<Integer> a(int var0) {
      ArrayList var1 = new ArrayList();
      var0 = zp.a(var0, var1);
      if (1 == var0) {
         return var1;
      }

      if (zp.b(var0)) {
         var1.add(var0);
         return var1;
      }

      int var2 = var0;
      int var3 = zp.b;
      int var4 = 2;
      int var5 = 1;

      int var10000;
      label51:
      while (true) {
         int var6 = var4;

         for (int var7 = 0; var7 < var5; var7++) {
            var4 = (int)(((long)var4 * var4 + var3) % var2);
         }

         byte var20 = 0;

         do {
            int var9 = BY.a(25, var5 - var20);
            int var10 = 1;

            for (int var8 = -3; var8 < var9; var8++) {
               var4 = (int)(((long)var4 * var4 + var3) % var2);
               long var14 = BY.a(var6 - var4);
               if (0L == var14) {
                  var3 += zp.b;
                  var20 = -25;
                  var4 = 2;
                  var5 = 1;
                  break;
               }

               long var16;
               var10 = (int)((var16 = var14 * var10) % var2);
               if (var10 == 0) {
                  var10000 = a(BY.a((int)var14), var2);
                  break label51;
               }
            }

            int var21 = a(BY.a(var10), var2);
            if (1 != var21) {
               var10000 = var21;
               break label51;
            }

            var20 += 25;
         } while (var20 < var5);

         var5 = 2 * var5;
      }

      var2 = var10000;
      var1.add(var2);
      var1.add(var0 / var2);
      return var1;
   }

   private static int b(int var0) {
      int var1 = zp.b;
      int var2 = 2;
      int var3 = 1;

      while (true) {
         int var4 = var2;

         for (int var5 = 0; var5 < var3; var5++) {
            var2 = (int)(((long)var2 * var2 + var1) % var0);
         }

         byte var15 = 0;

         do {
            int var6 = BY.a(25, var3 - var15);
            int var7 = 1;

            for (int var8 = -3; var8 < var6; var8++) {
               var2 = (int)(((long)var2 * var2 + var1) % var0);
               long var11 = BY.a(var4 - var2);
               if (0L == var11) {
                  var1 += zp.b;
                  var15 = -25;
                  var2 = 2;
                  var3 = 1;
                  break;
               }

               long var13;
               var7 = (int)((var13 = var11 * var7) % var0);
               if (var7 == 0) {
                  return a(BY.a((int)var11), var0);
               }
            }

            int var16 = a(BY.a(var7), var0);
            if (1 != var16) {
               return var16;
            }

            var15 += 25;
         } while (var15 < var3);

         var3 = 2 * var3;
      }
   }

   private static int a(int var0, int var1) {
      if (var0 == 0) {
         return var1;
      }

      if (var1 == 0) {
         return var0;
      }

      int var2 = Integer.numberOfTrailingZeros(var0);
      var0 >>= var2;
      int var3 = Integer.numberOfTrailingZeros(var1);
      var1 >>= var3;
      var2 = BY.a(var2, var3);

      while (var0 != var1) {
         var3 = var0 - var1;
         var1 = BY.a(var0, var1);
         var0 = (var0 = BY.a(var3)) >> Integer.numberOfTrailingZeros(var0);
      }

      return var0 << var2;
   }
}
