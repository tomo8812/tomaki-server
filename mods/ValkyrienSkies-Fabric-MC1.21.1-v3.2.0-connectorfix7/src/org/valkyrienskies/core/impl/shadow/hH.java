package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class hH<T> implements Serializable, fF<T> {
   private static final long a = -2407966402920578741L;
   private final fQ<? super T, Boolean> b;

   public static <T> fF<T> a(fQ<? super T, Boolean> var0) {
      if (var0 == null) {
         throw new NullPointerException("The transformer to call must not be null");
      } else {
         return new hH<>(var0);
      }
   }

   private hH(fQ<? super T, Boolean> var1) {
      this.b = var1;
   }

   @Override
   public final boolean a(T var1) {
      if ((var1 = this.b.a((T)var1)) == null) {
         throw new fk("Transformer must return an instanceof Boolean, it was a null object");
      } else {
         return var1;
      }
   }

   private fQ<? super T, Boolean> a() {
      return this.b;
   }
}
