package kotlinx.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

@InternalCoroutinesApi
public abstract class AbstractCoroutine<T> : JobSupport, Job, Continuation<T>, CoroutineScope {
   public final val context: CoroutineContext

   open fun AbstractCoroutine(parentContext: CoroutineContext, initParentJob: Boolean, active: Boolean) {
      super(active)
      if (initParentJob) {
         this.initParentJob(parentContext.get(Job.Key) as Job)
      }

      this.context = parentContext.plus(this as CoroutineContext)
   }

   public open val coroutineContext: CoroutineContext
      public open get() {
         return this.context
      }


   public open val isActive: Boolean
      public open get() {
         return this.isActive
      }


   protected open fun onCompleted(value: Any) {
   }

   protected open fun onCancelled(cause: Throwable, handled: Boolean) {
   }

   protected override fun cancellationExceptionMessage(): String {
      return "${classSimpleName} was cancelled"
   }

   protected override fun onCompletionInternal(state: Any?) {
      if (state is CompletedExceptionally) {
         this.onCancelled((state as CompletedExceptionally).cause, (state as CompletedExceptionally).handled)
      } else {
         this.onCompleted((T)state)
      }
   }

   public fun resumeWith(result: Result<Any>) {
      val state: Any = this.makeCompletingOnce$kotlinx_coroutines_core(CompletionStateKt.toState$default(result, null, 1, null))
      if (state != JobSupportKt.COMPLETING_WAITING_CHILDREN) {
         this.afterResume(state)
      }
   }

   protected open fun afterResume(state: Any?) {
      this.afterCompletion(state)
   }

   internal override fun handleOnCompletionException(exception: Throwable) {
      CoroutineExceptionHandlerKt.handleCoroutineException(this.context, exception)
   }

   internal override fun nameString(): String {
      val var10000: java.lang.String = coroutineName
      return if (var10000 == null) super.nameString$kotlinx_coroutines_core() else ""$var10000\":${super.nameString$kotlinx_coroutines_core()}"
   }

   public fun <R> start(start: CoroutineStart, receiver: Any, block: (Any, Continuation<Any>) -> Any?) {
      start.invoke(block, receiver, this)
   }
}
