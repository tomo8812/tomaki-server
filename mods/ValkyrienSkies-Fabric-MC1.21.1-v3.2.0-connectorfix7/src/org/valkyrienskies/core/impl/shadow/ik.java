package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ik<E> implements fJ<E> {
   private final Collection<? extends E> a;
   private Iterator<? extends E> b;

   public ik(Collection<? extends E> var1) {
      if (var1 == null) {
         throw new NullPointerException("The collection must not be null");
      }

      this.a = var1;
      this.c();
   }

   @Override
   public final boolean hasNext() {
      return this.a.size() > 0;
   }

   @Override
   public final E next() {
      if (this.a.size() == 0) {
         throw new NoSuchElementException("There are no elements for this iterator to loop on");
      }

      if (!this.b.hasNext()) {
         this.c();
      }

      return (E)this.b.next();
   }

   @Override
   public final void remove() {
      this.b.remove();
   }

   @Override
   public final void c() {
      this.b = this.a.iterator();
   }

   private int a() {
      return this.a.size();
   }
}
