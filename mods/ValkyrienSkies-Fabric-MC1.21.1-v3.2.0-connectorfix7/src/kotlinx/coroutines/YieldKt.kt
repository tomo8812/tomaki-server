package kotlinx.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlinx.coroutines.internal.DispatchedContinuation
import kotlinx.coroutines.internal.DispatchedContinuationKt

public suspend fun yield() {
   val context: CoroutineContext = `$completion`.getContext()
   JobKt.ensureActive(context)
   val var4: Continuation = IntrinsicsKt.intercepted(`$completion`)
   val var10000: DispatchedContinuation = var4 as? DispatchedContinuation
   var var7: Any
   if ((var4 as? DispatchedContinuation) == null) {
      var7 = Unit.INSTANCE
   } else {
      run label40@{
         if (var10000.dispatcher.isDispatchNeeded(context)) {
            var10000.dispatchYield$kotlinx_coroutines_core(context, Unit.INSTANCE)
         } else {
            val yieldContext: YieldContext = YieldContext()
            var10000.dispatchYield$kotlinx_coroutines_core(context.plus(yieldContext as CoroutineContext), Unit.INSTANCE)
            if (yieldContext.dispatcherWasUnconfined) {
               var7 = if (DispatchedContinuationKt.yieldUndispatched(var10000)) IntrinsicsKt.getCOROUTINE_SUSPENDED() else Unit.INSTANCE
               return@label40
            }
         }

         var7 = IntrinsicsKt.getCOROUTINE_SUSPENDED()
      }
   }

   if (var7 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      DebugProbesKt.probeCoroutineSuspended(`$completion`)
   }

   return if (var7 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var7 else Unit.INSTANCE
}
