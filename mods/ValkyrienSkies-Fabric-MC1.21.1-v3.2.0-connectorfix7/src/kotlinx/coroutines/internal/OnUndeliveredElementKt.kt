@file:SourceDebugExtension(["SMAP\nOnUndeliveredElement.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OnUndeliveredElement.kt\nkotlinx/coroutines/internal/OnUndeliveredElementKt\n+ 2 Exceptions.kt\nkotlinx/coroutines/ExceptionsKt\n*L\n1#1,44:1\n75#2:45\n*S KotlinDebug\n*F\n+ 1 OnUndeliveredElement.kt\nkotlinx/coroutines/internal/OnUndeliveredElementKt\n*L\n22#1:45\n*E\n"])

package kotlinx.coroutines.internal

import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineExceptionHandlerKt

internal fun <E> ((Any) -> Unit).callUndeliveredElementCatchingException(element: Any, undeliveredElementException: UndeliveredElementException? = null): UndeliveredElementException? {
   try {
      `$this$callUndeliveredElementCatchingException`(element)
   } catch (var6: java.lang.Throwable) {
      if (undeliveredElementException == null || undeliveredElementException.getCause() === var6) {
         return UndeliveredElementException("Exception in undelivered element handler for $element", var6)
      }

      ExceptionsKt.addSuppressed(undeliveredElementException, var6)
   }

   return undeliveredElementException
}

internal fun <E> ((Any) -> Unit).callUndeliveredElement(element: Any, context: CoroutineContext) {
   val var10000: UndeliveredElementException = callUndeliveredElementCatchingException(`$this$callUndeliveredElement`, element, null)
   if (var10000 != null) {
      CoroutineExceptionHandlerKt.handleCoroutineException(context, var10000)
   }
}

internal fun <E> ((Any) -> Unit).bindCancellationFun(element: Any, context: CoroutineContext): (Throwable) -> Unit {
   return { <anonymous parameter 0>: Throwable ->
      OnUndeliveredElementKt.callUndeliveredElement($this$bindCancellationFun, element, context)
   } as (java.lang.Throwable?) -> Unit
}
