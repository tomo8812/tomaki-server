package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.internal.SourceDebugExtension

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1$collect$1\n*L\n1#1,113:1\n*E\n"])
internal class `FlowKt__ZipKt$combineUnsafe$$inlined$unsafeFlow$1$1` : ContinuationImpl {
   @JvmSynthetic
   open Object result;
   open int label;
   @JvmSynthetic
   FlowKt__ZipKt$combineUnsafe$$inlined$unsafeFlow$1 this$0;

   fun `FlowKt__ZipKt$combineUnsafe$$inlined$unsafeFlow$1$1`(`this$0`: FlowKt__ZipKt$combineUnsafe$$inlined$unsafeFlow$1, `$completion`: Continuation) {
      super(`$completion`)
      this.this$0 = `this$0`
   }

   fun invokeSuspend(`$result`: Any) {
      this.result = `$result`
      this.label |= Integer.MIN_VALUE
      this.this$0.collect(null, this as Continuation<in Unit>)
   }
}
