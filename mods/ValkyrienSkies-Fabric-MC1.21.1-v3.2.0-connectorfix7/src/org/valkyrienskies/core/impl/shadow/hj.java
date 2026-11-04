package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class hj implements Serializable, fF<Object> {
   private static final long a = -6682656911025165584L;
   private final Class<?> b;

   public static fF<Object> a(Class<?> var0) {
      if (var0 == null) {
         throw new NullPointerException("The type to check instanceof must not be null");
      } else {
         return new hj(var0);
      }
   }

   private hj(Class<?> var1) {
      this.b = var1;
   }

   @Override
   public final boolean a(Object var1) {
      return this.b.isInstance(var1);
   }

   private Class<?> a() {
      return this.b;
   }
}
