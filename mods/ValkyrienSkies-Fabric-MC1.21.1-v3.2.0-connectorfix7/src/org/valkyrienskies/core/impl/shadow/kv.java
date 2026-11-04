package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

public final class kv<E> extends kx<E> implements NavigableSet<E> {
   private static final long b = 20150528L;

   public static <E> kv<E> a(NavigableSet<E> var0, fF<? super E> var1) {
      return new kv<>(var0, var1);
   }

   private kv(NavigableSet<E> var1, fF<? super E> var2) {
      super(var1, var2);
   }

   private NavigableSet<E> c() {
      return (NavigableSet<E>)super.a();
   }

   @Override
   public final E lower(E e) {
      return ((NavigableSet)super.a()).lower(e);
   }

   @Override
   public final E floor(E e) {
      return ((NavigableSet)super.a()).floor(e);
   }

   @Override
   public final E ceiling(E e) {
      return ((NavigableSet)super.a()).ceiling(e);
   }

   @Override
   public final E higher(E e) {
      return ((NavigableSet)super.a()).higher(e);
   }

   @Override
   public final E pollFirst() {
      return ((NavigableSet)super.a()).pollFirst();
   }

   @Override
   public final E pollLast() {
      return ((NavigableSet)super.a()).pollLast();
   }

   @Override
   public final NavigableSet<E> descendingSet() {
      return a(((NavigableSet)super.a()).descendingSet(), super.a);
   }

   @Override
   public final Iterator<E> descendingIterator() {
      return ((NavigableSet)super.a()).descendingIterator();
   }

   @Override
   public final NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
      return a(((NavigableSet)super.a()).subSet(fromElement, fromInclusive, toElement, toInclusive), super.a);
   }

   @Override
   public final NavigableSet<E> headSet(E toElement, boolean inclusive) {
      return a(((NavigableSet)super.a()).headSet(toElement, inclusive), super.a);
   }

   @Override
   public final NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
      return a(((NavigableSet)super.a()).tailSet(fromElement, inclusive), super.a);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected final SortedSet a() {
      return super.a();
   }

   // $VF: synthetic method
   @Override
   protected final Set b() {
      return super.a();
   }

   // $VF: synthetic method
   @Override
   public final Collection f() {
      return super.a();
   }
}
