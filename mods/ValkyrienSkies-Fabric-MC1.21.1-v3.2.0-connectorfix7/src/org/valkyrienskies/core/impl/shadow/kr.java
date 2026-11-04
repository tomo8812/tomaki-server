package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;

public abstract class kr<E> extends kq<E> implements SortedSet<E> {
   private static final long a = -3462240946294214398L;

   protected kr() {
   }

   protected kr(Set<E> var1) {
      super(var1);
   }

   protected SortedSet<E> b() {
      return (SortedSet<E>)super.c();
   }

   @Override
   public SortedSet<E> subSet(E fromElement, E toElement) {
      return this.b().subSet(fromElement, toElement);
   }

   @Override
   public SortedSet<E> headSet(E toElement) {
      return this.b().headSet(toElement);
   }

   @Override
   public SortedSet<E> tailSet(E fromElement) {
      return this.b().tailSet(fromElement);
   }

   @Override
   public E first() {
      return this.b().first();
   }

   @Override
   public E last() {
      return this.b().last();
   }

   @Override
   public Comparator<? super E> comparator() {
      return this.b().comparator();
   }

   // $VF: synthetic method
   @Override
   protected Set c() {
      return this.b();
   }

   // $VF: synthetic method
   @Override
   public Collection f() {
      return this.b();
   }
}
