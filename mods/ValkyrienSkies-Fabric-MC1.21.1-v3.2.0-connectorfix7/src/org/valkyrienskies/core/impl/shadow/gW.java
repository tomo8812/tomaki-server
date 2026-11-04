package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class gW<T> implements Serializable, fg<T> {
   private static final long a = 825802648423525485L;
   private static gW b = new gW();
   private static int c;

   public static <T> gW<T> a() {
      return b;
   }

   private gW() {
   }

   @Override
   public final boolean a(T var1, T var2) {
      return var1 == var2 || var1 != null && var1.equals(var2);
   }

   @Override
   public final int a(T var1) {
      return var1 == null ? -1 : var1.hashCode();
   }

   private static gW b() {
      return b;
   }
}
