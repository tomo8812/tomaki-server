package kotlinx.coroutines.future

import java.util.concurrent.CompletionException
import java.util.function.BiFunction
import kotlin.coroutines.Continuation
import org.jetbrains.annotations.Nullable

private class ContinuationHandler<T>(cont: Continuation<Any>?) : BiFunction<T, java.lang.Throwable, Unit> {
   @JvmField
   @Nullable
   public final var cont: Continuation<Any>?
      private set

   init {
      this.cont = cont
   }

   public open fun apply(result: Any?, exception: Throwable?) {
      if (this.cont != null) {
         if (exception == null) {
            this.cont.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(result))
         } else {
            var var6: CompletionException
            run label33@{
               var6 = exception as? CompletionException
               if ((exception as? CompletionException) != null) {
                  var6 = var6.getCause()
                  if (var6 != null) {
                     return@label33
                  }
               }

               var6 = exception
            }

            this.cont.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var6)))
         }
      }
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun apply(p0: Any, p1: Any) {
      this.apply((T)p0, p1 as java.lang.Throwable)
      Unit.INSTANCE
   }
}
