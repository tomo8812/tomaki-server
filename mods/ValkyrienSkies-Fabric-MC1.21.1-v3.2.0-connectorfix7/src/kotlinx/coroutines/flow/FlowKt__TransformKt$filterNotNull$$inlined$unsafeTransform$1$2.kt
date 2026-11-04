package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt

{ value: Any ->
   var `$continuation`: Continuation
   run label34@{
      if (var2 is FlowKt__TransformKt$filterNotNull$$inlined$unsafeTransform$1$2$1) {
         `$continuation` = var2 as FlowKt__TransformKt$filterNotNull$$inlined$unsafeTransform$1$2$1
         if (((var2 as FlowKt__TransformKt$filterNotNull$$inlined$unsafeTransform$1$2$1).label and Integer.MIN_VALUE) != 0) {
            `$continuation`.label -= Integer.MIN_VALUE
            return@label34
         }
      }

      `$continuation` = FlowKt__TransformKt$filterNotNull$$inlined$unsafeTransform$1$2$1(this, var2)
   }

   val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
   when (`$continuation`.label) {
      0 -> {
         ResultKt.throwOnFailure(`$result`)
         val `$this$filterNotNull_u24lambda_u244`: FlowCollector = this.$this_unsafeFlow
         if (value != null) {
            `$continuation`.label = 1
            if (`$this$filterNotNull_u24lambda_u244`.emit(value, `$continuation`) === var5) {
               return var5
            }
         }
      }
      1 -> ResultKt.throwOnFailure(`$result`)
      else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
   }

   return Unit.INSTANCE
}