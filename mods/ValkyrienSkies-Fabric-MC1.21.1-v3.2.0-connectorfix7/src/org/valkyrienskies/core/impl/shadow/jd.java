package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public abstract class jd<K, V> extends jb<K, V> {
   transient Map<K, V> a;

   protected jd() {
   }

   public jd(Map<K, V> var1) {
      if (var1 == null) {
         throw new NullPointerException("Map must not be null.");
      }

      this.a = var1;
   }

   public Map<K, V> i() {
      return this.a;
   }

   @Override
   public void clear() {
      this.i().clear();
   }

   @Override
   public boolean containsKey(Object key) {
      return this.i().containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return this.i().containsValue(value);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return this.i().entrySet();
   }

   @Override
   public V get(Object key) {
      return this.i().get(key);
   }

   @Override
   public boolean isEmpty() {
      return this.i().isEmpty();
   }

   @Override
   public Set<K> keySet() {
      return this.i().keySet();
   }

   @Override
   public V put(K key, V value) {
      return this.i().put(key, value);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> mapToCopy) {
      this.i().putAll(mapToCopy);
   }

   @Override
   public V remove(Object key) {
      return this.i().remove(key);
   }

   @Override
   public int size() {
      return this.i().size();
   }

   @Override
   public Collection<V> values() {
      return this.i().values();
   }

   @Override
   public boolean equals(Object object) {
      return object == this ? true : this.i().equals(object);
   }

   @Override
   public int hashCode() {
      return this.i().hashCode();
   }

   @Override
   public String toString() {
      return this.i().toString();
   }
}
