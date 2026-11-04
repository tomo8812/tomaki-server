package kotlinx.coroutines

import kotlin.coroutines.Continuation

public interface CancellableContinuation<T> : Continuation<T> {
   public val isActive: Boolean

   public val isCompleted: Boolean

   public val isCancelled: Boolean

   @InternalCoroutinesApi
   public abstract fun tryResume(value: Any, idempotent: Any? = ...): Any? {
   }

   @InternalCoroutinesApi
   public abstract fun tryResume(value: Any, idempotent: Any?, onCancellation: ((Throwable) -> Unit)?): Any? {
   }

   @InternalCoroutinesApi
   public abstract fun tryResumeWithException(exception: Throwable): Any? {
   }

   @InternalCoroutinesApi
   public abstract fun completeResume(token: Any) {
   }

   @InternalCoroutinesApi
   public abstract fun initCancellability() {
   }

   public abstract fun cancel(cause: Throwable? = ...): Boolean {
   }

   public abstract fun invokeOnCancellation(handler: (Throwable?) -> Unit) {
   }

   @ExperimentalCoroutinesApi
   public abstract fun CoroutineDispatcher.resumeUndispatched(value: Any) {
   }

   @ExperimentalCoroutinesApi
   public abstract fun CoroutineDispatcher.resumeUndispatchedWithException(exception: Throwable) {
   }

   @ExperimentalCoroutinesApi
   public abstract fun resume(value: Any, onCancellation: ((Throwable) -> Unit)?) {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      @JvmSynthetic
      fun `tryResume$default`(var0: CancellableContinuation, var1: Any, var2: Any, var3: Int, var4: Any) {
         if (var4 != null) {
            throw UnsupportedOperationException("Super calls with default arguments not supported in this target, function: tryResume")
         } else {
            if ((var3 and 2) != 0) {
               var2 = null
            }

            var0.tryResume(var1, var2)
         }
      }

      @JvmStatic
      @JvmSynthetic
      fun `cancel$default`(var0: CancellableContinuation, var1: java.lang.Throwable, var2: Int, var3: Any): Boolean {
         if (var3 != null) {
            throw UnsupportedOperationException("Super calls with default arguments not supported in this target, function: cancel")
         } else {
            if ((var2 and 1) != 0) {
               var1 = null
            }

            var0.cancel(var1)
         }
      }
   }
}
