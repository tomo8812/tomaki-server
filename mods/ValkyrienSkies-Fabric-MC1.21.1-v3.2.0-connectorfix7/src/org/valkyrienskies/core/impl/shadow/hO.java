package org.valkyrienskies.core.impl.shadow;

import java.util.ListIterator;

public class hO<E> implements ListIterator<E> {
   private final ListIterator<E> a;

   public hO(ListIterator<E> var1) {
      if (var1 == null) {
         throw new NullPointerException("ListIterator must not be null");
      }

      this.a = var1;
   }

   protected final ListIterator<E> a() {
      return this.a;
   }

   @Override
   public boolean hasNext() {
      return this.a.hasNext();
   }

   @Override
   public E next() {
      return this.a.next();
   }

   @Override
   public int nextIndex() {
      return this.a.nextIndex();
   }

   @Override
   public boolean hasPrevious() {
      return this.a.hasPrevious();
   }

   @Override
   public E previous() {
      return this.a.previous();
   }

   @Override
   public int previousIndex() {
      return this.a.previousIndex();
   }

   @Override
   public void remove() {
      this.a.remove();
   }

   @Override
   public void set(E obj) {
      this.a.set(obj);
   }

   @Override
   public void add(E obj) {
      this.a.add(obj);
   }
}
