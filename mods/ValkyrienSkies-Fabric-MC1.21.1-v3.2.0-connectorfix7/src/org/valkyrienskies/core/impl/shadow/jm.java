package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;

public final class jm<K, V> extends jg<K, V> implements Serializable, fa<K, V> {
   private static final long b = 3126019624511683653L;

   public static <K, V> jm<K, V> a(SortedMap<K, V> var0) {
      return new jm<>(var0);
   }

   private jm(SortedMap<K, V> var1) {
      super(var1);
   }

   private SortedMap<K, V> d() {
      return (SortedMap<K, V>)super.a;
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(super.a);
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      super.a = (Map<K, V>)var1.readObject();
   }

   @Override
   public final V put(K key, V value) {
      if (!super.a.containsKey(key)) {
         throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
      } else {
         return super.a.put(key, value);
      }
   }

   @Override
   public final void putAll(Map<? extends K, ? extends V> mapToCopy) {
      if (fd.a(mapToCopy.keySet(), this.keySet())) {
         throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
      }

      super.a.putAll(mapToCopy);
   }

   @Override
   public final void clear() {
      throw new UnsupportedOperationException("Map is fixed size");
   }

   @Override
   public final V remove(Object key) {
      throw new UnsupportedOperationException("Map is fixed size");
   }

   @Override
   public final Set<Entry<K, V>> entrySet() {
      return kC.a(super.a.entrySet());
   }

   @Override
   public final Set<K> keySet() {
      return kC.a(super.a.keySet());
   }

   @Override
   public final Collection<V> values() {
      return gC.b(super.a.values());
   }

   @Override
   public final SortedMap<K, V> subMap(K fromKey, K toKey) {
      return new jm<>(((SortedMap)super.a).subMap(fromKey, toKey));
   }

   @Override
   public final SortedMap<K, V> headMap(K toKey) {
      return new jm<>(((SortedMap)super.a).headMap(toKey));
   }

   @Override
   public final SortedMap<K, V> tailMap(K fromKey) {
      return new jm<>(((SortedMap)super.a).tailMap(fromKey));
   }

   @Override
   public final boolean a() {
      return true;
   }

   @Override
   public final int b() {
      return this.size();
   }
}
