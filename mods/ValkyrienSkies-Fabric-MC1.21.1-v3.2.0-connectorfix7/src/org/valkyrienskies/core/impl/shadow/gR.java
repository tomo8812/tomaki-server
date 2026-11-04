package org.valkyrienskies.core.impl.shadow;

public final class gR<T> implements fQ<T, T> {
   private static gR a = new gR();

   public static <T> fQ<T, T> a() {
      return a;
   }

   private gR() {
   }

   @Override
   public final T a(T var1) {
      return (T)(var1 == null ? null : hB.a(var1).a());
   }
}
