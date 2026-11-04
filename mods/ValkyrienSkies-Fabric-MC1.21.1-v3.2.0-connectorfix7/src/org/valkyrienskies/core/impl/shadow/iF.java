package org.valkyrienskies.core.impl.shadow;

import java.util.Map.Entry;

public abstract class iF<K, V> extends iE<K, V> implements Entry<K, V> {
   public iF(K var1, V var2) {
      super((K)var1, (V)var2);
   }

   @Override
   public V setValue(V value) {
      return super.setValue(value);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (!(obj instanceof Entry)) {
         return false;
      }

      obj = (Entry)obj;
      return (this.getKey() == null ? obj.getKey() == null : this.getKey().equals(obj.getKey()))
         && (this.getValue() == null ? obj.getValue() == null : this.getValue().equals(obj.getValue()));
   }

   @Override
   public int hashCode() {
      return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
   }
}
