package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;

public final class kD<E> extends kr<E> implements fU {
   private static final long a = -725356885467962424L;

   public static <E> SortedSet<E> a(SortedSet<E> var0) {
      return var0 instanceof fU ? var0 : new kD<>(var0);
   }

   private kD(SortedSet<E> var1) {
      super(var1);
   }

   @Override
   public final Iterator<E> iterator() {
      return iz.a(this.b().iterator());
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
      return a(this.b().subSet(fromElement, toElement));
   }

   @Override
   public final SortedSet<E> headSet(E toElement) {
      return a(this.b().headSet(toElement));
   }

   @Override
   public final SortedSet<E> tailSet(E fromElement) {
      return a(this.b().tailSet(fromElement));
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(this.b());
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.a((Collection<E>)var1.readObject());
   }
}
