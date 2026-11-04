package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

public final class qC<T> implements qr {
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
         int var8 = 1 + qx.a().nextInt(var3 - 2);

         for (int var9 = 0; var9 < var8; var9++) {
            var6.add(var4.get(var9));
            var7.add(var5.get(var9));
         }

         for (int var12 = var8; var12 < var3; var12++) {
            var6.add(var5.get(var12));
            var7.add(var4.get(var12));
         }

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
      int var7 = 1 + qx.a().nextInt(var2 - 2);

      for (int var8 = 0; var8 < var7; var8++) {
         var5.add(var3.get(var8));
         var6.add(var4.get(var8));
      }

      for (int var9 = var7; var9 < var2; var9++) {
         var5.add(var4.get(var9));
         var6.add(var3.get(var9));
      }

      return new qq(var0.c(), var1.c());
   }
}
