package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;

abstract class kH<K, V> extends kG<K, V> {
   private static final long c = 5155253417231339498L;
   private transient kH.i<K, V> d = new kH.i<>(null, null, -1);
   private transient volatile Set<K> e;
   private transient volatile Collection<V> f;
   private transient volatile Set<Entry<K, V>> g;
   private transient int h = 0;
   protected transient int b = 0;

   protected kH(kI<? super K> var1) {
      super(var1);
   }

   protected kH(kI<? super K> var1, Map<? extends K, ? extends V> var2) {
      super(var1);
      this.putAll(var2);
   }

   @Override
   public void clear() {
      this.d.a = null;
      this.d.c = -1;
      this.d.b = null;
      this.d.d = null;
      this.d.e = this.d;
      this.d.f = null;
      this.d.g = this.d;
      this.h = 0;
      this.h();
   }

   @Override
   public int size() {
      return this.h;
   }

   private void f() {
      this.h++;
      this.h();
   }

   private void g() {
      this.h--;
      this.h();
   }

   private void h() {
      this.b++;
   }

   @Override
   public V put(K key, V value) {
      if (key == null) {
         throw new NullPointerException("Key cannot be null");
      }

      int var3;
      if ((var3 = this.e(key)) == 0) {
         if (this.d.a()) {
            this.f();
         } else {
            this.h();
         }

         return this.d.a(key, value);
      } else {
         kH.i var4 = this.a(key, var3);
         if (this.b(key, var4.a)) {
            if (var4.a()) {
               this.f();
            } else {
               this.h();
            }

            return (V)var4.a(key, value);
         } else {
            int var5;
            if (!kI.a(var5 = this.a(key, var4.a))) {
               if (kI.d(var5)) {
                  key = (K)(new kH.i(key, value, var5));
                  this.a((kH.i<K, V>)key, var3);
                  this.f();
                  return null;
               }

               if (kI.c(var5)) {
                  if (this.d.a()) {
                     this.f();
                  } else {
                     this.h();
                  }

                  return this.d.a(key, value);
               }

               if (kI.b(var5) && var4 != this.d) {
                  this.h();
                  return (V)var4.a(key, value);
               }
            }

            throw new IllegalArgumentException("Failed to put: " + key + " -> " + value + ", " + var5);
         }
      }
   }

   private kH.i<K, V> a(kH.i<K, V> var1, int var2) {
      kH.i var3 = this.d.e;
      kH.i var4 = this.d;

      while (var3.c < var1.c && var3.c > var4.c) {
         var4 = var3;
         if (!this.a(var1.a, var3.c, var2)) {
            var3 = var3.e;
         } else {
            var3 = var3.f;
         }
      }

      var1.g = var1;
      if (!this.a(var1.a, var1.c, var2)) {
         var1.e = var1;
         var1.f = var3;
      } else {
         var1.e = var3;
         var1.f = var1;
      }

      var1.d = var4;
      if (var3.c >= var1.c) {
         var3.d = var1;
      }

      if (var3.c <= var4.c) {
         var3.g = var1;
      }

      if (var4 != this.d && this.a(var1.a, var4.c, var2)) {
         var4.f = var1;
      } else {
         var4.e = var1;
      }

      return var1;
   }

   @Override
   public V get(Object k) {
      kH.i var2;
      return (V)((var2 = this.f(k)) != null ? var2.getValue() : null);
   }

   final kH.i<K, V> f(Object var1) {
      if ((var1 = var1) == null) {
         return null;
      }

      int var2 = this.e((K)var1);
      kH.i var4;
      return !(var4 = this.a((K)var1, var2)).a() && this.b((K)var1, var4.a) ? var4 : null;
   }

   private Entry<K, V> k(K var1) {
      int var2 = this.e((K)var1);
      kH.h var3 = new kH.h((byte)0);
      return !this.a(this.d.e, -1, (K)var1, var2, var3) ? (Entry)var3.a : null;
   }

   private K l(K var1) {
      Entry var2;
      return (K)((var2 = this.k((K)var1)) == null ? null : var2.getKey());
   }

   private V m(K var1) {
      Entry var2;
      return (V)((var2 = this.k((K)var1)) == null ? null : var2.getValue());
   }

   private boolean a(kH.i<K, V> var1, int var2, K var3, int var4, kH.h<Entry<K, V>> var5) {
      while (var1.c > var2) {
         if (!this.a((K)var3, var1.c, var4)) {
            if (this.a(var1.e, var1.c, (K)var3, var4, var5)) {
               var4 = var4;
               var3 = var3;
               var2 = var1.c;
               var1 = var1.f;
               this = this;
               continue;
            }
         } else if (this.a(var1.f, var1.c, (K)var3, var4, var5)) {
            var4 = var4;
            var3 = var3;
            var2 = var1.c;
            var1 = var1.e;
            this = this;
            continue;
         }

         return false;
      }

      if (!var1.a()) {
         kH.i var6 = var1;
         var5.a = (E)var6;
         return false;
      } else {
         return true;
      }
   }

   @Override
   public boolean containsKey(Object k) {
      if (k == null) {
         return false;
      }

      k = k;
      int var2 = this.e((K)k);
      kH.i var4;
      return !(var4 = this.a((K)k, var2)).a() && this.b((K)k, var4.a);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      if (this.g == null) {
         this.g = new kH.a((byte)0);
      }

      return this.g;
   }

