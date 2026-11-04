@file:SourceDebugExtension(["SMAP\nCoroutineExceptionHandlerImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CoroutineExceptionHandlerImpl.kt\nkotlinx/coroutines/internal/CoroutineExceptionHandlerImplKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,50:1\n1#2:51\n*E\n"])

package kotlinx.coroutines.internal

import java.util.ServiceLoader
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineExceptionHandler

internal final val platformExceptionHandlers: Collection<CoroutineExceptionHandler> =
   SequencesKt.toList(SequencesKt.asSequence(ServiceLoader.load(CoroutineExceptionHandler.class, CoroutineExceptionHandler.class.getClassLoader()).iterator())) as java.util.Collection

internal fun ensurePlatformExceptionHandlerLoaded(callback: CoroutineExceptionHandler) {
   if (!platformExceptionHandlers.contains(callback)) {
      throw IllegalStateException("Exception handler was not found via a ServiceLoader".toString())
   }
}

internal fun propagateExceptionFinalResort(exception: Throwable) {
   val currentThread: Thread = Thread.currentThread()
   currentThread.getUncaughtExceptionHandler().uncaughtException(currentThread, exception)
}
