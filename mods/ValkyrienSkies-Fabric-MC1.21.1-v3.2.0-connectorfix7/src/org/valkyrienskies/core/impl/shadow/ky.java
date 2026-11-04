package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableSet;

public final class ky<E> extends kA<E> implements NavigableSet<E> {
   private static final long b = 20150528L;

   public static <E> ky<E> a(NavigableSet<E> var0, fQ<? super E, ? extends E> var1) {
      return new ky<>(var0, var1);
   }

   private static <E> ky<E> b(NavigableSet<E> var0, fQ<? super E, ? extends E> var1) {
      ky var2 = new ky(var0, var1);
      if (var0.size() > 0) {
         Object[] var3 = var0.toArray();
         var0.clear();
         Object[] var7 = var3;
         int var8 = var3.length;

         for (int var4 = 0; var4 < var8; var4++) {
            Object var5 = var7[var4];
            ((NavigableSet)var2.f()).add(var1.a(var5));
         }
      }

      return var2;
   }

   private ky(NavigableSet<E> var1, fQ<? super E, ? extends E> var2) {
      super(var1, var2);
   }

   private NavigableSet<E> a() {
      return (NavigableSet<E>)super.f();
   }

   @Override
   public final E lower(E e) {
      return ((NavigableSet)super.f()).lower(e);
   }

   @Override
   public final E floor(E e) {
      return ((NavigableSet)super.f()).floor(e);
   }

   @Override
   public final E ceiling(E e) {
      return ((NavigableSet)super.f()).ceiling(e);
   }

   @Override
   public final E higher(E e) {
      return ((NavigableSet)super.f()).higher(e);
   }

   @Override
   public final E pollFirst() {
      return ((NavigableSet)super.f()).pollFirst();
   }

   @Override
   public final E pollLast() {
      return ((NavigableSet)super.f()).pollLast();
   }

   @Override
   public final NavigableSet<E> descendingSet() {
      return a(((NavigableSet)super.f()).descendingSet(), super.a);
   }

   @Override
   public final Iterator<E> descendingIterator() {
      return ((NavigableSet)super.f()).descendingIterator();
   }

   @Override
   public final NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
      return a(((NavigableSet)super.f()).subSet(fromElement, fromInclusive, toElement, toInclusive), super.a);
   }

   @Override
   public final NavigableSet<E> headSet(E toElement, boolean inclusive) {
      return a(((NavigableSet)super.f()).headSet(toElement, inclusive), super.a);
   }

   @Override
   public final NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
      return a(((NavigableSet)super.f()).tailSet(fromElement, inclusive), super.a);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Collection f() {
      return super.f();
   }
}
