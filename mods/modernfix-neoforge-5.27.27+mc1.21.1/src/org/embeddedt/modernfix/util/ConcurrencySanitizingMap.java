package org.embeddedt.modernfix.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConcurrencySanitizingMap<K, V> implements Map<K, V> {
   private final Map<K, V> map;
   private final Thread owner;

   public ConcurrencySanitizingMap(Map<K, V> map, Thread owner) {
      this.map = map;
      this.owner = owner;
   }

   private void checkThread() {
      Thread current = Thread.currentThread();
      if (current != this.owner) {
         throw new IllegalStateException("Map is being accessed on thread " + current + " while owned by " + this.owner);
      }
   }

   @Override
   public int size() {
      this.checkThread();
      return this.map.size();
   }

   @Override
   public boolean isEmpty() {
      this.checkThread();
      return this.map.isEmpty();
   }

   @Override
   public boolean containsKey(Object key) {
      this.checkThread();
      return this.map.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      this.checkThread();
      return this.map.containsValue(value);
   }

   @Override
   public V get(Object key) {
      this.checkThread();
      return this.map.get(key);
   }

   @Nullable
   @Override
   public V put(K key, V value) {
      this.checkThread();
      return this.map.put(key, value);
   }

   @Override
   public V remove(Object key) {
      this.checkThread();
      return this.map.remove(key);
   }

   @Override
   public void putAll(@NotNull Map<? extends K, ? extends V> m) {
      this.checkThread();
      this.map.putAll(m);
   }

   @Override
   public void clear() {
      this.checkThread();
      this.map.clear();
   }

   @NotNull
   @Override
   public Set<K> keySet() {
      this.checkThread();
      return this.map.keySet();
   }

   @NotNull
   @Override
   public Collection<V> values() {
      this.checkThread();
      return this.map.values();
   }

   @NotNull
   @Override
   public Set<Entry<K, V>> entrySet() {
      this.checkThread();
      return this.map.entrySet();
   }
}
