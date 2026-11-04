package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;

public final class gL<T> extends gK<T> {
   private static final long b = -3094696765038308799L;

   public static <T> fF<T> a(fF<? super T>... var0) {
      hf.b(var0);
      if (var0.length == 0) {
         return hI.a();
      }

      fF var1;
      return var0.length == 1 ? (var1 = var0[0]) : new gL<>(hf.a(var0));
   }

   public static <T> fF<T> a(Collection<? extends fF<? super T>> var0) {
      fF[] var1;
      if ((var1 = hf.a(var0)).length == 0) {
         return hI.a();
      }

      fF var2;
      return var1.length == 1 ? (var2 = var1[0]) : new gL<>(var1);
   }

   private gL(fF<? super T>... var1) {
      super(var1);
   }

   @Override
   public final boolean a(T var1) {
      fF[] var2 = super.a;
      int var3 = super.a.length;

      for (int var4 = 0; var4 < var3; var4++) {
         fF var5;
         if (!(var5 = var2[var4]).a(var1)) {
            return false;
         }
      }

      return true;
   }
}
