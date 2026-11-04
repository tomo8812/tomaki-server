package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class gZ<T> implements Serializable, fh<T> {
   private static final long a = 7179106032121985545L;
   private static gZ b = new gZ();

   public static <T> fh<T> b() {
      return b;
   }

   private gZ() {
   }

   @Override
   public final T a() {
      throw new fk("ExceptionFactory invoked");
   }

   private static fh c() {
      return b;
   }
}
