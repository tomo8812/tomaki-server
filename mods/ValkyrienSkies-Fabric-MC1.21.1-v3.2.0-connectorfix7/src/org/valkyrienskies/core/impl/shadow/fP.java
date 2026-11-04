package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class fP {
   private fP() {
   }

   private static <K, V> fn<K, V> a(fl<K, V> var0) {
      if (var0 == null) {
         throw new NullPointerException("Get must not be null");
      } else if (var0 instanceof Map) {
         return var0 instanceof fn ? (fn)var0 : fv.a((Map<K, V>)var0);
      } else {
         return new fP.a<>(var0, (byte)0);
      }
   }

   private static <K, V> Map<K, V> a(fH<K, V> var0) {
      if (var0 == null) {
         throw new NullPointerException("Put must not be null");
      } else {
         return var0 instanceof Map ? (Map)var0 : new fP.b<>(var0, (byte)0);
      }
   }

   static class a<K, V> implements fU, fn<K, V> {
      private final fl<K, V> a;

      private a(fl<K, V> var1) {
         this.a = var1;
      }

      @Override
      public final void clear() {
         throw new UnsupportedOperationException();
      }

      @Override
      public final boolean containsKey(Object key) {
         return this.a.containsKey(key);
      }

      @Override
      public final boolean containsValue(Object value) {
         return this.a.containsValue(value);
      }

      @Override
      public final Set<Entry<K, V>> entrySet() {
         return jF.a(this.a.entrySet());
      }

      @Override
      public final boolean equals(Object arg0) {
         return arg0 == this ? true : arg0 instanceof fP.a && ((fP.a)arg0).a.equals(this.a);
      }

      @Override
      public final V get(Object key) {
         return this.a.get(key);
      }

      @Override
      public final int hashCode() {
         return "WrappedGet".hashCode() << 4 | this.a.hashCode();
      }

      @Override
      public final boolean isEmpty() {
         return this.a.isEmpty();
      }

      @Override
      public final Set<K> keySet() {
         return kC.a(this.a.keySet());
      }

      @Override
      public final V put(K key, V value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public final void putAll(Map<? extends K, ? extends V> t) {
         throw new UnsupportedOperationException();
      }

      @Override
      public final V remove(Object key) {
         return this.a.remove(key);
      }

      @Override
      public final int size() {
         return this.a.size();
      }

      @Override
      public final Collection<V> values() {
         return gC.b(this.a.values());
      }

      @Override
      public final fu<K, V> c() {
         fu var1;
         if (this.a instanceof fm) {
            var1 = ((fm)this.a).c();
         } else {
            var1 = new jk<>(this.a.entrySet());
         }

         return iB.a(var1);
      }

      // $VF: synthetic method
      a(fl var1, byte var2) {
         this(var1);
      }
   }

   static class b<K, V> implements Map<K, V>, fH<K, V> {
      private final fH<K, V> a;

      private b(fH<K, V> var1) {
         this.a = var1;
      }

      @Override
      public final void clear() {
         this.a.clear();
      }

      @Override
      public final boolean containsKey(Object key) {
         throw new UnsupportedOperationException();
      }

      @Override
      public final boolean containsValue(Object value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public final Set<Entry<K, V>> entrySet() {
         throw new UnsupportedOperationException();
      }

      @Override
      public final boolean equals(Object obj) {
         return obj == this ? true : obj instanceof fP.b && ((fP.b)obj).a.equals(this.a);
      }

      @Override
      public final V get(Object key) {
         throw new UnsupportedOperationException();
      }

      @Override
      public final int hashCode() {
         return "WrappedPut".hashCode() << 4 | this.a.hashCode();
      }

      @Override
      public final boolean isEmpty() {
         throw new UnsupportedOperationException();
      }

      @Override
      public final Set<K> keySet() {
         throw new UnsupportedOperationException();
      }

      @Override
      public final V put(K key, V value) {
         return (V)this.a.put(key, value);
      }

      @Override
      public final void putAll(Map<? extends K, ? extends V> t) {
         this.a.putAll(t);
      }

      @Override
      public final V remove(Object key) {
         throw new UnsupportedOperationException();
      }

      @Override
      public final int size() {
         throw new UnsupportedOperationException();
      }

      @Override
      public final Collection<V> values() {
         throw new UnsupportedOperationException();
      }

      // $VF: synthetic method
      b(fH var1, byte var2) {
         this(var1);
      }
   }
}
