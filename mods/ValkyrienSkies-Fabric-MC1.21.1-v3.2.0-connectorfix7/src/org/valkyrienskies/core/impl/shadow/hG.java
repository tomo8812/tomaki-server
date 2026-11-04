package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class hG<E> implements Serializable, fb<E> {
   private static final long a = -5194992589193388969L;
   private final fQ<? super E, ?> b;

   public static <E> fb<E> a(fQ<? super E, ?> var0) {
      return var0 == null ? ho.a() : new hG<>(var0);
   }

   private hG(fQ<? super E, ?> var1) {
      this.b = var1;
   }

   @Override
   public final void a(E var1) {
      this.b.a((E)var1);
   }

   private fQ<? super E, ?> a() {
      return this.b;
   }
}
