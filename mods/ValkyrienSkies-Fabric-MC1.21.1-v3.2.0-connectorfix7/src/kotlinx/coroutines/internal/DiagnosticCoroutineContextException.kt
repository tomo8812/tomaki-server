package kotlinx.coroutines.internal

import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nCoroutineExceptionHandlerImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CoroutineExceptionHandlerImpl.kt\nkotlinx/coroutines/internal/DiagnosticCoroutineContextException\n+ 2 ArrayIntrinsics.kt\nkotlin/ArrayIntrinsicsKt\n*L\n1#1,50:1\n26#2:51\n*S KotlinDebug\n*F\n+ 1 CoroutineExceptionHandlerImpl.kt\nkotlinx/coroutines/internal/DiagnosticCoroutineContextException\n*L\n46#1:51\n*E\n"])
internal class DiagnosticCoroutineContextException(context: CoroutineContext) : RuntimeException {
   private final val context: CoroutineContext

   init {
      this.context = context
   }

   public override fun getLocalizedMessage(): String {
      return this.context.toString()
   }

   public override fun fillInStackTrace(): Throwable {
      this.setStackTrace(arrayOfNulls(0))
      return this
   }
}
