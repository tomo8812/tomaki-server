package kotlinx.coroutines.flow

import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.internal.CombineKt

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Zip.kt\nkotlinx/coroutines/flow/FlowKt__ZipKt\n*L\n1#1,113:1\n33#2,2:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__ZipKt$combine$$inlined$unsafeFlow$1` : Flow<R> {
   @JvmSynthetic
   Flow $this_combine$inlined;
   @JvmSynthetic
   Flow $flow$inlined;
   @JvmSynthetic
   Function3 $transform$inlined;

   fun `FlowKt__ZipKt$combine$$inlined$unsafeFlow$1`(var1: Flow, var2: Flow, var3: Function3) {
      this.$this_combine$inlined = var1
      this.$flow$inlined = var2
      this.$transform$inlined = var3
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val var10000: Any = CombineKt.combineInternal(
         collector, arrayOf(this.$this_combine$inlined, this.$flow$inlined), FlowKt__ZipKt.access$nullArrayFactory(), {
            // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
         } as Function3, `$completion`
      )
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
