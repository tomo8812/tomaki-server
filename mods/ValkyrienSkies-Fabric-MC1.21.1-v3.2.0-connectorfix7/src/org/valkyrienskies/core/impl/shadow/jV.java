package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Set;

public final class jV<E> extends gy<E> implements fy<E> {
   private static final long b = 20150629L;

   public static <E> jV<E> a(fy<E> var0, fF<? super E> var1) {
      return new jV<>(var0, var1);
   }

   private jV(fy<E> var1, fF<? super E> var2) {
      super(var1, var2);
   }

   private fy<E> c() {
      return (fy<E>)super.f();
   }

   @Override
   public final boolean equals(Object object) {
      return object == this || ((fy)super.f()).equals(object);
   }

   @Override
   public final int hashCode() {
      return ((fy)super.f()).hashCode();
   }

   @Override
   public final int b(E var1, int var2) {
      this.b((E)var1);
      return ((fy)super.f()).b((E)var1, var2);
   }

   @Override
   public final int c(Object var1, int var2) {
      return ((fy)super.f()).c(var1, var2);
   }

   @Override
   public final int a(Object var1) {
      return ((fy)super.f()).a(var1);
   }

   @Override
   public final int a(E var1, int var2) {
      this.b((E)var1);
      return ((fy)super.f()).a((E)var1, var2);
   }

   @Override
   public final Set<E> a() {
      return ((fy)super.f()).a();
   }

   @Override
   public final Set<fy.a<E>> b() {
      return ((fy)super.f()).b();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Collection f() {
      return super.f();
   }
}
