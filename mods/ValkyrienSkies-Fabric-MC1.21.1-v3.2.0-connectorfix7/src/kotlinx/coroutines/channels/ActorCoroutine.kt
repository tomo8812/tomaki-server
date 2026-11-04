package kotlinx.coroutines.channels

import java.util.concurrent.CancellationException
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineExceptionHandlerKt
import kotlinx.coroutines.ExceptionsKt
import kotlinx.coroutines.Job

private open class ActorCoroutine<E>(parentContext: CoroutineContext, channel: Channel<Any>, active: Boolean) : ChannelCoroutine(
         parentContext, channel, false, active
      ),
   ActorScope<E> {
   init {
      this.initParentJob(parentContext.get(Job.Key) as Job)
   }

   protected override fun onCancelling(cause: Throwable?) {
      val var10000: Channel = this.get_channel()
      val var10001: CancellationException
      if (cause != null) {
         var var5: CancellationException = cause as? CancellationException
         if ((cause as? CancellationException) == null) {
            var5 = ExceptionsKt.CancellationException("${classSimpleName} was cancelled", cause)
         }

         var10001 = var5
      } else {
         var10001 = null
      }

      var10000.cancel(var10001)
   }

   protected override fun handleJobException(exception: Throwable): Boolean {
      CoroutineExceptionHandlerKt.handleCoroutineException(this.getContext(), exception)
      return true
   }
}
