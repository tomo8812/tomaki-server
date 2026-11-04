package kotlinx.coroutines.selects

import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
public sealed interface SelectClause {
   public val clauseObject: Any

   public val regFunc: (Any, SelectInstance<*>, Any?) -> Unit

   public val processResFunc: (Any, Any?, Any?) -> Any?

   public val onCancellationConstructor: ((SelectInstance<*>, Any?, Any?) -> (Throwable) -> Unit)?
}
