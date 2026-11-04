package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.coroutines.jvm.internal.DebugMetadata
import kotlin.jvm.internal.SourceDebugExtension

// $VF: Class flags could not be determined
@DebugMetadata(f = "Limit.kt", l = [142, 143], i = [0, 0, 1], s = ["L$0", "L$1", "L$0"], n = ["this", "value", "this"], m = "emit", c = "kotlinx.coroutines.flow.FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1")
@SourceDebugExtension(["SMAP\nLimit.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Limit.kt\nkotlinx/coroutines/flow/FlowKt__LimitKt$collectWhile$collector$1$emit$1\n*L\n1#1,141:1\n*E\n"])
internal class `FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1$1` : ContinuationImpl {
   open Object L$0;
   @JvmSynthetic
   open Object result;
   open int label;
   @JvmSynthetic
   FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1 this$0;
   open Object L$1;

   fun `FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1$1`(
      `this$0`: FlowKt__LimitKt$takeWhile$lambda$6$$inlined$collectWhile$1, `$completion`: Continuation
   ) {
      super(`$completion`)
      this.this$0 = `this$0`
   }

   fun invokeSuspend(`$result`: Any) {
      this.result = `$result`
      this.label |= Integer.MIN_VALUE
      this.this$0.emit(null, this as Continuation<in Unit>)
   }
}
