package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public final class hJ<T> implements Serializable, fF<T> {
   private static final long a = -3319417438027438040L;
   private final Set<T> b = new HashSet<>();

   public static <T> fF<T> a() {
      return new hJ<>();
   }

   @Override
   public final boolean a(T var1) {
      return this.b.add((T)var1);
   }
}
