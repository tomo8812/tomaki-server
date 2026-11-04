package kotlinx.coroutines.flow

import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.channels.BroadcastChannel

@SourceDebugExtension(["SMAP\nSafeCollector.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt$unsafeFlow$1\n+ 2 Channels.kt\nkotlinx/coroutines/flow/FlowKt__ChannelsKt\n*L\n1#1,113:1\n154#2,2:114\n*E\n"])
// $VF: local visibility outside of methodSupplier
internal class `FlowKt__ChannelsKt$asFlow$$inlined$unsafeFlow$1` : Flow<T> {
   @JvmSynthetic
   BroadcastChannel $this_asFlow$inlined;

   fun `FlowKt__ChannelsKt$asFlow$$inlined$unsafeFlow$1`(var1: BroadcastChannel) {
      this.$this_asFlow$inlined = var1
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      val var10000: Any = FlowKt.emitAll(collector, this.$this_asFlow$inlined.openSubscription(), `$completion`)
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
