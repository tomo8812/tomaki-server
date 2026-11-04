package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;

public final class jI<K, V> extends jg<K, V> implements Serializable, fU {
   private static final long b = 5805344239827376360L;

   public static <K, V> SortedMap<K, V> a(SortedMap<K, ? extends V> var0) {
      return var0 instanceof fU ? var0 : new jI<>(var0);
   }

   private jI(SortedMap<K, ? extends V> var1) {
      super(var1);
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
   public final void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public final V put(K key, V value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final void putAll(Map<? extends K, ? extends V> mapToCopy) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final V remove(Object key) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final Set<Entry<K, V>> entrySet() {
      return jF.a(super.entrySet());
   }

   @Override
   public final Set<K> keySet() {
      return kC.a(super.keySet());
   }

   @Override
   public final Collection<V> values() {
      return gC.b(super.values());
   }

   @Override
   public final K firstKey() {
      return this.c_().firstKey();
   }

   @Override
   public final K lastKey() {
      return this.c_().lastKey();
   }

   @Override
   public final Comparator<? super K> comparator() {
      return this.c_().comparator();
   }

   @Override
   public final SortedMap<K, V> subMap(K fromKey, K toKey) {
      return new jI<>(this.c_().subMap(fromKey, toKey));
   }

   @Override
   public final SortedMap<K, V> headMap(K toKey) {
      return new jI<>(this.c_().headMap(toKey));
   }

   @Override
   public final SortedMap<K, V> tailMap(K fromKey) {
      return new jI<>(this.c_().tailMap(fromKey));
   }
}
