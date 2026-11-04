package kotlinx.coroutines.time

import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.contracts.InvocationKind
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelayKt
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.TimeoutKt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowKt
import kotlinx.coroutines.selects.OnTimeoutKt
import kotlinx.coroutines.selects.SelectBuilder

public suspend fun delay(duration: Duration) {
   val var10000: Any = DelayKt.delay(coerceToMillis(duration), `$completion`)
   return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
}

@FlowPreview
public fun <T> Flow<Any>.debounce(timeout: Duration): Flow<Any> {
   return FlowKt.debounce(`$this$debounce`, coerceToMillis(timeout))
}

@FlowPreview
public fun <T> Flow<Any>.sample(period: Duration): Flow<Any> {
   return FlowKt.sample(`$this$sample`, coerceToMillis(period))
}

public fun <R> SelectBuilder<Any>.onTimeout(duration: Duration, block: (Continuation<Any>) -> Any?) {
   OnTimeoutKt.onTimeout(`$this$onTimeout`, coerceToMillis(duration), block)
}

public suspend fun <T> withTimeout(duration: Duration, block: (CoroutineScope, Continuation<Any>) -> Any?): Any {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   return TimeoutKt.withTimeout(coerceToMillis(duration), block, `$completion`)
}

public suspend fun <T> withTimeoutOrNull(duration: Duration, block: (CoroutineScope, Continuation<Any>) -> Any?): Any? {
   return TimeoutKt.withTimeoutOrNull(coerceToMillis(duration), block, `$completion`)
}

private fun Duration.coerceToMillis(): Long {
   if (`$this$coerceToMillis`.compareTo(Duration.ZERO) <= 0) {
      return 0L
   } else if (`$this$coerceToMillis`.compareTo(ChronoUnit.MILLIS.getDuration()) <= 0) {
      return 1L
   } else {
      return if (`$this$coerceToMillis`.getSeconds() >= 9223372036854775L
            && (`$this$coerceToMillis`.getSeconds() != 9223372036854775L || `$this$coerceToMillis`.getNano() >= 807000000))
         java.lang.Long.MAX_VALUE
         else
         `$this$coerceToMillis`.toMillis()
      }
}
