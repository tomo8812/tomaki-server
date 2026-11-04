@file:JvmMultifileClass
@file:JvmName("FlowKt")

package kotlinx.coroutines.flow

import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.TypeIntrinsics

private final val defaultKeySelector: (Any?) -> Any? = { it ->
   it
} as Function1

private final val defaultAreEquivalent: (Any?, Any?) -> Boolean = { old, new ->
   old == new
} as Function2

public fun <T> Flow<Any>.distinctUntilChanged(): Flow<Any> {
   return if (`$this$distinctUntilChanged` is StateFlow)
      `$this$distinctUntilChanged`
      else
      distinctUntilChangedBy$FlowKt__DistinctKt(`$this$distinctUntilChanged`, defaultKeySelector, defaultAreEquivalent)
   }

public fun <T> Flow<Any>.distinctUntilChanged(areEquivalent: (Any, Any) -> Boolean): Flow<Any> {
   val var10001: Function1 = defaultKeySelector
   return distinctUntilChangedBy$FlowKt__DistinctKt(
      `$this$distinctUntilChanged`, var10001, TypeIntrinsics.beforeCheckcastToFunctionOfArity(areEquivalent, 2) as (Any?, Any?) -> java.lang.Boolean
   )
}

public fun <T, K> Flow<Any>.distinctUntilChangedBy(keySelector: (Any) -> Any): Flow<Any> {
   return distinctUntilChangedBy$FlowKt__DistinctKt(`$this$distinctUntilChangedBy`, keySelector, defaultAreEquivalent)
}

private fun <T> Flow<Any>.distinctUntilChangedBy(keySelector: (Any) -> Any?, areEquivalent: (Any?, Any?) -> Boolean): Flow<Any> {
   return if (`$this$distinctUntilChangedBy` is DistinctFlowImpl
         && (`$this$distinctUntilChangedBy` as DistinctFlowImpl).keySelector === keySelector
         && (`$this$distinctUntilChangedBy` as DistinctFlowImpl).areEquivalent === areEquivalent)
      `$this$distinctUntilChangedBy`
      else
      DistinctFlowImpl(`$this$distinctUntilChangedBy`, keySelector, areEquivalent)
   }
