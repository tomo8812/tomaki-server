package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public abstract class gl<K, V> implements eY<K, V> {
   transient Map<K, V> a;
   transient Map<V, K> b;
   transient eY<V, K> c = null;
   private transient Set<K> d = null;
   private transient Set<V> e = null;
   private transient Set<Entry<K, V>> f = null;

   protected gl() {
   }

   protected gl(Map<K, V> var1, Map<V, K> var2) {
      this.a = var1;
      this.b = var2;
   }

   protected gl(Map<K, V> var1, Map<V, K> var2, eY<V, K> var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   protected abstract eY<V, K> a(Map<V, K> var1, Map<K, V> var2, eY<K, V> var3);

   @Override
   public V get(Object key) {
      return this.a.get(key);
   }

   @Override
   public int size() {
      return this.a.size();
   }

   @Override
   public boolean isEmpty() {
      return this.a.isEmpty();
   }

   @Override
   public boolean containsKey(Object key) {
      return this.a.containsKey(key);
   }

   @Override
   public boolean equals(Object obj) {
      return this.a.equals(obj);
   }

   @Override
   public int hashCode() {
      return this.a.hashCode();
   }

   @Override
   public String toString() {
      return this.a.toString();
   }

   @Override
   public V put(K key, V value) {
      if (this.a.containsKey(key)) {
         this.b.remove(this.a.get(key));
      }

      if (this.b.containsKey(value)) {
         this.a.remove(this.b.get(value));
      }

      Object var3 = this.a.put(key, value);
      this.b.put(value, key);
      return (V)var3;
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> map) {
      for (Entry var2 : map.entrySet()) {
         this.put((K)var2.getKey(), (V)var2.getValue());
      }
   }

   @Override
   public V remove(Object key) {
      Object var2 = null;
      if (this.a.containsKey(key)) {
         var2 = this.a.remove(key);
         this.b.remove(var2);
      }

      return (V)var2;
   }

   @Override
   public void clear() {
      this.a.clear();
      this.b.clear();
   }

   @Override
   public boolean containsValue(Object value) {
      return this.b.containsKey(value);
   }

   @Override
   public fu<K, V> c() {
      return new gl.a<>(this);
   }

   @Override
   public final K a(Object var1) {
      return this.b.get(var1);
   }

   @Override
   public final K b(Object var1) {
      Object var2 = null;
      if (this.b.containsKey(var1)) {
         var2 = this.b.remove(var1);
         this.a.remove(var2);
      }

      return (K)var2;
   }

   @Override
   public eY<V, K> a() {
      if (this.c == null) {
         this.c = this.a(this.b, this.a, this);
      }

      return this.c;
   }

   @Override
   public Set<K> keySet() {
      if (this.d == null) {
         this.d = new gl.d<>(this);
      }

      return this.d;
   }

   protected final Iterator<K> a(Iterator<K> var1) {
      return new gl.e<>(var1, this);
   }

   @Override
   public final Set<V> b() {
      if (this.e == null) {
         this.e = new gl.g<>(this);
      }

      return this.e;
   }

   protected final Iterator<V> b(Iterator<V> var1) {
      return new gl.h<>(var1, this);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      if (this.f == null) {
         this.f = new gl.b<>(this);
      }

      return this.f;
   }

   protected final Iterator<Entry<K, V>> c(Iterator<Entry<K, V>> var1) {
      return new gl.c<>(var1, this);
   }

   // $VF: synthetic method
   @Override
   public Collection values() {
      return this.b();
   }

   protected static class a<K, V> implements fJ<K>, fu<K, V> {
      private gl<K, V> a;
      private Iterator<Entry<K, V>> b;
      private Entry<K, V> c = null;
      private boolean d = false;

      protected a(gl<K, V> var1) {
         this.a = var1;
         this.b = var1.a.entrySet().iterator();
      }

      @Override
      public final boolean hasNext() {
         return this.b.hasNext();
      }

      @Override
      public final K next() {
         this.c = this.b.next();
         this.d = true;
         return this.c.getKey();
      }

      @Override
      public final void remove() {
         if (!this.d) {
            throw new IllegalStateException("Iterator remove() can only be called once after next()");
         }

         Object var1 = this.c.getValue();
         this.b.remove();
         this.a.b.remove(var1);
         this.c = null;
         this.d = false;
      }

      @Override
      public final K a() {
         if (this.c == null) {
            throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
         } else {
            return this.c.getKey();
         }
      }

      @Override
      public final V b() {
         if (this.c == null) {
            throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
         } else {
            return this.c.getValue();
         }
      }

      @Override
      public final V a(V var1) {
         if (this.c == null) {
            throw new IllegalStateException("Iterator setValue() can only be called after next() and before remove()");
         } else if (this.a.b.containsKey(var1) && this.a.b.get(var1) != this.c.getKey()) {
            throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
         } else {
            return this.a.put(this.c.getKey(), (V)var1);
         }
      }

      @Override
      public final void c() {
         this.b = this.a.a.entrySet().iterator();
         this.c = null;
         this.d = false;
      }

      @Override
      public final String toString() {
         return this.c != null ? "MapIterator[" + this.a() + "=" + this.b() + "]" : "MapIterator[]";
      }
   }

   protected static class b<K, V> extends gl.i<K, V, Entry<K, V>> implements Set<Entry<K, V>> {
      private static final long b = 4040410962603292348L;

      protected b(gl<K, V> var1) {
         super(var1.a.entrySet(), var1);
      }

      @Override
      public final Iterator<Entry<K, V>> iterator() {
         return super.a.c(super.iterator());
      }

      @Override
      public final boolean remove(Object obj) {
         if (!(obj instanceof Entry)) {
            return false;
         }

         Entry var4;
         Object var2 = (var4 = (Entry)obj).getKey();
         Object var3;
         if (!super.a.containsKey(var2) || ((var3 = super.a.a.get(var2)) == null ? var4.getValue() != null : !var3.equals(var4.getValue()))) {
            return false;
         }

         super.a.a.remove(var2);
         super.a.b.remove(var3);
         return true;
      }
   }

   protected static class c<K, V> extends hN<Entry<K, V>> {
      private gl<K, V> a;
      private Entry<K, V> b = null;
      private boolean c = false;

      protected c(Iterator<Entry<K, V>> var1, gl<K, V> var2) {
         super(var1);
         this.a = var2;
      }

      private Entry<K, V> b() {
         this.b = new gl.f<>((Entry<K, V>)super.next(), this.a);
         this.c = true;
         return this.b;
      }

      @Override
      public final void remove() {
         if (!this.c) {
            throw new IllegalStateException("Iterator remove() can only be called once after next()");
         }

         Object var1 = this.b.getValue();
         super.remove();
         this.a.b.remove(var1);
         this.b = null;
         this.c = false;
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         gl.c var1 = this;
         this.b = new gl.f<>((Entry<K, V>)var1.next(), var1.a);
         var1.c = true;
         return var1.b;
      }
   }

   protected static class d<K> extends gl.i<K, Object, K> implements Set<K> {
      private static final long b = -7107935777385040694L;

      protected d(gl<K, ?> var1) {
         super(var1.a.keySet(), var1);
      }

      @Override
      public final Iterator<K> iterator() {
         return super.a.a(super.iterator());
      }

      @Override
      public final boolean contains(Object key) {
         return super.a.a.containsKey(key);
      }

      @Override
      public final boolean remove(Object key) {
         if (super.a.a.containsKey(key)) {
            key = super.a.a.remove(key);
            super.a.b.remove(key);
            return true;
         } else {
            return false;
         }
      }
   }

   protected static class e<K> extends hN<K> {
      private gl<K, ?> a;
      private K b = (K)null;
      private boolean c = false;

      protected e(Iterator<K> var1, gl<K, ?> var2) {
         super(var1);
         this.a = var2;
      }

      @Override
      public final K next() {
         this.b = (K)super.next();
         this.c = true;
         return this.b;
      }

      @Override
      public final void remove() {
         if (!this.c) {
            throw new IllegalStateException("Iterator remove() can only be called once after next()");
         }

         Object var1 = this.a.a.get(this.b);
         super.remove();
         this.a.b.remove(var1);
         this.b = null;
         this.c = false;
      }
   }

   protected static class f<K, V> extends iG<K, V> {
      private gl<K, V> a;

      protected f(Entry<K, V> var1, gl<K, V> var2) {
         super(var1);
         this.a = var2;
      }

      @Override
      public final V setValue(V value) {
         Object var2 = this.getKey();
         if (this.a.b.containsKey(value) && this.a.b.get(value) != var2) {
            throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
         }

         this.a.put((K)var2, value);
         return super.setValue(value);
      }
   }

   protected static class g<V> extends gl.i<Object, V, V> implements Set<V> {
      private static final long b = 4023777119829639864L;

      protected g(gl<?, V> var1) {
         super(var1.a.values(), var1);
      }

      @Override
      public final Iterator<V> iterator() {
         return super.a.b(super.iterator());
      }

      @Override
      public final boolean contains(Object value) {
         return super.a.b.containsKey(value);
      }

      @Override
      public final boolean remove(Object value) {
         if (super.a.b.containsKey(value)) {
            value = super.a.b.remove(value);
            super.a.a.remove(value);
            return true;
         } else {
            return false;
         }
      }
   }

   protected static class h<V> extends hN<V> {
      private gl<Object, V> a;
      private V b = (V)null;
      private boolean c = false;

      protected h(Iterator<V> var1, gl<?, V> var2) {
         super(var1);
         this.a = var2;
      }

      @Override
      public final V next() {
         this.b = (V)super.next();
         this.c = true;
         return this.b;
      }

      @Override
      public final void remove() {
         if (!this.c) {
            throw new IllegalStateException("Iterator remove() can only be called once after next()");
         }

         super.remove();
         this.a.b.remove(this.b);
         this.b = null;
         this.c = false;
      }
   }

   protected abstract static class i<K, V, E> extends gv<E> {
      private static final long b = 4621510560119690639L;
      protected final gl<K, V> a;

      protected i(Collection<E> var1, gl<K, V> var2) {
         super(var1);
         this.a = var2;
      }

      @Override
      public boolean equals(Object object) {
         return object == this || this.f().equals(object);
      }

      @Override
      public int hashCode() {
         return this.f().hashCode();
      }

      @Override
      public boolean removeAll(Collection<?> coll) {
         if (!this.a.isEmpty() && !coll.isEmpty()) {
            boolean var2 = false;
            Iterator var3 = coll.iterator();

            while (var3.hasNext()) {
               var2 |= this.remove(var3.next());
            }

            return var2;
         } else {
            return false;
         }
      }

      @Override
      public boolean retainAll(Collection<?> coll) {
         if (this.a.isEmpty()) {
            return false;
         }

         if (coll.isEmpty()) {
            this.a.clear();
            return true;
         }

         boolean var2 = false;
         Iterator var3 = this.iterator();

         while (var3.hasNext()) {
            if (!coll.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }

      @Override
      public void clear() {
         this.a.clear();
      }
   }
}
