package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class id<E> implements Iterator<E> {
   private Iterator<? extends E> a;
   private fF<? super E> b;
   private E c;
   private boolean d = false;

   public id() {
   }

   private id(Iterator<? extends E> var1) {
      this.a = var1;
   }

   public id(Iterator<? extends E> var1, fF<? super E> var2) {
      this.a = var1;
      this.b = var2;
   }

   @Override
   public boolean hasNext() {
      return this.d || this.c();
   }

   @Override
   public E next() {
      if (!this.d && !this.c()) {
         throw new NoSuchElementException();
      }

      this.d = false;
      return this.c;
   }

   @Override
   public void remove() {
      if (this.d) {
         throw new IllegalStateException("remove() cannot be called");
      }

      this.a.remove();
   }

   private Iterator<? extends E> a() {
      return this.a;
   }

   private void a(Iterator<? extends E> var1) {
      this.a = var1;
      this.c = null;
      this.d = false;
   }

   private fF<? super E> b() {
      return this.b;
   }

   private void a(fF<? super E> var1) {
      this.b = var1;
      this.c = null;
      this.d = false;
   }

   private boolean c() {
      while (this.a.hasNext()) {
         Object var1 = this.a.next();
         if (this.b.a((E)var1)) {
            this.c = (E)var1;
            this.d = true;
            return true;
         }
      }

      return false;
   }
}
