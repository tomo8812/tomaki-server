package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class hs<T> implements Serializable, hz<T> {
   private static final long a = -2654603322338049674L;
   private final fF<? super T> b;

   public static <T> fF<T> a(fF<? super T> var0) {
      if (var0 == null) {
         throw new NullPointerException("Predicate must not be null");
      } else {
         return new hs<>(var0);
      }
   }

   private hs(fF<? super T> var1) {
      this.b = var1;
   }

   @Override
   public final boolean a(T var1) {
      return !this.b.a((T)var1);
   }

   @Override
   public final fF<? super T>[] a() {
      return new fF[]{this.b};
   }
}
