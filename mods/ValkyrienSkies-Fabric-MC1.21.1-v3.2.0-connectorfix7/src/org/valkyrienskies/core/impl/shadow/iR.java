package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class iR<E> extends iO<E> {
   private static final long a = -3620001881672L;

   private static <E> iR<E> a(List<E> var0) {
      return new iR<>(var0);
   }

   public iR() {
      super(new ArrayList<>());
   }

   private iR(int var1) {
      super(new ArrayList<>(var1));
   }

   private iR(List<E> var1) {
      super(var1);
   }

   @Override
   public final void add(int index, E element) {
      int var3 = this.c().size();
      if (index > var3) {
         this.c().addAll(Collections.nCopies(index - var3, null));
      }

      this.c().add(index, element);
   }

   @Override
   public final boolean addAll(int index, Collection<? extends E> coll) {
      int var3 = this.c().size();
      boolean var4 = false;
      if (index > var3) {
         this.c().addAll(Collections.nCopies(index - var3, null));
         var4 = true;
      }

      return this.c().addAll(index, coll) | var4;
   }

   @Override
   public final E set(int index, E element) {
      int var3 = this.c().size();
      if (index >= var3) {
         this.c().addAll(Collections.nCopies(index - var3 + 1, null));
      }

      return this.c().set(index, element);
   }
}
