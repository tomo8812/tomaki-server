package org.valkyrienskies.core.impl.shadow;

import java.util.HashMap;
import java.util.Map;

public final class us {
   private final Map<Long, us.a> a = new HashMap<>();

   public us(uq var1) {
      int var2 = var1.b;
      int var3 = var1.c;

      for (int var4 = 0; var4 < var2; var4++) {
         for (int var5 = 0; var5 < var3; var5++) {
            Long var6 = var1.a(var4, var5).a;
            if (this.a.get(var6) != null) {
               throw new oX();
            }

            this.a.put(var6, new us.a(var4, var5));
         }
      }
   }

   public final us.a a(ue var1) {
      return this.a.get(var1.a);
   }

   public static class a {
      final int a;
      final int b;

      public a(int var1, int var2) {
         this.a = var1;
         this.b = var2;
      }

      public final int a() {
         return this.a;
      }

      public final int b() {
         return this.b;
      }
   }
}
