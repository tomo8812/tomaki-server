package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class iQ<E> extends iO<E> implements eZ<E> {
   private static final long a = -2218010673611160319L;

   public static <E> iQ<E> a(List<E> var0) {
      return new iQ<>(var0);
   }

   private iQ(List<E> var1) {
      super(var1);
   }

   @Override
   public final boolean add(E object) {
      throw new UnsupportedOperationException("List is fixed size");
   }

   @Override
   public final void add(int index, E object) {
      throw new UnsupportedOperationException("List is fixed size");
   }

   @Override
   public final boolean addAll(Collection<? extends E> coll) {
      throw new UnsupportedOperationException("List is fixed size");
   }

   @Override
   public final boolean addAll(int index, Collection<? extends E> coll) {
      throw new UnsupportedOperationException("List is fixed size");
   }

   @Override
   public final void clear() {
      throw new UnsupportedOperationException("List is fixed size");
   }

   @Override
   public final E get(int index) {
      return this.c().get(index);
   }

   @Override
   public final int indexOf(Object object) {
      return this.c().indexOf(object);
   }

   @Override
   public final Iterator<E> iterator() {
      return iz.a(this.c().iterator());
   }

   @Override
   public final int lastIndexOf(Object object) {
      return this.c().lastIndexOf(object);
   }

   @Override
   public final ListIterator<E> listIterator() {
      return new iQ.a(this.c().listIterator(0));
   }

   @Override
   public final ListIterator<E> listIterator(int index) {
      return new iQ.a(this.c().listIterator(index));
   }

   @Override
   public final E remove(int index) {
      throw new UnsupportedOperationException("List is fixed size");
   }

   @Override
   public final boolean remove(Object object) {
      throw new UnsupportedOperationException("List is fixed size");
   }

   @Override
   public final boolean removeAll(Collection<?> coll) {
      throw new UnsupportedOperationException("List is fixed size");
   }

   @Override
   public final boolean retainAll(Collection<?> coll) {
      throw new UnsupportedOperationException("List is fixed size");
   }

   @Override
   public final E set(int index, E object) {
      return this.c().set(index, object);
   }

   @Override
   public final List<E> subList(int fromIndex, int toIndex) {
      List var3 = this.c().subList(fromIndex, toIndex);
      return new iQ<>(var3);
   }

   @Override
   public final boolean a() {
      return true;
   }

   @Override
   public final int b() {
      return this.size();
   }

   class a extends hO<E> {
      protected a(ListIterator<E> var2) {
         super(var2);
      }

      @Override
      public final void remove() {
         throw new UnsupportedOperationException("List is fixed size");
      }

      @Override
      public final void add(Object object) {
         throw new UnsupportedOperationException("List is fixed size");
      }
   }
}
