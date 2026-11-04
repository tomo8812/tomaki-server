package kotlinx.coroutines.flow.internal

import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Combine.kt\nkotlinx/coroutines/flow/internal/CombineKt\n*L\n1#1,113:1\n87#2:114\n142#2:115\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `CombineKt$zipImpl$$inlined$unsafeFlow$1` : Flow<R> {
   @JvmSynthetic
   Flow $flow2$inlined;
   @JvmSynthetic
   Flow $flow$inlined;
   @JvmSynthetic
   Function3 $transform$inlined;

   fun `CombineKt$zipImpl$$inlined$unsafeFlow$1`(var1: Flow, var2: Flow, var3: Function3) {
      this.$flow2$inlined = var1
      this.$flow$inlined = var2
      this.$transform$inlined = var3
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val `$this$zipImpl_u24lambda_u240`: FlowCollector = collector
      val var10000: Any = CoroutineScopeKt.coroutineScope({
         // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
      } as Function2, `$completion`)
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
