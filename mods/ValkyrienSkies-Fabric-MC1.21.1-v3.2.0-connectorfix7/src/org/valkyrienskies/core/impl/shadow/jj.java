package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public final class jj<K, V> extends jd<K, V> implements Serializable {
   private static final long b = 19698628745827L;
   private final fQ<? super K, ? extends V> c;

   private static <K, V> jj<K, V> a(Map<K, V> var0, V var1) {
      return new jj<>(var0, gV.b((V)var1));
   }

   private static <K, V> jj<K, V> a(Map<K, V> var0, fh<? extends V> var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Factory must not be null");
      } else {
         return new jj<>(var0, hc.a(var1));
      }
   }

   private static <K, V> Map<K, V> a(Map<K, V> var0, fQ<? super K, ? extends V> var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Transformer must not be null");
      } else {
         return new jj<>(var0, var1);
      }
   }

   private jj(V var1) {
      this(gV.b((V)var1));
   }

   private jj(fQ<? super K, ? extends V> var1) {
      this(new HashMap<>(), var1);
   }

   private jj(Map<K, V> var1, fQ<? super K, ? extends V> var2) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("Transformer must not be null.");
      }

      this.c = var2;
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
   public final V get(Object key) {
      Object var2;
      return (V)((var2 = super.a.get(key)) == null && !super.a.containsKey(key) ? this.c.a((K)key) : var2);
   }
}
