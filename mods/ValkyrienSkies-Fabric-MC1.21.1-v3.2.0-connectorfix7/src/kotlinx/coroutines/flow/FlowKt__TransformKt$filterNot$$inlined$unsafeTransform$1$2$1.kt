package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.coroutines.jvm.internal.DebugMetadata
import kotlin.jvm.internal.SourceDebugExtension

// $VF: Class flags could not be determined
@DebugMetadata(f = "Transform.kt", l = [223, 223], i = [0, 0], s = ["L$0", "L$1"], n = ["value", "$this$filterNot_u24lambda_u241"], m = "emit", c = "kotlinx.coroutines.flow.FlowKt__TransformKt$filterNot$$inlined$unsafeTransform$1$2")
@SourceDebugExtension(["SMAP\nEmitters.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt$unsafeTransform$1$1$emit$1\n*L\n1#1,222:1\n*E\n"])
internal class `FlowKt__TransformKt$filterNot$$inlined$unsafeTransform$1$2$1` : ContinuationImpl {
   @JvmSynthetic
   open Object result;
   open int label;
   @JvmSynthetic
   FlowKt__TransformKt$filterNot$$inlined$unsafeTransform$1$2 this$0;
   open Object L$0;
   open Object L$1;

   fun `FlowKt__TransformKt$filterNot$$inlined$unsafeTransform$1$2$1`(
      `this$0`: FlowKt__TransformKt$filterNot$$inlined$unsafeTransform$1$2, `$completion`: Continuation
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
