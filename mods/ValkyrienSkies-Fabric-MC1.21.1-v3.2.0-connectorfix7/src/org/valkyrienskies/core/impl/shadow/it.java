package org.valkyrienskies.core.impl.shadow;

import java.util.List;
import java.util.ListIterator;

public final class it<E> implements fK<E> {
   private final List<E> a;
   private ListIterator<E> b;
   private boolean c = true;

   public it(List<E> var1) {
      if (var1 == null) {
         throw new NullPointerException("List must not be null.");
      }

      this.a = var1;
      this.b = var1.listIterator(var1.size());
   }

   @Override
   public final boolean hasNext() {
      return this.b.hasPrevious();
   }

   @Override
   public final E next() {
      Object var1 = this.b.previous();
      this.c = true;
      return (E)var1;
   }

   @Override
   public final int nextIndex() {
      return this.b.previousIndex();
   }

   @Override
   public final boolean hasPrevious() {
      return this.b.hasNext();
   }

   @Override
   public final E previous() {
      Object var1 = this.b.next();
      this.c = true;
      return (E)var1;
   }

   @Override
   public final int previousIndex() {
      return this.b.nextIndex();
   }

   @Override
   public final void remove() {
      if (!this.c) {
         throw new IllegalStateException("Cannot remove from list until next() or previous() called");
      }

      this.b.remove();
   }

   @Override
   public final void set(E obj) {
      if (!this.c) {
         throw new IllegalStateException("Cannot set to list until next() or previous() called");
      }

      this.b.set(obj);
   }

   @Override
   public final void add(E obj) {
      if (!this.c) {
         throw new IllegalStateException("Cannot add to list until next() or previous() called");
      }

      this.c = false;
      this.b.add(obj);
      this.b.previous();
   }

   @Override
   public final void c() {
      this.b = this.a.listIterator(this.a.size());
   }
}
