@file:JvmMultifileClass
@file:JvmName("ThreadPoolDispatcherKt")
@file:SourceDebugExtension(["SMAP\nThreadPoolDispatcher.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ThreadPoolDispatcher.kt\nkotlinx/coroutines/ThreadPoolDispatcherKt__ThreadPoolDispatcherKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,47:1\n1#2:48\n*E\n"])

package kotlinx.coroutines

import java.util.concurrent.Executors
import kotlin.jvm.internal.SourceDebugExtension

@DelicateCoroutinesApi
public fun newFixedThreadPoolContext(nThreads: Int, name: String): ExecutorCoroutineDispatcher {
   if (nThreads < 1) {
      throw IllegalArgumentException(("Expected at least one thread, but $nThreads specified").toString())
   } else {
      return ExecutorsKt.from(Executors.newScheduledThreadPool(nThreads, { runnable: Runnable ->
         val t: Thread = Thread(runnable, if (`$nThreads` == 1) `$name` else "$`$name`-${`$threadNo`.incrementAndGet()}")
         t.setDaemon(true)
         t
      }))
   }
}
