package kotlinx.coroutines.flow.internal

import kotlin.coroutines.Continuation

internal abstract class AbstractSharedFlowSlot<F> {
   public abstract fun allocateLocked(flow: Any): Boolean {
   }

   public abstract fun freeLocked(flow: Any): Array<Continuation<Unit>?> {
   }
}
