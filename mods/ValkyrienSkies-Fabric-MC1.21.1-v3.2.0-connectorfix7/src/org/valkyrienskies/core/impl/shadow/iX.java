package org.valkyrienskies.core.impl.shadow;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public final class iX<E> extends AbstractList<E> {
   private iX.a<E> a;
   private int b;

   public iX() {
   }

   private iX(Collection<? extends E> var1) {
      if (!var1.isEmpty()) {
         this.a = new iX.a<>(var1, (byte)0);
         this.b = var1.size();
      }
   }

   @Override
   public final E get(int index) {
      this.a(index, 0, this.size() - 1);
      return this.a.a(index).g;
   }

   @Override
   public final int size() {
      return this.b;
   }

   @Override
   public final Iterator<E> iterator() {
      return this.listIterator(0);
   }

   @Override
   public final ListIterator<E> listIterator() {
      return this.listIterator(0);
   }

   @Override
   public final ListIterator<E> listIterator(int fromIndex) {
      this.a(fromIndex, 0, this.size());
      return new iX.b<>(this, fromIndex);
   }

   @Override
   public final int indexOf(Object object) {
      if (this.a == null) {
         return -1;
      }

      iX.a var2 = this.a;
      return this.a.a(object, this.a.f);
   }

   @Override
   public final boolean contains(Object object) {
      return this.indexOf(object) >= 0;
   }

   @Override
   public final Object[] toArray() {
      Object[] var1 = new Object[this.size()];
      if (this.a != null) {
         iX.a var2 = this.a;
         this.a.a(var1, this.a.f);
      }

      return var1;
   }

   @Override
   public final void add(int index, E obj) {
      this.modCount++;
      this.a(index, 0, this.size());
      if (this.a == null) {
         this.a = new iX.a<>(index, obj, null, null, (byte)0);
      } else {
         this.a = this.a.a(index, obj);
      }

      this.b++;
   }

   @Override
   public final boolean addAll(Collection<? extends E> c) {
      if (c.isEmpty()) {
         return false;
      }

      this.modCount = this.modCount + c.size();
      iX.a var2 = new iX.a(c, (byte)0);
      iX.a var10001;
      if (this.a == null) {
         var10001 = var2;
      } else {
         int var4 = this.b;
         iX.a var3 = var2;
         var2 = this.a;
         var4 = var4;
         var3 = var3;
         var2 = this.a;
         iX.a var5 = this.a.d();
         iX.a var6 = var3.e();
         if (var3.e > var2.e) {
            iX.a var7 = var2.f();
            ArrayDeque var13 = new ArrayDeque();
            iX.a var8 = var3;
            int var9 = var3.f + var4;
            int var10 = 0;

            while (var8 != null && var8.e > iX.a.a(var7)) {
               var10 = var9;
               var13.push(var8);
               if ((var8 = var8.a) != null) {
                  var9 += var8.f;
               }
            }

            var5.a(var7, null);
            var5.b(var8, var6);
            if (var7 != null) {
               var7.d().b(null, var5);
               var7.f -= var4 - 1;
            }

            if (var8 != null) {
               var8.e().a(null, var5);
               var8.f = var9 - var4 + 1;
            }

            var5.f = var4 - 1 - var10;
            var3.f += var4;
            var8 = var5;

            while (!var13.isEmpty()) {
               (var3 = (iX.a)var13.pop()).a(var8, null);
               var8 = var3.h();
            }

            var10001 = var8;
         } else {
            var3 = var3.g();
            ArrayDeque var20 = new ArrayDeque();
            iX.a var14;
            int var22 = (var14 = var2).f;
            int var23 = 0;

            while (var14 != null && var14.e > iX.a.a(var3)) {
               var23 = var22;
               var20.push(var14);
               if ((var14 = var14.c) != null) {
                  var22 += var14.f;
               }
            }

            var6.b(var3, null);
            var6.a(var14, var5);
            if (var3 != null) {
               var3.e().a(null, var6);
               var3.f++;
            }

            if (var14 != null) {
               var14.d().b(null, var6);
               var14.f = var22 - var4;
            }

            var6.f = var4 - var23;
            var2 = var6;

            while (!var20.isEmpty()) {
               iX.a var24;
               (var24 = (iX.a)var20.pop()).b(var2, null);
               var2 = var24.h();
            }

            var10001 = var2;
         }
      }

      this.a = var10001;
      this.b = this.b + c.size();
      return true;
   }

   @Override
   public final E set(int index, E obj) {
      this.a(index, 0, this.size() - 1);
      iX.a var4;
      Object var3 = (var4 = this.a.a(index)).g;
      obj = obj;
      var4.g = obj;
      return (E)var3;
   }

   @Override
   public final E remove(int index) {
      this.modCount++;
      this.a(index, 0, this.size() - 1);
      Object var2 = this.get(index);
      this.a = this.a.b(index);
      this.b--;
      return (E)var2;
   }

   @Override
   public final void clear() {
      this.modCount++;
      this.a = null;
      this.b = 0;
   }

   private void a(int var1, int var2, int var3) {
      if (var1 < 0 || var1 > var3) {
         throw new IndexOutOfBoundsException("Invalid index:" + var1 + ", size=" + this.size());
      }
   }

   static class a<E> {
      iX.a<E> a;
      boolean b;
      iX.a<E> c;
      boolean d;
      int e;
      int f;
      E g;

      private a(int var1, E var2, iX.a<E> var3, iX.a<E> var4) {
         this.f = var1;
         this.g = (E)var2;
         this.d = true;
         this.b = true;
         this.c = var3;
         this.a = var4;
      }

      private a(Collection<? extends E> var1) {
         this(var1.iterator(), 0, var1.size() - 1, 0, null, null);
      }

      private a(Iterator<? extends E> var1, int var2, int var3, int var4, iX.a<E> var5, iX.a<E> var6) {
         int var7 = var2 + (var3 - var2) / 2;
         if (var2 < var7) {
            this.a = new iX.a<>(var1, var2, var7 - 1, var7, var5, this);
         } else {
            this.b = true;
            this.a = var5;
         }

         this.g = (E)var1.next();
         this.f = var7 - var4;
         if (var7 < var3) {
            this.c = new iX.a<>(var1, var7 + 1, var3, var7, this, var6);
         } else {
            this.d = true;
            this.c = var6;
         }

         this.l();
      }

      final E a() {
         return this.g;
      }

      final void a(E var1) {
         this.g = (E)var1;
      }

      final iX.a<E> a(int var1) {
         while ((var1 = var1 - this.f) != 0) {
            iX.a var2;
            if ((var2 = var1 < 0 ? this.i() : this.j()) == null) {
               return null;
            }

            var1 = var1;
            this = var2;
         }

         return this;
      }

      final int a(Object var1, int var2) {
         int var3;
         while (this.i() == null || (var3 = this.a.a(var1, var2 + this.a.f)) == -1) {
            if (this.g == null ? this.g != var1 : !this.g.equals(var1)) {
               if (this.j() != null) {
                  var2 += this.c.f;
                  var1 = var1;
                  this = this.c;
                  continue;
               }

               return -1;
            }

            return var2;
         }

         return var3;
      }

      final void a(Object[] var1, int var2) {
         while (true) {
            var1[var2] = this.g;
            if (this.i() != null) {
               this.a.a(var1, var2 + this.a.f);
            }

            if (this.j() == null) {
               return;
            }

            var2 += this.c.f;
            var1 = var1;
            this = this.c;
         }
      }

      final iX.a<E> b() {
         return !this.d && this.c != null ? this.c.e() : this.c;
      }

      final iX.a<E> c() {
         return !this.b && this.a != null ? this.a.d() : this.a;
      }

      final iX.a<E> a(int var1, E var2) {
         int var3;
         return (var3 = var1 - this.f) <= 0 ? this.b(var3, (E)var2) : this.c(var3, (E)var2);
      }

      private iX.a<E> b(int var1, E var2) {
         if (this.i() == null) {
            this.a(new iX.a<>(-1, (E)var2, this, this.a), null);
         } else {
            this.a(this.a.a(var1, (E)var2), null);
         }

         if (this.f >= 0) {
            this.f++;
         }

         iX.a var3 = this.h();
         this.l();
         return var3;
      }

      private iX.a<E> c(int var1, E var2) {
         if (this.j() == null) {
            this.b(new iX.a<>(1, (E)var2, this.c, this), null);
         } else {
            this.b(this.c.a(var1, (E)var2), null);
         }

         if (this.f < 0) {
            this.f--;
         }

         iX.a var3 = this.h();
         this.l();
         return var3;
      }

      private iX.a<E> i() {
         return this.b ? null : this.a;
      }

      private iX.a<E> j() {
         return this.d ? null : this.c;
      }

      iX.a<E> d() {
         while (this.j() != null) {
            this = this.c;
         }

         return this;
      }

      iX.a<E> e() {
         while (this.i() != null) {
            this = this.a;
         }

         return this;
      }

      final iX.a<E> b(int var1) {
         if ((var1 = var1 - this.f) == 0) {
            return this.k();
         }

         if (var1 > 0) {
            this.b(this.c.b(var1), this.c.c);
            if (this.f < 0) {
               this.f++;
            }
         } else {
            this.a(this.a.b(var1), this.a.a);
            if (this.f > 0) {
               this.f--;
            }
         }

         this.l();
         return this.h();
      }

      iX.a<E> f() {
         if (this.j() == null) {
            return this.k();
         }

         this.b(this.c.f(), this.c.c);
         if (this.f < 0) {
            this.f++;
         }

         this.l();
         return this.h();
      }

      iX.a<E> g() {
         if (this.i() == null) {
            return this.k();
         }

         this.a(this.a.g(), this.a.a);
         if (this.f > 0) {
            this.f--;
         }

         this.l();
         return this.h();
      }

      private iX.a<E> k() {
         if (this.j() == null && this.i() == null) {
            return null;
         }

         if (this.j() == null) {
            if (this.f > 0) {
               this.a.f = this.a.f + this.f;
            }

            this.a.d().b(null, this.c);
            return this.a;
         } else {
            if (this.i() == null) {
               this.c.f = this.c.f + (this.f - (this.f < 0 ? 0 : 1));
               this.c.e().a(null, this.a);
               return this.c;
            }

            if (this.m() > 0) {
               iX.a var1 = this.c.e();
               this.g = var1.g;
               if (this.b) {
                  this.a = var1.a;
               }

               this.c = this.c.g();
               if (this.f < 0) {
                  this.f++;
               }
            } else {
               iX.a var2 = this.a.d();
               this.g = var2.g;
               if (this.d) {
                  this.c = var2.c;
               }

               var2 = this.a.a;
               this.a = this.a.f();
               if (this.a == null) {
                  this.a = var2;
                  this.b = true;
               }

               if (this.f > 0) {
                  this.f--;
               }
            }

            this.l();
            return this;
         }
      }

      iX.a<E> h() {
         switch (this.m()) {
            case -2:
               if (this.a.m() > 0) {
                  this.a(this.a.n(), null);
               }

               return this.o();
            case -1:
            case 0:
            case 1:
               return this;
            case 2:
               if (this.c.m() < 0) {
                  this.b(this.c.o(), null);
               }

               return this.n();
            default:
               throw new RuntimeException("tree inconsistent!");
         }
      }

      private static int d(iX.a<E> var0) {
         return var0 == null ? 0 : var0.f;
      }

      private int a(iX.a<E> var1, int var2) {
         if (var1 == null) {
            return 0;
         }

         int var3 = d(var1);
         var1.f = var2;
         return var3;
      }

      private void l() {
         this.e = Math.max(this.i() == null ? -1 : this.i().e, this.j() == null ? -1 : this.j().e) + 1;
      }

      static int a(iX.a<E> var0) {
         return var0 == null ? -1 : var0.e;
      }

      private int m() {
         return a(this.j()) - a(this.i());
      }

      private iX.a<E> n() {
         iX.a var1 = this.c;
         iX.a var2 = this.j().i();
         int var3 = this.f + d(var1);
         int var4 = -var1.f;
         int var5 = d(var1) + d(var2);
         this.b(var2, var1);
         var1.a(this, null);
         this.a(var1, var3);
         this.a(this, var4);
         this.a(var2, var5);
         return var1;
      }

      private iX.a<E> o() {
         iX.a var1 = this.a;
         iX.a var2 = this.i().j();
         int var3 = this.f + d(var1);
         int var4 = -var1.f;
         int var5 = d(var1) + d(var2);
         this.a(var2, var1);
         var1.b(this, null);
         this.a(var1, var3);
         this.a(this, var4);
         this.a(var2, var5);
         return var1;
      }

      void a(iX.a<E> var1, iX.a<E> var2) {
         this.b = var1 == null;
         this.a = this.b ? var2 : var1;
         this.l();
      }

      void b(iX.a<E> var1, iX.a<E> var2) {
         this.d = var1 == null;
         this.c = this.d ? var2 : var1;
         this.l();
      }

      private iX.a<E> b(iX.a<E> var1, int var2) {
         iX.a var3 = this.d();
         iX.a var4 = var1.e();
         if (var1.e > this.e) {
            iX.a var12 = this.f();
            ArrayDeque var14 = new ArrayDeque();
            iX.a var15 = var1;
            int var17 = var1.f + var2;
            int var18 = 0;

            while (var15 != null && var15.e > a(var12)) {
               var18 = var17;
               var14.push(var15);
               if ((var15 = var15.a) != null) {
                  var17 += var15.f;
               }
            }

            var3.a(var12, null);
            var3.b(var15, var4);
            if (var12 != null) {
               var12.d().b(null, var3);
               var12.f -= var2 - 1;
            }

            if (var15 != null) {
               var15.e().a(null, var3);
               var15.f = var17 - var2 + 1;
            }

            var3.f = var2 - 1 - var18;
            var1.f += var2;
            var15 = var3;

            while (!var14.isEmpty()) {
               (var1 = (iX.a)var14.pop()).a(var15, null);
               var15 = var1.h();
            }

            return var15;
         } else {
            var1 = var1.g();
            ArrayDeque var5 = new ArrayDeque();
            iX.a var6 = this;
            int var7 = this.f;
            int var8 = 0;

            while (var6 != null && var6.e > a(var1)) {
               var8 = var7;
               var5.push(var6);
               if ((var6 = var6.c) != null) {
                  var7 += var6.f;
               }
            }

            var4.b(var1, null);
            var4.a(var6, var3);
            if (var1 != null) {
               var1.e().a(null, var4);
               var1.f++;
            }

            if (var6 != null) {
               var6.d().b(null, var4);
               var6.f = var7 - var2;
            }

            var4.f = var2 - var8;
            var6 = var4;

            while (!var5.isEmpty()) {
               iX.a var9;
               (var9 = (iX.a)var5.pop()).b(var6, null);
               var6 = var9.h();
            }

            return var6;
         }
      }

      @Override
      public final String toString() {
         return "AVLNode(" + this.f + ',' + (this.a != null) + ',' + this.g + ',' + (this.j() != null) + ", faedelung " + this.d + " )";
      }

      // $VF: synthetic method
      a(Collection var1, byte var2) {
         this(var1);
      }

      // $VF: synthetic method
      static int b(iX.a var0) {
         return var0.f;
      }

      // $VF: synthetic method
      a(int var1, Object var2, iX.a var3, iX.a var4, byte var5) {
         this(var1, (E)var2, null, null);
      }

      // $VF: synthetic method
      static iX.a a(iX.a var0, iX.a var1, int var2) {
         return var0.b(var1, var2);
      }

      // $VF: synthetic method
      static Object c(iX.a var0) {
         return var0.g;
      }
   }

   static class b<E> implements ListIterator<E>, fC<E> {
      private final iX<E> a;
      private iX.a<E> b;
      private int c;
      private iX.a<E> d;
      private int e;
      private int f;

      protected b(iX<E> var1, int var2) {
         this.a = var1;
         this.f = var1.modCount;
         this.b = var1.a == null ? null : var1.a.a(var2);
         this.c = var2;
         this.e = -1;
      }

      private void a() {
         if (this.a.modCount != this.f) {
            throw new ConcurrentModificationException();
         }
      }

      @Override
      public final boolean hasNext() {
         return this.c < this.a.size();
      }

      @Override
      public final E next() {
         this.a();
         if (!this.hasNext()) {
            throw new NoSuchElementException("No element at index " + this.c + ".");
         }

         if (this.b == null) {
            this.b = this.a.a.a(this.c);
         }

         iX.a var1 = this.b;
         Object var3 = this.b.g;
         this.d = this.b;
         this.e = this.c++;
         iX.a var2 = this.b;
         this.b = !this.b.d && var2.c != null ? var2.c.e() : var2.c;
         return (E)var3;
      }

      @Override
      public final boolean hasPrevious() {
         return this.c > 0;
      }

      @Override
      public final E previous() {
         this.a();
         if (!this.hasPrevious()) {
            throw new NoSuchElementException("Already at start of list.");
         }

         iX.b var10000;
         iX.a var10001;
         if (this.b == null) {
            var10000 = this;
            var10001 = this.a.a.a(this.c - 1);
         } else {
            var10000 = this;
            iX.a var1 = this.b;
            var10001 = !this.b.b && var1.a != null ? var1.a.d() : var1.a;
         }

         var10000.b = var10001;
         iX.a var2 = this.b;
         Object var3 = this.b.g;
         this.d = this.b;
         this.e = --this.c;
         return (E)var3;
      }

      @Override
      public final int nextIndex() {
         return this.c;
      }

      @Override
      public final int previousIndex() {
         return this.nextIndex() - 1;
      }

      @Override
      public final void remove() {
         this.a();
         if (this.e == -1) {
            throw new IllegalStateException();
         }

         this.a.remove(this.e);
         if (this.c != this.e) {
            this.c--;
         }

         this.b = null;
         this.d = null;
         this.e = -1;
         this.f++;
      }

      @Override
      public final void set(E obj) {
         this.a();
         if (this.d == null) {
            throw new IllegalStateException();
         }

         Object var2 = obj;
         obj = (E)this.d;
         this.d.g = (E)var2;
      }

      @Override
      public final void add(E obj) {
         this.a();
         this.a.add(this.c, obj);
         this.d = null;
         this.e = -1;
         this.c++;
         this.f++;
      }
   }
}
