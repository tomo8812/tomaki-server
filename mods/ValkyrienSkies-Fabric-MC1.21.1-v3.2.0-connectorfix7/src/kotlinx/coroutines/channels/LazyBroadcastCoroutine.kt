package kotlinx.coroutines.channels

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlinx.coroutines.intrinsics.CancellableKt

private class LazyBroadcastCoroutine<E>(parentContext: CoroutineContext, channel: BroadcastChannel<Any>, block: (ProducerScope<Any>, Continuation<Unit>) -> Any?) : BroadcastCoroutine(
      parentContext, channel, false
   ) {
   private final val continuation: Continuation<Unit>

   init {
      this.continuation = IntrinsicsKt.createCoroutineUnintercepted(block, this, this)
   }

   public override fun openSubscription(): ReceiveChannel<Any> {
      val subscription: ReceiveChannel = this.get_channel().openSubscription()
      this.start()
      return subscription
   }

   protected override fun onStart() {
      CancellableKt.startCoroutineCancellable(this.continuation, this)
   }
}
