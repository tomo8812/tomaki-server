package org.valkyrienskies.core.impl.shadow;

import java.util.NoSuchElementException;

public final class iv<E> implements fK<E> {
   private boolean a = true;
   private boolean b = false;
   private boolean c = false;
   private E d;

   public iv(E var1) {
      this.d = (E)var1;
   }

   @Override
   public final boolean hasNext() {
      return this.a && !this.c;
   }

   @Override
   public final boolean hasPrevious() {
      return !this.a && !this.c;
   }

   @Override
   public final int nextIndex() {
      return this.a ? 0 : 1;
   }

   @Override
   public final int previousIndex() {
      return this.a ? -1 : 0;
   }

   @Override
   public final E next() {
      if (this.a && !this.c) {
         this.a = false;
         this.b = true;
         return this.d;
      } else {
         throw new NoSuchElementException();
      }
   }

   @Override
   public final E previous() {
      if (!this.a && !this.c) {
         this.a = true;
         return this.d;
      } else {
         throw new NoSuchElementException();
      }
   }

   @Override
   public final void remove() {
      if (this.b && !this.c) {
         this.d = null;
         this.c = true;
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public final void add(E obj) {
      throw new UnsupportedOperationException("add() is not supported by this iterator");
   }

   @Override
   public final void set(E obj) {
      if (this.b && !this.c) {
         this.d = obj;
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public final void c() {
      this.a = true;
      this.b = false;
   }
}
