package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

public final class qB<T> implements qr {
   private final int a;

   private qB(int var1) {
      if (var1 <= 0) {
         throw new pk(var1);
      }

      this.a = var1;
   }

   private int a() {
      return this.a;
   }

   @Override
   public final qq a(qp var1, qp var2) {
      if (var1 instanceof qm && var2 instanceof qm) {
         qm var10001 = (qm)var1;
         qm var3 = var2;
         var2 = var10001;
         qB var14 = this;
         int var4;
         if ((var4 = var2.b()) != var3.b()) {
            throw new oS(var3.b(), var4);
         }

         if (var14.a >= var4) {
            throw new pm(var14.a, var4, false);
         }

         List var5 = var2.a;
         List var6 = var3.a;
         ArrayList var7 = new ArrayList(var4);
         ArrayList var8 = new ArrayList(var4);
         zF var9 = qx.a();
         var7 = var7;
         var8 = var8;
         int var10 = var14.a;
         int var11 = 0;

         for (int var12 = 0; var12 < var14.a; var10--) {
            int var13 = var11 + 1 + var9.nextInt(var4 - var11 - var10);

            for (int var18 = var11; var18 < var13; var18++) {
               var7.add(var5.get(var18));
               var8.add(var6.get(var18));
            }

            ArrayList var19 = var7;
            var7 = var8;
            var8 = var19;
            var11 = var13;
            var12++;
         }

         for (int var20 = var11; var20 < var4; var20++) {
            var7.add(var5.get(var20));
            var8.add(var6.get(var20));
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

      if (this.a >= var3) {
         throw new pm(this.a, var3, false);
      }

      List var4 = var1.a;
      List var5 = var2.a;
      ArrayList var6 = new ArrayList(var3);
      ArrayList var7 = new ArrayList(var3);
      zF var8 = qx.a();
      var6 = var6;
      var7 = var7;
      int var9 = this.a;
      int var10 = 0;

      for (int var11 = 0; var11 < this.a; var9--) {
         int var12 = var10 + 1 + var8.nextInt(var3 - var10 - var9);

         for (int var15 = var10; var15 < var12; var15++) {
            var6.add(var4.get(var15));
            var7.add(var5.get(var15));
         }

         ArrayList var16 = var6;
         var6 = var7;
         var7 = var16;
         var10 = var12;
         var11++;
      }

      for (int var17 = var10; var17 < var3; var17++) {
         var6.add(var4.get(var17));
         var7.add(var5.get(var17));
      }

      return new qq(var1.c(), var2.c());
   }
}
