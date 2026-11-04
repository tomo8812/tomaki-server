package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

public final class iK<K, V> implements Serializable, Entry<K, V>, fr<K, V> {
   private static final long a = -8453869361373831205L;
   private final Map<K, V> b;
   private final K c;

   public iK(Map<K, V> var1, K var2) {
      this.b = var1;
      this.c = (K)var2;
   }

   @Override
   public final K getKey() {
      return this.c;
   }

   @Override
   public final V getValue() {
      return this.b.get(this.c);
   }

   @Override
   public final V setValue(V value) {
      if (value == this) {
         throw new IllegalArgumentException("Cannot set value to this map entry");
      } else {
         return this.b.put(this.c, value);
      }
   }

   @Override
   public final boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (!(obj instanceof Entry)) {
         return false;
      }

      obj = (Entry)obj;
      Object var2 = this.getValue();
      return (this.c == null ? obj.getKey() == null : this.c.equals(obj.getKey())) && (var2 == null ? obj.getValue() == null : var2.equals(obj.getValue()));
   }

   @Override
   public final int hashCode() {
      Object var1 = this.getValue();
      return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (var1 == null ? 0 : var1.hashCode());
   }

   @Override
   public final String toString() {
      return this.getKey() + "=" + this.getValue();
   }
}
