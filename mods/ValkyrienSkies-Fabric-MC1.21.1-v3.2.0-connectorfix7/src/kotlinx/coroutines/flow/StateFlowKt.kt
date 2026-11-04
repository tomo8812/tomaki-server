@file:SourceDebugExtension(["SMAP\nStateFlow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,428:1\n1#2:429\n*E\n"])

package kotlinx.coroutines.flow

import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.internal.NullSurrogateKt
import kotlinx.coroutines.internal.Symbol

private final val NONE: Symbol = Symbol("NONE")
private final val PENDING: Symbol = Symbol("PENDING")

public fun <T> MutableStateFlow(value: Any): MutableStateFlow<Any> {
   val var10000: StateFlowImpl = StateFlowImpl
   var var10002: Any = value
   if (value == null) {
      var10002 = NullSurrogateKt.NULL
   }

   var10000./* $VF: Unable to resugar constructor */<init>(var10002)
   return var10000
}

public inline fun <T> MutableStateFlow<Any>.updateAndGet(function: (Any) -> Any): Any {
   val prevValue: Any
   val nextValue: Any
   do {
      prevValue = `$this$updateAndGet`.value
      nextValue = function(prevValue)
   } while (!`$this$updateAndGet`.compareAndSet(prevValue, nextValue))

   return (T)nextValue
}

public inline fun <T> MutableStateFlow<Any>.getAndUpdate(function: (Any) -> Any): Any {
   val prevValue: Any
   do {
      prevValue = `$this$getAndUpdate`.value
   } while (!`$this$getAndUpdate`.compareAndSet(prevValue, function(prevValue)))

   return (T)prevValue
}

public inline fun <T> MutableStateFlow<Any>.update(function: (Any) -> Any) {
   val prevValue: Any
   do {
      prevValue = `$this$update`.value
   } while (!`$this$update`.compareAndSet(prevValue, function(prevValue)))
}

internal fun <T> StateFlow<Any>.fuseStateFlow(context: CoroutineContext, capacity: Int, onBufferOverflow: BufferOverflow): Flow<Any> {
   if (ASSERTIONS_ENABLED && capacity == -1) {
      throw AssertionError()
   } else {
      return if ((0 <= capacity && capacity < 2 || capacity == -2) && onBufferOverflow === BufferOverflow.DROP_OLDEST)
         `$this$fuseStateFlow`
         else
         SharedFlowKt.fuseSharedFlow(`$this$fuseStateFlow`, context, capacity, onBufferOverflow)
      }
}

@JvmSynthetic
fun `access$getNONE$p`(): Symbol {
   NONE
}

@JvmSynthetic
fun `access$getPENDING$p`(): Symbol {
   PENDING
}
