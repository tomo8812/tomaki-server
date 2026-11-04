package kotlinx.coroutines

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Element

public interface CompletableDeferred<T> : Deferred<T> {
   public abstract fun complete(value: Any): Boolean {
   }

   public abstract fun completeExceptionally(exception: Throwable): Boolean {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      /** @deprecated */
      @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
      @JvmStatic
      @JvmSynthetic
      fun cancel(`$this`: CompletableDeferred) {
         Deferred.DefaultImpls.cancel(`$this`)
      }

      /** @deprecated */
      @Deprecated(message = "Operator '+' on two Job objects is meaningless. Job is a coroutine context element and `+` is a set-sum operator for coroutine contexts. The job to the right of `+` just replaces the job the left of `+`.", level = DeprecationLevel.ERROR)
      @JvmStatic
      fun <T> plus(`$this`: CompletableDeferred<T>, other: Job): Job {
         Deferred.DefaultImpls.plus(`$this`, other)
      }

      @JvmStatic
      fun <T> plus(`$this`: CompletableDeferred<T>, context: CoroutineContext): CoroutineContext {
         Deferred.DefaultImpls.plus(`$this`, context)
      }

      @JvmStatic
      fun <T, R> fold(`$this`: CompletableDeferred<T>, initial: R, operation: (R?, Element?) -> R): R {
         Deferred.DefaultImpls.fold(`$this`, initial, operation)
      }

      @JvmStatic
      fun <T, E extends Element> get(`$this`: CompletableDeferred<T>, key: kotlin.coroutines.CoroutineContext.Key<E>): E? {
         Deferred.DefaultImpls.get(`$this`, key)
      }

      @JvmStatic
      fun <T> minusKey(`$this`: CompletableDeferred<T>, key: kotlin.coroutines.CoroutineContext.Key<*>): CoroutineContext {
         Deferred.DefaultImpls.minusKey(`$this`, key)
      }
   }
}
