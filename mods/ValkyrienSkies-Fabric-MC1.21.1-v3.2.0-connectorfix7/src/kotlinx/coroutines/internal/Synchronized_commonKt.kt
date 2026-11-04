@file:SourceDebugExtension(["SMAP\nSynchronized.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Synchronized.common.kt\nkotlinx/coroutines/internal/Synchronized_commonKt\n+ 2 Synchronized.kt\nkotlinx/coroutines/internal/SynchronizedKt\n*L\n1#1,33:1\n20#2:34\n*S KotlinDebug\n*F\n+ 1 Synchronized.common.kt\nkotlinx/coroutines/internal/Synchronized_commonKt\n*L\n31#1:34\n*E\n"])

package kotlinx.coroutines.internal

import kotlin.contracts.InvocationKind
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
public inline fun <T> synchronized(lock: Any, block: () -> Any): Any {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   val var10000: Any
   synchronized (lock) {
      val var5: Any = block()
      InlineMarker.finallyStart(1)
      InlineMarker.finallyEnd(1)
      var10000 = var5
   }

   return (T)var10000
}
