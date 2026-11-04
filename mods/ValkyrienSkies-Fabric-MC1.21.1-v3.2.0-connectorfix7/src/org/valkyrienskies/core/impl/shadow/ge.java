package org.valkyrienskies.core.impl.shadow;

import java.util.Comparator;

public final class ge<E> extends gd<E> implements fN<E> {
   private static final long b = 722374056718497858L;

   public static <E> ge<E> a(fN<E> var0) {
      return new ge<>(var0);
   }

   private ge(fN<E> var1) {
      super(var1);
   }

   private ge(eW<E> var1, Object var2) {
      super(var1, var2);
   }

   private fN<E> f() {
      return (fN<E>)this.e();
   }

   @Override
   public final synchronized E c() {
      synchronized (super.a) {
         return ((fN)this.e()).c();
      }
   }

   @Override
   public final synchronized E d() {
      synchronized (super.a) {
         return ((fN)this.e()).d();
      }
   }

   @Override
   public final synchronized Comparator<? super E> b() {
      synchronized (super.a) {
         return ((fN)this.e()).b();
      }
   }
}
