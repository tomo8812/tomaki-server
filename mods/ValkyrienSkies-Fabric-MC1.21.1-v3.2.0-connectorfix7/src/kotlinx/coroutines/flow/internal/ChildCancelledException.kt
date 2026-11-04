package kotlinx.coroutines.flow.internal

import java.util.concurrent.CancellationException
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nFlowExceptions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FlowExceptions.kt\nkotlinx/coroutines/flow/internal/ChildCancelledException\n+ 2 ArrayIntrinsics.kt\nkotlin/ArrayIntrinsicsKt\n*L\n1#1,30:1\n26#2:31\n*S KotlinDebug\n*F\n+ 1 FlowExceptions.kt\nkotlinx/coroutines/flow/internal/ChildCancelledException\n*L\n26#1:31\n*E\n"])
internal class ChildCancelledException : CancellationException("Child of the scoped flow was cancelled") {
   public override fun fillInStackTrace(): Throwable {
      if (DEBUG) {
         return super.fillInStackTrace()
      } else {
         this.setStackTrace(arrayOfNulls(0))
         return this
      }
   }
}
