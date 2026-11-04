package kotlinx.coroutines.flow.internal

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Element
import kotlin.coroutines.CoroutineContext.Key
import org.jetbrains.annotations.NotNull

internal class DownstreamExceptionContext(e: Throwable, originalContext: CoroutineContext) : CoroutineContext {
   @JvmField
   @NotNull
   public final val e: Throwable

   @JvmSynthetic
   private CoroutineContext $$delegate_0;

   init {
      this.e = e
      this.$$delegate_0 = originalContext
   }

   public open fun <R> fold(initial: Any, operation: (Any, Element) -> Any): Any {
      return (R)this.$$delegate_0.fold(initial, operation)
   }

   public open operator fun <E : Element> get(key: Key<Any>): Any? {
      return (E)this.$$delegate_0.get(key)
   }

   public open fun minusKey(key: Key<*>): CoroutineContext {
      return this.$$delegate_0.minusKey(key)
   }

   public open operator fun plus(context: CoroutineContext): CoroutineContext {
      return this.$$delegate_0.plus(context)
   }
}
