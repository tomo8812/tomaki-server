package kotlinx.coroutines.flow.internal

import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.SharedFlowImpl
import kotlinx.coroutines.flow.StateFlow

@SourceDebugExtension(["SMAP\nAbstractSharedFlow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AbstractSharedFlow.kt\nkotlinx/coroutines/flow/internal/SubscriptionCountStateFlow\n+ 2 Synchronized.common.kt\nkotlinx/coroutines/internal/Synchronized_commonKt\n+ 3 Synchronized.kt\nkotlinx/coroutines/internal/SynchronizedKt\n*L\n1#1,132:1\n28#2,4:133\n28#2,4:138\n20#3:137\n20#3:142\n*S KotlinDebug\n*F\n+ 1 AbstractSharedFlow.kt\nkotlinx/coroutines/flow/internal/SubscriptionCountStateFlow\n*L\n126#1:133,4\n128#1:138,4\n126#1:137\n128#1:142\n*E\n"])
private class SubscriptionCountStateFlow(initialValue: Int) : SharedFlowImpl(1, Integer.MAX_VALUE, BufferOverflow.DROP_OLDEST), StateFlow<Integer> {
   init {
      this.tryEmit(initialValue)
   }

   public open val value: Int
      public open get() {
         val var10000: Int
         synchronized (this) {
            var10000 = this.getLastReplayedLocked().intValue()
         }

         return var10000
      }


   public fun increment(delta: Int): Boolean {
      val var10000: Boolean
      synchronized (this) {
         var10000 = this.tryEmit(this.getLastReplayedLocked().intValue() + delta)
      }

      return var10000
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun getValue() {
      this.value
   }
}
