package org.valkyrienskies.core.impl.shadow;

import java.util.NoSuchElementException;

public final class io<E> extends in<E> implements fK<E> {
   private int d = -1;

   public io(E... var1) {
      super((E[])var1);
   }

   public io(E[] var1, int var2) {
      super((E[])var1, var2);
   }

   public io(E[] var1, int var2, int var3) {
      super((E[])var1, var2, var3);
   }

   @Override
   public final boolean hasPrevious() {
      return super.c > super.b;
   }

   @Override
   public final E previous() {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      }

      this.d = --super.c;
      return super.a[super.c];
   }

   @Override
   public final E next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      }

      this.d = super.c;
      return super.a[super.c++];
   }

   @Override
   public final int nextIndex() {
      return super.c - super.b;
   }

   @Override
   public final int previousIndex() {
      return super.c - super.b - 1;
   }

   @Override
   public final void add(E obj) {
      throw new UnsupportedOperationException("add() method is not supported");
   }

   @Override
   public final void set(E obj) {
      if (this.d == -1) {
         throw new IllegalStateException("must call next() or previous() before a call to set()");
      }

      super.a[this.d] = obj;
   }

   @Override
   public final void c() {
      super.c();
      this.d = -1;
   }
}
