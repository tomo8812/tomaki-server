package org.valkyrienskies.core.impl.shadow;

import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public final class il<E> implements fK<E> {
   private final List<E> a;
   private ListIterator<E> b;

   public il(List<E> var1) {
      if (var1 == null) {
         throw new NullPointerException("The list must not be null");
      }

      this.a = var1;
      this.a();
   }

   @Override
   public final boolean hasNext() {
      return !this.a.isEmpty();
   }

   @Override
   public final E next() {
      if (this.a.isEmpty()) {
         throw new NoSuchElementException("There are no elements for this iterator to loop on");
      }

      if (!this.b.hasNext()) {
         this.a();
      }

      return this.b.next();
   }

   @Override
   public final int nextIndex() {
      if (this.a.isEmpty()) {
         throw new NoSuchElementException("There are no elements for this iterator to loop on");
      } else {
         return !this.b.hasNext() ? 0 : this.b.nextIndex();
      }
   }

   @Override
   public final boolean hasPrevious() {
      return !this.a.isEmpty();
   }

   @Override
   public final E previous() {
      if (this.a.isEmpty()) {
         throw new NoSuchElementException("There are no elements for this iterator to loop on");
      }

      if (this.b.hasPrevious()) {
         return this.b.previous();
      }

      Object var1 = null;

      while (this.b.hasNext()) {
         var1 = this.b.next();
      }

      this.b.previous();
      return (E)var1;
   }

   @Override
   public final int previousIndex() {
      if (this.a.isEmpty()) {
         throw new NoSuchElementException("There are no elements for this iterator to loop on");
      } else {
         return !this.b.hasPrevious() ? this.a.size() - 1 : this.b.previousIndex();
      }
   }

   @Override
   public final void remove() {
      this.b.remove();
   }

   @Override
   public final void add(E obj) {
      this.b.add(obj);
   }

   @Override
   public final void set(E obj) {
      this.b.set(obj);
   }

   @Override
   public final void c() {
      this.a();
   }

   private void a() {
      this.b = this.a.listIterator();
   }

   private int b() {
      return this.a.size();
   }
}
