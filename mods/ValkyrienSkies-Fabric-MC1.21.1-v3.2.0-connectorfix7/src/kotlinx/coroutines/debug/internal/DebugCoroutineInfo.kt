package kotlinx.coroutines.debug.internal

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.jvm.internal.CoroutineStackFrame

@PublishedApi
internal class DebugCoroutineInfo internal constructor(source: DebugCoroutineInfoImpl, context: CoroutineContext) {
   public final val context: CoroutineContext
   internal final val creationStackBottom: CoroutineStackFrame?
   public final val sequenceNumber: Long
   public final val creationStackTrace: List<StackTraceElement>
   public final val state: String
   public final val lastObservedThread: Thread?
   public final val lastObservedFrame: CoroutineStackFrame?

   public final val lastObservedStackTrace: List<StackTraceElement>
      public final get() {
         return this.lastObservedStackTrace
      }


   init {
      this.context = context
      this.creationStackBottom = source.creationStackBottom
      this.sequenceNumber = source.sequenceNumber
      this.creationStackTrace = source.creationStackTrace
      this.state = source.state
      this.lastObservedThread = source.lastObservedThread
      this.lastObservedFrame = source.lastObservedFrame
      this.lastObservedStackTrace = source.lastObservedStackTrace$kotlinx_coroutines_core()
   }
}
