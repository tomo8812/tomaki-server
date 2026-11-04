package kotlinx.coroutines

internal class BlockingEventLoop(thread: Thread) : EventLoopImplBase {
   protected open val thread: Thread

   init {
      this.thread = thread
   }
}
