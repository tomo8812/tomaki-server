package kotlinx.coroutines

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Element
import kotlin.coroutines.CoroutineContext.Key

public interface ThreadContextElement<S> : Element {
   public abstract fun updateThreadContext(context: CoroutineContext): Any {
   }

   public abstract fun restoreThreadContext(context: CoroutineContext, oldState: Any) {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun <S, R> fold(`$this`: ThreadContextElement<S>, initial: R, operation: (R?, Element?) -> R): R {
         kotlin.coroutines.CoroutineContext.Element.DefaultImpls.fold(`$this`, initial, operation)
      }

      @JvmStatic
      fun <S, E extends Element> get(`$this`: ThreadContextElement<S>, key: Key<E>): E? {
         kotlin.coroutines.CoroutineContext.Element.DefaultImpls.get(`$this`, key)
      }

      @JvmStatic
      fun <S> minusKey(`$this`: ThreadContextElement<S>, key: Key<*>): CoroutineContext {
         kotlin.coroutines.CoroutineContext.Element.DefaultImpls.minusKey(`$this`, key)
      }

      @JvmStatic
      fun <S> plus(`$this`: ThreadContextElement<S>, context: CoroutineContext): CoroutineContext {
         kotlin.coroutines.CoroutineContext.Element.DefaultImpls.plus(`$this`, context)
      }
   }
}
