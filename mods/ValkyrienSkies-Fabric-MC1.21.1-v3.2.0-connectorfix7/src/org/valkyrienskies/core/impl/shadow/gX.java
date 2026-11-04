package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class gX<T> implements Serializable, fF<T> {
   private static final long a = 5633766978029907089L;
   private final T b;
   private final fg<T> c;

   public static <T> fF<T> b(T var0) {
      return var0 == null ? hw.a() : new gX<>((T)var0);
   }

   public static <T> fF<T> a(T var0, fg<T> var1) {
      return var0 == null ? hw.a() : new gX<>((T)var0, var1);
   }

   private gX(T var1) {
      this((T)var1, null);
   }

   private gX(T var1, fg<T> var2) {
      this.b = (T)var1;
      this.c = var2;
   }

   @Override
   public final boolean a(T var1) {
      return this.c != null ? this.c.a(this.b, (T)var1) : this.b.equals(var1);
   }

   private Object a() {
      return this.b;
   }
}
