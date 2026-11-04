package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class gs<K, V> extends gk<K, V> implements fU {
   private gs<V, K> b;

   private static <K, V> eY<K, V> a(eY<? extends K, ? extends V> var0) {
      return var0 instanceof fU ? var0 : new gs<>(var0);
   }

   private gs(eY<? extends K, ? extends V> var1) {
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
   public final fu<K, V> c() {
      return iB.a(this.h().c());
   }

   @Override
   public final synchronized eY<V, K> a() {
      if (this.b == null) {
         this.b = new gs<>(this.h().a());
         this.b.b = this;
      }

      return this.b;
   }

   // $VF: synthetic method
   @Override
   public final Collection values() {
      return this.b();
   }
}
