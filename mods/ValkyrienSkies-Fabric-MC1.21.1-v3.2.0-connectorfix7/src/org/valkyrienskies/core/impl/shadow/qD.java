package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public final class qD<T> implements qr {
   @Override
   public final qq a(qp var1, qp var2) {
      if (var1 instanceof qm && var2 instanceof qm) {
         qm var10000 = var1;
         var2 = var2;
         var1 = var10000;
         int var3;
         if ((var3 = var10000.b()) != var2.b()) {
            throw new oS(var2.b(), var3);
         }

         List var4 = var1.a;
         List var5 = var2.a;
         ArrayList var6 = new ArrayList(var3);
         ArrayList var7 = new ArrayList(var3);
         HashSet var8 = new HashSet(var3);
         HashSet var9 = new HashSet(var3);
         zF var10;
         int var11 = (var10 = qx.a()).nextInt(var3);

         int var12;
         do {
            var12 = var10.nextInt(var3);
         } while (var11 == var12);

         int var17 = BY.a(var11, var12);
         var11 = BY.b(var11, var12);
         var6.addAll(var4.subList(var17, var11 + 1));
         var8.addAll(var6);
         var7.addAll(var5.subList(var17, var11 + 1));
         var9.addAll(var7);

         for (int var19 = 1; var19 <= var3; var19++) {
            int var13 = (var11 + var19) % var3;
            Object var14 = var4.get(var13);
            Object var20 = var5.get(var13);
            if (!var8.contains(var20)) {
               var6.add(var20);
               var8.add(var20);
            }

            if (!var9.contains(var14)) {
               var7.add(var14);
               var9.add(var14);
            }
         }

         Collections.rotate(var6, var17);
         Collections.rotate(var7, var17);
         return new qq(var1.c(), var2.c());
      } else {
         throw new oV(px.INVALID_FIXED_LENGTH_CHROMOSOME);
      }
   }

   private static qq a(qm<T> var0, qm<T> var1) {
      int var2;
      if ((var2 = var0.b()) != var1.b()) {
         throw new oS(var1.b(), var2);
      }

      List var3 = var0.a;
      List var4 = var1.a;
      ArrayList var5 = new ArrayList(var2);
      ArrayList var6 = new ArrayList(var2);
      HashSet var7 = new HashSet(var2);
      HashSet var8 = new HashSet(var2);
      zF var9;
      int var10 = (var9 = qx.a()).nextInt(var2);

      int var11;
      do {
         var11 = var9.nextInt(var2);
      } while (var10 == var11);

      int var14 = BY.a(var10, var11);
      var10 = BY.b(var10, var11);
      var5.addAll(var3.subList(var14, var10 + 1));
      var7.addAll(var5);
      var6.addAll(var4.subList(var14, var10 + 1));
      var8.addAll(var6);

      for (int var16 = 1; var16 <= var2; var16++) {
         int var12 = (var10 + var16) % var2;
         Object var13 = var3.get(var12);
         Object var17 = var4.get(var12);
         if (!var7.contains(var17)) {
            var5.add(var17);
            var7.add(var17);
         }

         if (!var8.contains(var13)) {
            var6.add(var13);
            var8.add(var13);
         }
      }

      Collections.rotate(var5, var14);
      Collections.rotate(var6, var14);
      return new qq(var0.c(), var1.c());
   }
}
