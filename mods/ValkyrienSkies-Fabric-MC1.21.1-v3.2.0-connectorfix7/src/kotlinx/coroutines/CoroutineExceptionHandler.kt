package kotlinx.coroutines

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Element

public interface CoroutineExceptionHandler : Element {
   @JvmStatic
   CoroutineExceptionHandler.Key Key = CoroutineExceptionHandler.Key.$$INSTANCE;

   public abstract fun handleException(context: CoroutineContext, exception: Throwable) {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun <R> fold(`$this`: CoroutineExceptionHandler, initial: R, operation: (R?, Element?) -> R): R {
         kotlin.coroutines.CoroutineContext.Element.DefaultImpls.fold(`$this`, initial, operation)
      }

      @JvmStatic
      fun <E extends Element> get(`$this`: CoroutineExceptionHandler, key: kotlin.coroutines.CoroutineContext.Key<E>): E? {
         kotlin.coroutines.CoroutineContext.Element.DefaultImpls.get(`$this`, key)
      }

      @JvmStatic
      fun minusKey(`$this`: CoroutineExceptionHandler, key: kotlin.coroutines.CoroutineContext.Key<*>): CoroutineContext {
         kotlin.coroutines.CoroutineContext.Element.DefaultImpls.minusKey(`$this`, key)
      }

      @JvmStatic
      fun plus(`$this`: CoroutineExceptionHandler, context: CoroutineContext): CoroutineContext {
         kotlin.coroutines.CoroutineContext.Element.DefaultImpls.plus(`$this`, context)
      }
   }

   public companion object Key : kotlin.coroutines.CoroutineContext.Key<CoroutineExceptionHandler> {
      @JvmStatic
      @JvmSynthetic
      CoroutineExceptionHandler.Key $$INSTANCE = CoroutineExceptionHandler.Key();
   }
}
