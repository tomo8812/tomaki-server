@file:JvmMultifileClass
@file:JvmName("FlowKt")
@file:SourceDebugExtension(["SMAP\nShare.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Share.kt\nkotlinx/coroutines/flow/FlowKt__ShareKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,426:1\n1#2:427\n*E\n"])

package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CompletableDeferredKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.internal.ChannelFlow
import kotlinx.coroutines.internal.Symbol

public fun <T> Flow<Any>.shareIn(scope: CoroutineScope, started: SharingStarted, replay: Int = 0): SharedFlow<Any> {
   val config: SharingConfig = configureSharing$FlowKt__ShareKt(`$this$shareIn`, replay)
   val shared: MutableSharedFlow = SharedFlowKt.MutableSharedFlow(replay, config.extraBufferCapacity, config.onBufferOverflow)
   return ReadonlySharedFlow(shared, launchSharing$FlowKt__ShareKt(scope, config.context, config.upstream, shared, started, (Symbol)SharedFlowKt.NO_VALUE))
}

private fun <T> Flow<Any>.configureSharing(replay: Int): SharingConfig<Any> {
   if (ASSERTIONS_ENABLED && replay < 0) {
      throw AssertionError()
   } else {
      val var4: Int = RangesKt.coerceAtLeast(replay, Channel.Factory.CHANNEL_DEFAULT_CAPACITY) - replay
      if (`$this$configureSharing` is ChannelFlow) {
         val upstream: Flow = (`$this$configureSharing` as ChannelFlow).dropChannelOperators()
         if (upstream != null) {
            val var10000: SharingConfig = SharingConfig
var var10003: Int
            when ((`$this$configureSharing` as ChannelFlow).capacity) {
               -3, -2, 0 -> var10003 = if ((`$this$configureSharing` as ChannelFlow).onBufferOverflow === BufferOverflow.SUSPEND)
                     (if ((`$this$configureSharing` as ChannelFlow).capacity == 0) 0 else var4)
                     else
                     (if (replay == 0) 1 else 0)
                  -1 -> var10003 = (`$this$configureSharing` as ChannelFlow).capacity
               else -> var10003 = (`$this$configureSharing` as ChannelFlow).capacity
            }

            var10000./* $VF: Unable to resugar constructor */<init>(
               upstream, var10003, (`$this$configureSharing` as ChannelFlow).onBufferOverflow, (`$this$configureSharing` as ChannelFlow).context
            )
            return var10000
         }
      }

      return SharingConfig(`$this$configureSharing`, var4, BufferOverflow.SUSPEND, EmptyCoroutineContext.INSTANCE as CoroutineContext)
   }
}

private fun <T> CoroutineScope.launchSharing(
   context: CoroutineContext,
   upstream: Flow<Any>,
   shared: MutableSharedFlow<Any>,
   started: SharingStarted,
   initialValue: Any
): Job {
   return BuildersKt.launch(
      `$this$launchSharing`, context, if (started == SharingStarted.Companion.Eagerly) CoroutineStart.DEFAULT else CoroutineStart.UNDISPATCHED, {
         // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
      } as (CoroutineScope?, Continuation<in Unit>?) -> Any
   )
}

public fun <T> Flow<Any>.stateIn(scope: CoroutineScope, started: SharingStarted, initialValue: Any): StateFlow<Any> {
   val config: SharingConfig = configureSharing$FlowKt__ShareKt(`$this$stateIn`, 1)
   val state: MutableStateFlow = StateFlowKt.MutableStateFlow(initialValue)
   return ReadonlyStateFlow(state, launchSharing$FlowKt__ShareKt(scope, config.context, config.upstream, state, started, initialValue))
}

public suspend fun <T> Flow<Any>.stateIn(scope: CoroutineScope): StateFlow<Any> {
   val config: SharingConfig = configureSharing$FlowKt__ShareKt(`$this$stateIn`, 1)
   val result: CompletableDeferred = CompletableDeferredKt.CompletableDeferred$default(null, 1, null)
   launchSharingDeferred$FlowKt__ShareKt(scope, config.context, config.upstream, result)
   return result.await(`$completion`)
}

private fun <T> CoroutineScope.launchSharingDeferred(context: CoroutineContext, upstream: Flow<Any>, result: CompletableDeferred<StateFlow<Any>>) {
   BuildersKt.launch$default(`$this$launchSharingDeferred`, context, null, {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 2, null)
}

public fun <T> MutableSharedFlow<Any>.asSharedFlow(): SharedFlow<Any> {
   return ReadonlySharedFlow(`$this$asSharedFlow`, null)
}

public fun <T> MutableStateFlow<Any>.asStateFlow(): StateFlow<Any> {
   return ReadonlyStateFlow(`$this$asStateFlow`, null)
}

public fun <T> SharedFlow<Any>.onSubscription(action: (FlowCollector<Any>, Continuation<Unit>) -> Any?): SharedFlow<Any> {
   return SubscribedSharedFlow(`$this$onSubscription`, action)
}
