package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public final class ke<E> extends jZ<E> implements fU {
   private static final long a = 1832948656215393357L;

   public static <E> Queue<E> a(Queue<? extends E> var0) {
      return var0 instanceof fU ? var0 : new ke<>(var0);
   }

   private ke(Queue<? extends E> var1) {
      super(var1);
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      var1.writeObject(this.a());
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      this.a((Collection<E>)var1.readObject());
   }

   @Override
   public final Iterator<E> iterator() {
      return iz.a(this.a().iterator());
   }

   @Override
   public final boolean add(Object object) {
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
   public final boolean offer(E obj) {
      throw new UnsupportedOperationException();
   }

   @Override
   public final E poll() {
      throw new UnsupportedOperationException();
   }

   @Override
   public final E remove() {
      throw new UnsupportedOperationException();
   }
}
