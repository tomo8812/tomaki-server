package kotlinx.coroutines

import java.util.concurrent.CancellationException
import kotlin.contracts.InvocationKind
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.jvm.internal.InlineMarker
import kotlinx.coroutines.internal.ContextScope
import kotlinx.coroutines.internal.ScopeCoroutine
import kotlinx.coroutines.intrinsics.UndispatchedKt

public operator fun CoroutineScope.plus(context: CoroutineContext): CoroutineScope {
   return ContextScope(`$this$plus`.coroutineContext.plus(context))
}

public fun MainScope(): CoroutineScope {
   return ContextScope(SupervisorKt.SupervisorJob$default(null, 1, null).plus(Main as CoroutineContext))
}

public final val isActive: Boolean
   public final get() {
      val var10000: Job = `$this$isActive`.coroutineContext.get(Job.Key) as Job
      return var10000 == null || var10000.isActive
   }


public suspend fun <R> coroutineScope(block: (CoroutineScope, Continuation<Any>) -> Any?): Any {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   val coroutine: ScopeCoroutine = ScopeCoroutine(`$completion`.getContext(), `$completion`)
   val var10000: Any = UndispatchedKt.startUndispatchedOrReturn(coroutine, coroutine, block)
   if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      DebugProbesKt.probeCoroutineSuspended(`$completion`)
   }

   return var10000
}

public fun CoroutineScope(context: CoroutineContext): CoroutineScope {
   return ContextScope(if (context.get(Job.Key) != null) context else context.plus(JobKt.Job$default(null, 1, null) as CoroutineContext))
}

public fun CoroutineScope.cancel(cause: CancellationException? = null) {
   val var10000: Job = `$this$cancel`.coroutineContext.get(Job.Key) as Job
   if (var10000 == null) {
      throw IllegalStateException(("Scope cannot be cancelled because it does not have a job: $`$this$cancel`").toString())
   } else {
      var10000.cancel(cause)
   }
}

public fun CoroutineScope.cancel(message: String, cause: Throwable? = null) {
   cancel(`$this$cancel`, ExceptionsKt.CancellationException(message, cause))
}

public fun CoroutineScope.ensureActive() {
   JobKt.ensureActive(`$this$ensureActive`.coroutineContext)
}

public suspend inline fun currentCoroutineContext(): CoroutineContext {
   return `$completion`.getContext()
}

fun `currentCoroutineContext$$forInline`(`$completion`: Continuation<in CoroutineContext>) {
   InlineMarker.mark(3)
   null.getContext()
}
