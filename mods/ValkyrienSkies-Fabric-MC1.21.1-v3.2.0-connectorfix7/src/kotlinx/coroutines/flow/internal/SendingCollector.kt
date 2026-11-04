package kotlinx.coroutines.flow.internal

import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.FlowCollector

@InternalCoroutinesApi
public class SendingCollector<T>(channel: SendChannel<Any>) : FlowCollector<T> {
   private final val channel: SendChannel<Any>

   init {
      this.channel = channel
   }

   public override suspend fun emit(value: Any) {
      val var10000: Any = this.channel.send((T)value, `$completion`)
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
