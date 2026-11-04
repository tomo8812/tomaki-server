package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.coroutines.jvm.internal.DebugMetadata
import kotlin.jvm.internal.SourceDebugExtension

// $VF: Class flags could not be determined
@DebugMetadata(f = "Reduce.kt", l = [142], i = [0, 0], s = ["L$0", "L$1"], n = ["this", "it"], m = "emit", c = "kotlinx.coroutines.flow.FlowKt__ReduceKt$firstOrNull$$inlined$collectWhile$2")
@SourceDebugExtension(["SMAP\nLimit.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1$emit$1\n*L\n1#1,141:1\n*E\n"])
internal class `FlowKt__ReduceKt$firstOrNull$$inlined$collectWhile$2$1` : ContinuationImpl {
   open Object L$0;
   @JvmSynthetic
   open Object result;
   open int label;
   @JvmSynthetic
   FlowKt__ReduceKt$firstOrNull$$inlined$collectWhile$2 this$0;
   open Object L$1;

   fun `FlowKt__ReduceKt$firstOrNull$$inlined$collectWhile$2$1`(`this$0`: FlowKt__ReduceKt$firstOrNull$$inlined$collectWhile$2, `$completion`: Continuation) {
      super(`$completion`)
      this.this$0 = `this$0`
   }

   fun invokeSuspend(`$result`: Any) {
      this.result = `$result`
      this.label |= Integer.MIN_VALUE
      this.this$0.emit(null, this as Continuation<in Unit>)
   }
}
