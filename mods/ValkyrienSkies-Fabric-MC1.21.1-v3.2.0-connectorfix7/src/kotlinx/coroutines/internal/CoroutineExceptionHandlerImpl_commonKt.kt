package kotlinx.coroutines.internal

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineExceptionHandlerKt

internal fun handleUncaughtCoroutineException(context: CoroutineContext, exception: Throwable) {
   for (handler in platformExceptionHandlers) {
      try {
         handler.handleException(context, exception)
      } catch (var6: ExceptionSuccessfullyProcessed) {
         return
      } catch (var7: java.lang.Throwable) {
         CoroutineExceptionHandlerImplKt.propagateExceptionFinalResort(CoroutineExceptionHandlerKt.handlerException(exception, var7))
      }
   }

   try {
      ExceptionsKt.addSuppressed(exception, DiagnosticCoroutineContextException(context))
   } catch (var5: java.lang.Throwable) {
   }

   CoroutineExceptionHandlerImplKt.propagateExceptionFinalResort(exception)
}
