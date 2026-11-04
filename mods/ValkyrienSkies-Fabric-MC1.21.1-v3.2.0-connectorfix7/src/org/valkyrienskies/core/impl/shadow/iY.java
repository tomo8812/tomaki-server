package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class iY<E> extends iO<E> implements fU {
   private static final long a = 6595182819922443652L;

   public static <E> List<E> a(List<? extends E> var0) {
      return var0 instanceof fU ? var0 : new iY<>(var0);
   }

   private iY(List<? extends E> var1) {
      super(var1);
   }

   @Override
   public final Iterator<E> iterator() {
      return iz.a(this.c().iterator());
   }

   @Override
   public final boolean add(Object object) {
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

   @Override
   public final ListIterator<E> listIterator() {
      return iA.a(this.c().listIterator());
   }

   @Override
   public final ListIterator<E> listIterator(int index) {
      return iA.a(this.c().listIterator(index));
   }

   @Override
   public final void add(int index, E object) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean addAll(int index, Collection<? extends E> coll) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final E remove(int index) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final E set(int index, E object) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final List<E> subList(int fromIndex, int toIndex) {
      List var3 = this.c().subList(fromIndex, toIndex);
      return new iY<>(var3);
   }
}