   @Override
   public Set<K> keySet() {
      if (this.e == null) {
         this.e = new kH.b((byte)0);
      }

      return this.e;
   }

   @Override
   public Collection<V> values() {
      if (this.f == null) {
         this.f = new kH.l((byte)0);
      }

      return this.f;
   }

   @Override
   public V remove(Object k) {
      if (k == null) {
         return null;
      }

      k = k;
      int var2 = this.e((K)k);
      kH.i var3 = this.d.e;
      kH.i var4 = this.d;

      while (var3.c > var4.c) {
         var4 = var3;
         if (!this.a((K)k, var3.c, var2)) {
            var3 = var3.e;
         } else {
            var3 = var3.f;
         }
      }

      return !var3.a() && this.b((K)k, var3.a) ? this.a(var3) : null;
   }

   private kH.i<K, V> a(K var1, int var2) {
      kH.i var3 = this.d.e;
      kH.i var4 = this.d;

      while (var3.c > var4.c) {
         var4 = var3;
         if (!this.a((K)var1, var3.c, var2)) {
            var3 = var3.e;
         } else {
            var3 = var3.f;
         }
      }

      return var3;
   }

   final V a(kH.i<K, V> var1) {
      if (var1 != this.d) {
         if (var1.b()) {
            this.f(var1);
         } else {
            this.e(var1);
         }
      }

      this.g();
      return (V)var1.a(null, null);
   }

   private void e(kH.i<K, V> var1) {
      if (var1 == this.d) {
         throw new IllegalArgumentException("Cannot delete root Entry!");
      }

      if (!var1.c()) {
         throw new IllegalArgumentException(var1 + " is not an external Entry!");
      }

      kH.i var2 = var1.d;
      kH.i var3 = var1.e == var1 ? var1.f : var1.e;
      if (var2.e == var1) {
         var2.e = var3;
      } else {
         var2.f = var3;
      }

      if (var3.c > var2.c) {
         var3.d = var2;
      } else {
         var3.g = var2;
      }
   }

   private void f(kH.i<K, V> var1) {
      if (var1 == this.d) {
         throw new IllegalArgumentException("Cannot delete root Entry!");
      }

      if (!var1.b()) {
         throw new IllegalArgumentException(var1 + " is not an internal Entry!");
      }

      kH.i var2 = var1.g;
      var1.g.c = var1.c;
      kH.i var3 = var2.d;
      kH.i var4 = var2.e == var1 ? var2.f : var2.e;
      if (var2.g == var2 && var2.d != var1) {
         var2.g = var2.d;
      }

      if (var3.e == var2) {
         var3.e = var4;
      } else {
         var3.f = var4;
      }

      if (var4.c > var3.c) {
         var4.d = var3;
      }

      if (var1.e.d == var1) {
         var1.e.d = var2;
      }

      if (var1.f.d == var1) {
         var1.f.d = var2;
      }

      if (var1.d.e == var1) {
         var1.d.e = var2;
      } else {
         var1.d.f = var2;
      }

      var2.d = var1.d;
      var2.e = var1.e;
      var2.f = var1.f;
      if (b(var2.e, var2)) {
         var2.e.g = var2;
      }

      if (b(var2.f, var2)) {
         var2.f.g = var2;
      }
   }

   final kH.i<K, V> b(kH.i<K, V> var1) {
      return var1 == null ? this.b() : this.a(var1.g, var1, null);
   }

   private kH.i<K, V> a(kH.i<K, V> var1, kH.i<K, V> var2, kH.i<K, V> var3) {
      while (true) {
         kH.i var4 = var1;
         if (var2 == null || var1 != var2.g) {
            while (!var4.e.a() && var2 != var4.e) {
               if (b(var4.e, var4)) {
                  return var4.e;
               }

               var4 = var4.e;
            }
         }

         if (var4.a()) {
            return null;
         }

         if (var4.f == null) {
            return null;
         }

         if (var2 != var4.f) {
            if (b(var4.f, var4)) {
               return var4.f;
            }

            var2 = var2;
            var1 = var4.f;
            this = this;
         } else {
            while (var4 == var4.d.f) {
               if (var4 == var3) {
                  return null;
               }

               var4 = var4.d;
            }

            if (var4 == var3) {
               return null;
            }

            if (var4.d.f == null) {
               return null;
            }

            if (var2 != var4.d.f && b(var4.d.f, var4.d)) {
               return var4.d.f;
            }

            if (var4.d.f == var4.d) {
               return null;
            }

            var2 = var2;
            var1 = var4.d.f;
            this = this;
         }
      }
   }

   final kH.i<K, V> b() {
      return this.isEmpty() ? null : c(this.d);
   }

   static kH.i<K, V> c(kH.i<K, V> var0) {
      while (true) {
         kH.i var1 = var0.e;
         if (var0.e.a()) {
            var1 = var0.f;
         }

         if (var1.c <= var0.c) {
            return var1;
         }

         var0 = var1;
      }
   }

   @Override
   public Comparator<? super K> comparator() {
      return this.a();
   }

   @Override
   public K firstKey() {
      if (this.size() == 0) {
         throw new NoSuchElementException();
      } else {
         return this.b().getKey();
      }
   }

