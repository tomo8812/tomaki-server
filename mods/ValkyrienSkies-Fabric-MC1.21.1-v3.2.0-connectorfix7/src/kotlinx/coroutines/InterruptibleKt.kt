package kotlinx.coroutines

import java.util.concurrent.CancellationException
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.functions.Function0
import kotlin.jvm.functions.Function2

private const val WORKING: Int = 0
private const val FINISHED: Int = 1
private const val INTERRUPTING: Int = 2
private const val INTERRUPTED: Int = 3

public suspend fun <T> runInterruptible(context: CoroutineContext = ..., block: () -> Any): Any {
   return BuildersKt.withContext(context, {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, `$completion`)
}

@JvmSynthetic
fun `runInterruptible$default`(var0: CoroutineContext, var1: Function0, var2: Continuation, var3: Int, var4: Any) {
   if ((var3 and 1) != 0) {
      var0 = EmptyCoroutineContext.INSTANCE as CoroutineContext
   }

   runInterruptible(var0, var1, var2)
}

private fun <T> runInterruptibleInExpectedContext(coroutineContext: CoroutineContext, block: () -> Any): Any {
   try {
      val e: ThreadState = ThreadState(JobKt.getJob(coroutineContext))
      e.setup()

      try {
         return (T)block()
      } finally {
         e.clearInterrupt()
      }
   } catch (var7: InterruptedException) {
      throw CancellationException("Blocking call was interrupted due to parent cancellation").initCause(var7)
   }
}
