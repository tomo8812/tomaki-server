package kotlinx.coroutines.debug.internal

import kotlin.coroutines.jvm.internal.CoroutineStackFrame
import org.jetbrains.annotations.NotNull

@PublishedApi
internal class StackTraceFrame internal constructor(callerFrame: CoroutineStackFrame?, stackTraceElement: StackTraceElement) : CoroutineStackFrame {
   public open val callerFrame: CoroutineStackFrame?

   @JvmField
   @NotNull
   public final val stackTraceElement: StackTraceElement

   init {
      this.callerFrame = callerFrame
      this.stackTraceElement = stackTraceElement
   }

   public open fun getStackTraceElement(): StackTraceElement {
      return this.stackTraceElement
   }
}
