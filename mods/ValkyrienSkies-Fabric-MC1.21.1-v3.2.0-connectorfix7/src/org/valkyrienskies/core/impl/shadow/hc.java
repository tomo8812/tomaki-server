package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class hc<I, O> implements Serializable, fQ<I, O> {
   private static final long a = -6817674502475353160L;
   private final fh<? extends O> b;

   public static <I, O> fQ<I, O> a(fh<? extends O> var0) {
      if (var0 == null) {
         throw new NullPointerException("Factory must not be null");
      } else {
         return new hc<>(var0);
      }
   }

   private hc(fh<? extends O> var1) {
      this.b = var1;
   }

   @Override
   public final O a(I var1) {
      return (O)this.b.a();
   }

   private fh<? extends O> a() {
      return this.b;
   }
}
