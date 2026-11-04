package kotlinx.coroutines

import kotlinx.coroutines.scheduling.CoroutineScheduler

internal fun createEventLoop(): EventLoop {
   return BlockingEventLoop(Thread.currentThread())
}

@InternalCoroutinesApi
public fun processNextEventInCurrentThread(): Long {
   val var10000: EventLoop = ThreadLocalEventLoop.INSTANCE.currentOrNull$kotlinx_coroutines_core()
   return if (var10000 != null) var10000.processNextEvent() else java.lang.Long.MAX_VALUE
}

internal inline fun platformAutoreleasePool(crossinline block: () -> Unit) {
   block()
}

@InternalCoroutinesApi
@DelicateCoroutinesApi
@PublishedApi
internal fun runSingleTaskFromCurrentSystemDispatcher(): Long {
   val thread: Thread = Thread.currentThread()
   if (thread !is CoroutineScheduler.Worker) {
      throw IllegalStateException("Expected CoroutineScheduler.Worker, but got $thread")
   } else {
      return (thread as CoroutineScheduler.Worker).runSingleTask()
   }
}

@InternalCoroutinesApi
@DelicateCoroutinesApi
@PublishedApi
internal fun Thread.isIoDispatcherThread(): Boolean {
   return `$this$isIoDispatcherThread` is CoroutineScheduler.Worker && (`$this$isIoDispatcherThread` as CoroutineScheduler.Worker).isIo()
}
