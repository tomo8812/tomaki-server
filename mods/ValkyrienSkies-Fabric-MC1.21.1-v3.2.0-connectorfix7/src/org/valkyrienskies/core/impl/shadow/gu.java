package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;

public final class gu<K, V> extends gn<K, V> implements fU {
   private gu<V, K> b;

   private static <K, V> fO<K, V> a(fO<K, ? extends V> var0) {
      return var0 instanceof fU ? var0 : new gu<>(var0);
   }

   private gu(fO<K, ? extends V> var1) {
      super(var1);
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
   public final Set<V> b() {
      return kC.a(super.b());
   }

   @Override
   public final K b(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final fE<K, V> e() {
      return iC.a(this.k().e());
   }

   @Override
   public final fO<V, K> f() {
      if (this.b == null) {
         this.b = new gu<>(this.k().f());
         this.b.b = this;
      }

      return this.b;
   }

   @Override
   public final SortedMap<K, V> subMap(K fromKey, K toKey) {
      return jI.a(this.k().subMap(fromKey, toKey));
   }

   @Override
   public final SortedMap<K, V> headMap(K toKey) {
      return jI.a(this.k().headMap(toKey));
   }

   @Override
   public final SortedMap<K, V> tailMap(K fromKey) {
      return jI.a(this.k().tailMap(fromKey));
   }

   // $VF: synthetic method
   @Override
   public final fB d() {
      return this.f();
   }

   // $VF: synthetic method
   @Override
   public final eY a() {
      return this.f();
   }

   // $VF: synthetic method
   @Override
   public final Collection values() {
      return this.b();
   }

   // $VF: synthetic method
   @Override
   public final fu c() {
      return this.e();
   }
}
