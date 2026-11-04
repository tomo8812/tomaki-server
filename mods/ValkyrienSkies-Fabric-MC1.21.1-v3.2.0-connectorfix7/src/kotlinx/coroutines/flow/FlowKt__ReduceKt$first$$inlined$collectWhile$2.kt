package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.coroutines.flow.internal.AbortFlowException

@SourceDebugExtension(["SMAP\nLimit.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1\n+ 2 Reduce.kt\nkotlinx/coroutines/flow/FlowKt__ReduceKt\n*L\n1#1,141:1\n107#2,5:142\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__ReduceKt$first$$inlined$collectWhile$2` : FlowCollector<T> {
   @JvmSynthetic
   Function2 $predicate$inlined;
   @JvmSynthetic
   ObjectRef $result$inlined;

   fun `FlowKt__ReduceKt$first$$inlined$collectWhile$2`(var1: Function2, var2: ObjectRef) {
      this.$predicate$inlined = var1
      this.$result$inlined = var2
   }

   public override suspend fun emit(value: Any) {
      var `$continuation`: Continuation
      run label34@{
         if (var2 is FlowKt__ReduceKt$first$$inlined$collectWhile$2$1) {
            `$continuation` = var2 as FlowKt__ReduceKt$first$$inlined$collectWhile$2$1
            if (((var2 as FlowKt__ReduceKt$first$$inlined$collectWhile$2$1).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE
               return@label34
            }
         }

         `$continuation` = FlowKt__ReduceKt$first$$inlined$collectWhile$2$1(this, var2)
      }

      val `$result`: Any = `$continuation`.result
val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED()
var it: Any
var var10000: Any
      when (`$continuation`.label) {
         0 -> {
            ResultKt.throwOnFailure(`$result`)
            val var6: Continuation = `$continuation`
            it = value
            val var10: Function2 = this.$predicate$inlined
            `$continuation`.L$0 = this
            `$continuation`.L$1 = value
            `$continuation`.label = 1
            InlineMarker.mark(6)
            var10000 = var10(value, `$continuation`)
            InlineMarker.mark(7)
            if (var10000 === var5) {
               return var5
            }
         }
         1 -> {
            it = `$continuation`.L$1
            this = `$continuation`.L$0 as FlowKt__ReduceKt$first$$inlined$collectWhile$2
            ResultKt.throwOnFailure(`$result`)
            var10000 = `$result`
         }
         else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
      }

      val var11: Boolean
      if (var10000 as java.lang.Boolean) {
         this.$result$inlined.element = it
         var11 = false
      } else {
         var11 = true
      }

      if (!var11) {
         throw AbortFlowException(this as FlowCollector<*>)
      } else {
         return Unit.INSTANCE
      }
   }
}
