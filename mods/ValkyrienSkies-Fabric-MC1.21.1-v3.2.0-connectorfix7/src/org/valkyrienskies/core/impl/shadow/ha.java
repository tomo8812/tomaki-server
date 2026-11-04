package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class ha<T> implements Serializable, fF<T> {
   private static final long a = 7179106032121985545L;
   private static ha b = new ha();

   public static <T> fF<T> a() {
      return b;
   }

   private ha() {
   }

   @Override
   public final boolean a(T var1) {
      throw new fk("ExceptionPredicate invoked");
   }

   private static fF b() {
      return b;
   }
}
