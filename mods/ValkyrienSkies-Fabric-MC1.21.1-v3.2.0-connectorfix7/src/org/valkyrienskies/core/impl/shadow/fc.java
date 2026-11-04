package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public final class fc {
   private fc() {
   }

   private static <E> fb<E> a() {
      return gY.a();
   }

   private static <E> fb<E> b() {
      return ho.a();
   }

   private static <E> fb<E> a(fQ<? super E, ?> var0) {
      return hG.a(var0);
   }

   private static <E> fb<E> a(int var0, fb<? super E> var1) {
      var1 = var1;
      var0 = var0;
      if (var0 <= 0 || var1 == null) {
         return ho.a();
      } else {
         return var0 == 1 ? var1 : new he<>(var0, var1);
      }
   }

   private static <E> fb<E> a(fF<? super E> var0, fb<? super E> var1) {
      return hK.a(var0, var1, false);
   }

   private static <E> fb<E> a(fb<? super E> var0, fF<? super E> var1) {
      return hK.a(var1, var0, true);
   }

   private static <E> fb<E> a(String var0) {
      return hG.a(hm.a(var0));
   }

   private static <E> fb<E> a(String var0, Class<?>[] var1, Object[] var2) {
      return hG.a(hm.a(var0, var1, var2));
   }

   private static <E> fb<E> a(fb<? super E>... var0) {
      return gP.a(var0);
   }

   private static <E> fb<E> a(Collection<? extends fb<? super E>> var0) {
      return gP.a(var0);
   }

   private static <E> fb<E> b(fF<? super E> var0, fb<? super E> var1) {
      return hh.a(var0, var1);
   }

   private static <E> fb<E> a(fF<? super E> var0, fb<? super E> var1, fb<? super E> var2) {
      return hh.a(var0, var1, var2);
   }

   private static <E> fb<E> a(fF<? super E>[] var0, fb<? super E>[] var1) {
      return hD.a(var0, var1, null);
   }

   private static <E> fb<E> a(fF<? super E>[] var0, fb<? super E>[] var1, fb<? super E> var2) {
      return hD.a(var0, var1, var2);
   }

   private static <E> fb<E> a(Map<fF<E>, fb<E>> var0) {
      return hD.a(var0);
   }

   private static <E> fb<E> b(Map<? extends E, fb<E>> var0) {
      if (var0 == null) {
         throw new NullPointerException("The object and closure map must not be null");
      }

      fb var1 = (fb)var0.remove(null);
      int var2;
      fb[] var3 = new fb[var2 = var0.size()];
      fF[] var8 = new fF[var2];
      int var4 = 0;

      for (Entry var5 : var0.entrySet()) {
         var8[var4] = gX.b(var5.getKey());
         var3[var4] = (fb)var5.getValue();
         var4++;
      }

      fb var9 = var1;
      fb[] var7 = var3;
      return hD.a(var8, var7, var9);
   }
}
