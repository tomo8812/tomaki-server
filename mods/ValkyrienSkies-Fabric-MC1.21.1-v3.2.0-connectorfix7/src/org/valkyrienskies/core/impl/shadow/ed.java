package org.valkyrienskies.core.impl.shadow;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

abstract class ed<K, V, V2> implements ek<Map<K, V2>> {
   private final Map<K, ew<V>> a;

   ed(Map<K, ew<V>> map) {
      this.a = Collections.unmodifiableMap(map);
   }

   final Map<K, ew<V>> a() {
      return this.a;
   }

   public abstract static class a<K, V, V2> {
      final LinkedHashMap<K, ew<V>> a;

      a(int size) {
         this.a = eg.c(size);
      }

      ed.a<K, V, V2> a(K key, ew<V> providerOfValue) {
         this.a.put(ev.a((K)key, "key"), ev.a(providerOfValue, "provider"));
         return this;
      }

      ed.a<K, V, V2> a(ew<Map<K, V2>> mapOfProviders) {
         if (mapOfProviders instanceof ei) {
            ew var3 = (ei)mapOfProviders;
            return this.a(ev.a(var3.a));
         } else {
            ew var2 = (ed)mapOfProviders;
            this.a.putAll(var2.a);
            return this;
         }
      }
   }
}
