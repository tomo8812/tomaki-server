package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public final class gp<K, V> extends gl<K, V> implements Serializable {
   private static final long d = 721969328361810L;

   public gp() {
      super(new LinkedHashMap<>(), new LinkedHashMap<>());
   }

   private gp(Map<? extends K, ? extends V> var1) {
      super(new LinkedHashMap<>(), new LinkedHashMap<>());
      this.putAll(var1);
   }

   private gp(Map<K, V> var1, Map<V, K> var2, eY<V, K> var3) {
      super(var1, var2, var3);
   }

   @Override
   protected final eY<V, K> a(Map<V, K> var1, Map<K, V> var2, eY<K, V> var3) {
      return new gp<>(var1, var2, var3);
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(super.a);
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      super.a = new LinkedHashMap<>();
      super.b = new LinkedHashMap<>();
      Map var2 = (Map)var1.readObject();
      this.putAll(var2);
   }
}
