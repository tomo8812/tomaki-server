package kotlinx.coroutines.scheduling

@JvmName(name = "isSchedulerWorker")
internal fun isSchedulerWorker(thread: Thread): Boolean {
   return thread is CoroutineScheduler.Worker
}

@JvmName(name = "mayNotBlock")
internal fun mayNotBlock(thread: Thread): Boolean {
   return thread is CoroutineScheduler.Worker && (thread as CoroutineScheduler.Worker).state === CoroutineScheduler.WorkerState.CPU_ACQUIRED
}
