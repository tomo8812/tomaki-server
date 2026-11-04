package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

public final class kF<J, K, U, V> extends kE<K, V> implements Serializable, fH<J, U> {
   private static final long b = 5966875321133456994L;
   private final fQ<? super J, ? extends K> c;
   private final fQ<? super U, ? extends V> d;

   private static <J, K, U, V> kF<J, K, U, V> a(Map<K, V> var0, fQ<? super J, ? extends K> var1, fQ<? super U, ? extends V> var2) {
      return new kF<>(var0, var1, var2);
   }

   private kF(Map<K, V> var1, fQ<? super J, ? extends K> var2, fQ<? super U, ? extends V> var3) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("KeyTransformer must not be null.");
      }

      this.c = var2;
      if (var3 == null) {
         throw new NullPointerException("ValueTransformer must not be null.");
      }

      this.d = var3;
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(super.a);
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      super.a = (Map<K, V>)var1.readObject();
   }

   private K a(J var1) {
      return (K)this.c.a((J)var1);
   }

   private V b(U var1) {
      return (V)this.d.a((U)var1);
   }

   private Map<K, V> a(Map<? extends J, ? extends U> var1) {
      if (var1.isEmpty()) {
         return var1;
      }

      js var2 = new js(var1.size());

      for (Entry var3 : var1.entrySet()) {
         var2.put(this.a((J)var3.getKey()), this.b((U)var3.getValue()));
      }

      return var2;
   }

   private V c(U var1) {
      return (V)this.d.a((U)var1);
   }

   @Override
   public final V put(J key, U value) {
      return super.a.put(this.a(key), this.b(value));
   }

   @Override
   public final void putAll(Map<? extends J, ? extends U> mapToCopy) {
      Map var10000 = super.a;
      Map var2 = mapToCopy;
      kF var5 = this;
      Map var10001;
      if (var2.isEmpty()) {
         var10001 = var2;
      } else {
         js var3 = new js(var2.size());

         for (Entry var4 : var2.entrySet()) {
            var3.put(var5.a(var4.getKey()), var5.b(var4.getValue()));
         }

         var10001 = var3;
      }

      var10000.putAll(var10001);
   }

   @Override
   public final void clear() {
      super.a.clear();
   }
}
