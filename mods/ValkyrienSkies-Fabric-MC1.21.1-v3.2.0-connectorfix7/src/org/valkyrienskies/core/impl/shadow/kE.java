package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class kE<K, V> implements fm<K, V> {
   transient Map<K, V> a;

   public kE(Map<K, V> var1) {
      if (var1 == null) {
         throw new NullPointerException("Map must not be null.");
      }

      this.a = var1;
   }

   protected kE() {
   }

   protected final Map<K, V> a() {
      return this.a;
   }

   @Override
   public boolean containsKey(Object key) {
      return this.a.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return this.a.containsValue(value);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return this.a.entrySet();
   }

   @Override
   public V get(Object key) {
      return this.a.get(key);
   }

   @Override
   public V remove(Object key) {
      return this.a.remove(key);
   }

   @Override
   public boolean isEmpty() {
      return this.a.isEmpty();
   }

   @Override
   public Set<K> keySet() {
      return this.a.keySet();
   }

   @Override
   public int size() {
      return this.a.size();
   }

   @Override
   public Collection<V> values() {
      return this.a.values();
   }

   @Override
   public final fu<K, V> c() {
      return new jk<>(this.entrySet());
   }

   @Override
   public boolean equals(Object object) {
      return object == this ? true : this.a.equals(object);
   }

   @Override
   public int hashCode() {
      return this.a.hashCode();
   }

   @Override
   public String toString() {
      return this.a.toString();
   }
}
