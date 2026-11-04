package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class hb<I, O> implements Serializable, fQ<I, O> {
   private static final long a = 7179106032121985545L;
   private static hb b = new hb();

   public static <I, O> fQ<I, O> a() {
      return b;
   }

   private hb() {
   }

   @Override
   public final O a(I var1) {
      throw new fk("ExceptionTransformer invoked");
   }

   private static fQ b() {
      return b;
   }
}
