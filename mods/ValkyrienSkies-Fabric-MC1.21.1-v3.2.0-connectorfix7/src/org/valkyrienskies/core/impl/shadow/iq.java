package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class iq<E> implements Iterator<E> {
   private final Iterator<? extends E> a;
   private boolean b = false;
   private boolean c = false;
   private E d;

   public static <E> iq<E> a(Iterator<? extends E> var0) {
      if (var0 == null) {
         throw new NullPointerException("Iterator must not be null");
      }

      iq var1;
      return var0 instanceof iq ? (var1 = (iq)var0) : new iq<>(var0);
   }

   private iq(Iterator<? extends E> var1) {
      this.a = var1;
   }

   private void a() {
      if (!this.b && !this.c) {
         if (this.a.hasNext()) {
            this.d = (E)this.a.next();
            this.c = true;
         } else {
            this.b = true;
            this.d = null;
            this.c = false;
         }
      }
   }

   @Override
   public final boolean hasNext() {
      if (this.b) {
         return false;
      } else {
         return this.c ? true : this.a.hasNext();
      }
   }

   private E b() {
      this.a();
      return this.b ? null : this.d;
   }

   private E c() {
      this.a();
      if (this.b) {
         throw new NoSuchElementException();
      } else {
         return this.d;
      }
   }

   @Override
   public final E next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      }

      Object var1 = this.c ? this.d : this.a.next();
      this.d = null;
      this.c = false;
      return (E)var1;
   }

   @Override
   public final void remove() {
      if (this.c) {
         throw new IllegalStateException("peek() or element() called before remove()");
      }

      this.a.remove();
   }
}
