package kotlinx.coroutines.selects

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
public sealed interface SelectInstance<R> {
   public val context: CoroutineContext

   public abstract fun trySelect(clauseObject: Any, result: Any?): Boolean {
   }

   public abstract fun disposeOnCompletion(disposableHandle: DisposableHandle) {
   }

   public abstract fun selectInRegistrationPhase(internalResult: Any?) {
   }
}
