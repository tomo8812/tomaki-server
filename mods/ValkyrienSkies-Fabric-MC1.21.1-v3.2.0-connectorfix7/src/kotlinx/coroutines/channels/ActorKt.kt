package kotlinx.coroutines.channels

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineContextKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
public fun <E> CoroutineScope.actor(
   context: CoroutineContext = EmptyCoroutineContext.INSTANCE as CoroutineContext,
   capacity: Int = 0,
   start: CoroutineStart = CoroutineStart.DEFAULT,
   onCompletion: ((Throwable?) -> Unit)? = null,
   block: (ActorScope<Any>, Continuation<Unit>) -> Any?
): SendChannel<Any> {
   val newContext: CoroutineContext = CoroutineContextKt.newCoroutineContext(`$this$actor`, context)
   val channel: Channel = ChannelKt.Channel$default(capacity, null, null, 6, null)
   val coroutine: ActorCoroutine = if (start.isLazy) LazyActorCoroutine(newContext, channel, block) else ActorCoroutine(newContext, channel, true)
   if (onCompletion != null) {
      coroutine.invokeOnCompletion(onCompletion)
   }

   coroutine.start(start, coroutine, block)
   return coroutine
}
