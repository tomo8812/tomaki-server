package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

public final class ic<E> implements Iterator<E> {
   private final Collection<? super E> a;
   private Enumeration<? extends E> b;
   private E c;

   public ic() {
      this(null, null);
   }

   public ic(Enumeration<? extends E> var1) {
      this(var1, null);
   }

   public ic(Enumeration<? extends E> var1, Collection<? super E> var2) {
      this.b = var1;
      this.a = var2;
      this.c = null;
   }

   @Override
   public final boolean hasNext() {
      return this.b.hasMoreElements();
   }

   @Override
   public final E next() {
      this.c = (E)this.b.nextElement();
      return this.c;
   }

   @Override
   public final void remove() {
      if (this.a != null) {
         if (this.c != null) {
            this.a.remove(this.c);
         } else {
            throw new IllegalStateException("next() must have been called for remove() to function");
         }
      } else {
         throw new UnsupportedOperationException("No Collection associated with this Iterator");
      }
   }

   private Enumeration<? extends E> a() {
      return this.b;
   }

   private void a(Enumeration<? extends E> var1) {
      this.b = var1;
   }
}
