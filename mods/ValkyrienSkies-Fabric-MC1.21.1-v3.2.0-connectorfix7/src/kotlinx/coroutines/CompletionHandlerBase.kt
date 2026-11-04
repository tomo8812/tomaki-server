package kotlinx.coroutines

import kotlin.jvm.functions.Function1
import kotlinx.coroutines.internal.LockFreeLinkedListNode

internal abstract class CompletionHandlerBase : LockFreeLinkedListNode, Function1<java.lang.Throwable, Unit> {
   public abstract operator fun invoke(cause: Throwable?) {
   }
}
