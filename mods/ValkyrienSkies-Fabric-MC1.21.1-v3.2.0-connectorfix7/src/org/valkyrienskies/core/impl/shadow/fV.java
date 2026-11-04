package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Set;

public abstract class fV<E> extends gv<E> implements eW<E> {
   private static final long a = -3768146017343785417L;

   protected fV() {
   }

   protected fV(eW<E> var1) {
      super(var1);
   }

   protected eW<E> e() {
      return (eW<E>)super.f();
   }

   @Override
   public boolean equals(Object object) {
      return object == this || this.e().equals(object);
   }

   @Override
   public int hashCode() {
      return this.e().hashCode();
   }

   @Override
   public final int a(Object var1) {
      return this.e().a(var1);
   }

   @Override
   public boolean a(E var1, int var2) {
      return this.e().a((E)var1, var2);
   }

   @Override
   public boolean b(Object var1, int var2) {
      return this.e().b(var1, var2);
   }

   @Override
   public Set<E> a() {
      return this.e().a();
   }

   // $VF: synthetic method
   @Override
   public Collection f() {
      return this.e();
   }
}
