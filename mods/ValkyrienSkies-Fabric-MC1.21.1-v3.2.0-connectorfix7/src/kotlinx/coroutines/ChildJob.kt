package kotlinx.coroutines

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Element

/** @deprecated */
@Deprecated(message = "This is internal API and may be removed in the future releases", level = DeprecationLevel.ERROR)
@InternalCoroutinesApi
public interface ChildJob : Job {
   @InternalCoroutinesApi
   public abstract fun parentCancelled(parentJob: ParentJob) {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      /** @deprecated */
      @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
      @JvmStatic
      @JvmSynthetic
      fun cancel(`$this`: ChildJob) {
         Job.DefaultImpls.cancel(`$this`)
      }

      /** @deprecated */
      @Deprecated(message = "Operator '+' on two Job objects is meaningless. Job is a coroutine context element and `+` is a set-sum operator for coroutine contexts. The job to the right of `+` just replaces the job the left of `+`.", level = DeprecationLevel.ERROR)
      @JvmStatic
      fun plus(`$this`: ChildJob, other: Job): Job {
         Job.DefaultImpls.plus(`$this`, other)
      }

      @JvmStatic
      fun plus(`$this`: ChildJob, context: CoroutineContext): CoroutineContext {
         Job.DefaultImpls.plus(`$this`, context)
      }

      @JvmStatic
      fun <R> fold(`$this`: ChildJob, initial: R, operation: (R?, Element?) -> R): R {
         Job.DefaultImpls.fold(`$this`, initial, operation)
      }

      @JvmStatic
      fun <E extends Element> get(`$this`: ChildJob, key: kotlin.coroutines.CoroutineContext.Key<E>): E? {
         Job.DefaultImpls.get(`$this`, key)
      }

      @JvmStatic
      fun minusKey(`$this`: ChildJob, key: kotlin.coroutines.CoroutineContext.Key<*>): CoroutineContext {
         Job.DefaultImpls.minusKey(`$this`, key)
      }
   }
}
