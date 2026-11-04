package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class qs<T> implements qr {
   private final boolean a = false;

   public qs() {
      this(false);
   }

   private qs(boolean var1) {
   }

   private boolean a() {
      return this.a;
   }

   @Override
   public final qq a(qp var1, qp var2) {
      if (var1 instanceof qm && var2 instanceof qm) {
         qm var10001 = (qm)var1;
         qm var3 = var2;
         var2 = var10001;
         qs var14 = this;
         int var4;
         if ((var4 = var2.b()) != var3.b()) {
            throw new oS(var3.b(), var4);
         }

         List var5 = var2.a;
         List var6 = var3.a;
         ArrayList var7 = new ArrayList<>(var3.a);
         ArrayList var8 = new ArrayList<>(var2.a);
         HashSet var9 = new HashSet(var4);
         ArrayList var10 = new ArrayList(var4);
         int var15 = var14.a ? qx.a().nextInt(var4) : 0;
         int var11 = 1;

         while (var9.size() < var4) {
            var10.add(var15);
            Object var16 = var6.get(var15);
            var16 = var5.indexOf(var16);

            while (var16 != var10.get(0)) {
               var10.add(var16);
               var16 = var6.get(var16);
               var16 = var5.indexOf(var16);
            }

            if (var11++ % 2 != 0) {
               for (int var12 : var10) {
                  Object var13 = var7.get(var12);
                  var7.set(var12, var8.get(var12));
                  var8.set(var12, var13);
               }
            }

            var9.addAll(var10);
            var15 = ((Integer)var10.get(0) + 1) % var4;

            while (var9.contains(var15) && var9.size() < var4) {
               if (++var15 >= var4) {
                  var15 = 0;
               }
            }

            var10.clear();
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
      ArrayList var6 = new ArrayList<>(var2.a);
      ArrayList var7 = new ArrayList<>(var1.a);
      HashSet var8 = new HashSet(var3);
      ArrayList var9 = new ArrayList(var3);
      int var10 = this.a ? qx.a().nextInt(var3) : 0;
      int var11 = 1;

      while (var8.size() < var3) {
         var9.add(var10);
         Object var14 = var5.get(var10);
         var14 = var4.indexOf(var14);

         while (var14 != var9.get(0)) {
            var9.add(var14);
            var14 = var5.get(var14);
            var14 = var4.indexOf(var14);
         }

         if (var11++ % 2 != 0) {
            for (int var12 : var9) {
               Object var13 = var6.get(var12);
               var6.set(var12, var7.get(var12));
               var7.set(var12, var13);
            }
         }

         var8.addAll(var9);
         var10 = ((Integer)var9.get(0) + 1) % var3;

         while (var8.contains(var10) && var8.size() < var3) {
            if (++var10 >= var3) {
               var10 = 0;
            }
         }

         var9.clear();
      }

      return new qq(var1.c(), var2.c());
   }
}
