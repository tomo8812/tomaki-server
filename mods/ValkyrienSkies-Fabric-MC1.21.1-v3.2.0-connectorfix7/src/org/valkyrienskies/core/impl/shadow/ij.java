package org.valkyrienskies.core.impl.shadow;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public final class ij<E> implements fK<E> {
   private static final String a = "ListIteratorWrapper does not support optional operations of ListIterator.";
   private static final String b = "Cannot remove element at index {0}.";
   private final Iterator<? extends E> c;
   private final List<E> d = new ArrayList<>();
   private int e = 0;
   private int f = 0;
   private boolean g;

   public ij(Iterator<? extends E> var1) {
      if (var1 == null) {
         throw new NullPointerException("Iterator must not be null");
      }

      this.c = var1;
   }

   @Override
   public final void add(E obj) {
      if (this.c instanceof ListIterator) {
         ListIterator var2;
         (var2 = (ListIterator)this.c).add(obj);
      } else {
         throw new UnsupportedOperationException("ListIteratorWrapper does not support optional operations of ListIterator.");
      }
   }

   @Override
   public final boolean hasNext() {
      return this.e != this.f && !(this.c instanceof ListIterator) ? true : this.c.hasNext();
   }

   @Override
   public final boolean hasPrevious() {
      ListIterator var1;
      return this.c instanceof ListIterator ? (var1 = (ListIterator)this.c).hasPrevious() : this.e > 0;
   }

   @Override
   public final E next() {
      if (this.c instanceof ListIterator) {
         return (E)this.c.next();
      } else if (this.e < this.f) {
         this.e++;
         return this.d.get(this.e - 1);
      } else {
         Object var1 = this.c.next();
         this.d.add((E)var1);
         this.e++;
         this.f++;
         this.g = true;
         return (E)var1;
      }
   }

   @Override
   public final int nextIndex() {
      ListIterator var1;
      return this.c instanceof ListIterator ? (var1 = (ListIterator)this.c).nextIndex() : this.e;
   }

   @Override
   public final E previous() {
      if (this.c instanceof ListIterator) {
         ListIterator var1;
         return (E)(var1 = (ListIterator)this.c).previous();
      }

      if (this.e == 0) {
         throw new NoSuchElementException();
      }

      this.g = this.f == this.e;
      return this.d.get(--this.e);
   }

   @Override
   public final int previousIndex() {
      ListIterator var1;
      return this.c instanceof ListIterator ? (var1 = (ListIterator)this.c).previousIndex() : this.e - 1;
   }

   @Override
   public final void remove() {
      if (this.c instanceof ListIterator) {
         this.c.remove();
      } else {
         int var1 = this.e;
         if (this.e == this.f) {
            var1--;
         }

         if (this.g && this.f - this.e <= 1) {
            this.c.remove();
            this.d.remove(var1);
            this.e = var1;
            this.f--;
            this.g = false;
         } else {
            throw new IllegalStateException(MessageFormat.format("Cannot remove element at index {0}.", var1));
         }
      }
   }

   @Override
   public final void set(E obj) {
      if (this.c instanceof ListIterator) {
         ListIterator var2;
         (var2 = (ListIterator)this.c).set(obj);
      } else {
         throw new UnsupportedOperationException("ListIteratorWrapper does not support optional operations of ListIterator.");
      }
   }

   @Override
   public final void c() {
      if (!(this.c instanceof ListIterator)) {
         this.e = 0;
      } else {
         ListIterator var1 = (ListIterator)this.c;

         while (var1.previousIndex() >= 0) {
            var1.previous();
         }
      }
   }
}
