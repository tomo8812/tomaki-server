package org.valkyrienskies.core.impl.shadow;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;

public final class hT<E> extends hS<E> implements fK<E> {
   private int d = -1;

   public hT(Object var1) {
      super(var1);
   }

   public hT(Object var1, int var2) {
      super(var1, var2);
   }

   public hT(Object var1, int var2, int var3) {
      super(var1, var2, var3);
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
      return (E)Array.get(super.a, super.c);
   }

   @Override
   public final E next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      }

      this.d = super.c;
      return (E)Array.get(super.a, super.c++);
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
   public final void add(Object o) {
      throw new UnsupportedOperationException("add() method is not supported");
   }

   @Override
   public final void set(Object o) {
      if (this.d == -1) {
         throw new IllegalStateException("must call next() or previous() before a call to set()");
      }

      Array.set(super.a, this.d, o);
   }

   @Override
   public final void c() {
      super.c();
      this.d = -1;
   }
}
