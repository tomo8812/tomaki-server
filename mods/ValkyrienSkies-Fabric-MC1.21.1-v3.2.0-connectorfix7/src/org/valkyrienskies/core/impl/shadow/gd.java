package org.valkyrienskies.core.impl.shadow;

import java.util.Set;

public class gd<E> extends gz<E> implements eW<E> {
   private static final long b = 8084674570753837109L;

   public static <E> gd<E> a(eW<E> var0) {
      return new gd<>(var0);
   }

   protected gd(eW<E> var1) {
      super(var1);
   }

   protected gd(eW<E> var1, Object var2) {
      super(var1, var2);
   }

   private eW<E> b() {
      return (eW<E>)this.e();
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      }

      synchronized (super.a) {
         return this.e().equals(object);
      }
   }

   @Override
   public int hashCode() {
      synchronized (super.a) {
         return this.e().hashCode();
      }
   }

   @Override
   public final boolean a(E var1, int var2) {
      synchronized (super.a) {
         return ((eW)this.e()).a((E)var1, var2);
      }
   }

   @Override
   public final boolean b(Object var1, int var2) {
      synchronized (super.a) {
         return ((eW)this.e()).b(var1, var2);
      }
   }

   @Override
   public final Set<E> a() {
      synchronized (super.a) {
         Set var2 = ((eW)this.e()).a();
         return new gd.a(var2, super.a);
      }
   }

   @Override
   public final int a(Object var1) {
      synchronized (super.a) {
         return ((eW)this.e()).a(var1);
      }
   }

   class a extends gz<E> implements Set<E> {
      private static final long b = 2990565892366827855L;

      a(Set<E> var2, Object var3) {
         super(var2, var3);
      }
   }
}
