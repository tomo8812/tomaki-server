package kotlinx.coroutines

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Element
import kotlin.coroutines.CoroutineContext.Key

@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
public interface CopyableThreadContextElement<S> : ThreadContextElement<S> {
   public abstract fun copyForChild(): CopyableThreadContextElement<Any> {
   }

   public abstract fun mergeForChild(overwritingElement: Element): CoroutineContext {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun <S, R> fold(`$this`: CopyableThreadContextElement<S>, initial: R, operation: (R?, Element?) -> R): R {
         ThreadContextElement.DefaultImpls.fold(`$this`, initial, operation)
      }

      @JvmStatic
      fun <S, E extends Element> get(`$this`: CopyableThreadContextElement<S>, key: Key<E>): E? {
         ThreadContextElement.DefaultImpls.get(`$this`, key)
      }

      @JvmStatic
      fun <S> minusKey(`$this`: CopyableThreadContextElement<S>, key: Key<*>): CoroutineContext {
         ThreadContextElement.DefaultImpls.minusKey(`$this`, key)
      }

      @JvmStatic
      fun <S> plus(`$this`: CopyableThreadContextElement<S>, context: CoroutineContext): CoroutineContext {
         ThreadContextElement.DefaultImpls.plus(`$this`, context)
      }
   }
}
