package kotlinx.coroutines.internal

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope

internal class ContextScope(context: CoroutineContext) : CoroutineScope {
   public open val coroutineContext: CoroutineContext

   init {
      this.coroutineContext = context
   }

   public override fun toString(): String {
      return "CoroutineScope(coroutineContext=${this.coroutineContext})"
   }
}
