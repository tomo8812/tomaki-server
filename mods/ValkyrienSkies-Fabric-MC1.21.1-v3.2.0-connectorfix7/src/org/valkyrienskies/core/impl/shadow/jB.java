package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class jB<K, V> implements Serializable, Cloneable, fD<K, V>, fa<K, V>, fr<K, V> {
   private static final long a = -8931271118676803261L;
   private final K b;
   private V c;

   public jB() {
      this.b = null;
   }

   private jB(K var1, V var2) {
      this.b = (K)var1;
      this.c = (V)var2;
   }

   private jB(fr<K, V> var1) {
      this.b = (K)var1.getKey();
      this.c = (V)var1.getValue();
   }

   private jB(Entry<? extends K, ? extends V> var1) {
      this.b = (K)var1.getKey();
      this.c = (V)var1.getValue();
   }

   private jB(Map<? extends K, ? extends V> var1) {
      if (var1.size() != 1) {
         throw new IllegalArgumentException("The map size must be 1");
      }

      Entry var2 = (Entry)var1.entrySet().iterator().next();
      this.b = (K)var2.getKey();
      this.c = (V)var2.getValue();
   }

   @Override
   public final K getKey() {
      return this.b;
   }

   @Override
   public final V getValue() {
      return this.c;
   }

   public final V a(V var1) {
      Object var2 = this.c;
      this.c = (V)var1;
      return (V)var2;
   }

   @Override
   public final boolean a() {
      return true;
   }

   @Override
   public final int b() {
      return 1;
   }

   @Override
   public final V get(Object key) {
      return this.b(key) ? this.c : null;
   }

   @Override
   public final int size() {
      return 1;
   }

   @Override
   public final boolean isEmpty() {
      return false;
   }

   @Override
   public final boolean containsKey(Object key) {
      return this.b(key);
   }

   @Override
   public final boolean containsValue(Object value) {
      return this.e(value);
   }

   @Override
   public final V put(K key, V value) {
      if (this.b(key)) {
         return this.a(value);
      } else {
         throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size singleton");
      }
   }

   @Override
   public final void putAll(Map<? extends K, ? extends V> map) {
      switch (map.size()) {
         case 0:
            return;
         case 1:
            Entry var2 = map.entrySet().iterator().next();
            this.put((K)var2.getKey(), (V)var2.getValue());
            return;
         default:
            throw new IllegalArgumentException("The map size must be 0 or 1");
      }
   }

   @Override
   public final V remove(Object key) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public final Set<Entry<K, V>> entrySet() {
      return Collections.singleton(new iK<>(this, this.getKey()));
   }

   @Override
   public final Set<K> keySet() {
      return Collections.singleton(this.b);
   }

   @Override
   public final Collection<V> values() {
      return new jB.b<>(this);
   }

   @Override
   public final fE<K, V> e() {
      return new jB.a<>(this);
   }

   @Override
   public final K firstKey() {
      return this.getKey();
   }

   @Override
   public final K lastKey() {
      return this.getKey();
   }

   @Override
   public final K c(K var1) {
      return null;
   }

   @Override
   public final K d(K var1) {
      return null;
   }

   private boolean b(Object var1) {
      return var1 == null ? this.getKey() == null : var1.equals(this.getKey());
   }

   private boolean e(Object var1) {
      return var1 == null ? this.getValue() == null : var1.equals(this.getValue());
   }

   private jB<K, V> d() {
      try {
         return (jB<K, V>)super.clone();
      } catch (CloneNotSupportedException var1) {
         throw new InternalError();
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

      if ((obj = (Map)obj).size() != 1) {
         return false;
      }

      Entry var3 = (Entry)obj.entrySet().iterator().next();
      return this.b(var3.getKey()) && this.e(var3.getValue());
   }

   @Override
   public final int hashCode() {
      return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
   }

   @Override
   public final String toString() {
      return new StringBuilder(128)
         .append('{')
         .append(this.getKey() == this ? "(this Map)" : this.getKey())
         .append('=')
         .append(this.getValue() == this ? "(this Map)" : this.getValue())
         .append('}')
         .toString();
   }

   // $VF: synthetic method
   @Override
   public final Object clone() {
      return this.d();
   }

   // $VF: synthetic method
   @Override
   public final fu c() {
      return this.e();
   }

   static class a<K, V> implements fE<K, V>, fJ<K> {
      private final jB<K, V> a;
      private boolean b = true;
      private boolean c = false;

      a(jB<K, V> var1) {
         this.a = var1;
      }

      @Override
      public final boolean hasNext() {
         return this.b;
      }

      @Override
      public final K next() {
         if (!this.b) {
            throw new NoSuchElementException("No next() entry in the iteration");
         }

         this.b = false;
         this.c = true;
         return this.a.getKey();
      }

      @Override
      public final boolean hasPrevious() {
         return !this.b;
      }

      @Override
      public final K previous() {
         if (this.b) {
            throw new NoSuchElementException("No previous() entry in the iteration");
         }

         this.b = true;
         return this.a.getKey();
      }

      @Override
      public final void remove() {
         throw new UnsupportedOperationException();
      }

      @Override
      public final K a() {
         if (!this.c) {
            throw new IllegalStateException("getKey() can only be called after next() and before remove()");
         } else {
            return this.a.getKey();
         }
      }

      @Override
      public final V b() {
         if (!this.c) {
            throw new IllegalStateException("getValue() can only be called after next() and before remove()");
         } else {
            return this.a.getValue();
         }
      }

      @Override
      public final V a(V var1) {
         if (!this.c) {
            throw new IllegalStateException("setValue() can only be called after next() and before remove()");
         } else {
            return this.a.a((V)var1);
         }
      }

      @Override
      public final void c() {
         this.b = true;
      }

      @Override
      public final String toString() {
         return this.b ? "Iterator[]" : "Iterator[" + this.a() + "=" + this.b() + "]";
      }
   }

   static class b<V> extends AbstractSet<V> implements Serializable {
      private static final long a = -3689524741863047872L;
      private final jB<?, V> b;

      b(jB<?, V> var1) {
         this.b = var1;
      }

      @Override
      public final int size() {
         return 1;
      }

      @Override
      public final boolean isEmpty() {
         return false;
      }

      @Override
      public final boolean contains(Object object) {
         return this.b.containsValue(object);
      }

      @Override
      public final void clear() {
         throw new UnsupportedOperationException();
      }

      @Override
      public final Iterator<V> iterator() {
         return new iu<>(this.b.getValue(), false);
      }
   }
}
