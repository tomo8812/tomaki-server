package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;

public final class hq<T> extends gK<T> {
   private static final long b = 2007613066565892961L;

   public static <T> fF<T> a(fF<? super T>... var0) {
      hf.b(var0);
      return var0.length == 0 ? hI.a() : new hq<>(hf.a(var0));
   }

   public static <T> fF<T> a(Collection<? extends fF<? super T>> var0) {
      fF[] var1;
      return (var1 = hf.a(var0)).length == 0 ? hI.a() : new hq<>(var1);
   }

   private hq(fF<? super T>... var1) {
      super(var1);
   }

   @Override
   public final boolean a(T var1) {
      fF[] var2 = super.a;
      int var3 = super.a.length;

      for (int var4 = 0; var4 < var3; var4++) {
         fF var5;
         if ((var5 = var2[var4]).a(var1)) {
            return false;
         }
      }

      return true;
   }
}
