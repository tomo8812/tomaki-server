package kotlinx.coroutines.flow.internal

import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 FlowCoroutine.kt\nkotlinx/coroutines/flow/internal/FlowCoroutineKt\n*L\n1#1,113:1\n51#2,2:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowCoroutineKt$scopedFlow$$inlined$unsafeFlow$1` : Flow<R> {
   @JvmSynthetic
   Function3 $block$inlined;

   fun `FlowCoroutineKt$scopedFlow$$inlined$unsafeFlow$1`(var1: Function3) {
      this.$block$inlined = var1
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val `$this$scopedFlow_u24lambda_u241`: FlowCollector = collector
      val var10000: Any = FlowCoroutineKt.flowScope({
         // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
      } as Function2, `$completion`)
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
