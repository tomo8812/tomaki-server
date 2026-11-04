package kotlinx.coroutines.selects

import kotlin.contracts.InvocationKind
import kotlin.coroutines.Continuation
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.InlineMarker
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.Symbol

private final val DUMMY_PROCESS_RESULT_FUNCTION: (Any, Any?, Any?) -> Any? = { <anonymous parameter 0>, <anonymous parameter 1>, <anonymous parameter 2> ->
   null
} as Function3

private const val TRY_SELECT_SUCCESSFUL: Int = 0
private const val TRY_SELECT_REREGISTER: Int = 1
private const val TRY_SELECT_CANCELLED: Int = 2
private const val TRY_SELECT_ALREADY_SELECTED: Int = 3
private final val STATE_REG: Symbol = Symbol("STATE_REG")
private final val STATE_COMPLETED: Symbol = Symbol("STATE_COMPLETED")
private final val STATE_CANCELLED: Symbol = Symbol("STATE_CANCELLED")
private final val NO_RESULT: Symbol = Symbol("NO_RESULT")
internal final val PARAM_CLAUSE_0: Symbol = Symbol("PARAM_CLAUSE_0")

public suspend inline fun <R> select(crossinline builder: (SelectBuilder<Any>) -> Unit): Any {
   contract {
      callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
   }

   val `$this$select_u24lambda_u241`: SelectImplementation = SelectImplementation(`$completion`.getContext())
   builder(`$this$select_u24lambda_u241`)
   return `$this$select_u24lambda_u241`.doSelect(`$completion`)
}

fun <R> `select$$forInline`(builder: (SelectBuilder<in R>?) -> Unit, `$completion`: Continuation<in R>) {
   InlineMarker.mark(3)
   val `$this$select_u24lambda_u241`: SelectImplementation = SelectImplementation(null.getContext())
   builder(`$this$select_u24lambda_u241`)
   InlineMarker.mark(3)
   InlineMarker.mark(0)
   val var10000: Any = `$this$select_u24lambda_u241`.doSelect(null)
   InlineMarker.mark(1)
   var10000
}

private fun CancellableContinuation<Unit>.tryResume(onCancellation: ((Throwable) -> Unit)?): Boolean {
   val var10000: Any = `$this$tryResume`.tryResume(Unit.INSTANCE, null, onCancellation)
   if (var10000 == null) {
      return false
   } else {
      `$this$tryResume`.completeResume(var10000)
      return true
   }
}

private fun TrySelectDetailedResult(trySelectInternalResult: Int): TrySelectDetailedResult {
   var var10000: TrySelectDetailedResult
   when (trySelectInternalResult) {
      0 -> var10000 = TrySelectDetailedResult.SUCCESSFUL
      1 -> var10000 = TrySelectDetailedResult.REREGISTER
      2 -> var10000 = TrySelectDetailedResult.CANCELLED
      3 -> var10000 = TrySelectDetailedResult.ALREADY_SELECTED
      else -> throw IllegalStateException(("Unexpected internal result: $trySelectInternalResult").toString())
   }

   return var10000
}

/** @deprecated */
@InternalCoroutinesApi
@JvmSynthetic
fun `RegistrationFunction$annotations`() {
}

/** @deprecated */
@InternalCoroutinesApi
@JvmSynthetic
fun `ProcessResultFunction$annotations`() {
}

/** @deprecated */
@InternalCoroutinesApi
@JvmSynthetic
fun `OnCancellationConstructor$annotations`() {
}

@JvmSynthetic
fun `access$getDUMMY_PROCESS_RESULT_FUNCTION$p`(): Function3 {
   DUMMY_PROCESS_RESULT_FUNCTION
}

@JvmSynthetic
fun `access$getSTATE_REG$p`(): Symbol {
   STATE_REG
}

@JvmSynthetic
fun `access$getNO_RESULT$p`(): Symbol {
   NO_RESULT
}

@JvmSynthetic
fun `access$getSTATE_CANCELLED$p`(): Symbol {
   STATE_CANCELLED
}

@JvmSynthetic
fun `access$TrySelectDetailedResult`(trySelectInternalResult: Int): TrySelectDetailedResult {
   TrySelectDetailedResult(trySelectInternalResult)
}

@JvmSynthetic
fun `access$tryResume`(`$receiver`: CancellableContinuation, onCancellation: Function1): Boolean {
   tryResume(`$receiver`, onCancellation)
}

@JvmSynthetic
fun `access$getSTATE_COMPLETED$p`(): Symbol {
   STATE_COMPLETED
}
