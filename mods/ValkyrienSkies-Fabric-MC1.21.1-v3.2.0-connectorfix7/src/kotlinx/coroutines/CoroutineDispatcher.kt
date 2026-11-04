package kotlinx.coroutines

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.AbstractCoroutineContextKey
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.ContinuationInterceptor.DefaultImpls
import kotlin.coroutines.CoroutineContext.Element
import kotlinx.coroutines.internal.DispatchedContinuation
import kotlinx.coroutines.internal.LimitedDispatcher
import kotlinx.coroutines.internal.LimitedDispatcherKt

public abstract class CoroutineDispatcher : AbstractCoroutineContextElement, ContinuationInterceptor {
   open fun CoroutineDispatcher() {
      super(ContinuationInterceptor.Key as kotlin.coroutines.CoroutineContext.Key)
   }

   public open fun isDispatchNeeded(context: CoroutineContext): Boolean {
      return true
   }

   @ExperimentalCoroutinesApi
   public open fun limitedParallelism(parallelism: Int): CoroutineDispatcher {
      LimitedDispatcherKt.checkParallelism(parallelism)
      return LimitedDispatcher(this, parallelism)
   }

   public abstract fun dispatch(context: CoroutineContext, block: Runnable) {
   }

   @InternalCoroutinesApi
   public open fun dispatchYield(context: CoroutineContext, block: Runnable) {
      this.dispatch(context, block)
   }

   public fun <T> interceptContinuation(continuation: Continuation<Any>): Continuation<Any> {
      return DispatchedContinuation(this, continuation)
   }

   public fun releaseInterceptedContinuation(continuation: Continuation<*>) {
      (continuation as DispatchedContinuation).release$kotlinx_coroutines_core()
   }

   @Deprecated(message = "Operator '+' on two CoroutineDispatcher objects is meaningless. CoroutineDispatcher is a coroutine context element and `+` is a set-sum operator for coroutine contexts. The dispatcher to the right of `+` just replaces the dispatcher to the left.", level = DeprecationLevel.ERROR)
   public operator fun plus(other: CoroutineDispatcher): CoroutineDispatcher {
      return other
   }

   public open fun toString(): String {
      return "${classSimpleName}@${hexAddress}"
   }

   open fun <E extends Element> get(key: kotlin.coroutines.CoroutineContext.Key<E>): E? {
      DefaultImpls.get(this, key)
   }

   open fun minusKey(key: kotlin.coroutines.CoroutineContext.Key<*>): CoroutineContext {
      DefaultImpls.minusKey(this, key)
   }

   @ExperimentalStdlibApi
   public companion object Key : AbstractCoroutineContextKey(ContinuationInterceptor.Key as kotlin.coroutines.CoroutineContext.Key, { it ->
         it as? CoroutineDispatcher
      })
}
