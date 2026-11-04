package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class jD<K, V> extends ja<K, V> implements Serializable {
   private static final long d = 7023152376788900464L;
   protected final fQ<? super K, ? extends K> b;
   protected final fQ<? super V, ? extends V> c;

   public static <K, V> jD<K, V> a(Map<K, V> var0, fQ<? super K, ? extends K> var1, fQ<? super V, ? extends V> var2) {
      return new jD<>(var0, var1, var2);
   }

   private static <K, V> jD<K, V> b(Map<K, V> var0, fQ<? super K, ? extends K> var1, fQ<? super V, ? extends V> var2) {
      jD var4 = new jD(var0, var1, var2);
      if (var0.size() > 0) {
         var0 = var4.a(var0);
         var4.clear();
         var4.i().putAll(var0);
      }

      return var4;
   }

   protected jD(Map<K, V> var1, fQ<? super K, ? extends K> var2, fQ<? super V, ? extends V> var3) {
      super(var1);
      this.b = var2;
      this.c = var3;
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(super.a);
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      super.a = (Map<K, V>)var1.readObject();
   }

   private K b(K var1) {
      return (K)(this.b == null ? var1 : this.b.a((K)var1));
   }

   private V c(V var1) {
      return (V)(this.c == null ? var1 : this.c.a((V)var1));
   }

   protected final Map<K, V> a(Map<? extends K, ? extends V> var1) {
      if (var1.isEmpty()) {
         return var1;
      }

      js var2 = new js(var1.size());

      for (Entry var3 : var1.entrySet()) {
         var2.put(this.b((K)var3.getKey()), this.c((V)var3.getValue()));
      }

      return var2;
   }

   @Override
   protected final V a(V var1) {
      return (V)this.c.a((V)var1);
   }

   @Override
   protected final boolean a() {
      return this.c != null;
   }

   @Override
   public V put(K key, V value) {
      key = this.b(key);
      value = this.c(value);
      return this.i().put(key, value);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> mapToCopy) {
      mapToCopy = this.a(mapToCopy);
      this.i().putAll(mapToCopy);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Set entrySet() {
      return super.entrySet();
   }
}
