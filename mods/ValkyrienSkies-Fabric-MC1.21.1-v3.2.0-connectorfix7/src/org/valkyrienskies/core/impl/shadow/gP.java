package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Collection;

public final class gP<E> implements Serializable, fb<E> {
   private static final long a = -3520677225766901240L;
   private final fb<? super E>[] b;

   public static <E> fb<E> a(fb<? super E>... var0) {
      hf.b(var0);
      return var0.length == 0 ? ho.a() : new gP<>(var0);
   }

   public static <E> fb<E> a(Collection<? extends fb<? super E>> var0) {
      if (var0 == null) {
         throw new NullPointerException("Closure collection must not be null");
      }

      if (var0.size() == 0) {
         return ho.a();
      }

      fb[] var1 = new fb[var0.size()];
      int var2 = 0;

      for (fb var3 : var0) {
         var1[var2++] = var3;
      }

      hf.b(var1);
      return new gP<>(false, var1);
   }

   private gP(boolean var1, fb<? super E>... var2) {
      this.b = var1 ? hf.a(var2) : var2;
   }

   private gP(fb<? super E>... var1) {
      this(true, var1);
   }

   @Override
   public final void a(E var1) {
      fb[] var2 = this.b;
      int var3 = this.b.length;

      for (int var4 = 0; var4 < var3; var4++) {
         fb var5;
         (var5 = var2[var4]).a(var1);
      }
   }

   private fb<? super E>[] a() {
      return hf.a(this.b);
   }
}
