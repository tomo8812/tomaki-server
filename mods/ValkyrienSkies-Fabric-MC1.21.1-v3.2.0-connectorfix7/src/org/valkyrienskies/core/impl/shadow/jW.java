package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Set;

public final class jW<E> extends gz<E> implements fy<E> {
   private static final long b = 20150629L;

   public static <E> jW<E> a(fy<E> var0) {
      return new jW<>(var0);
   }

   private jW(fy<E> var1) {
      super(var1);
   }

   private jW(fy<E> var1, Object var2) {
      super(var1, var2);
   }

   private fy<E> c() {
      return (fy<E>)super.e();
   }

   @Override
   public final boolean equals(Object object) {
      if (object == this) {
         return true;
      }

      synchronized (super.a) {
         return ((fy)super.e()).equals(object);
      }
   }

   @Override
   public final int hashCode() {
      synchronized (super.a) {
         return ((fy)super.e()).hashCode();
      }
   }

   @Override
   public final int b(E var1, int var2) {
      synchronized (super.a) {
         return ((fy)super.e()).b((E)var1, var2);
      }
   }

   @Override
   public final int c(Object var1, int var2) {
      synchronized (super.a) {
         return ((fy)super.e()).c(var1, var2);
      }
   }

   @Override
   public final int a(Object var1) {
      synchronized (super.a) {
         return ((fy)super.e()).a(var1);
      }
   }

   @Override
   public final int a(E var1, int var2) {
      synchronized (super.a) {
         return ((fy)super.e()).a((E)var1, var2);
      }
   }

   @Override
   public final Set<E> a() {
      synchronized (super.a) {
         Set var2 = ((fy)super.e()).a();
         return new jW.a<>(var2, super.a);
      }
   }

   @Override
   public final Set<fy.a<E>> b() {
      synchronized (super.a) {
         Set var2 = ((fy)super.e()).b();
         return new jW.a<>(var2, super.a);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Collection e() {
      return super.e();
   }

   static class a<T> extends gz<T> implements Set<T> {
      private static final long b = 20150629L;

      a(Set<T> var1, Object var2) {
         super(var1, var2);
      }
   }
}
