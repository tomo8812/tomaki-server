package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class hA<T> implements Serializable, fQ<T, Boolean> {
   private static final long a = 5278818408044349346L;
   private final fF<? super T> b;

   public static <T> fQ<T, Boolean> a(fF<? super T> var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("Predicate must not be null");
      } else {
         return new hA<>(var0);
      }
   }

   private hA(fF<? super T> var1) {
      this.b = var1;
   }

   private Boolean b(T var1) {
      return this.b.a((T)var1);
   }

   private fF<? super T> a() {
      return this.b;
   }

   // $VF: synthetic method
   @Override
   public final Object a(Object var1) {
      Object var2 = var1;
      return this.b.a((T)var2);
   }
}
