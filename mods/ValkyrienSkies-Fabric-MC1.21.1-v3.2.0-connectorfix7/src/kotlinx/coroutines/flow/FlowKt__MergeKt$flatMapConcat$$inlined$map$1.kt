package kotlinx.coroutines.flow

import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n*L\n1#1,113:1\n51#2,5:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__MergeKt$flatMapConcat$$inlined$map$1` : Flow<Flow<? extends R>> {
   @JvmSynthetic
   Flow $this_unsafeTransform$inlined;
   @JvmSynthetic
   Function2 $transform$inlined$1;

   fun `FlowKt__MergeKt$flatMapConcat$$inlined$map$1`(var1: Flow, var2: Function2) {
      this.$this_unsafeTransform$inlined = var1
      this.$transform$inlined$1 = var2
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val var10000: Any = this.$this_unsafeTransform$inlined
         .collect(FlowKt__MergeKt$flatMapConcat$$inlined$map$1$2(collector, this.$transform$inlined$1), `$completion`)
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
