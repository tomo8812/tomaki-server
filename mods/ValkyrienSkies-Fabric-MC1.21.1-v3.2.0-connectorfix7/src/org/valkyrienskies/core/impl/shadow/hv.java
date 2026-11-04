package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class hv<T> implements Serializable, hz<T> {
   private static final long a = -7625133768987126273L;
   private final fF<? super T> b;

   public static <T> fF<T> a(fF<? super T> var0) {
      if (var0 == null) {
         throw new NullPointerException("Predicate must not be null");
      } else {
         return new hv<>(var0);
      }
   }

   private hv(fF<? super T> var1) {
      this.b = var1;
   }

   @Override
   public final boolean a(T var1) {
      return var1 == null ? true : this.b.a((T)var1);
   }

   @Override
   public final fF<? super T>[] a() {
      return new fF[]{this.b};
   }
}
