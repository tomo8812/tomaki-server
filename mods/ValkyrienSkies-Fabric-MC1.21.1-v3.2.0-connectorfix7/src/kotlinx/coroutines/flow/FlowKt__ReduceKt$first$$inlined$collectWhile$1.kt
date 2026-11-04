package kotlinx.coroutines.flow

import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.coroutines.flow.internal.AbortFlowException

@SourceDebugExtension(["SMAP\nLimit.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1\n+ 2 Reduce.kt\nkotlinx/coroutines/flow/FlowKt__ReduceKt\n*L\n1#1,141:1\n93#2,2:142\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__ReduceKt$first$$inlined$collectWhile$1` : FlowCollector<T> {
   @JvmSynthetic
   ObjectRef $result$inlined;

   fun `FlowKt__ReduceKt$first$$inlined$collectWhile$1`(var1: ObjectRef) {
      this.$result$inlined = var1
   }

   public override suspend fun emit(value: Any) {
      this.$result$inlined.element = value
      if (true) {
         throw AbortFlowException(this as FlowCollector<*>)
      } else {
         return Unit.INSTANCE
      }
   }
}
