package kotlinx.coroutines.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Delay
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.MainCoroutineDispatcher

@SourceDebugExtension(["SMAP\nMainDispatchers.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MainDispatchers.kt\nkotlinx/coroutines/internal/MissingMainCoroutineDispatcher\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,134:1\n1#2:135\n*E\n"])
private class MissingMainCoroutineDispatcher(cause: Throwable?, errorHint: String? = null) : MainCoroutineDispatcher, Delay {
   private final val cause: Throwable?
   private final val errorHint: String?

   init {
      this.cause = cause
      this.errorHint = errorHint
   }

   public open val immediate: MainCoroutineDispatcher
      public open get() {
         return this
      }


   public override fun isDispatchNeeded(context: CoroutineContext): Boolean {
      this.missing()
      throw KotlinNothingValueException()
   }

   public override fun limitedParallelism(parallelism: Int): CoroutineDispatcher {
      this.missing()
      throw KotlinNothingValueException()
   }

   public override fun invokeOnTimeout(timeMillis: Long, block: Runnable, context: CoroutineContext): DisposableHandle {
      this.missing()
      throw KotlinNothingValueException()
   }

   public open fun dispatch(context: CoroutineContext, block: Runnable): Nothing {
      this.missing()
      throw KotlinNothingValueException()
   }

   public open fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>): Nothing {
      this.missing()
      throw KotlinNothingValueException()
   }

   private fun missing(): Nothing {
      if (this.cause == null) {
         MainDispatchersKt.throwMissingMainDispatcherException()
         throw KotlinNothingValueException()
      } else {
         var var10000: StringBuilder
         var var10001: java.lang.String
         run label21@{
            var10000 = StringBuilder().append("Module with the Main dispatcher had failed to initialize")
            if (this.errorHint != null) {
               var10001 = ". ${this.errorHint}"
               if (". ${this.errorHint}" != null) {
                  return@label21
               }
            }

            var10001 = ""
         }

         throw IllegalStateException(var10000.append(var10001).toString(), this.cause)
      }
   }

   public override fun toString(): String {
      return "Dispatchers.Main[missing${if (this.cause != null) ", cause=${this.cause}" else ""}]"
   }

   /** @deprecated */
   @Deprecated(message = "Deprecated without replacement as an internal method never intended for public use", level = DeprecationLevel.ERROR)
   override fun delay(time: Long, `$completion`: Continuation<in Unit>) {
      Delay.DefaultImpls.delay(this, time, `$completion`)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun dispatch(context: CoroutineContext, block: Runnable) {
      this.dispatch(context, block)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation) {
      this.scheduleResumeAfterDelay(timeMillis, continuation)
   }
}
