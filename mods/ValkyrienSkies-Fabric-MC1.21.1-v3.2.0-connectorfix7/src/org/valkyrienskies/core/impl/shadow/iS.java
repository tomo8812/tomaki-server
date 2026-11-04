package org.valkyrienskies.core.impl.shadow;

import java.util.List;

public final class iS<E> extends iO<E> {
   private static final long a = -1708388017160694542L;
   private final fh<? extends E> b;

   public static <E> iS<E> a(List<E> var0, fh<? extends E> var1) {
      return new iS<>(var0, var1);
   }

   private iS(List<E> var1, fh<? extends E> var2) {
      super(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("Factory must not be null");
      }

      this.b = var2;
   }

   @Override
   public final E get(int index) {
      int var2 = this.c().size();
      if (index < var2) {
         Object var5;
         if ((var5 = this.c().get(index)) == null) {
            var5 = this.b.a();
            this.c().set(index, (E)var5);
            return (E)var5;
         } else {
            return (E)var5;
         }
      } else {
         for (int var3 = var2; var3 < index; var3++) {
            this.c().add(null);
         }

         Object var4 = this.b.a();
         this.c().add((E)var4);
         return (E)var4;
      }
   }

   @Override
   public final List<E> subList(int fromIndex, int toIndex) {
      List var3 = this.c().subList(fromIndex, toIndex);
      return new iS<>(var3, this.b);
   }
}
