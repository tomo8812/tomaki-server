package org.valkyrienskies.core.impl.shadow;

import java.util.LinkedList;
import java.util.Queue;

public final class fI {
   private static Queue a = ke.a(new LinkedList());

   private fI() {
   }

   private static <E> Queue<E> a(Queue<E> var0) {
      return kc.a(var0);
   }

   private static <E> Queue<E> b(Queue<? extends E> var0) {
      return ke.a(var0);
   }

   private static <E> Queue<E> a(Queue<E> var0, fF<? super E> var1) {
      return kb.a(var0, var1);
   }

   private static <E> Queue<E> a(Queue<E> var0, fQ<? super E, ? extends E> var1) {
      return kd.a(var0, var1);
   }

   private static <E> Queue<E> a() {
      return a;
   }
}
