package kotlinx.coroutines

import java.io.Closeable
import java.util.concurrent.Executor
import kotlin.coroutines.AbstractCoroutineContextKey

public abstract class ExecutorCoroutineDispatcher : CoroutineDispatcher, Closeable {
   public abstract val executor: Executor

   public abstract override fun close() {
   }

   @ExperimentalStdlibApi
   public companion object Key : AbstractCoroutineContextKey(CoroutineDispatcher.Key as kotlin.coroutines.CoroutineContext.Key, { it ->
         it as? ExecutorCoroutineDispatcher
      })
}
