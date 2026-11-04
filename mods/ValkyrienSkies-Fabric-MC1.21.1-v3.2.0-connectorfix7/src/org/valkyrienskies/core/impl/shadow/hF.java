package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class hF<T> implements Serializable, hz<T> {
   private static final long a = -5596090919668315834L;
   private final fQ<? super T, ? extends T> b;
   private final fF<? super T> c;

   public static <T> fF<T> a(fQ<? super T, ? extends T> var0, fF<? super T> var1) {
      if (var0 == null) {
         throw new NullPointerException("The transformer to call must not be null");
      } else if (var1 == null) {
         throw new NullPointerException("The predicate to call must not be null");
      } else {
         return new hF<>(var0, var1);
      }
   }

   private hF(fQ<? super T, ? extends T> var1, fF<? super T> var2) {
      this.b = var1;
      this.c = var2;
   }

   @Override
   public final boolean a(T var1) {
      var1 = this.b.a((T)var1);
      return this.c.a((T)var1);
   }

   @Override
   public final fF<? super T>[] a() {
      return new fF[]{this.c};
   }

   private fQ<? super T, ? extends T> b() {
      return this.b;
   }
}
