package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public final class is<E> implements Iterator<E> {
   private final Iterator<? extends E> a;
   private final Deque<E> b = new ArrayDeque<>();

   public static <E> is<E> a(Iterator<? extends E> var0) {
      if (var0 == null) {
         throw new NullPointerException("Iterator must not be null");
      }

      is var1;
      return var0 instanceof is ? (var1 = (is)var0) : new is<>(var0);
   }

   private is(Iterator<? extends E> var1) {
      this.a = var1;
   }

   private void a(E var1) {
      this.b.push((E)var1);
   }

   @Override
   public final boolean hasNext() {
      return !this.b.isEmpty() ? true : this.a.hasNext();
   }

   @Override
   public final E next() {
      return !this.b.isEmpty() ? this.b.pop() : this.a.next();
   }

   @Override
   public final void remove() {
      throw new UnsupportedOperationException();
   }
}
