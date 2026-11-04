package org.valkyrienskies.core.impl.shadow;

import java.util.NoSuchElementException;

public class in<E> implements fJ<E> {
   final E[] a;
   final int b;
   private int d;
   int c = 0;

   public in(E... var1) {
      this((E[])var1, 0, var1.length);
   }

   public in(E[] var1, int var2) {
      this((E[])var1, var2, var1.length);
   }

   public in(E[] var1, int var2, int var3) {
      if (var2 < 0) {
         throw new ArrayIndexOutOfBoundsException("Start index must not be less than zero");
      }

      if (var3 > var1.length) {
         throw new ArrayIndexOutOfBoundsException("End index must not be greater than the array length");
      }

      if (var2 > var1.length) {
         throw new ArrayIndexOutOfBoundsException("Start index must not be greater than the array length");
      }

      if (var3 < var2) {
         throw new IllegalArgumentException("End index must not be less than start index");
      }

      this.a = (E[])var1;
      this.b = var2;
      this.d = var3;
      this.c = var2;
   }

   @Override
   public boolean hasNext() {
      return this.c < this.d;
   }

   @Override
   public E next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.a[this.c++];
      }
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException("remove() method is not supported for an ObjectArrayIterator");
   }

   private E[] b() {
      return this.a;
   }

   public final int a() {
      return this.b;
   }

   private int d() {
      return this.d;
   }

   @Override
   public void c() {
      this.c = this.b;
   }
}
