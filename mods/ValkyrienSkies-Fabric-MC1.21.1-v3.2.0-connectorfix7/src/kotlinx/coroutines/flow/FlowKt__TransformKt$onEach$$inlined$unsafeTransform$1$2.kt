package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.InlineMarker

{ value: Any ->
   var `$continuation`: Continuation
   run label36@{
      if (var2 is FlowKt__TransformKt$onEach$$inlined$unsafeTransform$1$2$1) {
         `$continuation` = var2 as FlowKt__TransformKt$onEach$$inlined$unsafeTransform$1$2$1
         if (((var2 as FlowKt__TransformKt$onEach$$inlined$unsafeTransform$1$2$1).label and Integer.MIN_VALUE) != 0) {
            `$continuation`.label -= Integer.MIN_VALUE
            return@label36
         }
      }

      `$continuation` = FlowKt__TransformKt$onEach$$inlined$unsafeTransform$1$2$1(this, var2)
   }

   val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var valuex: Any
var `$this$onEach_u24lambda_u248`: FlowCollector
   when (`$continuation`.label) {
      0 -> {
         ResultKt.throwOnFailure(`$result`)
         val var6: Continuation = `$continuation`
         valuex = value
         `$this$onEach_u24lambda_u248` = this.$this_unsafeFlow
         val var10000: Function2 = this.$action$inlined
         `$continuation`.L$0 = value
         `$continuation`.L$1 = `$this$onEach_u24lambda_u248`
         `$continuation`.label = 1
         InlineMarker.mark(6)
         val var12: Any = var10000(value, `$continuation`)
         InlineMarker.mark(7)
         if (var12 === var5) {
            return var5
         }
      }
      1 -> {
         `$this$onEach_u24lambda_u248` = `$continuation`.L$1 as FlowCollector
         valuex = `$continuation`.L$0
         ResultKt.throwOnFailure(`$result`)
      }
      2 -> {
         ResultKt.throwOnFailure(`$result`)
         return Unit.INSTANCE
      }
      else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
   }

   `$continuation`.L$0 = null
   `$continuation`.L$1 = null
   `$continuation`.label = 2
   return if (`$this$onEach_u24lambda_u248`.emit(valuex, `$continuation`) === var5) var5 else Unit.INSTANCE
}