   @Override
   public K lastKey() {
      kH.i var1;
      if ((var1 = this.d()) != null) {
         return (K)var1.getKey();
      } else {
         throw new NoSuchElementException();
      }
   }

   @Override
   public final K c(K var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if ((var1 = this.f((Object)var1)) != null) {
         kH.i var3;
         return (K)((var3 = this.b(var1)) != null ? var3.getKey() : null);
      } else {
         return null;
      }
   }

   @Override
   public final K d(K var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if ((var1 = this.f((Object)var1)) != null) {
         kH.i var3;
         return (K)((var3 = this.d(var1)) != null ? var3.getKey() : null);
      } else {
         return null;
      }
   }

   @Override
   public fE<K, V> e() {
      return new kH.k((byte)0);
   }

   @Override
   public final SortedMap<K, V> a(K var1) {
      int var3 = this.e((K)var1);
      boolean var5 = false;
      Object var2 = var1;
      var1 = this;
      int var4;
      if ((var4 = var3 + 0) > var1.e(var2)) {
         throw new IllegalArgumentException("0" + " + " + var3 + " > " + var1.e(var2));
      } else {
         return var4 == 0 ? var1 : var1.new d(var2, 0, var3, (byte)0);
      }
   }

   private SortedMap<K, V> c(K var1, int var2, int var3) {
      if ((var2 = var3 + 0) > this.e((K)var1)) {
         throw new IllegalArgumentException("0" + " + " + var3 + " > " + this.e((K)var1));
      } else {
         return var2 == 0 ? this : new kH.d(var1, 0, var3, (byte)0);
      }
   }

   @Override
   public SortedMap<K, V> headMap(K toKey) {
      return new kH.e(null, toKey);
   }

   @Override
   public SortedMap<K, V> subMap(K fromKey, K toKey) {
      return new kH.e(fromKey, toKey);
   }

   @Override
   public SortedMap<K, V> tailMap(K fromKey) {
      return new kH.e(fromKey, null);
   }

   final kH.i<K, V> g(K var1) {
      int var2;
      if ((var2 = this.e((K)var1)) == 0) {
         if (!this.d.a()) {
            return this.size() > 1 ? this.b(this.d) : null;
         } else {
            return this.b();
         }
      } else {
         kH.i var3 = this.a((K)var1, var2);
         if (this.b((K)var1, var3.a)) {
            return this.b(var3);
         }

         int var4;
         if (kI.d(var4 = this.a((K)var1, var3.a))) {
            var1 = new kH.i<>(var1, null, var4);
            this.a(var1, var2);
            this.f();
            kH.i var6 = this.b(var1);
            this.a(var1);
            this.b -= 2;
            return var6;
         }

         if (kI.c(var4)) {
            if (!this.d.a()) {
               return this.b();
            } else {
               return this.size() > 1 ? this.b(this.b()) : null;
            }
         } else if (kI.b(var4)) {
            return this.b(var3);
         } else {
            throw new IllegalStateException("invalid lookup: " + var1);
         }
      }
   }

   final kH.i<K, V> h(K var1) {
      int var2;
      if ((var2 = this.e((K)var1)) == 0) {
         return !this.d.a() ? this.d : this.b();
      } else {
         kH.i var3 = this.a((K)var1, var2);
         if (this.b((K)var1, var3.a)) {
            return var3;
         } else {
            int var4;
            if (kI.d(var4 = this.a((K)var1, var3.a))) {
               var1 = new kH.i<>(var1, null, var4);
               this.a(var1, var2);
               this.f();
               kH.i var6 = this.b(var1);
               this.a(var1);
               this.b -= 2;
               return var6;
            } else if (kI.c(var4)) {
               return !this.d.a() ? this.d : this.b();
            } else if (kI.b(var4)) {
               return var3;
            } else {
               throw new IllegalStateException("invalid lookup: " + var1);
            }
         }
      }
   }

   final kH.i<K, V> i(K var1) {
      int var2;
      if ((var2 = this.e((K)var1)) == 0) {
         return null;
      } else {
         kH.i var3 = this.a((K)var1, var2);
         if (this.b((K)var1, var3.a)) {
            return this.d(var3);
         } else {
            int var4;
            if (kI.d(var4 = this.a((K)var1, var3.a))) {
               var1 = new kH.i<>(var1, null, var4);
               this.a(var1, var2);
               this.f();
               kH.i var6 = this.d(var1);
               this.a(var1);
               this.b -= 2;
               return var6;
            } else if (kI.c(var4)) {
               return null;
            } else if (kI.b(var4)) {
               return this.d(var3);
            } else {
               throw new IllegalStateException("invalid lookup: " + var1);
            }
         }
      }
   }

   final kH.i<K, V> j(K var1) {
      int var2;
      if ((var2 = this.e((K)var1)) == 0) {
         return !this.d.a() ? this.d : null;
      } else {
         kH.i var3 = this.a((K)var1, var2);
         if (this.b((K)var1, var3.a)) {
            return var3;
         } else {
            int var4;
            if (kI.d(var4 = this.a((K)var1, var3.a))) {
               var1 = new kH.i<>(var1, null, var4);
               this.a(var1, var2);
               this.f();
               kH.i var6 = this.d(var1);
               this.a(var1);
               this.b -= 2;
               return var6;
            } else if (kI.c(var4)) {
               return !this.d.a() ? this.d : null;
            } else if (kI.b(var4)) {
               return var3;
            } else {
               throw new IllegalStateException("invalid lookup: " + var1);
            }
         }
      }
   }

