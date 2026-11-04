package kotlinx.coroutines.selects

import kotlin.coroutines.Continuation
import kotlin.time.Duration
import kotlinx.coroutines.DelayKt
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
public fun <R> SelectBuilder<Any>.onTimeout(timeMillis: Long, block: (Continuation<Any>) -> Any?) {
   `$this$onTimeout`.invoke(OnTimeout(timeMillis).selectClause, block)
}

@ExperimentalCoroutinesApi
public fun <R> SelectBuilder<Any>.onTimeout(timeout: Duration, block: (Continuation<Any>) -> Any?) {
   onTimeout(`$this$onTimeout_u2d8Mi8wO0`, DelayKt.toDelayMillis_LRDsOJo/* $VF was: toDelayMillis-LRDsOJo */(timeout), block)
}
