package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2

{ value: Any ->
   var `$continuation`: Continuation
   run label36@{
      if (var2 is FlowKt__MergeKt$flatMapMerge$$inlined$map$1$2$1) {
         `$continuation` = var2 as FlowKt__MergeKt$flatMapMerge$$inlined$map$1$2$1
         if (((var2 as FlowKt__MergeKt$flatMapMerge$$inlined$map$1$2$1).label and Integer.MIN_VALUE) != 0) {
            `$continuation`.label -= Integer.MIN_VALUE
            return@label36
         }
      }

      `$continuation` = FlowKt__MergeKt$flatMapMerge$$inlined$map$1$2$1(this, var2)
   }

   val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var var9: FlowCollector
var var10000: Any
   when (`$continuation`.label) {
      0 -> {
         ResultKt.throwOnFailure(`$result`)
         var9 = this.$this_unsafeFlow
         var10000 = this.$transform$inlined
         `$continuation`.L$0 = this.$this_unsafeFlow
         `$continuation`.label = 1
         var10000 = (Function2)var10000(value, `$continuation`)
         if (var10000 === var5) {
            return var5
         }
      }
      1 -> {
         var9 = `$continuation`.L$0 as FlowCollector
         ResultKt.throwOnFailure(`$result`)
         var10000 = (Function2)`$result`
      }
      2 -> {
         ResultKt.throwOnFailure(`$result`)
         return Unit.INSTANCE
      }
      else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
   }

   `$continuation`.L$0 = null
   `$continuation`.label = 2
   return if (var9.emit(var10000, `$continuation`) === var5) var5 else Unit.INSTANCE
}