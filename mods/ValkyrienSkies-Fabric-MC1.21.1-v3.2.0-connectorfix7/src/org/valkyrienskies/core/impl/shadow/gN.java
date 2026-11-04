package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;

public final class gN<T> extends gK<T> {
   private static final long b = 7429999530934647542L;

   public static <T> fF<T> a(fF<? super T>... var0) {
      hf.b(var0);
      if (var0.length == 0) {
         return hd.a();
      } else {
         return var0.length == 1 ? var0[0] : new gN<>(hf.a(var0));
      }
   }

   public static <T> fF<T> a(Collection<? extends fF<? super T>> var0) {
      fF[] var1;
      if ((var1 = hf.a(var0)).length == 0) {
         return hd.a();
      } else {
         return var1.length == 1 ? var1[0] : new gN<>(var1);
      }
   }

   private gN(fF<? super T>... var1) {
      super(var1);
   }

   @Override
   public final boolean a(T var1) {
      fF[] var2 = super.a;
      int var3 = super.a.length;

      for (int var4 = 0; var4 < var3; var4++) {
         fF var5;
         if ((var5 = var2[var4]).a(var1)) {
            return true;
         }
      }

      return false;
   }
}
