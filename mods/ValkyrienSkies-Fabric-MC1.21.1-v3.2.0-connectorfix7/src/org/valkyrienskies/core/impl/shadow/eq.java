package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class eq<V> implements Map<Class<?>, V> {
   private final Map<String, V> a;

   public static <V> Map<Class<?>, V> a(Map<String, V> delegate) {
      return new eq<>(delegate);
   }

   private eq(Map<String, V> delegate) {
      this.a = delegate;
   }

   @Override
   public final V get(Object key) {
      if (!(key instanceof Class)) {
         throw new IllegalArgumentException("Key must be a class");
      } else {
         return this.a.get(((Class)key).getName());
      }
   }

   @Override
   public final Set<Class<?>> keySet() {
      throw new UnsupportedOperationException("Maps created with @LazyClassKey do not support usage of keySet(). Consider @ClassKey instead.");
   }

   @Override
   public final Collection<V> values() {
      return this.a.values();
   }

   @Override
   public final boolean isEmpty() {
      return this.a.isEmpty();
   }

   @Override
   public final boolean containsKey(@Cx Object key) {
      if (!(key instanceof Class)) {
         throw new IllegalArgumentException("Key must be a class");
      } else {
         return this.a.containsKey(((Class)key).getName());
      }
   }

   @Override
   public final boolean containsValue(@Cx Object value) {
      return this.a.containsValue(value);
   }

   @Override
   public final int size() {
      return this.a.size();
   }

   @Override
   public final Set<Entry<Class<?>, V>> entrySet() {
      throw new UnsupportedOperationException("Maps created with @LazyClassKey do not support usage of entrySet(). Consider @ClassKey instead.");
   }

   @Override
   public final V remove(@Cx Object key) {
      throw new UnsupportedOperationException("Dagger map bindings are immutable");
   }

   @Override
   public final void clear() {
      throw new UnsupportedOperationException("Dagger map bindings are immutable");
   }

   private static V a() {
      throw new UnsupportedOperationException("Dagger map bindings are immutable");
   }

   @Override
   public final void putAll(Map<? extends Class<?>, ? extends V> map) {
      throw new UnsupportedOperationException("Dagger map bindings are immutable");
   }

   // $VF: synthetic method
   @Override
   public final Object put(Object key, Object value) {
      throw new UnsupportedOperationException("Dagger map bindings are immutable");
   }

   public static class a<V> implements ek<Map<Class<?>, V>> {
      private ek<Map<String, V>> a;

      private static <V> eq.a<V> a(ek<Map<String, V>> delegate) {
         return new eq.a<>(delegate);
      }

      private a(ek<Map<String, V>> delegate) {
         this.a = delegate;
      }

      private Map<Class<?>, V> a() {
         return eq.a(this.a.get());
      }

      // $VF: synthetic method
      @Override
      public final Object get() {
         return eq.a(this.a.get());
      }
   }

   public static class b<V> implements ek<Map<Class<?>, ew<V>>> {
      private ek<Map<String, ew<V>>> a;

      private static <V> eq.b<V> a(ek<Map<String, ew<V>>> delegate) {
         return new eq.b<>(delegate);
      }

      private b(ek<Map<String, ew<V>>> delegate) {
         this.a = delegate;
      }

      private Map<Class<?>, ew<V>> a() {
         return eq.a(this.a.get());
      }

      // $VF: synthetic method
      @Override
      public final Object get() {
         return eq.a(this.a.get());
      }
   }
}
