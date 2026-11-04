@file:JvmMultifileClass
@file:JvmName("FlowKt")
@file:SourceDebugExtension(["SMAP\nDelay.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Delay.kt\nkotlinx/coroutines/flow/FlowKt__DelayKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,415:1\n1#2:416\n*E\n"])

package kotlinx.coroutines.flow

import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelayKt
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ProduceKt
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.internal.FlowCoroutineKt

@FlowPreview
public fun <T> Flow<Any>.debounce(timeoutMillis: Long): Flow<Any> {
   if (timeoutMillis < 0L) {
      throw IllegalArgumentException("Debounce timeout should not be negative".toString())
   } else {
      return if (timeoutMillis == 0L) `$this$debounce` else debounceInternal$FlowKt__DelayKt(`$this$debounce`, { it: Any ->
         return timeoutMillis
      } as Function1)
   }
}

@FlowPreview
@OverloadResolutionByLambdaReturnType
public fun <T> Flow<Any>.debounce(timeoutMillis: (Any) -> Long): Flow<Any> {
   return debounceInternal$FlowKt__DelayKt(`$this$debounce`, timeoutMillis)
}

@FlowPreview
public fun <T> Flow<Any>.debounce(timeout: Duration): Flow<Any> {
   return FlowKt.debounce(`$this$debounce_u2dHG0u8IE`, DelayKt.toDelayMillis_LRDsOJo/* $VF was: toDelayMillis-LRDsOJo */(timeout))
}

@FlowPreview
@JvmName(name = "debounceDuration")
@OverloadResolutionByLambdaReturnType
public fun <T> Flow<Any>.debounce(timeout: (Any) -> Duration): Flow<Any> {
   return debounceInternal$FlowKt__DelayKt(`$this$debounce`, { emittedItem: Any ->
      return DelayKt.toDelayMillis_LRDsOJo/* $VF was: toDelayMillis-LRDsOJo */((timeout(emittedItem) as Duration).unbox_impl/* $VF was: unbox-impl */())
   } as Function1)
}

private fun <T> Flow<Any>.debounceInternal(timeoutMillisSelector: (Any) -> Long): Flow<Any> {
   return FlowCoroutineKt.scopedFlow({
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function3)
}

@FlowPreview
public fun <T> Flow<Any>.sample(periodMillis: Long): Flow<Any> {
   if (periodMillis <= 0L) {
      throw IllegalArgumentException("Sample period should be positive".toString())
   } else {
      return FlowCoroutineKt.scopedFlow({
         // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
      } as Function3)
   }
}

internal fun CoroutineScope.fixedPeriodTicker(delayMillis: Long, initialDelayMillis: Long = delayMillis): ReceiveChannel<Unit> {
   if (delayMillis < 0L) {
      throw IllegalArgumentException(("Expected non-negative delay, but has $delayMillis ms").toString())
   } else if (initialDelayMillis < 0L) {
      throw IllegalArgumentException(("Expected non-negative initial delay, but has $initialDelayMillis ms").toString())
   } else {
      return ProduceKt.produce$default(`$this$fixedPeriodTicker`, null, 0, {
         // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
      } as Function2, 1, null)
   }
}

@FlowPreview
public fun <T> Flow<Any>.sample(period: Duration): Flow<Any> {
   return FlowKt.sample(`$this$sample_u2dHG0u8IE`, DelayKt.toDelayMillis_LRDsOJo/* $VF was: toDelayMillis-LRDsOJo */(period))
}

@FlowPreview
public fun <T> Flow<Any>.timeout(timeout: Duration): Flow<Any> {
   return timeoutInternal_HG0u8IE$FlowKt__DelayKt/* $VF was: timeoutInternal-HG0u8IE$FlowKt__DelayKt */(`$this$timeout_u2dHG0u8IE`, timeout)
}

private fun <T> Flow<Any>.timeoutInternal(timeout: Duration): Flow<Any> {
   return FlowCoroutineKt.scopedFlow({
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function3)
}
