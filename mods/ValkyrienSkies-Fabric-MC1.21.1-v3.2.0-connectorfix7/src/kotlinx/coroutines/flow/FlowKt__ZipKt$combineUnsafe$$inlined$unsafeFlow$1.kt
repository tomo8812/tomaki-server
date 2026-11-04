package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function0
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.internal.CombineKt

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Zip.kt\nkotlinx/coroutines/flow/FlowKt__ZipKt\n*L\n1#1,113:1\n262#2,2:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__ZipKt$combineUnsafe$$inlined$unsafeFlow$1` : Flow<R> {
   @JvmSynthetic
   Flow[] $flows$inlined;
   @JvmSynthetic
   Function2 $transform$inlined;

   fun `FlowKt__ZipKt$combineUnsafe$$inlined$unsafeFlow$1`(var1: Array<Flow>, var2: Function2) {
      this.$flows$inlined = var1
      this.$transform$inlined = var2
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val var10001: Array<Flow> = this.$flows$inlined
      val var10002: Function0 = FlowKt__ZipKt.access$nullArrayFactory()
      Intrinsics.needClassReification()
      val var10000: Any = CombineKt.combineInternal(collector, var10001, var10002, {
         // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
      } as Function3, `$completion`)
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }

   fun `collect$$forInline`(collector: FlowCollector, `$completion`: Continuation) {
      InlineMarker.mark(4)
      FlowKt__ZipKt$combineUnsafe$$inlined$unsafeFlow$1$1(this, `$completion`)
      InlineMarker.mark(5)
      val var10001: Array<Flow> = this.$flows$inlined
      val var10002: Function0 = FlowKt__ZipKt.access$nullArrayFactory()
      Intrinsics.needClassReification()
      val var10003: Function3 = {
         // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
      } as Function3
      InlineMarker.mark(0)
      CombineKt.combineInternal(collector, var10001, var10002, var10003, `$completion`)
      InlineMarker.mark(1)
      Unit.INSTANCE
   }
}
