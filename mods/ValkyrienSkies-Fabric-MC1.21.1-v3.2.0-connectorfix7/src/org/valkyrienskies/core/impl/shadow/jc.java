package org.valkyrienskies.core.impl.shadow;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public abstract class jc<K, V> extends iZ<K, V> implements fD<K, V> {
   transient jc.c<K, V> j;

   protected jc() {
   }

   protected jc(int var1, float var2, int var3) {
      super(16, 0.75F, 12);
   }

   protected jc(int var1) {
      super(var1);
   }

   protected jc(int var1, float var2) {
      super(var1, var2);
   }

   protected jc(Map<? extends K, ? extends V> var1) {
      super(var1);
   }

   @Override
   protected final void d() {
      this.j = this.b(null, -1, null, null);
      this.j.e = this.j.f = this.j;
   }

   @Override
   public boolean containsValue(Object value) {
      if (value == null) {
         for (jc.c var2 = this.j.f; var2 != this.j; var2 = var2.f) {
            if (var2.getValue() == null) {
               return true;
            }
         }
      } else {
         for (jc.c var3 = this.j.f; var3 != this.j; var3 = var3.f) {
            if (this.b(value, var3.getValue())) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public void clear() {
      super.clear();
      this.j.e = this.j.f = this.j;
   }

   @Override
   public K firstKey() {
      if (super.c == 0) {
         throw new NoSuchElementException("Map is empty");
      } else {
         return this.j.f.getKey();
      }
   }

   @Override
   public K lastKey() {
      if (super.c == 0) {
         throw new NoSuchElementException("Map is empty");
      } else {
         return this.j.e.getKey();
      }
   }

   @Override
   public final K c(Object var1) {
      jc.c var2;
      return (var2 = this.f(var1)) != null && var2.f != this.j ? var2.f.getKey() : null;
   }

   protected final jc.c<K, V> f(Object var1) {
      return (jc.c<K, V>)super.e(var1);
   }

   @Override
   public final K d(Object var1) {
      jc.c var2;
      return (var2 = this.f(var1)) != null && var2.e != this.j ? var2.e.getKey() : null;
   }

   protected final jc.c<K, V> a(int var1) {
      if (var1 < 0) {
         throw new IndexOutOfBoundsException("Index " + var1 + " is less than zero");
      }

      if (var1 >= super.c) {
         throw new IndexOutOfBoundsException("Index " + var1 + " is invalid for size " + super.c);
      }

      jc.c var2;
      if (var1 < super.c / 2) {
         var2 = this.j.f;

         for (int var3 = 0; var3 < var1; var3++) {
            var2 = var2.f;
         }
      } else {
         var2 = this.j;

         for (int var4 = super.c; var4 > var1; var4--) {
            var2 = var2.e;
         }
      }

      return var2;
   }

   @Override
   protected final void a(iZ.c<K, V> var1, int var2) {
      jc.c var3;
      (var3 = (jc.c)var1).f = this.j;
      var3.e = this.j.e;
      this.j.e.f = var3;
      this.j.e = var3;
      super.d[var2] = var3;
   }

   private jc.c<K, V> b(iZ.c<K, V> var1, int var2, K var3, V var4) {
      return new jc.c<>(var1, var2, this.a(var3), (V)var4);
   }

   @Override
   protected final void b(iZ.c<K, V> var1, int var2, iZ.c<K, V> var3) {
      jc.c var4;
      (var4 = (jc.c)var1).e.f = var4.f;
      var4.f.e = var4.e;
      var4.f = null;
      var4.e = null;
      super.b(var1, var2, var3);
   }

   private static jc.c<K, V> a(jc.c<K, V> var0) {
      return var0.e;
   }

   private static jc.c<K, V> b(jc.c<K, V> var0) {
      return var0.f;
   }

   @Override
   public final fE<K, V> e() {
      return super.c == 0 ? ia.d() : new jc.e<>(this);
   }

   @Override
   protected final Iterator<Entry<K, V>> f() {
      return this.size() == 0 ? hZ.a() : new jc.a<>(this);
   }

   @Override
   protected final Iterator<K> g() {
      return this.size() == 0 ? hZ.a() : new jc.b<>(this);
   }

   @Override
   protected final Iterator<V> h() {
      return this.size() == 0 ? hZ.a() : new jc.f<>(this);
   }

   // $VF: synthetic method
   @Override
   public final fu c() {
      return this.e();
   }

   // $VF: synthetic method
   @Override
   protected final iZ.c a(iZ.c var1, int var2, Object var3, Object var4) {
      return this.b(var1, var2, (K)var3, (V)var4);
   }

   // $VF: synthetic method
   @Override
   protected final iZ.c e(Object var1) {
      return this.f(var1);
   }

   protected static class a<K, V> extends jc.d<K, V> implements fC<Entry<K, V>>, fJ<Entry<K, V>> {
      protected a(jc<K, V> var1) {
         super(var1);
      }

      private Entry<K, V> a() {
         return super.d();
      }

      private Entry<K, V> b() {
         return super.e();
      }

      // $VF: synthetic method
      @Override
      public final Object previous() {
         return super.e();
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         return super.d();
      }
   }

   protected static class b<K> extends jc.d<K, Object> implements fC<K>, fJ<K> {
      protected b(jc<K, ?> var1) {
         super(var1);
      }

      @Override
      public final K next() {
         return super.d().getKey();
      }

      @Override
      public final K previous() {
         return super.e().getKey();
      }
   }

   protected static class c<K, V> extends iZ.c<K, V> {
      protected jc.c<K, V> e;
      protected jc.c<K, V> f;

      protected c(iZ.c<K, V> var1, int var2, Object var3, V var4) {
         super(var1, var2, var3, (V)var4);
      }
   }

   protected abstract static class d<K, V> {
      private jc<K, V> a;
      private jc.c<K, V> b;
      private jc.c<K, V> c;
      private int d;

      protected d(jc<K, V> var1) {
         this.a = var1;
         this.c = var1.j.f;
         this.d = var1.f;
      }

      public boolean hasNext() {
         return this.c != this.a.j;
      }

      public boolean hasPrevious() {
         return this.c.e != this.a.j;
      }

      protected final jc.c<K, V> d() {
         if (this.a.f != this.d) {
            throw new ConcurrentModificationException();
         }

         if (this.c == this.a.j) {
            throw new NoSuchElementException("No next() entry in the iteration");
         }

         this.b = this.c;
         this.c = this.c.f;
         return this.b;
      }

      protected final jc.c<K, V> e() {
         if (this.a.f != this.d) {
            throw new ConcurrentModificationException();
         }

         jc.c var1 = this.c.e;
         if (this.c.e == this.a.j) {
            throw new NoSuchElementException("No previous() entry in the iteration");
         }

         this.c = var1;
         this.b = var1;
         return this.b;
      }

      protected final jc.c<K, V> f() {
         return this.b;
      }

      public void remove() {
         if (this.b == null) {
            throw new IllegalStateException("remove() can only be called once after next()");
         }

         if (this.a.f != this.d) {
            throw new ConcurrentModificationException();
         }

         this.a.remove(this.b.getKey());
         this.b = null;
         this.d = this.a.f;
      }

      public final void c() {
         this.b = null;
         this.c = this.a.j.f;
      }

      @Override
      public String toString() {
         return this.b != null ? "Iterator[" + this.b.getKey() + "=" + this.b.getValue() + "]" : "Iterator[]";
      }
   }

   protected static class e<K, V> extends jc.d<K, V> implements fE<K, V>, fJ<K> {
      protected e(jc<K, V> var1) {
         super(var1);
      }

      @Override
      public final K next() {
         return super.d().getKey();
      }

      @Override
      public final K previous() {
         return super.e().getKey();
      }

      @Override
      public final K a() {
         jc.c var1;
         if ((var1 = this.f()) == null) {
            throw new IllegalStateException("getKey() can only be called after next() and before remove()");
         } else {
            return (K)var1.getKey();
         }
      }

      @Override
      public final V b() {
         jc.c var1;
         if ((var1 = this.f()) == null) {
            throw new IllegalStateException("getValue() can only be called after next() and before remove()");
         } else {
            return (V)var1.getValue();
         }
      }

      @Override
      public final V a(V var1) {
         jc.c var2;
         if ((var2 = this.f()) == null) {
            throw new IllegalStateException("setValue() can only be called after next() and before remove()");
         } else {
            return (V)var2.setValue(var1);
         }
      }
   }

   protected static class f<V> extends jc.d<Object, V> implements fC<V>, fJ<V> {
      protected f(jc<?, V> var1) {
         super(var1);
      }

      @Override
      public final V next() {
         return super.d().getValue();
      }

      @Override
      public final V previous() {
         return super.e().getValue();
      }
   }
}
