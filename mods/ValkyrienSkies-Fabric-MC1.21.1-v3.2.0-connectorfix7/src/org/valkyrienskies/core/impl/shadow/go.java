package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public final class go<K, V> extends gl<K, V> implements Serializable {
   private static final long d = 721969328361808L;

   public go() {
      super(new HashMap<>(), new HashMap<>());
   }

   private go(Map<? extends K, ? extends V> var1) {
      super(new HashMap<>(), new HashMap<>());
      this.putAll(var1);
   }

   private go(Map<K, V> var1, Map<V, K> var2, eY<V, K> var3) {
      super(var1, var2, var3);
   }

   @Override
   protected final eY<V, K> a(Map<V, K> var1, Map<K, V> var2, eY<K, V> var3) {
      return new go<>(var1, var2, var3);
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(super.a);
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      super.a = new HashMap<>();
      super.b = new HashMap<>();
      Map var2 = (Map)var1.readObject();
      this.putAll(var2);
   }
}
