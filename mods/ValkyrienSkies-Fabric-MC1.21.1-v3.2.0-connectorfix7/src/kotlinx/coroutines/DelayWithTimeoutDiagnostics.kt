package kotlinx.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.time.Duration

@InternalCoroutinesApi
internal interface DelayWithTimeoutDiagnostics : Delay {
   public abstract fun timeoutMessage(timeout: Duration): String {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      /** @deprecated */
      @Deprecated(message = "Deprecated without replacement as an internal method never intended for public use", level = DeprecationLevel.ERROR)
      @JvmStatic
      fun delay(`$this`: DelayWithTimeoutDiagnostics, time: Long, `$completion`: Continuation<in Unit>) {
         val var10000: Any = Delay.DefaultImpls.delay(`$this`, time, `$completion`)
         if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
      }

      @JvmStatic
      fun invokeOnTimeout(`$this`: DelayWithTimeoutDiagnostics, timeMillis: Long, block: Runnable, context: CoroutineContext): DisposableHandle {
         Delay.DefaultImpls.invokeOnTimeout(`$this`, timeMillis, block, context)
      }
   }
}
