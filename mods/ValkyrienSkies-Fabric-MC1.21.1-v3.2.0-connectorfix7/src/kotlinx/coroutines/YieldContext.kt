package kotlinx.coroutines

import kotlin.coroutines.AbstractCoroutineContextElement

@PublishedApi
internal class YieldContext : AbstractCoroutineContextElement(Key) {
   @JvmField
   public final var dispatcherWasUnconfined: Boolean
      private set

   public companion object Key : kotlin.coroutines.CoroutineContext.Key<YieldContext>
}
