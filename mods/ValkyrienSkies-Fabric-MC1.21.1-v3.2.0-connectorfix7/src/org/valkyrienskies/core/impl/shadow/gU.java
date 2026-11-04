package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class gU<T> implements Serializable, fh<T> {
   private static final long a = -3520677225766901240L;
   private static gU b = new gU(null);
   private final T c;

   public static <T> fh<T> a(T var0) {
      return var0 == null ? b : new gU<>((T)var0);
   }

   private gU(T var1) {
      this.c = (T)var1;
   }

   @Override
   public final T a() {
      return this.c;
   }

   private T b() {
      return this.c;
   }
}
