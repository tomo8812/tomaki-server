package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;

public final class gg<E> extends gf<E> implements fN<E> {
   private static final long b = -251737742649401930L;

   public static <E> gg<E> a(fN<E> var0, fQ<? super E, ? extends E> var1) {
      return new gg<>(var0, var1);
   }

   private static <E> gg<E> b(fN<E> var0, fQ<? super E, ? extends E> var1) {
      gg var2 = new gg(var0, var1);
      if (var0.size() > 0) {
         Object[] var3 = var0.toArray();
         var0.clear();
         Object[] var6 = var3;
         int var7 = var3.length;

         for (int var4 = 0; var4 < var7; var4++) {
            Object var5 = var6[var4];
            var2.f().add(var1.a(var5));
         }
      }

      return var2;
   }

   private gg(fN<E> var1, fQ<? super E, ? extends E> var2) {
      super(var1, var2);
   }

   private fN<E> e() {
      return (fN<E>)this.f();
   }

   @Override
   public final E c() {
      return ((fN)this.f()).c();
   }

   @Override
   public final E d() {
      return ((fN)this.f()).d();
   }

   @Override
   public final Comparator<? super E> b() {
      return ((fN)this.f()).b();
   }
}
