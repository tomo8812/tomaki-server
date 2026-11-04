package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public final class jw<K, V> extends jd<K, V> implements Serializable {
   private static final long b = 1L;
   private final Map<Object, Long> c = new HashMap<>();
   private final jw.b<K, V> d;

   private static long b(long var0, TimeUnit var2) {
      if (var2 == null) {
         throw new NullPointerException("Time unit must not be null");
      } else {
         return TimeUnit.MILLISECONDS.convert(var0, var2);
      }
   }

   public jw() {
      this(-1L);
   }

   private jw(jw.b<K, V> var1) {
      this(var1, new HashMap<>());
   }

   private jw(jw.b<K, V> var1, Map<K, V> var2) {
      super(var2);
      if (var1 == null) {
         throw new NullPointerException("Policy must not be null.");
      }

      this.d = var1;
   }

   private jw(long var1) {
      this(new jw.a<>(var1), new HashMap<>());
   }

   private jw(long var1, Map<K, V> var3) {
      this(new jw.a<>(var1), var3);
   }

   private jw(long var1, TimeUnit var3) {
      this(b(var1, var3));
   }

   private jw(long var1, TimeUnit var3, Map<K, V> var4) {
      this(b(var1, var3), var4);
   }

   private jw(Map<K, V> var1) {
      this(-1L, var1);
   }

   @Override
   public final void clear() {
      super.clear();
      this.c.clear();
   }

   @Override
   public final boolean containsKey(Object key) {
      this.a(key, System.currentTimeMillis());
      return super.containsKey(key);
   }

   @Override
   public final boolean containsValue(Object value) {
      this.a(System.currentTimeMillis());
      return super.containsValue(value);
   }

   @Override
   public final Set<Entry<K, V>> entrySet() {
      this.a(System.currentTimeMillis());
      return super.entrySet();
   }

   @Override
   public final V get(Object key) {
      this.a(key, System.currentTimeMillis());
      return super.get(key);
   }

   @Override
   public final boolean isEmpty() {
      this.a(System.currentTimeMillis());
      return super.isEmpty();
   }

   private static boolean a(long var0, Long var2) {
      long var3;
      return var2 != null ? (var3 = var2) >= 0L && var0 >= var3 : false;
   }

   @Override
   public final Set<K> keySet() {
      this.a(System.currentTimeMillis());
      return super.keySet();
   }

   private static long a() {
      return System.currentTimeMillis();
   }

   @Override
   public final V put(K key, V value) {
      long var3 = this.d.a();
      this.c.put(key, var3);
      return super.put(key, value);
   }

   @Override
   public final void putAll(Map<? extends K, ? extends V> mapToCopy) {
      for (Entry var2 : mapToCopy.entrySet()) {
         this.put((K)var2.getKey(), (V)var2.getValue());
      }
   }

   @Override
   public final V remove(Object key) {
      this.c.remove(key);
      return super.remove(key);
   }

   private void a(long var1) {
      Iterator var3 = this.c.entrySet().iterator();

      while (var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (a(var1, (Long)var4.getValue())) {
            super.remove(var4.getKey());
            var3.remove();
         }
      }
   }

   private void a(Object var1, long var2) {
      Long var4 = this.c.get(var1);
      if (a(var2, var4)) {
         this.remove(var1);
      }
   }

   @Override
   public final int size() {
      this.a(System.currentTimeMillis());
      return super.size();
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      super.a = (Map<K, V>)var1.readObject();
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(super.a);
   }

   @Override
   public final Collection<V> values() {
      this.a(System.currentTimeMillis());
      return super.values();
   }

   public static class a<K, V> implements jw.b<K, V> {
      private static final long a = 1L;
      private final long b;

      public a() {
         this(-1L);
      }

      public a(long var1) {
         this.b = var1;
      }

      private a(long var1, TimeUnit var3) {
         this(jw.b(var1, var3));
      }

      @Override
      public final long a() {
         if (this.b >= 0L) {
            long var1;
            return (var1 = System.currentTimeMillis()) > Long.MAX_VALUE - this.b ? -1L : var1 + this.b;
         } else {
            return -1L;
         }
      }
   }

   public interface b<K, V> extends Serializable {
      long a();
   }
}
