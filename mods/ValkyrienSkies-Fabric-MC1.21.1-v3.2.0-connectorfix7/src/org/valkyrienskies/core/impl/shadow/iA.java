package org.valkyrienskies.core.impl.shadow;

import java.util.ListIterator;

public final class iA<E> implements ListIterator<E>, fU {
   private final ListIterator<? extends E> a;

   public static <E> ListIterator<E> a(ListIterator<? extends E> var0) {
      if (var0 == null) {
         throw new NullPointerException("ListIterator must not be null");
      } else {
         return var0 instanceof fU ? var0 : new iA<>(var0);
      }
   }

   private iA(ListIterator<? extends E> var1) {
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
   public final int nextIndex() {
      return this.a.nextIndex();
   }

   @Override
   public final boolean hasPrevious() {
      return this.a.hasPrevious();
   }

   @Override
   public final E previous() {
      return (E)this.a.previous();
   }

   @Override
   public final int previousIndex() {
      return this.a.previousIndex();
   }

   @Override
   public final void remove() {
      throw new UnsupportedOperationException("remove() is not supported");
   }

   @Override
   public final void set(E obj) {
      throw new UnsupportedOperationException("set() is not supported");
   }

   @Override
   public final void add(E obj) {
      throw new UnsupportedOperationException("add() is not supported");
   }
}
