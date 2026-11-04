package kotlinx.coroutines

import java.util.concurrent.CancellationException
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.jvm.internal.CoroutineStackFrame
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.internal.DispatchedContinuation
import kotlinx.coroutines.internal.StackTraceRecoveryKt
import kotlinx.coroutines.internal.ThreadContextKt
import kotlinx.coroutines.scheduling.Task
import kotlinx.coroutines.scheduling.TaskContext

@PublishedApi
@SourceDebugExtension(["SMAP\nDispatchedTask.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DispatchedTask.kt\nkotlinx/coroutines/DispatchedTask\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 CoroutineContext.kt\nkotlinx/coroutines/CoroutineContextKt\n+ 4 DispatchedTask.kt\nkotlinx/coroutines/DispatchedTaskKt\n+ 5 StackTraceRecovery.kt\nkotlinx/coroutines/internal/StackTraceRecoveryKt\n+ 6 Exceptions.kt\nkotlinx/coroutines/ExceptionsKt\n*L\n1#1,222:1\n1#2:223\n107#3,10:224\n118#3,2:238\n220#4:234\n221#4:237\n61#5,2:235\n75#6:240\n*S KotlinDebug\n*F\n+ 1 DispatchedTask.kt\nkotlinx/coroutines/DispatchedTask\n*L\n90#1:224,10\n90#1:238,2\n103#1:234\n103#1:237\n103#1:235,2\n142#1:240\n*E\n"])
internal abstract class DispatchedTask<T> : Task {
   @JvmField
   public final var resumeMode: Int
      private set

   open fun DispatchedTask(resumeMode: Int) {
      this.resumeMode = resumeMode
   }

   internal abstract val delegate: Continuation<Any>

   internal abstract fun takeState(): Any? {
   }

   internal open fun cancelCompletedResult(takenState: Any?, cause: Throwable) {
   }

   internal open fun <T> getSuccessfulResult(state: Any?): Any {
      return (T)state
   }

   internal open fun getExceptionalResult(state: Any?): Throwable? {
      return if ((state as? CompletedExceptionally) != null) (state as? CompletedExceptionally).cause else null
   }

   public override fun run() {
      if (ASSERTIONS_ENABLED && this.resumeMode == -1) {
         throw AssertionError()
      } else {
         val var38: TaskContext = this.taskContext
         var fatalException: java.lang.Throwable = null

         run label268@{
            try {
               val var52: Boolean = true
               val var10000: Continuation = this.delegate
               val var41: Continuation = (var10000 as DispatchedContinuation).continuation
               val var43: Any = (var10000 as DispatchedContinuation).countOrElement
               val var49: CoroutineContext = (var10000 as DispatchedContinuation).continuation.getContext()
               val `oldValue$iv`: Any = ThreadContextKt.updateThreadContext(var49, var43)
               val `undispatchedCompletion$iv`: UndispatchedCoroutine = if (`oldValue$iv` != ThreadContextKt.NO_THREAD_ELEMENTS)
                  CoroutineContextKt.updateUndispatchedCompletion(var41, var49, `oldValue$iv`)
                  else
                  null

               try {
                  val context: CoroutineContext = var41.getContext()
                  val state: Any = this.takeState$kotlinx_coroutines_core()
                  val exception: java.lang.Throwable = this.getExceptionalResult$kotlinx_coroutines_core(state)
                  val job: Job = if (exception == null && isCancellableMode) context.get(Job.Key) as Job else null
                  if (job != null && !job.isActive) {
                     val cause: CancellationException = job.getCancellationException()
                     this.cancelCompletedResult$kotlinx_coroutines_core(state, cause)
                     var41.resumeWith(
                        Result.constructor_impl/* $VF was: constructor-impl */(
                           ResultKt.createFailure(
                              if (RECOVER_STACK_TRACES && var41 is CoroutineStackFrame)
                                 StackTraceRecoveryKt.access$recoverFromStackFrame(cause, var41 as CoroutineStackFrame)
                                 else
                                 cause
                           )
                        )
                     )
                  } else if (exception != null) {
                     var41.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(exception)))
                  } else {
                     var41.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(this.getSuccessfulResult$kotlinx_coroutines_core(state)))
                  }
                  break label268;
               } finally {
                  if (`undispatchedCompletion$iv` == null || `undispatchedCompletion$iv`.clearThreadContext()) {
                     ThreadContextKt.restoreThreadContext(var49, `oldValue$iv`)
                  }
               }
            } catch (var36: java.lang.Throwable) {
               fatalException = var36
            } finally {
               if (var25) {
                  var `$this$run_u24lambda_u242`: Any
                  try {
                     var38.afterTask()
                     `$this$run_u24lambda_u242` = Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
                  } catch (var32: java.lang.Throwable) {
                     `$this$run_u24lambda_u242` = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var32))
                  }

                  this.handleFatalException$kotlinx_coroutines_core(
                     fatalException, Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(`$this$run_u24lambda_u242`)
                  )
               }
            }

            var `$this$run_u24lambda_u242`: Any
            try {
               var38.afterTask()
               `$this$run_u24lambda_u242` = Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
            } catch (var33: java.lang.Throwable) {
               `$this$run_u24lambda_u242` = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var33))
            }

            this.handleFatalException$kotlinx_coroutines_core(
               fatalException, Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(`$this$run_u24lambda_u242`)
            )
            return
            val var25: <unknown>
         }

         val var42: DispatchedTask = this

         var var44: Any
         try {
            var44 = var42
            var38.afterTask()
            var44 = Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE)
         } catch (var34: java.lang.Throwable) {
            var44 = Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var34))
         }

         this.handleFatalException$kotlinx_coroutines_core(null, Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(var44))
      }
   }

   internal fun handleFatalException(exception: Throwable?, finallyException: Throwable?) {
      if (exception != null || finallyException != null) {
         if (exception != null && finallyException != null) {
            kotlin.ExceptionsKt.addSuppressed(exception, finallyException)
         }

         var var10000: java.lang.Throwable = exception
         if (exception == null) {
            var10000 = finallyException
         }

         val var10002: java.lang.String = "Fatal exception in coroutines machinery for $this. Please read KDoc to 'handleFatalException' method and report this incident to maintainers"
         CoroutineExceptionHandlerKt.handleCoroutineException(this.delegate.getContext(), CoroutinesInternalError(var10002, var10000))
      }
   }
}