   final kH.i<K, V> b(K var1, int var2, int var3) {
      kH.i var4 = this.d.e;
      kH.i var5 = this.d;

      while (var4.c > var5.c && var3 > var4.c) {
         var5 = var4;
         if (!this.a((K)var1, var2 + var4.c, var2 + var3)) {
            var4 = var4.e;
         } else {
            var4 = var4.f;
         }
      }

      if ((var4 = var4.a() ? var5 : var4).a()) {
         return null;
      }

      int var8 = var2 + var3;
      if (var4 == this.d && this.e((K)var4.getKey()) < var8) {
         return null;
      }

      if (this.a((K)var1, var8 - 1, var8) != this.a(var4.a, var3 - 1, this.e(var4.a))) {
         return null;
      }

      int var6;
      return (var6 = this.a().a((K)var1, var2, var3, var4.a, 0, this.e((K)var4.getKey()))) >= 0 && var6 < var3 ? null : var4;
   }

   final kH.i<K, V> d() {
      return g(this.d.e);
   }

   private static kH.i<K, V> g(kH.i<K, V> var0) {
      if (var0.f == null) {
         return null;
      }

      while (var0.f.c > var0.c) {
         var0 = var0.f;
      }

      return var0.f;
   }

   final kH.i<K, V> d(kH.i<K, V> var1) {
      if (var1.g == null) {
         throw new IllegalArgumentException("must have come from somewhere!");
      }

      if (var1.g.f == var1) {
         return b(var1.g.e, var1.g) ? var1.g.e : g(var1.g.e);
      }

      var1 = var1.g;

      while (var1.d != null && var1 == var1.d.e) {
         var1 = var1.d;
      }

      if (var1.d == null) {
         return null;
      }

      if (b(var1.d.e, var1.d)) {
         if (var1.d.e == this.d) {
            return this.d.a() ? null : this.d;
         } else {
            return var1.d.e;
         }
      } else {
         return g(var1.d.e);
      }
   }

   final kH.i<K, V> a(kH.i<K, V> var1, kH.i<K, V> var2) {
      return var1 == null ? this.b() : this.a(var1.g, var1, var2);
   }

   private static boolean b(kH.i<?, ?> var0, kH.i<?, ?> var1) {
      return var0 != null && var0.c <= var1.c && !var0.a();
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.d = new kH.i<>(null, null, -1);
      int var2 = var1.readInt();

      for (int var3 = 0; var3 < var2; var3++) {
         Object var4 = var1.readObject();
         Object var5 = var1.readObject();
         this.put((K)var4, (V)var5);
      }
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeInt(this.size());

      for (Entry var3 : this.entrySet()) {
         var1.writeObject(var3.getKey());
         var1.writeObject(var3.getValue());
      }
   }

   // $VF: synthetic method
   @Override
   public final fu c() {
      return this.e();
   }

   class a extends AbstractSet<Entry<K, V>> {
      private a() {
      }

      @Override
      public final Iterator<Entry<K, V>> iterator() {
         return new kH.a.a((byte)0);
      }

      @Override
      public final boolean contains(Object o) {
         kH.i var2;
         return !(o instanceof Entry) ? false : (var2 = kH.this.f(((Entry)o).getKey())) != null && var2.equals(o);
      }

      @Override
      public final boolean remove(Object obj) {
         if (!(obj instanceof Entry)) {
            return false;
         }

         if (!this.contains(obj)) {
            return false;
         }

         obj = (Entry)obj;
         kH.this.remove(obj.getKey());
         return true;
      }

      @Override
      public final int size() {
         return kH.this.size();
      }

      @Override
      public final void clear() {
         kH.this.clear();
      }

      // $VF: synthetic method
      a(byte var2) {
         this();
      }

      class a extends kH<K, V>.j<Entry<K, V>> {
         private a() {
         }

         private Entry<K, V> a() {
            return this.c();
         }

         // $VF: synthetic method
         @Override
         public final Object next() {
            return this.c();
         }

         // $VF: synthetic method
         a(byte var2) {
            this();
         }
      }
   }

   class b extends AbstractSet<K> {
      private b() {
      }

      @Override
      public final Iterator<K> iterator() {
         return new kH.b.a((byte)0);
      }

      @Override
      public final int size() {
         return kH.this.size();
      }

      @Override
      public final boolean contains(Object o) {
         return kH.this.containsKey(o);
      }

      @Override
      public final boolean remove(Object o) {
         int var2 = this.size();
         kH.this.remove(o);
         return var2 != this.size();
      }

      @Override
      public final void clear() {
         kH.this.clear();
      }

      // $VF: synthetic method
      b(byte var2) {
         this();
      }

      class a extends kH<K, V>.j<K> {
         private a() {
         }

         @Override
         public final K next() {
            return (K)this.c().getKey();
         }

         // $VF: synthetic method
         a(byte var2) {
            this();
         }
      }
   }

