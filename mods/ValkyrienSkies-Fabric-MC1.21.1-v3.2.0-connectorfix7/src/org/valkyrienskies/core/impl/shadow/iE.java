package org.valkyrienskies.core.impl.shadow;

public abstract class iE<K, V> implements fr<K, V> {
   private K a;
   private V b;

   protected iE(K var1, V var2) {
      this.a = (K)var1;
      this.b = (V)var2;
   }

   @Override
   public K getKey() {
      return this.a;
   }

   protected K a(K var1) {
      Object var2 = this.a;
      this.a = (K)var1;
      return (K)var2;
   }

   @Override
   public V getValue() {
      return this.b;
   }

   protected V setValue(V value) {
      Object var2 = this.b;
      this.b = value;
      return (V)var2;
   }

   @Override
   public String toString() {
      return "" + this.getKey() + '=' + this.getValue();
   }
}
