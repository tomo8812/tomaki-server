package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;

public final class iz<E> implements Iterator<E>, fU {
   private final Iterator<? extends E> a;

   public static <E> Iterator<E> a(Iterator<? extends E> var0) {
      if (var0 == null) {
         throw new NullPointerException("Iterator must not be null");
      } else {
         return var0 instanceof fU ? var0 : new iz<>(var0);
      }
   }

   private iz(Iterator<? extends E> var1) {
      this.a = var1;
   }

   @Override
   public final boolean hasNext() {
      return this.a.hasNext();
   }

   @Override
   public final E next() {
      return (E)this.a.next();
   }

   @Override
   public final void remove() {
      throw new UnsupportedOperationException("remove() is not supported");
   }
}
