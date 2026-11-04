package org.valkyrienskies.core.impl.shadow;

import java.util.Collections;
import java.util.Map;

public final class er<K, V> {
   private final Map<K, V> a;

   private er(int size) {
      this.a = eg.c(size);
   }

   private static <K, V> er<K, V> a(int size) {
      return new er<>(size);
   }

   private er<K, V> a(K key, V value) {
      this.a.put((K)key, (V)value);
      return this;
   }

   private er<K, V> a(Map<K, V> map) {
      this.a.putAll(map);
      return this;
   }

   private Map<K, V> a() {
      return this.a.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(this.a);
   }
}
