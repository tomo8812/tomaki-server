package kotlinx.coroutines.selects

import kotlin.contracts.InvocationKind
import kotlin.coroutines.Continuation
import kotlin.jvm.internal.InlineMarker

public suspend inline fun <R> selectUnbiased(crossinline builder: (SelectBuilder<Any>) -> Unit): Any {
   contract {
      callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
   }

   val `$this$selectUnbiased_u24lambda_u241`: UnbiasedSelectImplementation = UnbiasedSelectImplementation(`$completion`.getContext())
   builder(`$this$selectUnbiased_u24lambda_u241`)
   return `$this$selectUnbiased_u24lambda_u241`.doSelect(`$completion`)
}

fun <R> `selectUnbiased$$forInline`(builder: (SelectBuilder<in R>?) -> Unit, `$completion`: Continuation<in R>) {
   InlineMarker.mark(3)
   val `$this$selectUnbiased_u24lambda_u241`: UnbiasedSelectImplementation = UnbiasedSelectImplementation(null.getContext())
   builder(`$this$selectUnbiased_u24lambda_u241`)
   InlineMarker.mark(3)
   InlineMarker.mark(0)
   val var10000: Any = `$this$selectUnbiased_u24lambda_u241`.doSelect(null)
   InlineMarker.mark(1)
   var10000
}
