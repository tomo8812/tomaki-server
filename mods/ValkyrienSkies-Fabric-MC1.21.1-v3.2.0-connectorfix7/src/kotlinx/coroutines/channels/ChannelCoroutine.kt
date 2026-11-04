package kotlinx.coroutines.channels

import java.util.concurrent.CancellationException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.internal.LowPriorityInOverloadResolution
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.AbstractCoroutine
import kotlinx.coroutines.JobCancellationException
import kotlinx.coroutines.JobSupport
import kotlinx.coroutines.selects.SelectClause1
import kotlinx.coroutines.selects.SelectClause2

@SourceDebugExtension(["SMAP\nChannelCoroutine.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ChannelCoroutine.kt\nkotlinx/coroutines/channels/ChannelCoroutine\n+ 2 JobSupport.kt\nkotlinx/coroutines/JobSupport\n*L\n1#1,41:1\n706#2,2:42\n706#2,2:44\n706#2,2:46\n*S KotlinDebug\n*F\n+ 1 ChannelCoroutine.kt\nkotlinx/coroutines/channels/ChannelCoroutine\n*L\n21#1:42,2\n26#1:44,2\n32#1:46,2\n*E\n"])
internal open class ChannelCoroutine<E>(parentContext: CoroutineContext, _channel: Channel<Any>, initParentJob: Boolean, active: Boolean) : AbstractCoroutine(
         parentContext, initParentJob, active
      ),
   Channel<E> {
   protected final val _channel: Channel<Any>

   init {
      this._channel = _channel
   }

   public open val isClosedForReceive: Boolean

   public open val isClosedForSend: Boolean

   public open val isEmpty: Boolean

   public open val onReceive: SelectClause1<Any>

   public open val onReceiveCatching: SelectClause1<ChannelResult<Any>>

   public open val onReceiveOrNull: SelectClause1<Any?>
      public open get() {
         return this._channel.getOnReceiveOrNull()
      }


   public open val onSend: SelectClause2<Any, SendChannel<Any>>

   public override fun close(cause: Throwable?): Boolean {
      return this._channel.close(cause)
   }

   public override fun invokeOnClose(handler: (Throwable?) -> Unit) {
      this._channel.invokeOnClose(handler)
   }

   public override operator fun iterator(): ChannelIterator<Any> {
      return this._channel.iterator()
   }

   @Deprecated(message = "Deprecated in the favour of 'trySend' method", replaceWith = @ReplaceWith(expression = "trySend(element).isSuccess", imports = []), level = DeprecationLevel.ERROR)
   public override fun offer(element: Any): Boolean {
      return this._channel.offer((E)element)
   }

   @Deprecated(message = "Deprecated in the favour of 'tryReceive'. Please note that the provided replacement does not rethrow channel's close cause as 'poll' did, for the precise replacement please refer to the 'poll' documentation", replaceWith = @ReplaceWith(expression = "tryReceive().getOrNull()", imports = []), level = DeprecationLevel.ERROR)
   public override fun poll(): Any? {
      return this._channel.poll()
   }

   public override suspend fun receive(): Any {
      return this._channel.receive(`$completion`)
   }

   public override suspend fun receiveCatching(): ChannelResult<Any> {
      val var10000: Any = this._channel.receiveCatching_JP2dKIU/* $VF was: receiveCatching-JP2dKIU */(`$completion`)
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else var10000
   }

   @Deprecated(message = "Deprecated in favor of 'receiveCatching'. Please note that the provided replacement does not rethrow channel's close cause as 'receiveOrNull' did, for the detailed replacement please refer to the 'receiveOrNull' documentation", replaceWith = @ReplaceWith(expression = "receiveCatching().getOrNull()", imports = []), level = DeprecationLevel.ERROR)
   @LowPriorityInOverloadResolution
   public override suspend fun receiveOrNull(): Any? {
      return this._channel.receiveOrNull(`$completion`)
   }

   public override suspend fun send(element: Any) {
      return this._channel.send((E)element, `$completion`)
   }

   public override fun tryReceive(): ChannelResult<Any> {
      return this._channel.tryReceive_PtdJZtk/* $VF was: tryReceive-PtdJZtk */()
   }

   public override fun trySend(element: Any): ChannelResult<Unit> {
      return this._channel.trySend_JP2dKIU/* $VF was: trySend-JP2dKIU */((E)element)
   }

   public final val channel: Channel<Any>
      public final get() {
         return this
      }


   @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
   @JvmSynthetic
   public override fun cancel() {
      this.cancelInternal(JobCancellationException(JobSupport.access$cancellationExceptionMessage(this), null, this))
   }

   @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
   @JvmSynthetic
   public override fun cancel(cause: Throwable?): Boolean {
      this.cancelInternal(JobCancellationException(JobSupport.access$cancellationExceptionMessage(this), null, this))
      return true
   }

   public override fun cancel(cause: CancellationException?) {
      if (!this.isCancelled()) {
         var var10001: CancellationException = cause
         if (cause == null) {
            var10001 = JobCancellationException(JobSupport.access$cancellationExceptionMessage(this), null, this)
         }

         this.cancelInternal(var10001)
      }
   }

   public override fun cancelInternal(cause: Throwable) {
      val exception: CancellationException = JobSupport.toCancellationException$default(this, cause, null, 1, null)
      this._channel.cancel(exception)
      this.cancelCoroutine(exception)
   }
}
