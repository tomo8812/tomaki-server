package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;

public class kx<E> extends kw<E> implements SortedSet<E> {
   private static final long b = -9110948148132275052L;

   public static <E> kx<E> a(SortedSet<E> var0, fF<? super E> var1) {
      return new kx<>(var0, var1);
   }

   protected kx(SortedSet<E> var1, fF<? super E> var2) {
      super(var1, var2);
   }

   protected SortedSet<E> a() {
      return (SortedSet<E>)super.b();
   }

   @Override
   public Comparator<? super E> comparator() {
      return this.a().comparator();
   }

   @Override
   public E first() {
      return this.a().first();
   }

   @Override
   public E last() {
      return this.a().last();
   }

   @Override
   public SortedSet<E> subSet(E fromElement, E toElement) {
      fromElement = (E)this.a().subSet(fromElement, toElement);
      return new kx<>(fromElement, super.a);
   }

   @Override
   public SortedSet<E> headSet(E toElement) {
      toElement = (E)this.a().headSet(toElement);
      return new kx<>(toElement, super.a);
   }

   @Override
   public SortedSet<E> tailSet(E fromElement) {
      fromElement = (E)this.a().tailSet(fromElement);
      return new kx<>(fromElement, super.a);
   }

   // $VF: synthetic method
   @Override
   protected Set b() {
      return this.a();
   }

   // $VF: synthetic method
   @Override
   public Collection f() {
      return this.a();
   }
}
