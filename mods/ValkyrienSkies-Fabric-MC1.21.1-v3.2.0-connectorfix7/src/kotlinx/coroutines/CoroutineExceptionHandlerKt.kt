@file:SourceDebugExtension(["SMAP\nCoroutineExceptionHandler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CoroutineExceptionHandler.kt\nkotlinx/coroutines/CoroutineExceptionHandlerKt\n+ 2 Exceptions.kt\nkotlinx/coroutines/ExceptionsKt\n*L\n1#1,110:1\n75#2:111\n*S KotlinDebug\n*F\n+ 1 CoroutineExceptionHandler.kt\nkotlinx/coroutines/CoroutineExceptionHandlerKt\n*L\n38#1:111\n*E\n"])

package kotlinx.coroutines

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.internal.CoroutineExceptionHandlerImpl_commonKt

@InternalCoroutinesApi
public fun handleCoroutineException(context: CoroutineContext, exception: Throwable) {
   try {
      val var10000: CoroutineExceptionHandler = context.get(CoroutineExceptionHandler.Key) as CoroutineExceptionHandler
      if (var10000 != null) {
         var10000.handleException(context, exception)
         return
      }
   } catch (var5: java.lang.Throwable) {
      CoroutineExceptionHandlerImpl_commonKt.handleUncaughtCoroutineException(context, handlerException(exception, var5))
      return
   }

   CoroutineExceptionHandlerImpl_commonKt.handleUncaughtCoroutineException(context, exception)
}

internal fun handlerException(originalException: Throwable, thrownException: Throwable): Throwable {
   if (originalException === thrownException) {
      return originalException
   } else {
      val var2: RuntimeException = RuntimeException("Exception while trying to handle coroutine exception", thrownException)
      kotlin.ExceptionsKt.addSuppressed(var2, originalException)
      return var2
   }
}

public inline fun CoroutineExceptionHandler(crossinline handler: (CoroutineContext, Throwable) -> Unit): CoroutineExceptionHandler {
   return object : AbstractCoroutineContextElement, CoroutineExceptionHandler {
      public override fun handleException(context: CoroutineContext, exception: Throwable) {
         handler(context, exception)
      }
   }
}
