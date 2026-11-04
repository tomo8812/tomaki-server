package org.valkyrienskies.core.impl.shadow;

import java.util.Map.Entry;

public final class iI<K, V> extends iF<K, V> {
   private iI(K var1, V var2) {
      super((K)var1, (V)var2);
   }

   public iI(fr<? extends K, ? extends V> var1) {
      super((K)var1.getKey(), (V)var1.getValue());
   }

   public iI(Entry<? extends K, ? extends V> var1) {
      super((K)var1.getKey(), (V)var1.getValue());
   }
}
