package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Set;

public abstract class kq<E> extends gv<E> implements Set<E> {
   private static final long a = -4678668309576958546L;

   protected kq() {
   }

   public kq(Set<E> var1) {
      super(var1);
   }

   protected Set<E> c() {
      return (Set<E>)super.f();
   }

   @Override
   public boolean equals(Object object) {
      return object == this || this.c().equals(object);
   }

   @Override
   public int hashCode() {
      return this.c().hashCode();
   }

   // $VF: synthetic method
   @Override
   public Collection f() {
      return this.c();
   }
}
