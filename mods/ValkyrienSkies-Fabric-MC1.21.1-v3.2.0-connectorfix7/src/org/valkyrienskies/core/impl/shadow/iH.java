package org.valkyrienskies.core.impl.shadow;

import java.util.Map.Entry;

public final class iH<K, V> extends iE<K, V> {
   public iH() {
      super(null, null);
   }

   private iH(K var1, V var2) {
      super((K)var1, (V)var2);
   }

   private iH(fr<? extends K, ? extends V> var1) {
      super((K)var1.getKey(), (V)var1.getValue());
   }

   private iH(Entry<? extends K, ? extends V> var1) {
      super((K)var1.getKey(), (V)var1.getValue());
   }

   @Override
   public final K a(K var1) {
      if (var1 == this) {
         throw new IllegalArgumentException("DefaultKeyValue may not contain itself as a key.");
      } else {
         return super.a((K)var1);
      }
   }

   @Override
   public final V setValue(V value) {
      if (value == this) {
         throw new IllegalArgumentException("DefaultKeyValue may not contain itself as a value.");
      } else {
         return super.setValue(value);
      }
   }

   private Entry<K, V> a() {
      return new iI<>(this);
   }

   @Override
   public final boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (!(obj instanceof iH)) {
         return false;
      }

      obj = (iH)obj;
      return (this.getKey() == null ? ((iE)obj).getKey() == null : this.getKey().equals(((iE)obj).getKey()))
         && (this.getValue() == null ? ((iE)obj).getValue() == null : this.getValue().equals(((iE)obj).getValue()));
   }

   @Override
   public final int hashCode() {
      return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
   }
}
