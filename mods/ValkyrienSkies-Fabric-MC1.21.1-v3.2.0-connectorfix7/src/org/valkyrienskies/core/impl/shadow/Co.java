package org.valkyrienskies.core.impl.shadow;

public class Co<K, V> {
   public final K a;
   public final V b;

   public Co(K var1, V var2) {
      this.a = (K)var1;
      this.b = (V)var2;
   }

   private Co(Co<? extends K, ? extends V> var1) {
      this(var1.a, (V)var1.c());
   }

   public final K e() {
      return this.a;
   }

   public V c() {
      return this.b;
   }

   public final K f() {
      return this.a;
   }

   public final V g() {
      return this.b;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (!(o instanceof Co)) {
         return false;
      }

      o = (Co)o;
      return (this.a == null ? ((Co)o).a == null : this.a.equals(((Co)o).a)) && (this.b == null ? ((Co)o).b == null : this.b.equals(((Co)o).b));
   }

   @Override
   public int hashCode() {
      int var1 = this.a == null ? 0 : this.a.hashCode();
      int var2 = this.b == null ? 0 : this.b.hashCode();
      return var1 * 37 + var2 ^ var2 >>> 16;
   }

   @Override
   public String toString() {
      return "[" + this.a + ", " + this.c() + "]";
   }

   private static <K, V> Co<K, V> a(K var0, V var1) {
      return new Co<>((K)var0, (V)var1);
   }
}
