package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class hw<T> implements Serializable, fF<T> {
   private static final long a = 7533784454832764388L;
   private static hw b = new hw();

   public static <T> fF<T> a() {
      return b;
   }

   private hw() {
   }

   @Override
   public final boolean a(T var1) {
      return var1 == null;
   }

   private static fF b() {
      return b;
   }
}
