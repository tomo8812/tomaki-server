package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Iterator;

public final class gC<E> extends gv<E> implements fU {
   private static final long a = -239892006883819945L;

   public static <T> Collection<T> b(Collection<? extends T> var0) {
      return var0 instanceof fU ? var0 : new gC(var0);
   }

   private gC(Collection<? extends E> var1) {
      super(var1);
   }

   @Override
   public final Iterator<E> iterator() {
      return iz.a(this.f().iterator());
   }

   @Override
   public final boolean add(E object) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean addAll(Collection<? extends E> coll) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean remove(Object object) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean removeAll(Collection<?> coll) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean retainAll(Collection<?> coll) {
      throw new UnsupportedOperationException();
   }
}