   final class c extends kH<K, V>.f {
      private final kH<K, V>.d c;
      private kH.i<K, V> d;
      private int e = 0;

      public c(kH<K, V>.d var2) {
         super(var2);
         this.c = var2;
      }

      @Override
      public final int size() {
         return this.c.f();
      }

      @Override
      public final Iterator<Entry<K, V>> iterator() {
         if (kH.this.b != this.e) {
            this.d = kH.this.b((K)this.c.a, this.c.b, this.c.c);
            this.e = kH.this.b;
         }

         if (this.d == null) {
            return Collections.<Entry<K, V>>emptySet().iterator();
         } else {
            return this.c.c > this.d.c ? new kH.c.b(this.d) : new kH.c.a(this.d, (K)this.c.a, this.c.b, this.c.c);
         }
      }

      final class a extends kH<K, V>.j<Entry<K, V>> {
         private final Object d;
         private final int e;
         private final int f;
         private boolean g;
         private kH.i<K, V> h;

         a(kH.i<K, V> var2, K var3, int var4, int var5) {
            this.h = var2;
            super.b = kH.c(var2);
            this.d = var3;
            this.e = var4;
            this.f = var5;
         }

         private Entry<K, V> a() {
            kH.i var1 = this.c();
            if (this.g) {
               super.b = null;
            }

            return var1;
         }

         @Override
         protected final kH.i<K, V> a(kH.i<K, V> var1) {
            return kH.this.a(var1, this.h);
         }

         @Override
         public final void remove() {
            boolean var1 = false;
            int var2 = this.h.c;
            if (super.c == this.h) {
               var1 = true;
            }

            super.remove();
            if (var2 != this.h.c || var1) {
               this.h = kH.this.b((K)this.d, this.e, this.f);
            }

            if (this.f >= this.h.c) {
               this.g = true;
            }
         }

         // $VF: synthetic method
         @Override
         public final Object next() {
            kH.c.a var1 = this;
            kH.i var2 = this.c();
            if (var1.g) {
               var1.b = null;
            }

            return var2;
         }
      }

      final class b implements Iterator<Entry<K, V>> {
         private final kH.i<K, V> a;
         private int b = 0;

         public b(kH.i<K, V> var2) {
            this.a = var2;
         }

         @Override
         public final boolean hasNext() {
            return this.b == 0;
         }

         private Entry<K, V> a() {
            if (this.b != 0) {
               throw new NoSuchElementException();
            }

            this.b++;
            return this.a;
         }

         @Override
         public final void remove() {
            if (this.b != 1) {
               throw new IllegalStateException();
            }

            this.b++;
            kH.this.a(this.a);
         }

         // $VF: synthetic method
         @Override
         public final Object next() {
            kH.c.b var1 = this;
            if (this.b != 0) {
               throw new NoSuchElementException();
            }

            var1.b++;
            return var1.a;
         }
      }
   }

   class d extends kH<K, V>.g {
      private final Object a;
      private final int b;
      private final int c;
      private Object d = null;
      private Object e = null;
      private transient int f = 0;
      private int g = -1;

      private d(K var2, int var3, int var4) {
         super((byte)0);
         this.a = var2;
         this.b = var3;
         this.c = var4;
      }

      private int f() {
         if (this.g == -1 || kH.this.b != this.f) {
            Iterator var1 = super.entrySet().iterator();
            this.g = 0;
            kH.i var2 = null;
            if (var1.hasNext()) {
               var2 = (Entry)var1.next();
               this.g = 1;
            }

            this.d = var2 == null ? null : var2.getKey();
            if (this.d != null) {
               kH.i var3 = kH.this.d(var2);
               this.d = var3 == null ? null : var3.getKey();
            }

            for (this.e = this.d; var1.hasNext(); var2 = (Entry)var1.next()) {
               this.g++;
            }

            this.e = var2 == null ? null : var2.getKey();
            if (this.e != null) {
               var2 = kH.this.b(var2);
               this.e = var2 == null ? null : var2.getKey();
            }

            this.f = kH.this.b;
         }

         return this.g;
      }

      @Override
      public final K firstKey() {
         this.f();
         kH.i var1;
         if (this.d == null) {
            var1 = kH.this.b();
         } else {
            var1 = kH.this.g((K)this.d);
         }

         Object var2 = var1 != null ? var1.getKey() : null;
         if (var1 != null) {
            kH var3 = kH.this;
            if (kH.super.a.a((K)this.a, this.b, this.c, (K)var2)) {
               return (K)var2;
            }
         }

         throw new NoSuchElementException();
      }

      @Override
      public final K lastKey() {
         this.f();
         kH.i var1;
         if (this.e == null) {
            var1 = kH.this.d();
         } else {
            var1 = kH.this.i((K)this.e);
         }

         Object var2 = var1 != null ? var1.getKey() : null;
         if (var1 != null) {
            kH var3 = kH.this;
            if (kH.super.a.a((K)this.a, this.b, this.c, (K)var2)) {
               return (K)var2;
            }
         }

         throw new NoSuchElementException();
      }

      @Override
      protected final boolean a(K var1) {
         kH var2 = kH.this;
         return kH.super.a.a((K)this.a, this.b, this.c, (K)var1);
      }

      @Override
      protected final boolean b(K var1) {
         return this.a((K)var1);
      }

