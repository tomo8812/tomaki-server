package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

public final class qL<T> implements qr {
   private final double a;

   private qL(double var1) {
      if (!(var1 < 0.0) && !(var1 > 1.0)) {
         this.a = var1;
      } else {
         throw new po(px.CROSSOVER_RATE, var1, 0.0, 1.0);
      }
   }

   private double a() {
      return this.a;
   }

   @Override
   public final qq a(qp var1, qp var2) {
      if (var1 instanceof qm && var2 instanceof qm) {
         qm var10001 = (qm)var1;
         qm var3 = var2;
         var2 = var10001;
         qL var11 = this;
         int var4;
         if ((var4 = var2.b()) != var3.b()) {
            throw new oS(var3.b(), var4);
         }

         List var5 = var2.a;
         List var6 = var3.a;
         ArrayList var7 = new ArrayList(var4);
         ArrayList var8 = new ArrayList(var4);
         zF var9 = qx.a();

         for (int var10 = 0; var10 < var4; var10++) {
            if (var9.nextDouble() < var11.a) {
               var7.add(var6.get(var10));
               var8.add(var5.get(var10));
            } else {
               var7.add(var5.get(var10));
               var8.add(var6.get(var10));
            }
         }

         return new qq(var2.c(), var3.c());
      } else {
         throw new oV(px.INVALID_FIXED_LENGTH_CHROMOSOME);
      }
   }

   private qq a(qm<T> var1, qm<T> var2) {
      int var3;
      if ((var3 = var1.b()) != var2.b()) {
         throw new oS(var2.b(), var3);
      }

      List var4 = var1.a;
      List var5 = var2.a;
      ArrayList var6 = new ArrayList(var3);
      ArrayList var7 = new ArrayList(var3);
      zF var8 = qx.a();

      for (int var9 = 0; var9 < var3; var9++) {
         if (var8.nextDouble() < this.a) {
            var6.add(var5.get(var9));
            var7.add(var4.get(var9));
         } else {
            var6.add(var4.get(var9));
            var7.add(var5.get(var9));
         }
      }

      return new qq(var1.c(), var2.c());
   }
}
