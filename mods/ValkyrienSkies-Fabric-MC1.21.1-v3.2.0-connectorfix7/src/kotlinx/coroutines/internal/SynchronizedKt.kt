package kotlinx.coroutines.internal

import kotlin.jvm.internal.InlineMarker
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
public inline fun <T> synchronizedImpl(lock: Any, block: () -> Any): Any {
   synchronized (lock) {
      val var4: Any = block()
      InlineMarker.finallyStart(1)
      InlineMarker.finallyEnd(1)
      return (T)var4
   }
}

/** @deprecated */
@InternalCoroutinesApi
@JvmSynthetic
fun `SynchronizedObject$annotations`() {
}
