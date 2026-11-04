package org.valkyrienskies.core.impl.shadow;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public final class DJ<T> extends AbstractList<T> {
   final DK<T> a = new DK<>(null);
   DK<T> b;

   public DJ() {
      this.b = this.a;
   }

   private DJ(Collection<? extends T> var1) {
      ArrayList var2 = new ArrayList(var1.size());

      for (Object var3 : var1) {
         var2.add(new DK<>(var3));
      }

      this.b = DH.a(var2, this.a);
   }

   DK<T> a(int var1) {
      if (var1 >= 0 && var1 < this.b.b) {
         var1 = var1;
         DK var2 = this.b;

         while (var1 != ((DK)var2.j).b) {
            if (var1 < ((DK)var2.j).b) {
               var2 = (DK)var2.j;
            } else {
               var1 -= ((DK)var2.j).b + 1;
               var2 = (DK)var2.k;
            }
         }

         return var2;
      } else {
         throw new IndexOutOfBoundsException("Index " + var1 + " is not in the range [0, " + this.b.b + ")");
      }
   }

   @Override
   public final T get(int index) {
      return this.a(index).a;
   }

   @Override
   public final int size() {
      return this.b.b;
   }

   @Override
   public final boolean isEmpty() {
      return this.b == this.a;
   }

   @Override
   public final T set(int index, T value) {
      this.modCount++;
      DK var4;
      Object var3 = (var4 = this.a(index)).a;
      var4.a = value;
      return (T)var3;
   }

   @Override
   public final void add(int index, T value) {
      if (index >= 0 && index <= this.b.b) {
         this.modCount++;
         DK var6;
         (var6 = new DK(value)).j = (N)this.a;
         var6.k = (N)this.a;
         if (this.b.d()) {
            this.b = var6;
            var6.l = false;
         } else {
            var6.l = true;
            if (index < this.b.b) {
               DK var3;
               if (((DK)(var3 = this.a(index)).j).d()) {
                  var3.j = (N)var6;
                  var6.i = (N)var3;
               } else {
                  DK var4;
                  (var4 = (DK)var3.h()).k = (N)var6;
                  var6.i = (N)var4;
               }
            } else {
               DK var5;
               (var5 = (DK)this.b.g()).k = (N)var6;
               var6.i = (N)var5;
            }

            this.b = (DK<T>)var6.j();
         }
      } else {
         throw new IndexOutOfBoundsException("Index " + index + " is not in the range [0, " + this.b.b + "]");
      }
   }

   @Override
   public final T remove(int index) {
      DK var2 = this.a(index);
      this.modCount++;
      this.b = (DK<T>)var2.l();
      return var2.a;
   }

   @Override
   public final boolean addAll(int index, Collection<? extends T> values) {
      if (index < 0 || index > this.b.b) {
         throw new IndexOutOfBoundsException("Index " + index + " is not in the range [0, " + this.b.b + "]");
      }

      if (index >= this.b.b) {
         return this.addAll(values);
      }

      this.modCount++;
      int var3;
      if ((var3 = values.size()) == 0) {
         return false;
      }

      ArrayList var4 = new ArrayList(var3);

      for (Object var5 : values) {
         var4.add(new DK<>(var5));
      }

      DK[] var7 = this.b.a(this.a(index));
      if (var3 == 1) {
         this.b = var7[0].a(var7[1], (DK)var4.get(0));
      } else {
         DK var8 = DK.a(var4.subList(1, var3 - 1), this.a);
         this.b = var7[0].a(var8, (DK)var4.get(0)).a(var7[1], (DK)var4.get(var3 - 1));
      }

      return true;
   }

   @Override
   public final boolean addAll(Collection<? extends T> values) {
      this.modCount++;
      int var2;
      if ((var2 = values.size()) == 0) {
         return false;
      }

      ArrayList var3 = new ArrayList(var2);

      for (Object var4 : values) {
         var3.add(new DK<>(var4));
      }

      DK var6 = DK.a(var3.subList(1, var2), this.a);
      this.b = this.b.a(var6, (DK)var3.get(0));
      return true;
   }

   @Override
   protected final void removeRange(int startIndex, int endIndex) {
      if (startIndex != endIndex) {
         this.modCount++;
         DK var5;
         if (endIndex == this.b.b) {
            var5 = this.a;
         } else {
            DK[] var3 = this.b.a(this.a(endIndex));
            this.b = var3[0];
            var5 = var3[1];
         }

         if (startIndex == 0) {
            this.b = var5;
            return;
         }

         DK var6 = this.a(startIndex - 1);
         DK var4 = this.b.a(var6)[0];
         this.b = var4.a(var5, var6);
      }
   }

   @Override
   public final void clear() {
      this.modCount++;
      this.b = this.a;
   }

   @Override
   public final Iterator<T> iterator() {
      return new DJ.a();
   }

   @Override
   public final ListIterator<T> listIterator(int index) {
      if (index >= 0 && index <= this.b.b) {
         return new DJ.b(index);
      } else {
         throw new IndexOutOfBoundsException("Index " + index + " is not in the range [0, " + this.b.b + "]");
      }
   }

   class a implements Iterator<T> {
      private int a = DJ.this.modCount;
      private DK<T> b;
      private DK<T> c;
      private boolean d;

      a() {
         if (DJ.this.b.d()) {
            this.c = null;
         } else {
            this.c = (DK<T>)DJ.this.b.f();
         }
      }

      @Override
      public final boolean hasNext() {
         return this.c != null;
      }

      @Override
      public final T next() {
         if (this.c == null) {
            throw new NoSuchElementException("Reached the end of the list");
         }

         if (DJ.this.modCount != this.a) {
            throw new ConcurrentModificationException();
         }

         this.d = true;
         this.b = this.c;
         this.c = (DK<T>)this.c.i();
         return this.b.a;
      }

      @Override
      public final void remove() {
         if (this.b == null) {
            if (!this.d) {
               throw new IllegalStateException("Must call next() before calling remove()");
            } else {
               throw new IllegalStateException("Already removed this element");
            }
         } else {
            if (DJ.this.modCount != this.a) {
               throw new ConcurrentModificationException();
            }

            DJ.this.b = (DK<T>)this.b.l();
            this.b = null;
            DJ.this.modCount++;
            this.a = DJ.this.modCount;
         }
      }
   }

   class b implements ListIterator<T> {
      private int a = DJ.this.modCount;
      private int b;
      private DK<T> c;
      private DK<T> d;
      private boolean e;
      private boolean f;
      private boolean g;

      b(int var2) {
         this.b = var2;
         if (var2 > 0) {
            this.d = DJ.this.a(var2 - 1);
            this.c = (DK<T>)this.d.i();
         } else {
            this.d = null;
            if (DJ.this.b.b > 0) {
               this.c = (DK<T>)DJ.this.b.f();
            } else {
               this.c = null;
            }
         }
      }

      @Override
      public final boolean hasNext() {
         if (this.a != DJ.this.modCount) {
            throw new ConcurrentModificationException();
         } else {
            return this.c != null;
         }
      }

      @Override
      public final T next() {
         if (this.c == null) {
            throw new NoSuchElementException("Reached the end of the list");
         }

         if (this.a != DJ.this.modCount) {
            throw new ConcurrentModificationException();
         }

         this.e = true;
         this.f = true;
         this.g = false;
         this.b++;
         this.d = this.c;
         this.c = (DK<T>)this.c.i();
         return this.d.a;
      }

      @Override
      public final int nextIndex() {
         if (this.a != DJ.this.modCount) {
            throw new ConcurrentModificationException();
         } else {
            return this.b;
         }
      }

      @Override
      public final boolean hasPrevious() {
         if (this.a != DJ.this.modCount) {
            throw new ConcurrentModificationException();
         } else {
            return this.d != null;
         }
      }

      @Override
      public final T previous() {
         if (this.d == null) {
            throw new NoSuchElementException("Reached the beginning of the list");
         }

         if (this.a != DJ.this.modCount) {
            throw new ConcurrentModificationException();
         }

         this.e = true;
         this.f = false;
         this.g = false;
         this.b--;
         this.c = this.d;
         this.d = (DK<T>)this.d.h();
         return this.c.a;
      }

      @Override
      public final int previousIndex() {
         return this.b - 1;
      }

      @Override
      public final void set(T value) {
         if (!this.e) {
            throw new IllegalStateException("Must call next() or previous() before calling \"set\"");
         }

         if (this.g) {
            throw new IllegalStateException("Already modified the list at this position");
         }

         if (this.a != DJ.this.modCount) {
            throw new ConcurrentModificationException();
         }

         if (this.f) {
            this.d.a = value;
         } else {
            this.c.a = value;
         }

         DJ.this.modCount++;
         this.a = DJ.this.modCount;
      }

      @Override
      public final void add(T value) {
         if (this.g) {
            throw new IllegalStateException("Already modified the list at this position");
         }

         if (this.a != DJ.this.modCount) {
            throw new ConcurrentModificationException();
         }

         DK var2;
         (var2 = new DK(value)).j = (N)DJ.this.a;
         var2.k = (N)DJ.this.a;
         var2.l = true;
         if (this.c != null && ((DK)this.c.j).d()) {
            this.c.j = (N)var2;
            var2.i = (N)this.c;
         } else if (this.d != null) {
            this.d.k = (N)var2;
            var2.i = (N)this.d;
         } else {
            DJ.this.b = var2;
         }

         this.d = var2;
         DJ.this.b = (DK<T>)var2.j();
         this.b++;
         this.g = true;
         DJ.this.modCount++;
         this.a = DJ.this.modCount;
      }

      @Override
      public final void remove() {
         if (!this.e) {
            throw new IllegalStateException("Must call next() or previous() before calling remove()");
         }

         if (this.g) {
            throw new IllegalStateException("Already modified the list at this position");
         }

         if (this.a != DJ.this.modCount) {
            throw new ConcurrentModificationException();
         }

         if (this.f) {
            DK var1 = (DK)this.d.h();
            DJ.this.b = (DK<T>)this.d.l();
            this.d = var1;
         } else {
            DK var2 = (DK)this.c.i();
            DJ.this.b = (DK<T>)this.c.l();
            this.c = var2;
         }

         this.g = true;
         DJ.this.modCount++;
         this.a = DJ.this.modCount;
      }
   }
}
