package kotlinx.coroutines.debug.internal

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

internal class HashedWeakRef<T>(ref: Any, queue: ReferenceQueue<Any>?) : WeakReference((T)ref, queue) {
   @JvmField
   public final val hash: Int

   init {
      this.hash = if (ref != null) ref.hashCode() else 0
   }
}
