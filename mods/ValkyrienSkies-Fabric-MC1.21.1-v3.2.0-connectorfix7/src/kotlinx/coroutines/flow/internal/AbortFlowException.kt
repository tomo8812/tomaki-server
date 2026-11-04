package kotlinx.coroutines.flow.internal

import java.util.concurrent.CancellationException
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.FlowCollector
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nFlowExceptions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FlowExceptions.kt\nkotlinx/coroutines/flow/internal/AbortFlowException\n+ 2 ArrayIntrinsics.kt\nkotlin/ArrayIntrinsicsKt\n*L\n1#1,30:1\n26#2:31\n*S KotlinDebug\n*F\n+ 1 FlowExceptions.kt\nkotlinx/coroutines/flow/internal/AbortFlowException\n*L\n17#1:31\n*E\n"])
internal class AbortFlowException(owner: FlowCollector<*>) : CancellationException("Flow was aborted, no more elements needed") {
   @JvmField
   @NotNull
   public final val owner: FlowCollector<*>

   init {
      this.owner = owner
   }

   public override fun fillInStackTrace(): Throwable {
      if (DEBUG) {
         return super.fillInStackTrace()
      } else {
         this.setStackTrace(arrayOfNulls(0))
         return this
      }
   }
}
