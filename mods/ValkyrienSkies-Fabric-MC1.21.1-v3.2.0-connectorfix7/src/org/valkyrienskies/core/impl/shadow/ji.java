package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class ji<K, V> extends jb<K, V> implements Serializable {
   private static final long a = -6096931280583808322L;
   private Map<K, V>[] b;
   private ji.a<K, V> c;

   public ji() {
      this(new Map[0], null);
   }

   private ji(Map<K, V> var1, Map<K, V> var2) {
      this(new Map[]{var1, var2}, null);
   }

   private ji(Map<K, V> var1, Map<K, V> var2, ji.a<K, V> var3) {
      this(new Map[]{var1, var2}, var3);
   }

   private ji(Map<K, V>... var1) {
      this(var1, null);
   }

   private ji(Map<K, V>[] var1, ji.a<K, V> var2) {
      this.c = var2;
      this.b = new Map[0];

      for (int var7 = var1.length - 1; var7 >= 0; var7--) {
         Map var4 = var1[var7];
         ji var3 = this;

         for (int var5 = this.b.length - 1; var5 >= 0; var5--) {
            if (fd.a(var3.b[var5].keySet(), var4.keySet()).size() != 0) {
               if (var3.c == null) {
                  throw new IllegalArgumentException("Key collision adding Map to CompositeMap");
               }

               Map var10000 = var3.b[var5];
            }
         }

         Map[] var8 = new Map[var3.b.length + 1];
         System.arraycopy(var3.b, 0, var8, 0, var3.b.length);
         var8[var8.length - 1] = var4;
         var3.b = var8;
      }
   }

   private void a(ji.a<K, V> var1) {
      this.c = var1;
   }

   private void a(Map<K, V> var1) {
      for (int var2 = this.b.length - 1; var2 >= 0; var2--) {
         if (fd.a(this.b[var2].keySet(), var1.keySet()).size() != 0) {
            if (this.c == null) {
               throw new IllegalArgumentException("Key collision adding Map to CompositeMap");
            }

            Map var10000 = this.b[var2];
         }
      }

      Map[] var4 = new Map[this.b.length + 1];
      System.arraycopy(this.b, 0, var4, 0, this.b.length);
      var4[var4.length - 1] = var1;
      this.b = var4;
   }

   private Map<K, V> b(Map<K, V> var1) {
      int var2 = this.b.length;

      for (int var3 = 0; var3 < var2; var3++) {
         if (this.b[var3].equals(var1)) {
            Map[] var4 = new Map[var2 - 1];
            System.arraycopy(this.b, 0, var4, 0, var3);
            System.arraycopy(this.b, var3 + 1, var4, var3, var2 - var3 - 1);
            this.b = var4;
            return var1;
         }
      }

      return null;
   }

   @Override
   public final void clear() {
      for (int var1 = this.b.length - 1; var1 >= 0; var1--) {
         this.b[var1].clear();
      }
   }

   @Override
   public final boolean containsKey(Object key) {
      for (int var2 = this.b.length - 1; var2 >= 0; var2--) {
         if (this.b[var2].containsKey(key)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final boolean containsValue(Object value) {
      for (int var2 = this.b.length - 1; var2 >= 0; var2--) {
         if (this.b[var2].containsValue(value)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final Set<Entry<K, V>> entrySet() {
      ks var1 = new ks();

      for (int var2 = this.b.length - 1; var2 >= 0; var2--) {
         var1.a(this.b[var2].entrySet());
      }

      return var1;
   }

   @Override
   public final V get(Object key) {
      for (int var2 = this.b.length - 1; var2 >= 0; var2--) {
         if (this.b[var2].containsKey(key)) {
            return this.b[var2].get(key);
         }
      }

      return null;
   }

   @Override
   public final boolean isEmpty() {
      for (int var1 = this.b.length - 1; var1 >= 0; var1--) {
         if (!this.b[var1].isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public final Set<K> keySet() {
      ks var1 = new ks();

      for (int var2 = this.b.length - 1; var2 >= 0; var2--) {
         var1.a(this.b[var2].keySet());
      }

      return var1;
   }

   @Override
   public final V put(K key, V value) {
      if (this.c == null) {
         throw new UnsupportedOperationException("No mutator specified");
      } else {
         return this.c.b();
      }
   }

   @Override
   public final void putAll(Map<? extends K, ? extends V> map) {
      if (this.c == null) {
         throw new UnsupportedOperationException("No mutator specified");
      }
   }

   @Override
   public final V remove(Object key) {
      for (int var2 = this.b.length - 1; var2 >= 0; var2--) {
         if (this.b[var2].containsKey(key)) {
            return this.b[var2].remove(key);
         }
      }

      return null;
   }

   @Override
   public final int size() {
      int var1 = 0;

      for (int var2 = this.b.length - 1; var2 >= 0; var2--) {
         var1 += this.b[var2].size();
      }

      return var1;
   }

   @Override
   public final Collection<V> values() {
      gw var1 = new gw();

      for (int var2 = this.b.length - 1; var2 >= 0; var2--) {
         var1.a(this.b[var2].values());
      }

      return var1;
   }

   @Override
   public final boolean equals(Object obj) {
      if (obj instanceof Map) {
         obj = (Map)obj;
         return this.entrySet().equals(obj.entrySet());
      } else {
         return false;
      }
   }

   @Override
   public final int hashCode() {
      int var1 = 0;

      for (Entry var3 : this.entrySet()) {
         var1 += var3.hashCode();
      }

      return var1;
   }

   public interface a<K, V> extends Serializable {
      void a();

      V b();

      void c();
   }
}
