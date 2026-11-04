package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class jl<K, V> extends jd<K, V> implements Serializable, fa<K, V> {
   private static final long b = 7450927208116179316L;

   public static <K, V> jl<K, V> a(Map<K, V> var0) {
      return new jl<>(var0);
   }

   private jl(Map<K, V> var1) {
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
   public final V put(K key, V value) {
      if (!super.a.containsKey(key)) {
         throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
      } else {
         return super.a.put(key, value);
      }
   }

   @Override
   public final void putAll(Map<? extends K, ? extends V> mapToCopy) {
      for (Object var3 : mapToCopy.keySet()) {
         if (!this.containsKey(var3)) {
            throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
         }
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
   public final boolean a() {
      return true;
   }

   @Override
   public final int b() {
      return this.size();
   }
}
