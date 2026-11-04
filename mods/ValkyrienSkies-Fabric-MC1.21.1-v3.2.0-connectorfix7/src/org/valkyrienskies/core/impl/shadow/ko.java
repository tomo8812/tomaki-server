package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

public abstract class ko<E> extends kr<E> implements NavigableSet<E> {
   private static final long a = 20150528L;

   protected ko() {
   }

   protected ko(NavigableSet<E> var1) {
      super(var1);
   }

   protected final NavigableSet<E> a() {
      return (NavigableSet<E>)super.b();
   }

   @Override
   public E lower(E e) {
      return ((NavigableSet)super.b()).lower(e);
   }

   @Override
   public E floor(E e) {
      return ((NavigableSet)super.b()).floor(e);
   }

   @Override
   public E ceiling(E e) {
      return ((NavigableSet)super.b()).ceiling(e);
   }

   @Override
   public E higher(E e) {
      return ((NavigableSet)super.b()).higher(e);
   }

   @Override
   public E pollFirst() {
      return ((NavigableSet)super.b()).pollFirst();
   }

   @Override
   public E pollLast() {
      return ((NavigableSet)super.b()).pollLast();
   }

   @Override
   public NavigableSet<E> descendingSet() {
      return ((NavigableSet)super.b()).descendingSet();
   }

   @Override
   public Iterator<E> descendingIterator() {
      return ((NavigableSet)super.b()).descendingIterator();
   }

   @Override
   public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
      return ((NavigableSet)super.b()).subSet(fromElement, fromInclusive, toElement, toInclusive);
   }

   @Override
   public NavigableSet<E> headSet(E toElement, boolean inclusive) {
      return ((NavigableSet)super.b()).headSet(toElement, inclusive);
   }

   @Override
   public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
      return ((NavigableSet)super.b()).tailSet(fromElement, inclusive);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected final SortedSet b() {
      return super.b();
   }

   // $VF: synthetic method
   @Override
   protected final Set c() {
      return super.b();
   }

   // $VF: synthetic method
   @Override
   public final Collection f() {
      return super.b();
   }
}
