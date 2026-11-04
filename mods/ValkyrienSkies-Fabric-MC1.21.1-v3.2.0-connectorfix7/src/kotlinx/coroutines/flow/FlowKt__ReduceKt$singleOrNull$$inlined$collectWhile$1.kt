package kotlinx.coroutines.flow

import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.coroutines.flow.internal.AbortFlowException
import kotlinx.coroutines.flow.internal.NullSurrogateKt

@SourceDebugExtension(["SMAP\nLimit.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1\n+ 2 Reduce.kt\nkotlinx/coroutines/flow/FlowKt__ReduceKt\n*L\n1#1,141:1\n74#2,7:142\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__ReduceKt$singleOrNull$$inlined$collectWhile$1` : FlowCollector<T> {
   @JvmSynthetic
   ObjectRef $result$inlined;

   fun `FlowKt__ReduceKt$singleOrNull$$inlined$collectWhile$1`(var1: ObjectRef) {
      this.$result$inlined = var1
   }

   public override suspend fun emit(value: Any) {
      val var10000: Boolean
      if (this.$result$inlined.element === NullSurrogateKt.NULL) {
         this.$result$inlined.element = value
         var10000 = true
      } else {
         this.$result$inlined.element = NullSurrogateKt.NULL
         var10000 = false
      }

      if (!var10000) {
         throw AbortFlowException(this as FlowCollector<*>)
      } else {
         return Unit.INSTANCE
      }
   }
}
