package kotlinx.coroutines

import java.util.concurrent.CancellationException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Element
import kotlin.jvm.functions.Function1
import kotlinx.coroutines.selects.SelectClause0

public interface Job : Element {
   @JvmStatic
   Job.Key Key = Job.Key.$$INSTANCE;

   public val parent: Job?

   public val isActive: Boolean

   public val isCompleted: Boolean

   public val isCancelled: Boolean

   @InternalCoroutinesApi
   public abstract fun getCancellationException(): CancellationException {
   }

   public abstract fun start(): Boolean {
   }

   public abstract fun cancel(cause: CancellationException? = ...) {
   }

   @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
   @JvmSynthetic
   public open fun cancel() {
   }

   @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
   @JvmSynthetic
   public abstract fun cancel(cause: Throwable? = ...): Boolean {
   }

   public val children: Sequence<Job>

   @InternalCoroutinesApi
   public abstract fun attachChild(child: ChildJob): ChildHandle {
   }

   public abstract suspend fun join() {
   }

   public val onJoin: SelectClause0

   public abstract fun invokeOnCompletion(handler: (Throwable?) -> Unit): DisposableHandle {
   }

   @InternalCoroutinesApi
   public abstract fun invokeOnCompletion(onCancelling: Boolean = ..., invokeImmediately: Boolean = ..., handler: (Throwable?) -> Unit): DisposableHandle {
   }

   @Deprecated(message = "Operator '+' on two Job objects is meaningless. Job is a coroutine context element and `+` is a set-sum operator for coroutine contexts. The job to the right of `+` just replaces the job the left of `+`.", level = DeprecationLevel.ERROR)
   public open operator fun plus(other: Job): Job {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      /** @deprecated */
      @ExperimentalCoroutinesApi
      @JvmStatic
      @JvmSynthetic
      fun `getParent$annotations`() {
      }

      @JvmStatic
      @JvmSynthetic
      fun `cancel$default`(var0: Job, var1: CancellationException, var2: Int, var3: Any) {
         if (var3 != null) {
            throw UnsupportedOperationException("Super calls with default arguments not supported in this target, function: cancel")
         } else {
            if ((var2 and 1) != 0) {
               var1 = null
            }

            var0.cancel(var1)
         }
      }

      /** @deprecated */
      @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
      @JvmStatic
      @JvmSynthetic
      fun cancel(`$this`: Job) {
         `$this`.cancel((CancellationException)null)
      }

      /** @deprecated */
      @JvmStatic
      @JvmSynthetic
      fun `cancel$default`(var0: Job, var1: java.lang.Throwable, var2: Int, var3: Any): Boolean {
         if (var3 != null) {
            throw UnsupportedOperationException("Super calls with default arguments not supported in this target, function: cancel")
         } else {
            if ((var2 and 1) != 0) {
               var1 = null
            }

            var0.cancel(var1)
         }
      }

      @JvmStatic
      @JvmSynthetic
      fun `invokeOnCompletion$default`(var0: Job, var1: Boolean, var2: Boolean, var3: Function1, var4: Int, var5: Any): DisposableHandle {
         if (var5 != null) {
            throw UnsupportedOperationException("Super calls with default arguments not supported in this target, function: invokeOnCompletion")
         } else {
            if ((var4 and 1) != 0) {
               var1 = false
            }

            if ((var4 and 2) != 0) {
               var2 = true
            }

            var0.invokeOnCompletion(var1, var2, var3)
         }
      }

      /** @deprecated */
      @Deprecated(message = "Operator '+' on two Job objects is meaningless. Job is a coroutine context element and `+` is a set-sum operator for coroutine contexts. The job to the right of `+` just replaces the job the left of `+`.", level = DeprecationLevel.ERROR)
      @JvmStatic
      fun plus(`$this`: Job, other: Job): Job {
         other
      }

      @JvmStatic
      fun plus(`$this`: Job, context: CoroutineContext): CoroutineContext {
         kotlin.coroutines.CoroutineContext.Element.DefaultImpls.plus(`$this`, context)
      }

      @JvmStatic
      fun <R> fold(`$this`: Job, initial: R, operation: (R?, Element?) -> R): R {
         kotlin.coroutines.CoroutineContext.Element.DefaultImpls.fold(`$this`, initial, operation)
      }

      @JvmStatic
      fun <E extends Element> get(`$this`: Job, key: kotlin.coroutines.CoroutineContext.Key<E>): E? {
         kotlin.coroutines.CoroutineContext.Element.DefaultImpls.get(`$this`, key)
      }

      @JvmStatic
      fun minusKey(`$this`: Job, key: kotlin.coroutines.CoroutineContext.Key<*>): CoroutineContext {
         kotlin.coroutines.CoroutineContext.Element.DefaultImpls.minusKey(`$this`, key)
      }
   }

   public companion object Key : kotlin.coroutines.CoroutineContext.Key<Job> {
      @JvmStatic
      @JvmSynthetic
      Job.Key $$INSTANCE = Job.Key();
   }
}
