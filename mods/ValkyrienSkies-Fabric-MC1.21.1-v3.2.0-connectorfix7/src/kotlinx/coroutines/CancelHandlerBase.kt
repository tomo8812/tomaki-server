package kotlinx.coroutines

import kotlin.jvm.functions.Function1

internal abstract class CancelHandlerBase : Function1<java.lang.Throwable, Unit> {
   public abstract operator fun invoke(cause: Throwable?) {
   }
}
