package kotlinx.coroutines

import kotlin.contracts.InvocationKind
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlinx.coroutines.intrinsics.UndispatchedKt

public fun SupervisorJob(parent: Job? = null): CompletableJob {
   return SupervisorJobImpl(parent)
}

@Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
@JvmName(name = "SupervisorJob")
@JvmSynthetic
public fun SupervisorJob0(parent: Job? = ...): Job {
   return SupervisorJob(parent)
}

/** @deprecated */
@JvmSynthetic
fun `SupervisorJob$default`(var0: Job, var1: Int, var2: Any): Job {
   if ((var1 and 1) != 0) {
      var0 = null
   }

   SupervisorJob(var0)
}

public suspend fun <R> supervisorScope(block: (CoroutineScope, Continuation<Any>) -> Any?): Any {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   val coroutine: SupervisorCoroutine = SupervisorCoroutine(`$completion`.getContext(), `$completion`)
   val var10000: Any = UndispatchedKt.startUndispatchedOrReturn(coroutine, coroutine, block)
   if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      DebugProbesKt.probeCoroutineSuspended(`$completion`)
   }

   return var10000
}
