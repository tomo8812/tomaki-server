package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;

public final class hx<T> extends gK<T> {
   private static final long b = -8125389089924745785L;

   public static <T> fF<T> a(fF<? super T>... var0) {
      hf.b(var0);
      if (var0.length == 0) {
         return hd.a();
      } else {
         return var0.length == 1 ? var0[0] : new hx<>(hf.a(var0));
      }
   }

   public static <T> fF<T> a(Collection<? extends fF<? super T>> var0) {
      fF[] var1 = hf.a(var0);
      return new hx<>(var1);
   }

   private hx(fF<? super T>... var1) {
      super(var1);
   }

   @Override
   public final boolean a(T var1) {
      boolean var2 = false;
      fF[] var3 = super.a;
      int var4 = super.a.length;

      for (int var5 = 0; var5 < var4; var5++) {
         fF var6;
         if ((var6 = var3[var5]).a(var1)) {
            if (var2) {
               return false;
            }

            var2 = true;
         }
      }

      return var2;
   }
}
