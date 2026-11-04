package org.valkyrienskies.core.impl.shadow;

import java.util.Queue;

public final class kd<E> extends gA<E> implements Queue<E> {
   private static final long b = -7901091318986132033L;

   public static <E> kd<E> a(Queue<E> var0, fQ<? super E, ? extends E> var1) {
      return new kd<>(var0, var1);
   }

   private static <E> kd<E> b(Queue<E> var0, fQ<? super E, ? extends E> var1) {
      kd var2 = new kd(var0, var1);
      if (var0.size() > 0) {
         Object[] var3 = var0.toArray();
         var0.clear();
         Object[] var6 = var3;
         int var7 = var3.length;

         for (int var4 = 0; var4 < var7; var4++) {
            Object var5 = var6[var4];
            var2.f().add(var1.a(var5));
         }
      }

      return var2;
   }

   private kd(Queue<E> var1, fQ<? super E, ? extends E> var2) {
      super(var1, var2);
   }

   private Queue<E> a() {
      return (Queue<E>)this.f();
   }

   @Override
   public final boolean offer(E obj) {
      return ((Queue)this.f()).offer(this.b(obj));
   }

   @Override
   public final E poll() {
      return ((Queue)this.f()).poll();
   }

   @Override
   public final E peek() {
      return ((Queue)this.f()).peek();
   }

   @Override
   public final E element() {
      return ((Queue)this.f()).element();
   }

   @Override
   public final E remove() {
      return ((Queue)this.f()).remove();
   }
}
