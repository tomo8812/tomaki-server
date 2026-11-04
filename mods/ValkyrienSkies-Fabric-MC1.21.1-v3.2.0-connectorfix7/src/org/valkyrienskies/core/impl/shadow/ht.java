package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class ht<T> implements Serializable, hz<T> {
   private static final long a = 3243449850504576071L;
   private final fF<? super T> b;

   public static <T> fF<T> a(fF<? super T> var0) {
      if (var0 == null) {
         throw new NullPointerException("Predicate must not be null");
      } else {
         return new ht<>(var0);
      }
   }

   private ht(fF<? super T> var1) {
      this.b = var1;
   }

   @Override
   public final boolean a(T var1) {
      if (var1 == null) {
         throw new fk("Input Object must not be null");
      } else {
         return this.b.a((T)var1);
      }
   }

   @Override
   public final fF<? super T>[] a() {
      return new fF[]{this.b};
   }
}
