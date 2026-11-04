package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class gS<T> implements Serializable, fQ<T, T> {
   private static final long a = 478466901448617286L;
   private final fb<? super T> b;

   public static <T> fQ<T, T> a(fb<? super T> var0) {
      if (var0 == null) {
         throw new NullPointerException("Closure must not be null");
      } else {
         return new gS<>(var0);
      }
   }

   private gS(fb<? super T> var1) {
      this.b = var1;
   }

   @Override
   public final T a(T var1) {
      this.b.a((T)var1);
      return (T)var1;
   }

   private fb<? super T> a() {
      return this.b;
   }
}
