package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class jx<K, V> extends ja<K, V> implements Serializable {
   private static final long d = 7412622456128415156L;
   protected final fF<? super K> b;
   protected final fF<? super V> c;

   public static <K, V> jx<K, V> a(Map<K, V> var0, fF<? super K> var1, fF<? super V> var2) {
      return new jx<>(var0, var1, var2);
   }

   protected jx(Map<K, V> var1, fF<? super K> var2, fF<? super V> var3) {
      super(var1);
      this.b = var2;
      this.c = var3;

      for (Entry var5 : var1.entrySet()) {
         this.a((K)var5.getKey(), (V)var5.getValue());
      }
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(super.a);
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      super.a = (Map<K, V>)var1.readObject();
   }

   private void a(K var1, V var2) {
      if (this.b != null && !this.b.a((K)var1)) {
         throw new IllegalArgumentException("Cannot add key - Predicate rejected it");
      }

      if (this.c != null && !this.c.a((V)var2)) {
         throw new IllegalArgumentException("Cannot add value - Predicate rejected it");
      }
   }

   @Override
   protected final V a(V var1) {
      if (!this.c.a((V)var1)) {
         throw new IllegalArgumentException("Cannot set value - Predicate rejected it");
      } else {
         return (V)var1;
      }
   }

   @Override
   protected final boolean a() {
      return this.c != null;
   }

   @Override
   public V put(K key, V value) {
      this.a(key, value);
      return super.a.put(key, value);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> mapToCopy) {
      for (Entry var3 : mapToCopy.entrySet()) {
         this.a((K)var3.getKey(), (V)var3.getValue());
      }

      super.putAll(mapToCopy);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Set entrySet() {
      return super.entrySet();
   }
}
