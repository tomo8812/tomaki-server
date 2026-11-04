package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Comparator;

public final class gc<E> extends gb<E> implements fN<E> {
   private static final long b = 3448581314086406616L;

   public static <E> gc<E> a(fN<E> var0, fF<? super E> var1) {
      return new gc<>(var0, var1);
   }

   private gc(fN<E> var1, fF<? super E> var2) {
      super(var1, var2);
   }

   private fN<E> g() {
      return (fN<E>)super.e();
   }

   @Override
   public final E c() {
      return ((fN)super.e()).c();
   }

   @Override
   public final E d() {
      return ((fN)super.e()).d();
   }

   @Override
   public final Comparator<? super E> b() {
      return ((fN)super.e()).b();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected final eW e() {
      return super.e();
   }

   // $VF: synthetic method
   @Override
   public final Collection f() {
      return super.e();
   }
}
