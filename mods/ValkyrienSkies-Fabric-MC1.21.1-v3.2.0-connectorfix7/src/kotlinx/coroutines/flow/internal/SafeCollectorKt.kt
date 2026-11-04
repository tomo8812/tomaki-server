package kotlinx.coroutines.flow.internal

import kotlin.coroutines.Continuation
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.TypeIntrinsics
import kotlinx.coroutines.flow.FlowCollector

private final val emitFun: (FlowCollector<Any?>, Any?, Continuation<Unit>) -> Any?

@JvmSynthetic
fun `access$getEmitFun$p`(): Function3 {
   emitFun
}

fun {
   val var10000: <unrepresentable> = <unrepresentable>.INSTANCE
   emitFun = TypeIntrinsics.beforeCheckcastToFunctionOfArity(var10000, 3) as (FlowCollector<Object>?, Any?, Continuation<in Unit>?) -> Any
}
