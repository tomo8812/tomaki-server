@file:SourceDebugExtension(["SMAP\nLimitedDispatcher.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LimitedDispatcher.kt\nkotlinx/coroutines/internal/LimitedDispatcherKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,134:1\n1#2:135\n*E\n"])

package kotlinx.coroutines.internal

import kotlin.jvm.internal.SourceDebugExtension

internal fun Int.checkParallelism() {
   if (`$this$checkParallelism` < 1) {
      throw IllegalArgumentException(("Expected positive parallelism level, but got $`$this$checkParallelism`").toString())
   }
}
