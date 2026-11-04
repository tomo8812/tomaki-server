package kotlinx.coroutines.channels

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineExceptionHandler

@SourceDebugExtension(["SMAP\nCoroutineExceptionHandler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CoroutineExceptionHandler.kt\nkotlinx/coroutines/CoroutineExceptionHandlerKt$CoroutineExceptionHandler$1\n+ 2 Broadcast.kt\nkotlinx/coroutines/channels/BroadcastKt\n*L\n1#1,110:1\n51#2:111\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `BroadcastKt$broadcast$$inlined$CoroutineExceptionHandler$1` : AbstractCoroutineContextElement, CoroutineExceptionHandler {
   fun `BroadcastKt$broadcast$$inlined$CoroutineExceptionHandler$1`(`$super_call_param$1`: CoroutineExceptionHandler.Key) {
      super(`$super_call_param$1`)
   }

   public override fun handleException(context: CoroutineContext, exception: Throwable) {
   }
}
