package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Iterator;

public final class gB<E> extends gv<E> implements eZ<E>, fU {
   private static final long a = -7112672385450340330L;

   private static <E> eZ<E> a(eZ<? extends E> var0) {
      return var0 instanceof fU ? var0 : new gB<>(var0);
   }

   public static <E> eZ<E> b(Collection<? extends E> var0) {
      if (var0 == null) {
         throw new NullPointerException("Collection must not be null.");
      }

      for (int var1 = 0; var1 < 1000 && !(var0 instanceof eZ); var1++) {
         if (var0 instanceof gv) {
            var0 = ((gv)var0).f();
         } else if (var0 instanceof gz) {
            var0 = ((gz)var0).e();
         }
      }

      if (!(var0 instanceof eZ)) {
         throw new IllegalArgumentException("Collection is not a bounded collection.");
      } else {
         return new gB<>((eZ<? extends E>)var0);
      }
   }

   private gB(eZ<? extends E> var1) {
      super(var1);
   }

   @Override
   public final Iterator<E> iterator() {
      return iz.a(((eZ)super.f()).iterator());
   }

   @Override
   public final boolean add(E object) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean addAll(Collection<? extends E> coll) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean remove(Object object) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean removeAll(Collection<?> coll) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean retainAll(Collection<?> coll) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final boolean a() {
      return ((eZ)super.f()).a();
   }

   @Override
   public final int b() {
      return ((eZ)super.f()).b();
   }

   private eZ<E> c() {
      return (eZ<E>)super.f();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected final Collection f() {
      return super.f();
   }
}
