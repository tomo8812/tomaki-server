package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Queue;

public abstract class jZ<E> extends gv<E> implements Queue<E> {
   private static final long a = -2629815475789577029L;

   protected jZ() {
   }

   protected jZ(Queue<E> var1) {
      super(var1);
   }

   protected final Queue<E> a() {
      return (Queue<E>)super.f();
   }

   @Override
   public boolean offer(E obj) {
      return ((Queue)super.f()).offer(obj);
   }

   @Override
   public E poll() {
      return ((Queue)super.f()).poll();
   }

   @Override
   public E peek() {
      return ((Queue)super.f()).peek();
   }

   @Override
   public E element() {
      return ((Queue)super.f()).element();
   }

   @Override
   public E remove() {
      return ((Queue)super.f()).remove();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Collection f() {
      return super.f();
   }
}
