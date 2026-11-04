@file:SourceDebugExtension(["SMAP\nBroadcast.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Broadcast.kt\nkotlinx/coroutines/channels/BroadcastKt\n+ 2 CoroutineExceptionHandler.kt\nkotlinx/coroutines/CoroutineExceptionHandlerKt\n*L\n1#1,202:1\n48#2,4:203\n*S KotlinDebug\n*F\n+ 1 Broadcast.kt\nkotlinx/coroutines/channels/BroadcastKt\n*L\n51#1:203,4\n*E\n"])

package kotlinx.coroutines.channels

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineContextKt
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi

@Deprecated(message = "BroadcastChannel is deprecated in the favour of SharedFlow and is no longer supported", level = DeprecationLevel.WARNING)
@ObsoleteCoroutinesApi
public fun <E> ReceiveChannel<Any>.broadcast(capacity: Int = 1, start: CoroutineStart = CoroutineStart.LAZY): BroadcastChannel<Any> {
   val scope: CoroutineScope = CoroutineScopeKt.plus(
      CoroutineScopeKt.plus(GlobalScope.INSTANCE, Unconfined as CoroutineContext),
      BroadcastKt$broadcast$$inlined$CoroutineExceptionHandler$1(CoroutineExceptionHandler.Key) as CoroutineContext
   )
   val var6: ReceiveChannel = `$this$broadcast`
   return broadcast$default(scope, null, capacity, start, { it: Throwable? ->
      ChannelsKt.cancelConsumed($this$broadcast, it)
   } as Function1, {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 1, null)
}

@Deprecated(message = "BroadcastChannel is deprecated in the favour of SharedFlow and is no longer supported", level = DeprecationLevel.WARNING)
@ObsoleteCoroutinesApi
public fun <E> CoroutineScope.broadcast(
   context: CoroutineContext = EmptyCoroutineContext.INSTANCE as CoroutineContext,
   capacity: Int = 1,
   start: CoroutineStart = CoroutineStart.LAZY,
   onCompletion: ((Throwable?) -> Unit)? = null,
   block: (ProducerScope<Any>, Continuation<Unit>) -> Any?
): BroadcastChannel<Any> {
   val newContext: CoroutineContext = CoroutineContextKt.newCoroutineContext(`$this$broadcast`, context)
   val channel: BroadcastChannel = BroadcastChannelKt.BroadcastChannel(capacity)
   val coroutine: BroadcastCoroutine = if (start.isLazy) LazyBroadcastCoroutine(newContext, channel, block) else BroadcastCoroutine(newContext, channel, true)
   if (onCompletion != null) {
      coroutine.invokeOnCompletion(onCompletion)
   }

   coroutine.start(start, coroutine, block)
   return coroutine
}
