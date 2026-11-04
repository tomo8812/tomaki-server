package org.valkyrienskies.core.impl.shadow;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Provider;

public final class et<K, V> extends ed<K, V, ew<V>> implements dT<Map<K, ew<V>>> {
   private static <K, V> et.a<K, V> a(int size) {
      return new et.a<>(size, (byte)0);
   }

   private et(Map<K, ew<V>> contributingMap) {
      super(contributingMap);
   }

   private Map<K, ew<V>> b() {
      return this.a();
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      return this.a();
   }

   // $VF: synthetic method
   et(Map var1, byte var2) {
      this(var1);
   }

   public static final class a<K, V> extends ed.a<K, V, ew<V>> {
      private a(int size) {
         super(size);
      }

      private et.a<K, V> b(K key, ew<V> providerOfValue) {
         super.a((K)key, providerOfValue);
         return this;
      }

      @Deprecated
      private et.a<K, V> a(K key, Provider<V> providerOfValue) {
         return this.b((K)key, ey.a(providerOfValue));
      }

      private et.a<K, V> b(ew<Map<K, ew<V>>> mapProviderFactory) {
         super.a(mapProviderFactory);
         return this;
      }

      @Deprecated
      private et.a<K, V> a(final Provider<Map<K, Provider<V>>> mapProviderFactory) {
         ew var2 = new ew<Map<K, ew<V>>>() {
            private Map<K, ew<V>> a() {
               Map var1;
               if ((var1 = (Map)mapProviderFactory.get()).isEmpty()) {
                  return Collections.emptyMap();
               }

               LinkedHashMap var2x = eg.c(var1.size());

               for (Entry var3 : var1.entrySet()) {
                  var2x.put(var3.getKey(), ey.a((Provider)var3.getValue()));
               }

               return Collections.unmodifiableMap(var2x);
            }

            // $VF: synthetic method
            @Override
            public final Object get() {
               Map var1;
               if ((var1 = (Map)mapProviderFactory.get()).isEmpty()) {
                  return Collections.emptyMap();
               }

               LinkedHashMap var2 = eg.c(var1.size());

               for (Entry var3 : var1.entrySet()) {
                  var2.put(var3.getKey(), ey.a((Provider)var3.getValue()));
               }

               return Collections.unmodifiableMap(var2);
            }
         };
         Provider var3 = this;
         super.a(var2);
         return var3;
      }

      private et<K, V> a() {
         return new et<>(super.a, (byte)0);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public final ed.a a(ew mapProviderFactory) {
         ew var2 = mapProviderFactory;
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
