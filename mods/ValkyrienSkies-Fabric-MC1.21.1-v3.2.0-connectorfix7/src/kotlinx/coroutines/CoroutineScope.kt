package kotlinx.coroutines

import kotlin.coroutines.CoroutineContext

public interface CoroutineScope {
   public val coroutineContext: CoroutineContext
}
