package kotlinx.coroutines.channels

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineExceptionHandlerKt

private class ProducerCoroutine<E>(parentContext: CoroutineContext, channel: Channel<Any>) : ChannelCoroutine(parentContext, channel, true, true),
   ProducerScope<E> {
   public open val isActive: Boolean
      public open get() {
         return super.isActive()
      }


   protected open fun onCompleted(value: Unit) {
      SendChannel.DefaultImpls.close$default(this.get_channel(), null, 1, null)
   }

   protected override fun onCancelled(cause: Throwable, handled: Boolean) {
      if (!this.get_channel().close(cause) && !handled) {
         CoroutineExceptionHandlerKt.handleCoroutineException(this.getContext(), cause)
      }
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun onCompleted(value: Any) {
      this.onCompleted(value as Unit)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun getChannel(): SendChannel {
      this.getChannel() as SendChannel
   }
}
