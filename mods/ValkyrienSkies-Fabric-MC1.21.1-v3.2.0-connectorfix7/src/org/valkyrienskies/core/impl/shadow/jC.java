package org.valkyrienskies.core.impl.shadow;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class jC<K, V> extends jb<K, V> {
   private static final int a = 255;
   private final jC.g<K, V>[] b;
   private final jC.f[] c;

   public jC() {
      this(255);
   }

   private jC(int var1) {
      if ((var1 = Math.max(17, 255)) % 2 == 0) {
         var1--;
      }

      this.b = new jC.g[var1];
      this.c = new jC.f[var1];

      for (int var2 = 0; var2 < var1; var2++) {
         this.c[var2] = new jC.f((byte)0);
      }
   }

   private int a(Object var1) {
      if (var1 == null) {
         return 0;
      }

      int var2;
      int var3;
      int var4;
      int var5;
      int var6;
      int var7;
      int var8;
      return (
               var8 = (
                     (
                           var7 = (var6 = (var5 = (var4 = (var3 = (var2 = var1.hashCode()) + ~(var2 << 15)) ^ var3 >>> 10) + (var4 << 3)) ^ var5 >>> 6)
                              + ~(var6 << 11)
                        )
                        ^ var7 >>> 16
                  )
                  % this.b.length
            )
            < 0
         ? -var8
         : var8;
   }

   @Override
   public final int size() {
      int var1 = 0;

      for (int var2 = 0; var2 < this.b.length; var2++) {
         synchronized (this.c[var2]) {
            var1 += this.c[var2].a;
         }
      }

      return var1;
   }

   @Override
   public final boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public final V get(Object key) {
      int var2 = this.a(key);
      synchronized (this.c[var2]) {
         for (jC.g var5 = this.b[var2]; var5 != null; var5 = var5.c) {
            if (var5.a == key || var5.a != null && var5.a.equals(key)) {
               return var5.b;
            }
         }

         return null;
      }
   }

   @Override
   public final boolean containsKey(Object key) {
      int var2 = this.a(key);
      synchronized (this.c[var2]) {
         for (jC.g var5 = this.b[var2]; var5 != null; var5 = var5.c) {
            if (var5.a == key || var5.a != null && var5.a.equals(key)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public final boolean containsValue(Object value) {
      for (int var2 = 0; var2 < this.b.length; var2++) {
         synchronized (this.c[var2]) {
            for (jC.g var4 = this.b[var2]; var4 != null; var4 = var4.c) {
               if (var4.b == value || var4.b != null && var4.b.equals(value)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Override
   public final V put(K key, V value) {
      int var3 = this.a(key);
      synchronized (this.c[var3]) {
         jC.g var5;
         if ((var5 = this.b[var3]) == null) {
            (var5 = new jC.g((byte)0)).a = key;
            var5.b = value;
            this.b[var3] = var5;
            this.c[var3].a++;
            return null;
         }

         for (jC.g var6 = var5; var6 != null; var6 = var6.c) {
            var5 = var6;
            if (var6.a == key || var5.a != null && var5.a.equals(key)) {
               key = (K)var5.b;
               var5.b = value;
               return (V)key;
            }
         }

         jC.g var10;
         (var10 = new jC.g((byte)0)).a = key;
         var10.b = value;
         var5.c = var10;
         this.c[var3].a++;
         return null;
      }
   }

   @Override
   public final V remove(Object key) {
      int var2 = this.a(key);
      synchronized (this.c[var2]) {
         jC.g var4 = this.b[var2];
         jC.g var5 = null;

         while (var4 != null) {
            if (var4.a == key || var4.a != null && var4.a.equals(key)) {
               if (var5 == null) {
                  this.b[var2] = var4.c;
               } else {
                  var5.c = var4.c;
               }

               this.c[var2].a--;
               return var4.b;
            }

            var5 = var4;
            var4 = var4.c;
         }

         return null;
      }
   }

   @Override
   public final Set<K> keySet() {
      return new jC.e((byte)0);
   }

   @Override
   public final Collection<V> values() {
      return new jC.i((byte)0);
   }

   @Override
   public final Set<Entry<K, V>> entrySet() {
      return new jC.c((byte)0);
   }

   @Override
   public final void putAll(Map<? extends K, ? extends V> map) {
      for (Entry var2 : map.entrySet()) {
         this.put((K)var2.getKey(), (V)var2.getValue());
      }
   }

   @Override
   public final void clear() {
      for (int var1 = 0; var1 < this.b.length; var1++) {
         jC.f var2;
         synchronized (var2 = this.c[var1]) {
            this.b[var1] = null;
            var2.a = 0;
         }
      }
   }

   @Override
   public final boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (!(obj instanceof Map)) {
         return false;
      }

      obj = (Map)obj;
      return this.entrySet().equals(obj.entrySet());
   }

   @Override
   public final int hashCode() {
      int var1 = 0;

      for (int var2 = 0; var2 < this.b.length; var2++) {
         synchronized (this.c[var2]) {
            for (jC.g var4 = this.b[var2]; var4 != null; var4 = var4.c) {
               var1 += var4.hashCode();
            }
         }
      }

      return var1;
   }

   private void a(Runnable var1) {
      if (var1 == null) {
         throw new NullPointerException();
      }

      this.a(var1, 0);
   }

   private void a(Runnable var1, int var2) {
      if (var2 >= this.b.length) {
         var1.run();
      } else {
         synchronized (this.c[var2]) {
            this.a(var1, var2 + 1);
         }
      }
   }

   class a {
      private final ArrayList<Entry<K, V>> a = new ArrayList<>();
      private int b;
      private Entry<K, V> c;

      private a() {
      }

      public boolean hasNext() {
         if (this.a.size() > 0) {
            return true;
         }

         while (this.b < jC.this.b.length) {
            synchronized (jC.this.c[this.b]) {
               for (jC.g var2 = jC.this.b[this.b]; var2 != null; var2 = var2.c) {
                  this.a.add(var2);
               }

               this.b++;
               if (this.a.size() > 0) {
                  return true;
               }
            }
         }

         return false;
      }

      protected final Entry<K, V> a() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         }

         this.c = this.a.remove(this.a.size() - 1);
         return this.c;
      }

      public void remove() {
         if (this.c == null) {
            throw new IllegalStateException();
         }

         jC.this.remove(this.c.getKey());
         this.c = null;
      }

      // $VF: synthetic method
      a(byte var2) {
         this();
      }
   }

   class b extends jC<K, V>.a implements Iterator<Entry<K, V>> {
      private b() {
         super((byte)0);
      }

      private Entry<K, V> b() {
         return this.a();
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         return this.a();
      }

      // $VF: synthetic method
      b(byte var2) {
         this();
      }
   }

   class c extends AbstractSet<Entry<K, V>> {
      private c() {
      }

      @Override
      public final int size() {
         return jC.this.size();
      }

      @Override
      public final void clear() {
         jC.this.clear();
      }

      @Override
      public final Iterator<Entry<K, V>> iterator() {
         return jC.this.new b((byte)0);
      }

      @Override
      public final boolean contains(Object obj) {
         obj = (Entry)obj;
         int var2 = jC.this.a(obj.getKey());
         synchronized (jC.this.c[var2]) {
            for (jC.g var6 = jC.this.b[var2]; var6 != null; var6 = var6.c) {
               if (var6.equals(obj)) {
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public final boolean remove(Object obj) {
         if (!(obj instanceof Entry)) {
            return false;
         }

         obj = (Entry)obj;
         int var2 = jC.this.a(obj.getKey());
         synchronized (jC.this.c[var2]) {
            for (jC.g var6 = jC.this.b[var2]; var6 != null; var6 = var6.c) {
               if (var6.equals(obj)) {
                  jC.this.remove(var6.getKey());
                  return true;
               }
            }

            return false;
         }
      }

      // $VF: synthetic method
      c(byte var2) {
         this();
      }
   }

   class d extends jC<K, V>.a implements Iterator<K> {
      private d() {
         super((byte)0);
      }

      @Override
      public final K next() {
         return (K)this.a().getKey();
      }

      // $VF: synthetic method
      d(byte var2) {
         this();
      }
   }

   class e extends AbstractSet<K> {
      private e() {
      }

      @Override
      public final int size() {
         return jC.this.size();
      }

      @Override
      public final void clear() {
         jC.this.clear();
      }

      @Override
      public final Iterator<K> iterator() {
         return jC.this.new d((byte)0);
      }

      @Override
      public final boolean contains(Object obj) {
         return jC.this.containsKey(obj);
      }

      @Override
      public final boolean remove(Object obj) {
         int var2 = jC.this.a(obj);
         synchronized (jC.this.c[var2]) {
            for (jC.g var6 = jC.this.b[var2]; var6 != null; var6 = var6.c) {
               Object var4;
               if ((var4 = var6.getKey()) == obj || var4 != null && var4.equals(obj)) {
                  jC.this.remove(var4);
                  return true;
               }
            }

            return false;
         }
      }

      // $VF: synthetic method
      e(byte var2) {
         this();
      }
   }

   static final class f {
      public int a;

      private f() {
      }

      // $VF: synthetic method
      f(byte var1) {
         this();
      }
   }

   static final class g<K, V> implements Entry<K, V>, fr<K, V> {
      protected K a;
      protected V b;
      protected jC.g<K, V> c;

      private g() {
      }

      @Override
      public final K getKey() {
         return this.a;
      }

      @Override
      public final V getValue() {
         return this.b;
      }

      @Override
      public final int hashCode() {
         return (this.a == null ? 0 : this.a.hashCode()) ^ (this.b == null ? 0 : this.b.hashCode());
      }

      @Override
      public final boolean equals(Object obj) {
         if (obj == this) {
            return true;
         }

         if (!(obj instanceof Entry)) {
            return false;
         }

         obj = (Entry)obj;
         return (this.a == null ? obj.getKey() == null : this.a.equals(obj.getKey()))
            && (this.b == null ? obj.getValue() == null : this.b.equals(obj.getValue()));
      }

      @Override
      public final V setValue(V obj) {
         Object var2 = this.b;
         this.b = obj;
         return (V)var2;
      }

      // $VF: synthetic method
      g(byte var1) {
         this();
      }
   }

   class h extends jC<K, V>.a implements Iterator<V> {
      private h() {
         super((byte)0);
      }

      @Override
      public final V next() {
         return (V)this.a().getValue();
      }

      // $VF: synthetic method
      h(byte var2) {
         this();
      }
   }

   class i extends AbstractCollection<V> {
      private i() {
      }

      @Override
      public final int size() {
         return jC.this.size();
      }

      @Override
      public final void clear() {
         jC.this.clear();
      }

      @Override
      public final Iterator<V> iterator() {
         return jC.this.new h((byte)0);
      }

      // $VF: synthetic method
      i(byte var2) {
         this();
      }
   }
}
