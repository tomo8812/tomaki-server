package org.valkyrienskies.core.impl.shadow;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Provider;

public final class es<K, V> extends ed<K, V, V> {
   private static final ew<Map<Object, Object>> a = eo.a(Collections.emptyMap());

   private static <K, V> es.a<K, V> a(int size) {
      return new es.a<>(size, (byte)0);
   }

   private static <K, V> ew<Map<K, V>> b() {
      return (ew<Map<K, V>>)a;
   }

   private es(Map<K, ew<V>> map) {
      super(map);
   }

   private Map<K, V> c() {
      LinkedHashMap var1 = eg.c(this.a().size());

      for (Entry var3 : this.a().entrySet()) {
         var1.put(var3.getKey(), ((ew)var3.getValue()).get());
      }

      return Collections.unmodifiableMap(var1);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      es var1 = this;
      LinkedHashMap var2 = eg.c(this.a().size());

      for (Entry var3 : var1.a().entrySet()) {
         var2.put(var3.getKey(), ((ew)var3.getValue()).get());
      }

      return Collections.unmodifiableMap(var2);
   }

   // $VF: synthetic method
   es(Map var1, byte var2) {
      this(var1);
   }

   public static final class a<K, V> extends ed.a<K, V, V> {
      private a(int size) {
         super(size);
      }

      private es.a<K, V> b(K key, ew<V> providerOfValue) {
         super.a((K)key, providerOfValue);
         return this;
      }

      @Deprecated
      private es.a<K, V> a(K key, Provider<V> providerOfValue) {
         return this.b((K)key, ey.a(providerOfValue));
      }

      private es.a<K, V> b(ew<Map<K, V>> mapFactory) {
         super.a(mapFactory);
         return this;
      }

      @Deprecated
      private es.a<K, V> a(Provider<Map<K, V>> mapFactory) {
         var var2 = ey.a(mapFactory);
         Provider var3 = this;
         super.a(var2);
         return var3;
      }

      private es<K, V> a() {
         return new es<>(super.a, (byte)0);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public final ed.a a(ew mapFactory) {
         ew var2 = mapFactory;
         ew var3 = this;
         super.a(var2);
         return var3;
      }

      // $VF: synthetic method
      @Override
      public final ed.a a(Object key, ew providerOfValue) {
         return this.b((K)key, providerOfValue);
      }

      // $VF: synthetic method
      a(int var1, byte var2) {
         this(var1);
      }
   }
}
