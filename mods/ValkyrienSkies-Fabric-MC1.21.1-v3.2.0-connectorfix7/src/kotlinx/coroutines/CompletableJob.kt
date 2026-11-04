package kotlinx.coroutines

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Element

public interface CompletableJob : Job {
   public abstract fun complete(): Boolean {
   }

   public abstract fun completeExceptionally(exception: Throwable): Boolean {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      /** @deprecated */
      @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
      @JvmStatic
      @JvmSynthetic
      fun cancel(`$this`: CompletableJob) {
         Job.DefaultImpls.cancel(`$this`)
      }

      /** @deprecated */
      @Deprecated(message = "Operator '+' on two Job objects is meaningless. Job is a coroutine context element and `+` is a set-sum operator for coroutine contexts. The job to the right of `+` just replaces the job the left of `+`.", level = DeprecationLevel.ERROR)
      @JvmStatic
      fun plus(`$this`: CompletableJob, other: Job): Job {
         Job.DefaultImpls.plus(`$this`, other)
      }

      @JvmStatic
      fun plus(`$this`: CompletableJob, context: CoroutineContext): CoroutineContext {
         Job.DefaultImpls.plus(`$this`, context)
      }

      @JvmStatic
      fun <R> fold(`$this`: CompletableJob, initial: R, operation: (R?, Element?) -> R): R {
         Job.DefaultImpls.fold(`$this`, initial, operation)
      }

      @JvmStatic
      fun <E extends Element> get(`$this`: CompletableJob, key: kotlin.coroutines.CoroutineContext.Key<E>): E? {
         Job.DefaultImpls.get(`$this`, key)
      }

      @JvmStatic
      fun minusKey(`$this`: CompletableJob, key: kotlin.coroutines.CoroutineContext.Key<*>): CoroutineContext {
         Job.DefaultImpls.minusKey(`$this`, key)
      }
   }
}
