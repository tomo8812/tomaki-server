package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.internal.Intrinsics

{ value: Any ->
   var `$continuation`: Continuation
   run label34@{
      if (var2 is FlowKt__TransformKt$filterIsInstance$$inlined$filter$1$2$1) {
         `$continuation` = var2 as FlowKt__TransformKt$filterIsInstance$$inlined$filter$1$2$1
         if (((var2 as FlowKt__TransformKt$filterIsInstance$$inlined$filter$1$2$1).label and Integer.MIN_VALUE) != 0) {
            `$continuation`.label -= Integer.MIN_VALUE
            return@label34
         }
      }

      `$continuation` = FlowKt__TransformKt$filterIsInstance$$inlined$filter$1$2$1(this, var2)
   }

   val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
   when (`$continuation`.label) {
      0 -> {
         ResultKt.throwOnFailure(`$result`)
         val `$this$filter_u24lambda_u240`: FlowCollector = this.$this_unsafeFlow
         Intrinsics.reifiedOperationMarker(3, "R")
         if (value is Any) {
            `$continuation`.label = 1
            if (`$this$filter_u24lambda_u240`.emit(value, `$continuation`) === var5) {
               return var5
            }
         }
      }
      1 -> ResultKt.throwOnFailure(`$result`)
      else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
   }

   return Unit.INSTANCE
}