      @Override
      protected final boolean a(K var1, boolean var2) {
         kH var3 = kH.this;
         return kH.super.a.a((K)this.a, this.b, this.c, (K)var1);
      }

      @Override
      protected final boolean b(K var1, boolean var2) {
         kH var3 = kH.this;
         return kH.super.a.a((K)this.a, this.b, this.c, (K)var1);
      }

      @Override
      protected final Set<Entry<K, V>> a() {
         return kH.this.new c(this);
      }

      @Override
      public final K b() {
         return (K)this.d;
      }

      @Override
      public final K c() {
         return (K)this.e;
      }

      @Override
      public final boolean d() {
         return false;
      }

      @Override
      public final boolean e() {
         return false;
      }

      @Override
      protected final SortedMap<K, V> a(K var1, boolean var2, K var3, boolean var4) {
         return kH.this.new e(var1, var2, var3, var4);
      }

      @Override
      public final void clear() {
         Iterator var1 = kH.this.entrySet().iterator();
         Set var2 = this.keySet();

         while (var1.hasNext()) {
            if (var2.contains(((Entry)var1.next()).getKey())) {
               var1.remove();
            }
         }
      }

      // $VF: synthetic method
      d(Object var2, int var3, int var4, byte var5) {
         this(var2, var3, var4);
      }
   }

   class e extends kH<K, V>.g {
      private final Object a;
      private final Object b;
      private final boolean c;
      private final boolean d;

      protected e(K var2, K var3) {
         this(var2, true, var3, false);
      }

      protected e(K var2, boolean var3, K var4, boolean var5) {
         super((byte)0);
         if (var2 == null && var4 == null) {
            throw new IllegalArgumentException("must have a from or to!");
         }

         if (var2 != null && var4 != null && kH.super.a.compare((K)var2, (K)var4) > 0) {
            throw new IllegalArgumentException("fromKey > toKey");
         }

         this.a = var2;
         this.c = var3;
         this.b = var4;
         this.d = var5;
      }

      @Override
      public final K firstKey() {
         kH.i var1;
         if (this.a == null) {
            var1 = kH.this.b();
         } else if (this.c) {
            var1 = kH.this.h((K)this.a);
         } else {
            var1 = kH.this.g((K)this.a);
         }

         Object var2 = var1 != null ? var1.getKey() : null;
         if (var1 != null && (this.b == null || this.b((boolean)var2, false))) {
            return (K)var2;
         } else {
            throw new NoSuchElementException();
         }
      }

      @Override
      public final K lastKey() {
         kH.i var1;
         if (this.b == null) {
            var1 = kH.this.d();
         } else if (this.d) {
            var1 = kH.this.j((K)this.b);
         } else {
            var1 = kH.this.i((K)this.b);
         }

         Object var2 = var1 != null ? var1.getKey() : null;
         if (var1 != null && (this.a == null || this.a((boolean)var2, false))) {
            return (K)var2;
         } else {
            throw new NoSuchElementException();
         }
      }

      @Override
      protected final Set<Entry<K, V>> a() {
         return kH.this.new f(this);
      }

      @Override
      public final K b() {
         return (K)this.a;
      }

      @Override
      public final K c() {
         return (K)this.b;
      }

      @Override
      public final boolean d() {
         return this.c;
      }

      @Override
      public final boolean e() {
         return this.d;
      }

      @Override
      protected final SortedMap<K, V> a(K var1, boolean var2, K var3, boolean var4) {
         return kH.this.new e(var1, var2, var3, var4);
      }
   }

   class f extends AbstractSet<Entry<K, V>> {
      private final kH<K, V>.g a;
      private transient int c = -1;
      private transient int d;

      public f(kH<K, V>.g var2) {
         if (var2 == null) {
            throw new NullPointerException("delegate");
         }

         this.a = var2;
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         kH.i var1 = this.a.b();
         Object var2 = this.a.c();
         if (var1 == null) {
            var1 = kH.this.b();
         } else {
            var1 = kH.this.h((K)var1);
         }

         kH.i var3 = null;
         if (var2 != null) {
            var3 = kH.this.h((K)var2);
         }

         return new kH.f.a(var1, var3, (byte)0);
      }

      @Override
      public int size() {
         if (this.c == -1 || this.d != kH.this.b) {
            this.c = 0;
            Iterator var1 = this.iterator();

            while (var1.hasNext()) {
               this.c++;
               var1.next();
            }

            this.d = kH.this.b;
         }

         return this.c;
      }

      @Override
      public boolean isEmpty() {
         return !this.iterator().hasNext();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         }

