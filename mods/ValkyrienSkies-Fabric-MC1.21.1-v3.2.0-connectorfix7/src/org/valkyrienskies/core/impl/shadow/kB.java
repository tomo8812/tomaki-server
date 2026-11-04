package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;

public final class kB<E> extends ko<E> implements fU {
   private static final long a = 20150528L;

   public static <E> NavigableSet<E> a(NavigableSet<E> var0) {
      return var0 instanceof fU ? var0 : new kB<>(var0);
   }

   private kB(NavigableSet<E> var1) {
      super(var1);
   }

   @Override
   public final Iterator<E> iterator() {
      return iz.a(this.a().iterator());
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
   public final SortedSet<E> subSet(E fromElement, E toElement) {
      return kD.a(this.a().subSet(fromElement, toElement));
   }

   @Override
   public final SortedSet<E> headSet(E toElement) {
      return kD.a(this.a().headSet(toElement));
   }

   @Override
   public final SortedSet<E> tailSet(E fromElement) {
      return kD.a(this.a().tailSet(fromElement));
   }

   @Override
   public final NavigableSet<E> descendingSet() {
      return a(this.a().descendingSet());
   }

   @Override
   public final Iterator<E> descendingIterator() {
      return iz.a(this.a().descendingIterator());
   }

   @Override
   public final NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
      return a(this.a().subSet(fromElement, fromInclusive, toElement, toInclusive));
   }

   @Override
   public final NavigableSet<E> headSet(E toElement, boolean inclusive) {
      return a(this.a().headSet(toElement, inclusive));
   }

   @Override
   public final NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
      return a(this.a().tailSet(fromElement, inclusive));
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(this.a());
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.a((Collection<E>)var1.readObject());
   }
}
