package org.valkyrienskies.core.impl.shadow;

final class DI<T> {
   private final T a;

   public DI(T var1) {
      this.a = (T)var1;
   }

   @Override
   public final boolean equals(Object obj) {
      return !(obj instanceof DI var2) ? false : this.a == var2.a;
   }

   @Override
   public final int hashCode() {
      return System.identityHashCode(this.a);
   }
}
