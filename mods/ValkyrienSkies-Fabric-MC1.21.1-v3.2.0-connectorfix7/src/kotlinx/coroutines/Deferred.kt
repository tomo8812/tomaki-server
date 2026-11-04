package kotlinx.coroutines

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Element
import kotlinx.coroutines.selects.SelectClause1

public interface Deferred<T> : Job {
   public abstract suspend fun await(): Any {
   }

   public val onAwait: SelectClause1<Any>

   @ExperimentalCoroutinesApi
   public abstract fun getCompleted(): Any {
   }

   @ExperimentalCoroutinesApi
   public abstract fun getCompletionExceptionOrNull(): Throwable? {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      /** @deprecated */
      @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
      @JvmStatic
      @JvmSynthetic
      fun cancel(`$this`: Deferred) {
         Job.DefaultImpls.cancel(`$this`)
      }

      /** @deprecated */
      @Deprecated(message = "Operator '+' on two Job objects is meaningless. Job is a coroutine context element and `+` is a set-sum operator for coroutine contexts. The job to the right of `+` just replaces the job the left of `+`.", level = DeprecationLevel.ERROR)
      @JvmStatic
      fun <T> plus(`$this`: Deferred<out T>, other: Job): Job {
         Job.DefaultImpls.plus(`$this`, other)
      }

      @JvmStatic
      fun <T> plus(`$this`: Deferred<out T>, context: CoroutineContext): CoroutineContext {
         Job.DefaultImpls.plus(`$this`, context)
      }

      @JvmStatic
      fun <T, R> fold(`$this`: Deferred<out T>, initial: R, operation: (R?, Element?) -> R): R {
         Job.DefaultImpls.fold(`$this`, initial, operation)
      }

      @JvmStatic
      fun <T, E extends Element> get(`$this`: Deferred<out T>, key: kotlin.coroutines.CoroutineContext.Key<E>): E? {
         Job.DefaultImpls.get(`$this`, key)
      }

      @JvmStatic
      fun <T> minusKey(`$this`: Deferred<out T>, key: kotlin.coroutines.CoroutineContext.Key<*>): CoroutineContext {
         Job.DefaultImpls.minusKey(`$this`, key)
      }
   }
}