         Entry var3;
         Object var2 = (var3 = (Entry)o).getKey();
         kH.i var4;
         return !this.a.a((K)var2) ? false : (var4 = kH.this.f(var2)) != null && kG.c(var4.getValue(), var3.getValue());
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry var3;
            kH.i var2 = (kH.i)(var3 = (Entry)o).getKey();
            if (!this.a.a((K)var2)) {
               return false;
            } else if ((var2 = kH.this.f(var2)) != null && kG.c(var2.getValue(), var3.getValue())) {
               kH.this.a(var2);
               return true;
            } else {
               return false;
            }
         }
      }

      final class a extends kH<K, V>.j<Entry<K, V>> {
         private final Object d;

         private a(kH.i<K, V> var2, kH.i<K, V> var3) {
            super(var2);
            this.d = var3 != null ? var3.getKey() : null;
         }

         @Override
         public final boolean hasNext() {
            return super.b != null && !kG.c(super.b.a, this.d);
         }

         private Entry<K, V> a() {
            if (super.b != null && !kG.c(super.b.a, this.d)) {
               return this.c();
            } else {
               throw new NoSuchElementException();
            }
         }

         // $VF: synthetic method
         @Override
         public final Object next() {
            kH.f.a var1 = this;
            if (super.b != null && !kG.c(var1.b.a, var1.d)) {
               return var1.c();
            } else {
               throw new NoSuchElementException();
            }
         }

         // $VF: synthetic method
         a(kH.i var2, kH.i var3, byte var4) {
            this(var2, var3);
         }
      }
   }

   abstract class g extends AbstractMap<K, V> implements SortedMap<K, V> {
      private transient volatile Set<Entry<K, V>> a;

      private g() {
      }

      protected abstract Set<Entry<K, V>> a();

      protected abstract K b();

      protected abstract boolean d();

      protected abstract K c();

      protected abstract boolean e();

      @Override
      public Comparator<? super K> comparator() {
         return kH.this.comparator();
      }

      @Override
      public boolean containsKey(Object key) {
         return !this.a((K)kH.b(key)) ? false : kH.this.containsKey(key);
      }

      @Override
      public V remove(Object key) {
         return !this.a((K)kH.b(key)) ? null : kH.this.remove(key);
      }

      @Override
      public V get(Object key) {
         return !this.a((K)kH.b(key)) ? null : kH.this.get(key);
      }

      @Override
      public V put(K key, V value) {
         if (!this.a(key)) {
            throw new IllegalArgumentException("Key is out of range: " + key);
         } else {
            return kH.this.put(key, value);
         }
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
         if (this.a == null) {
            this.a = this.a();
         }

         return this.a;
      }

      @Override
      public SortedMap<K, V> subMap(K fromKey, K toKey) {
         if (!this.b(fromKey)) {
            throw new IllegalArgumentException("FromKey is out of range: " + fromKey);
         } else if (!this.b(toKey)) {
            throw new IllegalArgumentException("ToKey is out of range: " + toKey);
         } else {
            return (SortedMap<K, V>)this.a((boolean)fromKey, this.d(), (boolean)toKey, this.e());
         }
      }

      @Override
      public SortedMap<K, V> headMap(K toKey) {
         if (!this.b(toKey)) {
            throw new IllegalArgumentException("ToKey is out of range: " + toKey);
         } else {
            return (SortedMap<K, V>)this.a((boolean)this.b(), this.d(), (boolean)toKey, this.e());
         }
      }

      @Override
      public SortedMap<K, V> tailMap(K fromKey) {
         if (!this.b(fromKey)) {
            throw new IllegalArgumentException("FromKey is out of range: " + fromKey);
         } else {
            return (SortedMap<K, V>)this.a((boolean)fromKey, this.d(), (boolean)this.c(), this.e());
         }
      }

      protected boolean a(K var1) {
         Object var2 = this.b();
         Object var3 = this.c();
         return (var2 == null || this.a((boolean)var1, false)) && (var3 == null || this.b((boolean)var1, false));
      }

      protected boolean b(K var1) {
         Object var2 = this.b();
         Object var3 = this.c();
         return (var2 == null || this.a((boolean)var1, false)) && (var3 == null || this.b((boolean)var1, true));
      }

      protected boolean a(K var1, boolean var2) {
         Object var6 = this.b();
         boolean var3 = this.d();
         kH var4 = kH.this;
         var1 = kH.super.a.compare((K)var1, (K)var6);
         return var3 ? var1 >= 0 : var1 > 0;
      }

      protected boolean b(K var1, boolean var2) {
         Object var3 = this.c();
         boolean var4 = this.e();
         kH var5 = kH.this;
         var1 = kH.super.a.compare((K)var1, (K)var3);
         return !var4 && !var2 ? var1 < 0 : var1 <= 0;
      }

      protected abstract SortedMap<K, V> a(K var1, boolean var2, K var3, boolean var4);

      // $VF: synthetic method
      g(byte var2) {
         this();
      }
   }

   static class h<E> {
      E a;

      private h() {
      }

      public final void a(E var1) {
         this.a = (E)var1;
      }

      public final E a() {
         return this.a;
      }

      // $VF: synthetic method
      h(byte var1) {
         this();
      }
   }

   protected static class i<K, V> extends kG.a<K, V> {
      private static final long h = 4596023148184140013L;
      protected int c;
      protected kH.i<K, V> d;
      protected kH.i<K, V> e;
      protected kH.i<K, V> f;
      protected kH.i<K, V> g;

      public i(K var1, V var2, int var3) {
         super((K)var1, (V)var2);
         this.c = var3;
         this.d = null;
         this.e = this;
         this.f = null;
         this.g = this;
      }

      public final boolean a() {
         return super.a == null;
      }

      public final boolean b() {
         return this.e != this && this.f != this;
      }

      public final boolean c() {
         return !this.b();
      }

      @Override
      public final String toString() {
         StringBuilder var1 = new StringBuilder();
         if (this.c == -1) {
            var1.append("RootEntry(");
         } else {
            var1.append("Entry(");
         }

         var1.append("key=").append(this.getKey()).append(" [").append(this.c).append("], ");
         var1.append("value=").append(this.getValue()).append(", ");
         if (this.d != null) {
            if (this.d.c == -1) {
               var1.append("parent=ROOT");
            } else {
               var1.append("parent=").append(this.d.getKey()).append(" [").append(this.d.c).append("]");
            }
         } else {
            var1.append("parent=null");
         }

         var1.append(", ");
         if (this.e != null) {
            if (this.e.c == -1) {
               var1.append("left=ROOT");
            } else {
               var1.append("left=").append(this.e.getKey()).append(" [").append(this.e.c).append("]");
            }
         } else {
            var1.append("left=null");
         }

         var1.append(", ");
         if (this.f != null) {
            if (this.f.c == -1) {
               var1.append("right=ROOT");
            } else {
               var1.append("right=").append(this.f.getKey()).append(" [").append(this.f.c).append("]");
            }
         } else {
            var1.append("right=null");
         }

         var1.append(", ");
         if (this.g != null) {
            if (this.g.c == -1) {
               var1.append("predecessor=ROOT");
            } else {
               var1.append("predecessor=").append(this.g.getKey()).append(" [").append(this.g.c).append("]");
            }
         }

         var1.append(")");
         return var1.toString();
      }
   }

   abstract class j<E> implements Iterator<E> {
      protected int a;
      protected kH.i<K, V> b;
      protected kH.i<K, V> c;

      protected j() {
         this.a = kH.this.b;
         this.b = kH.this.b(null);
      }

      protected j(kH.i<K, V> var2) {
         this.a = kH.this.b;
         this.b = var2;
      }

      protected kH.i<K, V> c() {
         if (this.a != kH.this.b) {
            throw new ConcurrentModificationException();
         }

         kH.i var1 = this.b;
         if (this.b == null) {
            throw new NoSuchElementException();
         }

         this.b = this.a(var1);
         this.c = var1;
         return var1;
      }

      protected kH.i<K, V> a(kH.i<K, V> var1) {
         return kH.this.b(var1);
      }

      @Override
      public boolean hasNext() {
         return this.b != null;
      }

      @Override
      public void remove() {
         if (this.c == null) {
            throw new IllegalStateException();
         }

         if (this.a != kH.this.b) {
            throw new ConcurrentModificationException();
         }

         kH.i var1 = this.c;
         this.c = null;
         kH.this.a(var1);
         this.a = kH.this.b;
      }
   }

   class k extends kH<K, V>.j<K> implements fE<K, V> {
      private kH.i<K, V> d;

      private k() {
      }

      @Override
      public final K next() {
         return (K)this.c().getKey();
      }

      @Override
      public final K a() {
         if (super.c == null) {
            throw new IllegalStateException();
         } else {
            return super.c.getKey();
         }
      }

      @Override
      public final V b() {
         if (super.c == null) {
            throw new IllegalStateException();
         } else {
            return super.c.getValue();
         }
      }

      @Override
      public final V a(V var1) {
         if (super.c == null) {
            throw new IllegalStateException();
         } else {
            return super.c.setValue((V)var1);
         }
      }

      @Override
      public final boolean hasPrevious() {
         return this.d != null;
      }

      @Override
      public final K previous() {
         kH.k var1 = this;
         if (super.a != kH.this.b) {
            throw new ConcurrentModificationException();
         }

         kH.i var2 = var1.d;
         if (var1.d == null) {
            throw new NoSuchElementException();
         }

         var1.d = kH.this.d(var2);
         var1.b = var1.c;
         var1.c = var2;
         return var1.c.getKey();
      }

      @Override
      protected final kH.i<K, V> c() {
         kH.i var1 = super.c();
         this.d = var1;
         return var1;
      }

      private kH.i<K, V> d() {
         if (super.a != kH.this.b) {
            throw new ConcurrentModificationException();
         }

         kH.i var1 = this.d;
         if (this.d == null) {
            throw new NoSuchElementException();
         }

         this.d = kH.this.d(var1);
         super.b = super.c;
         super.c = var1;
         return super.c;
      }

      // $VF: synthetic method
      k(byte var2) {
         this();
      }
   }

   class l extends AbstractCollection<V> {
      private l() {
      }

      @Override
      public final Iterator<V> iterator() {
         return new kH.l.a((byte)0);
      }

      @Override
      public final int size() {
         return kH.this.size();
      }

      @Override
      public final boolean contains(Object o) {
         return kH.this.containsValue(o);
      }

      @Override
      public final void clear() {
         kH.this.clear();
      }

      @Override
      public final boolean remove(Object o) {
         Iterator var2 = this.iterator();

         while (var2.hasNext()) {
            if (kG.c(var2.next(), o)) {
               var2.remove();
               return true;
            }
         }

         return false;
      }

      // $VF: synthetic method
      l(byte var2) {
         this();
      }

      class a extends kH<K, V>.j<V> {
         private a() {
         }

         @Override
         public final V next() {
            return (V)this.c().getValue();
         }

         // $VF: synthetic method
         a(byte var2) {
            this();
         }
      }
   }
}
