package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Set;

public class kw<E> extends gy<E> implements Set<E> {
   private static final long b = -684521469108685117L;

   public static <E> kw<E> a(Set<E> var0, fF<? super E> var1) {
      return new kw<>(var0, var1);
   }

   protected kw(Set<E> var1, fF<? super E> var2) {
      super(var1, var2);
   }

   protected Set<E> b() {
      return (Set<E>)super.f();
   }

   @Override
   public boolean equals(Object object) {
      return object == this || this.b().equals(object);
   }

   @Override
   public int hashCode() {
      return this.b().hashCode();
   }

   // $VF: synthetic method
   @Override
   public Collection f() {
      return this.b();
   }
}
