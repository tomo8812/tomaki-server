package _COROUTINE

internal class ArtificialStackFrames {
   public fun coroutineCreation(): StackTraceElement {
      return CoroutineDebuggingKt.access$artificialFrame(Exception(), _CREATION.class.getSimpleName())
   }

   public fun coroutineBoundary(): StackTraceElement {
      return CoroutineDebuggingKt.access$artificialFrame(Exception(), _BOUNDARY.class.getSimpleName())
   }
}
