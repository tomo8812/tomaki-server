package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public final class kC<E> extends kp<E> implements fU {
   private static final long a = 6499119872185240161L;

   public static <E> Set<E> a(Set<? extends E> var0) {
      return var0 instanceof fU ? var0 : new kC<>(var0);
   }

   private kC(Set<? extends E> var1) {
      super(var1);
   }

   @Override
   public final Iterator<E> iterator() {
      return iz.a(this.c().iterator());
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
