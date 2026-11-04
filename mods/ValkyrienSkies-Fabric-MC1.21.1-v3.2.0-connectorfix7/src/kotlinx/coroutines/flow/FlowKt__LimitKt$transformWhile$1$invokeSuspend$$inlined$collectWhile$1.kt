package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.internal.AbortFlowException

@SourceDebugExtension(["SMAP\nLimit.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1\n+ 2 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt$transformWhile$1\n*L\n1#1,141:1\n120#2:142\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__LimitKt$transformWhile$1$invokeSuspend$$inlined$collectWhile$1` : FlowCollector<T> {
   @JvmSynthetic
   Function3 $transform$inlined;
   @JvmSynthetic
   FlowCollector $$this$flow$inlined;

   fun `FlowKt__LimitKt$transformWhile$1$invokeSuspend$$inlined$collectWhile$1`(var1: Function3, var2: FlowCollector) {
      this.$transform$inlined = var1
      this.$$this$flow$inlined = var2
   }

   public override suspend fun emit(value: Any) {
      var `$continuation`: Continuation
      run label29@{
         if (var2 is FlowKt__LimitKt$transformWhile$1$invokeSuspend$$inlined$collectWhile$1$1) {
            `$continuation` = var2 as FlowKt__LimitKt$transformWhile$1$invokeSuspend$$inlined$collectWhile$1$1
            if (((var2 as FlowKt__LimitKt$transformWhile$1$invokeSuspend$$inlined$collectWhile$1$1).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE
               return@label29
            }
         }

         `$continuation` = FlowKt__LimitKt$transformWhile$1$invokeSuspend$$inlined$collectWhile$1$1(this, var2)
      }

      val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var var10000: Any
      when (`$continuation`.label) {
         0 -> {
            ResultKt.throwOnFailure(`$result`)
            val var6: Continuation = `$continuation`
            val var10: Function3 = this.$transform$inlined
            val var10001: FlowCollector = this.$$this$flow$inlined
            `$continuation`.L$0 = this
            `$continuation`.label = 1
            InlineMarker.mark(6)
            var10000 = var10(var10001, value, `$continuation`)
            InlineMarker.mark(7)
            if (var10000 === var5) {
               return var5
            }
         }
         1 -> {
            this = `$continuation`.L$0 as FlowKt__LimitKt$transformWhile$1$invokeSuspend$$inlined$collectWhile$1
            ResultKt.throwOnFailure(`$result`)
            var10000 = `$result`
         }
         else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
      }

      if (!var10000 as java.lang.Boolean) {
         throw AbortFlowException(this as FlowCollector<*>)
      } else {
         return Unit.INSTANCE
      }
   }
}
