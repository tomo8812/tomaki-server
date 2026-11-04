package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class gY<E> implements Serializable, fb<E> {
   private static final long a = 7179106032121985545L;
   private static gY b = new gY();

   public static <E> fb<E> a() {
      return b;
   }

   private gY() {
   }

   @Override
   public final void a(E var1) {
      throw new fk("ExceptionClosure invoked");
   }

   private static fb b() {
      return b;
   }
}
