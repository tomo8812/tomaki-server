package org.valkyrienskies.core.impl.shadow;

import java.util.ListIterator;
import java.util.NoSuchElementException;

public final class ie<E> implements ListIterator<E> {
   private ListIterator<? extends E> a;
   private fF<? super E> b;
   private E c;
   private boolean d = false;
   private E e;
   private boolean f = false;
   private int g = 0;

   public ie() {
   }

   private ie(ListIterator<? extends E> var1) {
      this.a = var1;
   }

   public ie(ListIterator<? extends E> var1, fF<? super E> var2) {
      this.a = var1;
      this.b = var2;
   }

   private ie(fF<? super E> var1) {
      this.b = var1;
   }

   @Override
   public final void add(E o) {
      throw new UnsupportedOperationException("FilterListIterator.add(Object) is not supported.");
   }

   @Override
   public final boolean hasNext() {
      return this.d || this.d();
   }

   @Override
   public final boolean hasPrevious() {
      return this.f || this.f();
   }

   @Override
   public final E next() {
      if (!this.d && !this.d()) {
         throw new NoSuchElementException();
      }

      this.g++;
      Object var1 = this.c;
      this.c();
      return (E)var1;
   }

   @Override
   public final int nextIndex() {
      return this.g;
   }

   @Override
   public final E previous() {
      if (!this.f && !this.f()) {
         throw new NoSuchElementException();
      }

      this.g--;
      Object var1 = this.e;
      this.e();
      return (E)var1;
   }

   @Override
   public final int previousIndex() {
      return this.g - 1;
   }

   @Override
   public final void remove() {
      throw new UnsupportedOperationException("FilterListIterator.remove() is not supported.");
   }

   @Override
   public final void set(E o) {
      throw new UnsupportedOperationException("FilterListIterator.set(Object) is not supported.");
   }

   private ListIterator<? extends E> a() {
      return this.a;
   }

   private void a(ListIterator<? extends E> var1) {
      this.a = var1;
   }

   private fF<? super E> b() {
      return this.b;
   }

   private void a(fF<? super E> var1) {
      this.b = var1;
   }

   private void c() {
      this.c = null;
      this.d = false;
   }

   private boolean d() {
      if (this.f) {
         this.e();
         if (!this.d()) {
            return false;
         }

         this.c();
      }

      if (this.a == null) {
         return false;
      }

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

   private void e() {
      this.e = null;
      this.f = false;
   }

   private boolean f() {
      if (this.d) {
         this.c();
         if (!this.f()) {
            return false;
         }

         this.e();
      }

      if (this.a == null) {
         return false;
      }

      while (this.a.hasPrevious()) {
         Object var1 = this.a.previous();
         if (this.b.a((E)var1)) {
            this.e = (E)var1;
            this.f = true;
            return true;
         }
      }

      return false;
   }
}
