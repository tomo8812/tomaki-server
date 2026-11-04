package org.valkyrienskies.core.impl.shadow;

import java.util.Set;

public class kz<E> extends gA<E> implements Set<E> {
   private static final long b = 306127383500410386L;

   public static <E> kz<E> a(Set<E> var0, fQ<? super E, ? extends E> var1) {
      return new kz<>(var0, var1);
   }

   private static <E> Set<E> b(Set<E> var0, fQ<? super E, ? extends E> var1) {
      kz var2 = new kz(var0, var1);
      if (var0.size() > 0) {
         Object[] var3 = var0.toArray();
         var0.clear();
         Object[] var6 = var3;
         int var7 = var3.length;

         for (int var4 = 0; var4 < var7; var4++) {
            Object var5 = var6[var4];
            var2.f().add(var1.a(var5));
         }
      }

      return var2;
   }

   protected kz(Set<E> var1, fQ<? super E, ? extends E> var2) {
      super(var1, var2);
   }

   @Override
   public boolean equals(Object object) {
      return object == this || this.f().equals(object);
   }

   @Override
   public int hashCode() {
      return this.f().hashCode();
   }
}
