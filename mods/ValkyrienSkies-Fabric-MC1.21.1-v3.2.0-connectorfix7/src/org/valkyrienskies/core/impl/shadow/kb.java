package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Queue;

public final class kb<E> extends gy<E> implements Queue<E> {
   private static final long b = 2307609000539943581L;

   public static <E> kb<E> a(Queue<E> var0, fF<? super E> var1) {
      return new kb<>(var0, var1);
   }

   private kb(Queue<E> var1, fF<? super E> var2) {
      super(var1, var2);
   }

   private Queue<E> a() {
      return (Queue<E>)super.f();
   }

   @Override
   public final boolean offer(E object) {
      this.b(object);
      return ((Queue)super.f()).offer(object);
   }

   @Override
   public final E poll() {
      return ((Queue)super.f()).poll();
   }

   @Override
   public final E peek() {
      return ((Queue)super.f()).peek();
   }

   @Override
   public final E element() {
      return ((Queue)super.f()).element();
   }

   @Override
   public final E remove() {
      return ((Queue)super.f()).remove();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Collection f() {
      return super.f();
   }
}
