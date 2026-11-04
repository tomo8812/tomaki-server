package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Map;

public final class hn<I, O> implements Serializable, fQ<I, O> {
   private static final long a = 862391807045468939L;
   private final Map<? super I, ? extends O> b;

   public static <I, O> fQ<I, O> a(Map<? super I, ? extends O> var0) {
      return var0 == null ? gV.a() : new hn<>(var0);
   }

   private hn(Map<? super I, ? extends O> var1) {
      this.b = var1;
   }

   @Override
   public final O a(I var1) {
      return (O)this.b.get(var1);
   }

   private Map<? super I, ? extends O> a() {
      return this.b;
   }
}
