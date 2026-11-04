package org.valkyrienskies.core.impl.shadow;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;

public class hS<E> implements fJ<E> {
   final Object a;
   final int b;
   private int d;
   int c = 0;

   public hS(Object var1) {
      this(var1, 0);
   }

   public hS(Object var1, int var2) {
      this(var1, var2, Array.getLength(var1));
   }

   public hS(Object var1, int var2, int var3) {
      this.a = var1;
      this.b = var2;
      this.d = var3;
      this.c = var2;
      var1 = Array.getLength(var1);
      a(var2, var1, "start");
      a(var3, var1, "end");
      if (var3 < var2) {
         throw new IllegalArgumentException("End index must not be less than start index.");
      }
   }

   private static void a(int var0, int var1, String var2) {
      if (var0 > var1) {
         throw new ArrayIndexOutOfBoundsException("Attempt to make an ArrayIterator that " + var2 + "s beyond the end of the array. ");
      }

      if (var0 < 0) {
         throw new ArrayIndexOutOfBoundsException("Attempt to make an ArrayIterator that " + var2 + "s before the start of the array. ");
      }
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
         return (E)Array.get(this.a, this.c++);
      }
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException("remove() method is not supported");
   }

   private Object a() {
      return this.a;
   }

   private int b() {
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
