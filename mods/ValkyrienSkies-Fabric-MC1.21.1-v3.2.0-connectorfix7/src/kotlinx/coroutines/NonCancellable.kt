package kotlinx.coroutines

import java.util.concurrent.CancellationException
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlinx.coroutines.selects.SelectClause0

public object NonCancellable : AbstractCoroutineContextElement(Job.Key), Job {
   private const val message: String = "NonCancellable can be used only as an argument for 'withContext', direct usages of its API are prohibited"

   @Deprecated(
      message = "NonCancellable can be used only as an argument for 'withContext', direct usages of its API are prohibited",
      level = DeprecationLevel.WARNING
   )
   public open val parent: Job?
      public open get() {
         return null
      }


   @Deprecated(
      message = "NonCancellable can be used only as an argument for 'withContext', direct usages of its API are prohibited",
      level = DeprecationLevel.WARNING
   )
   public open val isActive: Boolean
      public open get() {
         return true
      }


   @Deprecated(
      message = "NonCancellable can be used only as an argument for 'withContext', direct usages of its API are prohibited",
      level = DeprecationLevel.WARNING
   )
   public open val isCompleted: Boolean
      public open get() {
         return false
      }


   @Deprecated(
      message = "NonCancellable can be used only as an argument for 'withContext', direct usages of its API are prohibited",
      level = DeprecationLevel.WARNING
   )
   public open val isCancelled: Boolean
      public open get() {
         return false
      }


   @Deprecated(message = "NonCancellable can be used only as an argument for 'withContext', direct usages of its API are prohibited", level = DeprecationLevel.WARNING)
   public override fun start(): Boolean {
      return false
   }

   @Deprecated(message = "NonCancellable can be used only as an argument for 'withContext', direct usages of its API are prohibited", level = DeprecationLevel.WARNING)
   public override suspend fun join() {
      throw UnsupportedOperationException("This job is always active")
   }

   @Deprecated(
      message = "NonCancellable can be used only as an argument for 'withContext', direct usages of its API are prohibited",
      level = DeprecationLevel.WARNING
   )
   public open val onJoin: SelectClause0
      public open get() {
         throw UnsupportedOperationException("This job is always active")
      }


   @Deprecated(message = "NonCancellable can be used only as an argument for 'withContext', direct usages of its API are prohibited", level = DeprecationLevel.WARNING)
   public override fun getCancellationException(): CancellationException {
      throw IllegalStateException("This job is always active")
   }

   @Deprecated(message = "NonCancellable can be used only as an argument for 'withContext', direct usages of its API are prohibited", level = DeprecationLevel.WARNING)
   public override fun invokeOnCompletion(handler: (Throwable?) -> Unit): DisposableHandle {
      return NonDisposableHandle.INSTANCE
   }

   @Deprecated(message = "NonCancellable can be used only as an argument for 'withContext', direct usages of its API are prohibited", level = DeprecationLevel.WARNING)
   public override fun invokeOnCompletion(onCancelling: Boolean, invokeImmediately: Boolean, handler: (Throwable?) -> Unit): DisposableHandle {
      return NonDisposableHandle.INSTANCE
   }

   @Deprecated(message = "NonCancellable can be used only as an argument for 'withContext', direct usages of its API are prohibited", level = DeprecationLevel.WARNING)
   public override fun cancel(cause: CancellationException?) {
   }

   @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
   @JvmSynthetic
   public override fun cancel(cause: Throwable?): Boolean {
      return false
   }

   @Deprecated(
      message = "NonCancellable can be used only as an argument for 'withContext', direct usages of its API are prohibited",
      level = DeprecationLevel.WARNING
   )
   public open val children: Sequence<Job>
      public open get() {
         return SequencesKt.emptySequence()
      }


   @Deprecated(message = "NonCancellable can be used only as an argument for 'withContext', direct usages of its API are prohibited", level = DeprecationLevel.WARNING)
   public override fun attachChild(child: ChildJob): ChildHandle {
      return NonDisposableHandle.INSTANCE
   }

   public override fun toString(): String {
      return "NonCancellable"
   }

   /** @deprecated */
   @Deprecated(message = "Since 1.2.0, binary compatibility with versions <= 1.1.x", level = DeprecationLevel.HIDDEN)
   @JvmSynthetic
   override fun cancel() {
      Job.DefaultImpls.cancel(this)
   }

   /** @deprecated */
   @Deprecated(message = "Operator '+' on two Job objects is meaningless. Job is a coroutine context element and `+` is a set-sum operator for coroutine contexts. The job to the right of `+` just replaces the job the left of `+`.", level = DeprecationLevel.ERROR)
   override fun plus(other: Job): Job {
      Job.DefaultImpls.plus(this, other)
   }
}
