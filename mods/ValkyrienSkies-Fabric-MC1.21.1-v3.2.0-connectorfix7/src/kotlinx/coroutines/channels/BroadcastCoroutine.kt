package kotlinx.coroutines.channels

import java.util.concurrent.CancellationException
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.AbstractCoroutine
import kotlinx.coroutines.CoroutineExceptionHandlerKt
import kotlinx.coroutines.Job
import kotlinx.coroutines.JobCancellationException
import kotlinx.coroutines.JobSupport
import kotlinx.coroutines.selects.SelectClause2

@SourceDebugExtension(["SMAP\nBroadcast.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Broadcast.kt\nkotlinx/coroutines/channels/BroadcastCoroutine\n+ 2 JobSupport.kt\nkotlinx/coroutines/JobSupport\n*L\n1#1,202:1\n706#2,2:203\n706#2,2:205\n*S KotlinDebug\n*F\n+ 1 Broadcast.kt\nkotlinx/coroutines/channels/BroadcastCoroutine\n*L\n152#1:203,2\n157#1:205,2\n*E\n"])
private open class BroadcastCoroutine<E>(parentContext: CoroutineContext, _channel: BroadcastChannel<Any>, active: Boolean) : AbstractCoroutine(
         parentContext, false, active
      ),
   ProducerScope<E>,
   BroadcastChannel<E> {
   protected final val _channel: BroadcastChannel<Any>

   init {
      this._channel = _channel
      this.initParentJob(parentContext.get(Job.Key) as Job)
   }

   public open val isClosedForSend: Boolean

   public open val onSend: SelectClause2<Any, SendChannel<Any>>

   public override fun invokeOnClose(handler: (Throwable?) -> Unit) {
      this._channel.invokeOnClose(handler)
   }

   @Deprecated(message = "Deprecated in the favour of 'trySend' method", replaceWith = @ReplaceWith(expression = "trySend(element).isSuccess", imports = []), level = DeprecationLevel.ERROR)
   public override fun offer(element: Any): Boolean {
      return this._channel.offer((E)element)
   }

   public override fun openSubscription(): ReceiveChannel<Any> {
      return this._channel.openSubscription()
   }

   public override suspend fun send(element: Any) {
      return this._channel.send((E)element, `$completion`)
   }

   public override fun trySend(element: Any): ChannelResult<Unit> {
      return this._channel.trySend_JP2dKIU/* $VF was: trySend-JP2dKIU */((E)element)
   }

   public open val isActive: Boolean
      public open get() {
         return this.isActive
      }


   public open val channel: SendChannel<Any>
      public open get() {
         return this
      }


   @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
   @JvmSynthetic
   public override fun cancel(cause: Throwable?): Boolean {
      var var10001: java.lang.Throwable = cause
      if (cause == null) {
         var10001 = JobCancellationException(JobSupport.access$cancellationExceptionMessage(this), null, this)
      }

      this.cancelInternal(var10001)
      return true
   }

   public override fun cancel(cause: CancellationException?) {
      var var10001: CancellationException = cause
      if (cause == null) {
         var10001 = JobCancellationException(JobSupport.access$cancellationExceptionMessage(this), null, this)
      }

      this.cancelInternal(var10001)
   }

   public override fun cancelInternal(cause: Throwable) {
      val exception: CancellationException = JobSupport.toCancellationException$default(this, cause, null, 1, null)
      this._channel.cancel(exception)
      this.cancelCoroutine(exception)
   }

   protected open fun onCompleted(value: Unit) {
      SendChannel.DefaultImpls.close$default(this._channel, null, 1, null)
   }

   protected override fun onCancelled(cause: Throwable, handled: Boolean) {
      if (!this._channel.close(cause) && !handled) {
         CoroutineExceptionHandlerKt.handleCoroutineException(this.getContext(), cause)
      }
   }

   public override fun close(cause: Throwable?): Boolean {
      val result: Boolean = this._channel.close(cause)
      this.start()
      return result
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun onCompleted(value: Any) {
      this.onCompleted(value as Unit)
   }
}
