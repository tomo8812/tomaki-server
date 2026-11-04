package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class gM<T> implements Serializable, hz<T> {
   private static final long a = 4189014213763186912L;
   private final fF<? super T> b;
   private final fF<? super T> c;

   public static <T> fF<T> a(fF<? super T> var0, fF<? super T> var1) {
      if (var0 != null && var1 != null) {
         return new gM<>(var0, var1);
      } else {
         throw new NullPointerException("Predicate must not be null");
      }
   }

   private gM(fF<? super T> var1, fF<? super T> var2) {
      this.b = var1;
      this.c = var2;
   }

   @Override
   public final boolean a(T var1) {
      return this.b.a((T)var1) && this.c.a((T)var1);
   }

   @Override
   public final fF<? super T>[] a() {
      return new fF[]{this.b, this.c};
   }
}
