package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2

{ value: Any ->
   var `$continuation`: Continuation
   run label48@{
      if (var2 is FlowKt__TransformKt$filter$$inlined$unsafeTransform$1$2$1) {
         `$continuation` = var2 as FlowKt__TransformKt$filter$$inlined$unsafeTransform$1$2$1
         if (((var2 as FlowKt__TransformKt$filter$$inlined$unsafeTransform$1$2$1).label and Integer.MIN_VALUE) != 0) {
            `$continuation`.label -= Integer.MIN_VALUE
            return@label48
         }
      }

      `$continuation` = FlowKt__TransformKt$filter$$inlined$unsafeTransform$1$2$1(this, var2)
   }

   val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var valuex: Any
var `$this$filter_u24lambda_u240`: FlowCollector
var var10000: Any
   when (`$continuation`.label) {
      0 -> {
         ResultKt.throwOnFailure(`$result`)
         valuex = value
         `$this$filter_u24lambda_u240` = this.$this_unsafeFlow
         val var11: Function2 = this.$predicate$inlined
         `$continuation`.L$0 = value
         `$continuation`.L$1 = `$this$filter_u24lambda_u240`
         `$continuation`.label = 1
         var10000 = var11(value, `$continuation`)
         if (var10000 === var5) {
            return var5
         }
      }
      1 -> {
         `$this$filter_u24lambda_u240` = `$continuation`.L$1 as FlowCollector
         valuex = `$continuation`.L$0
         ResultKt.throwOnFailure(`$result`)
         var10000 = `$result`
      }
      2 -> {
         ResultKt.throwOnFailure(`$result`)
         return Unit.INSTANCE
      }
      else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
   }

   if (var10000 as java.lang.Boolean) {
      `$continuation`.L$0 = null
      `$continuation`.L$1 = null
      `$continuation`.label = 2
      if (`$this$filter_u24lambda_u240`.emit(valuex, `$continuation`) === var5) {
         return var5
      }
   }

   return Unit.INSTANCE
}