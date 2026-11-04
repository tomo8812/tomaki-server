package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;

public final class kK<K, V> implements Serializable, fS<K, V>, fU {
   private static final long a = -7156426030315945159L;
   private final fS<K, V> b;

   public static <K, V> fS<K, V> a(fS<K, ? extends V> var0) {
      return var0 instanceof fU ? var0 : new kK<>(var0);
   }

   private kK(fS<K, ? extends V> var1) {
      if (var1 == null) {
         throw new NullPointerException("Trie must not be null");
      }

      var1 = var1;
      this.b = var1;
   }

   @Override
   public final Set<Entry<K, V>> entrySet() {
      return Collections.unmodifiableSet(this.b.entrySet());
   }

   @Override
   public final Set<K> keySet() {
      return Collections.unmodifiableSet(this.b.keySet());
   }

   @Override
   public final Collection<V> values() {
      return Collections.unmodifiableCollection(this.b.values());
   }

   @Override
   public final void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean containsKey(Object key) {
      return this.b.containsKey(key);
   }

   @Override
   public final boolean containsValue(Object value) {
      return this.b.containsValue(value);
   }

   @Override
   public final V get(Object key) {
      return this.b.get(key);
   }

   @Override
   public final boolean isEmpty() {
      return this.b.isEmpty();
   }

   @Override
   public final V put(K key, V value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final void putAll(Map<? extends K, ? extends V> m) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final V remove(Object key) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final int size() {
      return this.b.size();
   }

   @Override
   public final K firstKey() {
      return this.b.firstKey();
   }

   @Override
   public final SortedMap<K, V> headMap(K toKey) {
      return Collections.unmodifiableSortedMap(this.b.headMap(toKey));
   }

   @Override
   public final K lastKey() {
      return this.b.lastKey();
   }

   @Override
   public final SortedMap<K, V> subMap(K fromKey, K toKey) {
      return Collections.unmodifiableSortedMap(this.b.subMap(fromKey, toKey));
   }

   @Override
   public final SortedMap<K, V> tailMap(K fromKey) {
      return Collections.unmodifiableSortedMap(this.b.tailMap(fromKey));
   }

   @Override
   public final SortedMap<K, V> a(K var1) {
      return Collections.unmodifiableSortedMap(this.b.a((K)var1));
   }

   @Override
   public final Comparator<? super K> comparator() {
      return this.b.comparator();
   }

   @Override
   public final fE<K, V> e() {
      return iC.a(this.b.e());
   }

   @Override
   public final K c(K var1) {
      return this.b.c((K)var1);
   }

   @Override
   public final K d(K var1) {
      return this.b.d((K)var1);
   }

   @Override
   public final int hashCode() {
      return this.b.hashCode();
   }

   @Override
   public final boolean equals(Object obj) {
      return this.b.equals(obj);
   }

   @Override
   public final String toString() {
      return this.b.toString();
   }

   // $VF: synthetic method
   @Override
   public final fu c() {
      return this.e();
   }
}
