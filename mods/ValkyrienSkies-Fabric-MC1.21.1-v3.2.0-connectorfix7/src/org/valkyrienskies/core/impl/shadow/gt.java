package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class gt<K, V> extends gm<K, V> implements fU {
   private gt<V, K> b;

   private static <K, V> fB<K, V> a(fB<? extends K, ? extends V> var0) {
      return var0 instanceof fU ? var0 : new gt<>(var0);
   }

   private gt(fB<? extends K, ? extends V> var1) {
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
   public final fB<V, K> d() {
      gt var1 = this;
      if (this.b == null) {
         var1.b = new gt<>(var1.j().d());
         var1.b.b = var1;
      }

      return var1.b;
   }

   @Override
   public final fE<K, V> e() {
      return iC.a(this.j().e());
   }

   private fB<V, K> f() {
      if (this.b == null) {
         this.b = new gt<>(this.j().d());
         this.b.b = this;
      }

      return this.b;
   }

   // $VF: synthetic method
   @Override
   public final eY a() {
      return this.d();
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
