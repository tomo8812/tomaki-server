package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class hU<E> implements Iterator<E> {
   private final Iterator<? extends E> a;
   private final long b;
   private final long c;
   private long d;

   public hU(Iterator<? extends E> var1, long var2, long var4) {
      if (var1 == null) {
         throw new NullPointerException("Iterator must not be null");
      }

      if (var2 < 0L) {
         throw new IllegalArgumentException("Offset parameter must not be negative.");
      }

      if (var4 < 0L) {
         throw new IllegalArgumentException("Max parameter must not be negative.");
      }

      this.a = var1;
      this.b = var2;
      this.c = var4;
      this.d = 0L;

      for (hU var6 = this; var6.d < var6.b && var6.a.hasNext(); var6.d++) {
         var6.a.next();
      }
   }

   private void a() {
      while (this.d < this.b && this.a.hasNext()) {
         this.a.next();
         this.d++;
      }
   }

   @Override
   public final boolean hasNext() {
      return !this.b() ? false : this.a.hasNext();
   }

   private boolean b() {
      return this.d - this.b + 1L <= this.c;
   }

   @Override
   public final E next() {
      if (!this.b()) {
         throw new NoSuchElementException();
      }

      Object var1 = this.a.next();
      this.d++;
      return (E)var1;
   }

   @Override
   public final void remove() {
      if (this.d <= this.b) {
         throw new IllegalStateException("remove() can not be called before calling next()");
      }

      this.a.remove();
   }
}
