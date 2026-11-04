package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt

{ value: Any ->
   var `$continuation`: Continuation
   run label48@{
      if (var2 is FlowKt__TransformKt$mapNotNull$$inlined$unsafeTransform$1$2$1) {
         `$continuation` = var2 as FlowKt__TransformKt$mapNotNull$$inlined$unsafeTransform$1$2$1
         if (((var2 as FlowKt__TransformKt$mapNotNull$$inlined$unsafeTransform$1$2$1).label and Integer.MIN_VALUE) != 0) {
            `$continuation`.label -= Integer.MIN_VALUE
            return@label48
         }
      }

      `$continuation` = FlowKt__TransformKt$mapNotNull$$inlined$unsafeTransform$1$2$1(this, var2)
   }

   val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var `$this$mapNotNull_u24lambda_u246`: FlowCollector
var var10000: Any
   when (`$continuation`.label) {
      0 -> {
         ResultKt.throwOnFailure(`$result`)
         `$this$mapNotNull_u24lambda_u246` = this.$this_unsafeFlow
         var10000 = this.$transform$inlined
         `$continuation`.L$0 = this.$this_unsafeFlow
         `$continuation`.label = 1
         var10000 = var10000(value, `$continuation`)
         if (var10000 === var5) {
            return var5
         }
      }
      1 -> {
         `$this$mapNotNull_u24lambda_u246` = `$continuation`.L$0 as FlowCollector
         ResultKt.throwOnFailure(`$result`)
         var10000 = `$result`
      }
      2 -> {
         ResultKt.throwOnFailure(`$result`)
         return Unit.INSTANCE
      }
      else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
   }

   if (var10000 != null) {
      `$continuation`.L$0 = null
      `$continuation`.label = 2
      if (`$this$mapNotNull_u24lambda_u246`.emit(var10000, `$continuation`) === var5) {
         return var5
      }
   }

   return Unit.INSTANCE
}