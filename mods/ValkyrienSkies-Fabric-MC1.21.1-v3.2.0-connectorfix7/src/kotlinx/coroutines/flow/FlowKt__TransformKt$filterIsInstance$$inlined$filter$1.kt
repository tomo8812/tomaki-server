package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n*L\n1#1,113:1\n51#2,5:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__TransformKt$filterIsInstance$$inlined$filter$1` : Flow<Object> {
   @JvmSynthetic
   Flow $this_unsafeTransform$inlined;

   fun `FlowKt__TransformKt$filterIsInstance$$inlined$filter$1`(var1: Flow) {
      this.$this_unsafeTransform$inlined = var1
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val var10000: Flow = this.$this_unsafeTransform$inlined
      Intrinsics.needClassReification()
      val var6: Any = var10000.collect(FlowKt__TransformKt$filterIsInstance$$inlined$filter$1$2(collector), `$completion`)
      return if (var6 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var6 else Unit.INSTANCE
   }

   fun `collect$$forInline`(collector: FlowCollector, `$completion`: Continuation) {
      InlineMarker.mark(4)
      FlowKt__TransformKt$filterIsInstance$$inlined$filter$1$1(this, `$completion`)
      InlineMarker.mark(5)
      val var10000: Flow = this.$this_unsafeTransform$inlined
      Intrinsics.needClassReification()
      val var10001: FlowCollector = FlowKt__TransformKt$filterIsInstance$$inlined$filter$1$2(collector)
      InlineMarker.mark(0)
      var10000.collect(var10001, `$completion`)
      InlineMarker.mark(1)
      Unit.INSTANCE
   }
}
