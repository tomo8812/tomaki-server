package org.valkyrienskies.core.impl.shadow

import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher

public class Gy : CoroutineDispatcher {
   private final val a: ConcurrentLinkedQueue<Runnable> = ConcurrentLinkedQueue()

   public override fun dispatch(p0: CoroutineContext, p1: Runnable) {
      this.a.add(block)
   }

   public fun a() {
      CollectionsKt.removeAll(this.a, <unrepresentable>.a)
   }
}
