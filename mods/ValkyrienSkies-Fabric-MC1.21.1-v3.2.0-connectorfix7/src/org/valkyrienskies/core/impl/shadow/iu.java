package org.valkyrienskies.core.impl.shadow;

import java.util.NoSuchElementException;

public final class iu<E> implements fJ<E> {
   private final boolean a;
   private boolean b = true;
   private boolean c = false;
   private E d;

   public iu(E var1) {
      this((E)var1, true);
   }

   public iu(E var1, boolean var2) {
      this.d = (E)var1;
      this.a = var2;
   }

   @Override
   public final boolean hasNext() {
      return this.b && !this.c;
   }

   @Override
   public final E next() {
      if (this.b && !this.c) {
         this.b = false;
         return this.d;
      } else {
         throw new NoSuchElementException();
      }
   }

   @Override
   public final void remove() {
      if (this.a) {
         if (!this.c && !this.b) {
            this.d = null;
            this.c = true;
         } else {
            throw new IllegalStateException();
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public final void c() {
      this.b = true;
   }
}
