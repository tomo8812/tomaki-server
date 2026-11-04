package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Queue;

public final class kc<E> extends gz<E> implements Queue<E> {
   private static final long b = 1L;

   public static <E> kc<E> a(Queue<E> var0) {
      return new kc<>(var0);
   }

   private kc(Queue<E> var1) {
      super(var1);
   }

   private kc(Queue<E> var1, Object var2) {
      super(var1, var2);
   }

   private Queue<E> a() {
      return (Queue<E>)super.e();
   }

   @Override
   public final E element() {
      synchronized (super.a) {
         return ((Queue)super.e()).element();
      }
   }

   @Override
   public final boolean equals(Object object) {
      if (object == this) {
         return true;
      }

      synchronized (super.a) {
         return super.e().equals(object);
      }
   }

   @Override
   public final int hashCode() {
      synchronized (super.a) {
         return super.e().hashCode();
      }
   }

   @Override
   public final boolean offer(E e) {
      synchronized (super.a) {
         return ((Queue)super.e()).offer(e);
      }
   }

   @Override
   public final E peek() {
      synchronized (super.a) {
         return ((Queue)super.e()).peek();
      }
   }

   @Override
   public final E poll() {
      synchronized (super.a) {
         return ((Queue)super.e()).poll();
      }
   }

   @Override
   public final E remove() {
      synchronized (super.a) {
         return ((Queue)super.e()).remove();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Collection e() {
      return super.e();
   }
}
