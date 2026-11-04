package kotlinx.coroutines

import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import org.jetbrains.annotations.NotNull

private class DispatcherExecutor(dispatcher: CoroutineDispatcher) : Executor {
   @JvmField
   @NotNull
   public final val dispatcher: CoroutineDispatcher

   init {
      this.dispatcher = dispatcher
   }

   public override fun execute(block: Runnable) {
      if (this.dispatcher.isDispatchNeeded(EmptyCoroutineContext.INSTANCE as CoroutineContext)) {
         this.dispatcher.dispatch(EmptyCoroutineContext.INSTANCE as CoroutineContext, block)
      } else {
         block.run()
      }
   }

   public override fun toString(): String {
      return this.dispatcher.toString()
   }
}
