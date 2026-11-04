package kotlinx.coroutines.flow

import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function4
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.internal.CombineKt

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Zip.kt\nkotlinx/coroutines/flow/FlowKt__ZipKt\n*L\n1#1,113:1\n262#2,2:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__ZipKt$combine$$inlined$combineUnsafe$FlowKt__ZipKt$1` : Flow<R> {
   @JvmSynthetic
   Flow[] $flows$inlined;
   @JvmSynthetic
   Function4 $transform$inlined$1;

   fun `FlowKt__ZipKt$combine$$inlined$combineUnsafe$FlowKt__ZipKt$1`(var1: Array<Flow>, var2: Function4) {
      this.$flows$inlined = var1
      this.$transform$inlined$1 = var2
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val var10000: Any = CombineKt.combineInternal(
         collector,
         this.$flows$inlined,
         FlowKt__ZipKt.access$nullArrayFactory(),
         FlowKt__ZipKt$combine$$inlined$combineUnsafe$FlowKt__ZipKt$1$2(null, this.$transform$inlined$1),
         `$completion`
      )
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
