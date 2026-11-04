package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class hI<T> implements Serializable, fF<T> {
   private static final long a = 3374767158756189740L;
   private static hI b = new hI();

   public static <T> fF<T> a() {
      return b;
   }

   private hI() {
   }

   @Override
   public final boolean a(T var1) {
      return true;
   }

   private static fF b() {
      return b;
   }
}
