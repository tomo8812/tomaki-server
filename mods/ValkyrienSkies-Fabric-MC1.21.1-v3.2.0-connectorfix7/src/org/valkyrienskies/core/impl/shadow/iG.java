package org.valkyrienskies.core.impl.shadow;

import java.util.Map.Entry;

public abstract class iG<K, V> implements Entry<K, V>, fr<K, V> {
   private final Entry<K, V> a;

   public iG(Entry<K, V> var1) {
      if (var1 == null) {
         throw new NullPointerException("Map Entry must not be null.");
      }

      this.a = var1;
   }

   protected final Entry<K, V> a() {
      return this.a;
   }

   @Override
   public K getKey() {
      return this.a.getKey();
   }

   @Override
   public V getValue() {
      return this.a.getValue();
   }

   @Override
   public V setValue(V object) {
      return this.a.setValue(object);
   }

   @Override
   public boolean equals(Object object) {
      return object == this ? true : this.a.equals(object);
   }

   @Override
   public int hashCode() {
      return this.a.hashCode();
   }

   @Override
   public String toString() {
      return this.a.toString();
   }
}
