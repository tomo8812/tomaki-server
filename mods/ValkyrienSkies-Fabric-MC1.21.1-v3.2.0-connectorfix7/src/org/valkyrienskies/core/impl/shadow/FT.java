package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;

public final class FT<E> extends AbstractQueue<E> implements Serializable, BlockingQueue<E> {
   private final ReentrantLock a = new ReentrantLock();
   private final Condition b = this.a.newCondition();
   private final SortedSet<E> c = new TreeSet<>();

   private int a() {
      this.a.lock();

      try {
         return this.c.size();
      } finally {
         this.a.unlock();
      }
   }

   @Override
   public final Iterator<E> iterator() {
      this.a.lock();

      try {
         return CollectionsKt.toMutableList(this.c).listIterator();
      } finally {
         this.a.unlock();
      }
   }

   @Override
   public final E poll(long timeout, TimeUnit unit) {
      Intrinsics.checkNotNullParameter(unit, "");
      long var4 = unit.toNanos(timeout);
      this.a.lockInterruptibly();

      try {
         Object var8 = null;

         while (var4 > 0L && (var8 = this.b()) == null) {
            var4 = this.b.awaitNanos(var4);
         }

         return (E)var8;
      } finally {
         this.a.unlock();
      }
   }

   @Override
   public final E poll() {
      this.a.lock();

      try {
         return this.b();
      } finally {
         this.a.unlock();
      }
   }

   private final E b() {
      if (this.c.isEmpty()) {
         return null;
      }

      Object var1 = this.c.first();
      this.c.remove(var1);
      return (E)var1;
   }

   @Override
   public final E peek() {
      this.a.lock();

      try {
         return !this.c.isEmpty() ? this.c.first() : null;
      } finally {
         this.a.unlock();
      }
   }

   @Override
   public final E take() {
      this.a.lockInterruptibly();

      try {
         Object var1;
         while ((var1 = this.b()) == null) {
            this.b.await();
         }

         return (E)var1;
      } finally {
         this.a.unlock();
      }
   }

   @Override
   public final int remainingCapacity() {
      return Integer.MAX_VALUE;
   }

   @Override
   public final int drainTo(Collection<? super E> drain, int maxElements) {
      Intrinsics.checkNotNullParameter(drain, "");
      if (Intrinsics.areEqual(drain, this)) {
         throw new IllegalArgumentException();
      }

      if (maxElements <= 0) {
         return 0;
      }

      this.a.lock();

      try {
         int var3 = 0;

         for (Iterator var4 = this.c.iterator(); var4.hasNext() && var3 < maxElements; var3++) {
            drain.add(var4.next());
         }

         return var3;
      } finally {
         this.a.unlock();
      }
   }

   @Override
   public final int drainTo(Collection<? super E> drain) {
      Intrinsics.checkNotNullParameter(drain, "");
      return this.drainTo(drain, Integer.MAX_VALUE);
   }

   @Override
   public final void put(E e) {
      this.offer((E)e);
   }

   @Override
   public final boolean offer(E e, long timeout, TimeUnit unit) {
      Intrinsics.checkNotNullParameter(unit, "");
      return this.offer((E)e);
   }

   @Override
   public final boolean offer(E e) {
      this.a.lock();

      try {
         this.c.add((E)e);
         this.b.signal();
      } finally {
         this.a.unlock();
      }

      return true;
   }

   private boolean a(E var1) {
      this.a.lock();

      try {
         return this.c.remove(var1);
      } finally {
         this.a.unlock();
      }
   }

   private void a(Function0<Unit> var1) {
      Intrinsics.checkNotNullParameter(var1, "");
      this.a.lock();

      try {
         var1.invoke();
      } finally {
         InlineMarker.finallyStart(1);
         this.a.unlock();
         InlineMarker.finallyEnd(1);
      }
   }

   @Override
   public final int size() {
      return this.a();
   }

   // $VF: synthetic method
   private static ReentrantLock a(FT var0) {
      return var0.a;
   }
}
