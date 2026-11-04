package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ip<E> implements Iterator<E> {
   private final Deque<Iterator<? extends E>> a = new ArrayDeque<>(8);
   private E b;
   private final fQ<? super E, ? extends E> c;
   private boolean d = false;
   private Iterator<? extends E> e;
   private E f;
   private Iterator<? extends E> g;

   public ip(E var1, fQ<? super E, ? extends E> var2) {
      if (var1 instanceof Iterator) {
         this.e = (Iterator<? extends E>)var1;
      } else {
         this.b = (E)var1;
      }

      this.c = var2;
   }

   private ip(Iterator<? extends E> var1) {
      this.e = var1;
      this.c = null;
   }

   private void a() {
      if (!this.d) {
         if (this.e == null) {
            if (this.b != null) {
               if (this.c == null) {
                  this.a(this.b);
               } else {
                  this.a((E)this.c.a(this.b));
               }

               this.b = null;
               return;
            }
         } else {
            this.a(this.e);
         }
      }
   }

   private void a(E var1) {
      if (var1 instanceof Iterator) {
         this.a((Iterator<? extends E>)var1);
      } else {
         this.f = (E)var1;
         this.d = true;
      }
   }

   private void a(Iterator<? extends E> var1) {
      while (true) {
         if (var1 != this.e) {
            if (this.e != null) {
               this.a.push(this.e);
            }

            this.e = var1;
         }

         while (this.e.hasNext() && !this.d) {
            Object var2 = this.e.next();
            if (this.c != null) {
               var2 = this.c.a((E)var2);
            }

            this.a((E)var2);
         }

         if (this.d || this.a.isEmpty()) {
            return;
         }

         this.e = this.a.pop();
         var1 = this.e;
         this = this;
      }
   }

   @Override
   public final boolean hasNext() {
      this.a();
      return this.d;
   }

   @Override
   public final E next() {
      this.a();
      if (!this.d) {
         throw new NoSuchElementException("No more elements in the iteration");
      }

      this.g = this.e;
      Object var1 = this.f;
      this.f = null;
      this.d = false;
      return (E)var1;
   }

   @Override
   public final void remove() {
      if (this.g == null) {
         throw new IllegalStateException("Iterator remove() cannot be called at this time");
      }

      this.g.remove();
      this.g = null;
   }
}
