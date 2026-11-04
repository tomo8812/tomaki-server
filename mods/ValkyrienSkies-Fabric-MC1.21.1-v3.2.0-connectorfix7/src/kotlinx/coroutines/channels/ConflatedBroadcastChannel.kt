package kotlinx.coroutines.channels

import java.util.concurrent.CancellationException
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.selects.SelectClause2

/** @deprecated */
@Deprecated(message = "ConflatedBroadcastChannel is deprecated in the favour of SharedFlow and is no longer supported", level = DeprecationLevel.WARNING)
@ObsoleteCoroutinesApi
public class ConflatedBroadcastChannel<E> private constructor(broadcast: BroadcastChannelImpl<Any>) : BroadcastChannel<E> {
   private final val broadcast: BroadcastChannelImpl<Any>

   init {
      this.broadcast = broadcast
   }

   public open val isClosedForSend: Boolean

   public open val onSend: SelectClause2<Any, SendChannel<Any>>

   @Deprecated(message = "Binary compatibility only", level = DeprecationLevel.HIDDEN)
   @JvmSynthetic
   public override fun cancel(cause: Throwable? = ...): Boolean {
      return this.broadcast.cancel(cause)
   }

   public override fun cancel(cause: CancellationException? = ...) {
      this.broadcast.cancel(cause)
   }

   public override fun close(cause: Throwable?): Boolean {
      return this.broadcast.close(cause)
   }

   public override fun invokeOnClose(handler: (Throwable?) -> Unit) {
      this.broadcast.invokeOnClose(handler)
   }

   @Deprecated(message = "Deprecated in the favour of 'trySend' method", replaceWith = @ReplaceWith(expression = "trySend(element).isSuccess", imports = []), level = DeprecationLevel.ERROR)
   public override fun offer(element: Any): Boolean {
      return this.broadcast.offer((E)element)
   }

   public override fun openSubscription(): ReceiveChannel<Any> {
      return this.broadcast.openSubscription()
   }

   public override suspend fun send(element: Any) {
      return this.broadcast.send((E)element, `$completion`)
   }

   public override fun trySend(element: Any): ChannelResult<Unit> {
      return this.broadcast.trySend_JP2dKIU/* $VF was: trySend-JP2dKIU */((E)element)
   }

   public constructor() : this(BroadcastChannelImpl<>(-1))
   public constructor(value: Any) : this() {
      this.trySend_JP2dKIU/* $VF was: trySend-JP2dKIU */((E)value)
   }

   public final val value: Any
      public final get() {
         return this.broadcast.value
      }


   public final val valueOrNull: Any?
      public final get() {
         return this.broadcast.valueOrNull
      }

}
