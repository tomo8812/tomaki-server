package kotlinx.coroutines.flow

import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KClass

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n*L\n1#1,113:1\n51#2,5:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__TransformKt$filterIsInstance$$inlined$filter$2` : Flow<Object> {
   @JvmSynthetic
   Flow $this_unsafeTransform$inlined;
   @JvmSynthetic
   KClass $klass$inlined;

   fun `FlowKt__TransformKt$filterIsInstance$$inlined$filter$2`(var1: Flow, var2: KClass) {
      this.$this_unsafeTransform$inlined = var1
      this.$klass$inlined = var2
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val var10000: Any = this.$this_unsafeTransform$inlined
         .collect(FlowKt__TransformKt$filterIsInstance$$inlined$filter$2$2(collector, this.$klass$inlined), `$completion`)
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
