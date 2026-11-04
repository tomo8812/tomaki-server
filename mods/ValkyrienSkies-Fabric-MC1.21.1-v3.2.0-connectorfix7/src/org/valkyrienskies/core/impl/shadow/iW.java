package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public final class iW<E> extends gA<E> implements List<E> {
   private static final long b = 1077193035000013141L;

   public static <E> iW<E> a(List<E> var0, fQ<? super E, ? extends E> var1) {
      return new iW<>(var0, var1);
   }

   private static <E> iW<E> b(List<E> var0, fQ<? super E, ? extends E> var1) {
      iW var2 = new iW(var0, var1);
      if (var0.size() > 0) {
         Object[] var3 = var0.toArray();
         var0.clear();
         Object[] var6 = var3;
         int var7 = var3.length;

         for (int var4 = 0; var4 < var7; var4++) {
            Object var5 = var6[var4];
            var2.f().add(var1.a(var5));
         }
      }

      return var2;
   }

   private iW(List<E> var1, fQ<? super E, ? extends E> var2) {
      super(var1, var2);
   }

   private List<E> a() {
      return (List<E>)this.f();
   }

   @Override
   public final boolean equals(Object object) {
      return object == this || this.f().equals(object);
   }

   @Override
   public final int hashCode() {
      return this.f().hashCode();
   }

   @Override
   public final E get(int index) {
      return ((List)this.f()).get(index);
   }

   @Override
   public final int indexOf(Object object) {
      return ((List)this.f()).indexOf(object);
   }

   @Override
   public final int lastIndexOf(Object object) {
      return ((List)this.f()).lastIndexOf(object);
   }

   @Override
   public final E remove(int index) {
      return ((List)this.f()).remove(index);
   }

   @Override
   public final void add(int index, E object) {
      object = this.b(object);
      ((List)this.f()).add(index, object);
   }

   @Override
   public final boolean addAll(int index, Collection<? extends E> coll) {
      coll = this.b(coll);
      return ((List)this.f()).addAll(index, coll);
   }

   @Override
   public final ListIterator<E> listIterator() {
      return this.listIterator(0);
   }

   @Override
   public final ListIterator<E> listIterator(int i) {
      return new iW.a(((List)this.f()).listIterator(i));
   }

   @Override
   public final E set(int index, E object) {
      object = this.b(object);
      return ((List)this.f()).set(index, object);
   }

   @Override
   public final List<E> subList(int fromIndex, int toIndex) {
      List var4 = ((List)this.f()).subList(fromIndex, toIndex);
      return new iW<>(var4, super.a);
   }

   protected class a extends hO<E> {
      protected a(ListIterator<E> var2) {
         super(var2);
      }

      @Override
      public final void add(E object) {
         object = iW.this.b(object);
         this.a().add(object);
      }

      @Override
      public final void set(E object) {
         object = iW.this.b(object);
         this.a().set(object);
      }
   }
}
