package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Set;

public abstract class jT<E> extends gv<E> implements fy<E> {
   private static final long a = 20150610L;

   protected jT() {
   }

   protected jT(fy<E> var1) {
      super(var1);
   }

   protected final fy<E> c() {
      return (fy<E>)super.f();
   }

   @Override
   public boolean equals(Object object) {
      return object == this || ((fy)super.f()).equals(object);
   }

   @Override
   public int hashCode() {
      return ((fy)super.f()).hashCode();
   }

   @Override
   public final int a(Object var1) {
      return ((fy)super.f()).a(var1);
   }

   @Override
   public int a(E var1, int var2) {
      return ((fy)super.f()).a((E)var1, var2);
   }

   @Override
   public int b(E var1, int var2) {
      return ((fy)super.f()).b((E)var1, var2);
   }

   @Override
   public int c(Object var1, int var2) {
      return ((fy)super.f()).c(var1, var2);
   }

   @Override
   public Set<E> a() {
      return ((fy)super.f()).a();
   }

   @Override
   public Set<fy.a<E>> b() {
      return ((fy)super.f()).b();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Collection f() {
      return super.f();
   }
}
