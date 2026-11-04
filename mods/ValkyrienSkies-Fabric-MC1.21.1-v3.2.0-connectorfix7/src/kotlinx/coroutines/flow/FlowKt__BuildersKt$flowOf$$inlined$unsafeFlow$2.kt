package kotlinx.coroutines.flow

import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Builders.kt\nkotlinx/coroutines/flow/FlowKt__BuildersKt\n*L\n1#1,113:1\n136#2,2:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__BuildersKt$flowOf$$inlined$unsafeFlow$2` : Flow<T> {
   @JvmSynthetic
   Object $value$inlined;

   fun `FlowKt__BuildersKt$flowOf$$inlined$unsafeFlow$2`(var1: Any) {
      this.$value$inlined = var1
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val var10000: Any = collector.emit(this.$value$inlined, `$completion`)
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
