package org.valkyrienskies.core.impl.shadow;

import java.util.Map.Entry;

public final class iL<K, V> extends iF<K, V> implements fU {
   public iL(K var1, V var2) {
      super((K)var1, (V)var2);
   }

   private iL(fr<? extends K, ? extends V> var1) {
      super((K)var1.getKey(), (V)var1.getValue());
   }

   private iL(Entry<? extends K, ? extends V> var1) {
      super((K)var1.getKey(), (V)var1.getValue());
   }

   @Override
   public final V setValue(V value) {
      throw new UnsupportedOperationException("setValue() is not supported");
   }
}
