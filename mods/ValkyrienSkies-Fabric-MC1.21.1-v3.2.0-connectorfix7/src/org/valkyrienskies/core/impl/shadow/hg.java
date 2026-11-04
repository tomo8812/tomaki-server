package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class hg<T> implements Serializable, fF<T> {
   private static final long a = -89901658494523293L;
   private final T b;

   public static <T> fF<T> b(T var0) {
      return var0 == null ? hw.a() : new hg<>((T)var0);
   }

   private hg(T var1) {
      this.b = (T)var1;
   }

   @Override
   public final boolean a(T var1) {
      return this.b == var1;
   }

   private T a() {
      return this.b;
   }
}
