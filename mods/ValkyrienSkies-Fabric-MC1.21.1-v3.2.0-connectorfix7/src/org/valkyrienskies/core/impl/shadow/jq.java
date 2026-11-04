package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class jq<K, V> extends jd<K, V> implements Serializable {
   private static final long c = 7990956402564206740L;
   protected final fQ<? super K, ? extends V> b;

   public static <K, V> jq<K, V> a(Map<K, V> var0, fh<? extends V> var1) {
      return new jq<>(var0, var1);
   }

   public static <V, K> jq<K, V> a(Map<K, V> var0, fQ<? super K, ? extends V> var1) {
      return new jq<>(var0, var1);
   }

   protected jq(Map<K, V> var1, fh<? extends V> var2) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("Factory must not be null");
      }

      this.b = hc.a(var2);
   }

   protected jq(Map<K, V> var1, fQ<? super K, ? extends V> var2) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("Factory must not be null");
      }

      this.b = var2;
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
   public V get(Object key) {
      if (!super.a.containsKey(key)) {
         key = key;
         Object var2 = this.b.a((K)key);
         super.a.put((K)key, (V)var2);
         return (V)var2;
      } else {
         return super.a.get(key);
      }
   }
}
