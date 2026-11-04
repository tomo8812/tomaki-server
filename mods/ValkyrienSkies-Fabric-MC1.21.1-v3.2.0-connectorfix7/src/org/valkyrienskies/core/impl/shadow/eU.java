package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.EmptyStackException;

@Deprecated
public final class eU<E> extends ArrayList<E> {
   private static final long a = 2130079159931574599L;

   public eU() {
   }

   private eU(int var1) {
      super(var1);
   }

   private boolean a() {
      return this.isEmpty();
   }

   private E b() {
      int var1;
      if ((var1 = this.size()) <= 0) {
         throw new EmptyStackException();
      } else {
         return this.get(var1 - 1);
      }
   }

   private E a(int var1) {
      if ((var1 = this.size() - var1 - 1) < 0) {
         throw new EmptyStackException();
      } else {
         return this.get(var1);
      }
   }

   private E c() {
      int var1;
      if ((var1 = this.size()) <= 0) {
         throw new EmptyStackException();
      } else {
         return this.remove(var1 - 1);
      }
   }

   private E a(E var1) {
      this.add((E)var1);
      return (E)var1;
   }

   private int b(Object var1) {
      int var2 = this.size() - 1;

      for (int var3 = 1; var2 >= 0; var3++) {
         Object var4 = this.get(var2);
         if (var1 == null && var4 == null || var1 != null && var1.equals(var4)) {
            return var3;
         }

         var2--;
      }

      return -1;
   }
}
