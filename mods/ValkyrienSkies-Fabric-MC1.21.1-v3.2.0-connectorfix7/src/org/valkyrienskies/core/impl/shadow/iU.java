package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public final class iU<E> extends gy<E> implements List<E> {
   private static final long b = -5722039223898659102L;

   public static <T> iU<T> a(List<T> var0, fF<? super T> var1) {
      return new iU(var0, var1);
   }

   private iU(List<E> var1, fF<? super E> var2) {
      super(var1, var2);
   }

   private List<E> a() {
      return (List<E>)super.f();
   }

   @Override
   public final boolean equals(Object object) {
      return object == this || ((List)super.f()).equals(object);
   }

   @Override
   public final int hashCode() {
      return ((List)super.f()).hashCode();
   }

   @Override
   public final E get(int index) {
      return ((List)super.f()).get(index);
   }

   @Override
   public final int indexOf(Object object) {
      return ((List)super.f()).indexOf(object);
   }

   @Override
   public final int lastIndexOf(Object object) {
      return ((List)super.f()).lastIndexOf(object);
   }

   @Override
   public final E remove(int index) {
      return ((List)super.f()).remove(index);
   }

   @Override
   public final void add(int index, E object) {
      this.b(object);
      ((List)super.f()).add(index, object);
   }

   @Override
   public final boolean addAll(int index, Collection<? extends E> coll) {
      for (Object var4 : coll) {
         this.b((E)var4);
      }

      return ((List)super.f()).addAll(index, coll);
   }

   @Override
   public final ListIterator<E> listIterator() {
      return this.listIterator(0);
   }

   @Override
   public final ListIterator<E> listIterator(int i) {
      return new iU.a(((List)super.f()).listIterator(i));
   }

   @Override
   public final E set(int index, E object) {
      this.b(object);
      return ((List)super.f()).set(index, object);
   }

   @Override
   public final List<E> subList(int fromIndex, int toIndex) {
      List var4 = ((List)super.f()).subList(fromIndex, toIndex);
      return new iU<>(var4, super.a);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Collection f() {
      return super.f();
   }

   protected class a extends hO<E> {
      protected a(ListIterator<E> var2) {
         super(var2);
      }

      @Override
      public final void add(E object) {
         iU.this.b(object);
         this.a().add(object);
      }

      @Override
      public final void set(E object) {
         iU.this.b(object);
         this.a().set(object);
      }
   }
}
