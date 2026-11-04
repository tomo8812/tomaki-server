package kotlinx.coroutines.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.jvm.internal.DebugProbesKt

internal inline fun <T> probeCoroutineCreated(completion: Continuation<Any>): Continuation<Any> {
   return DebugProbesKt.probeCoroutineCreated(completion)
}
