package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class jG<K, V> extends jd<K, V> implements Serializable, fU {
   private static final long b = 2737023427269031941L;

   public static <K, V> Map<K, V> a(Map<? extends K, ? extends V> var0) {
      return var0 instanceof fU ? var0 : new jG<>(var0);
   }

   private jG(Map<? extends K, ? extends V> var1) {
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
   public final fu<K, V> c() {
      return super.a instanceof fn ? iB.a(((fn)super.a).c()) : iB.a(new ib<>(super.a));
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
}
