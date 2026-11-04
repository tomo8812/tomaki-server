package kotlinx.coroutines

import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nCancellableContinuationImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CancellableContinuationImpl.kt\nkotlinx/coroutines/CompletedContinuation\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,662:1\n1#2:663\n*E\n"])
private data class CompletedContinuation(result: Any?,
   cancelHandler: CancelHandler? = null,
   onCancellation: ((Throwable) -> Unit)? = null,
   idempotentResume: Any? = null,
   cancelCause: Throwable? = null
) {
   @JvmField
   @Nullable
   public final val result: Any?

   @JvmField
   @Nullable
   public final val cancelHandler: CancelHandler?

   @JvmField
   @Nullable
   public final val onCancellation: ((Throwable) -> Unit)?

   @JvmField
   @Nullable
   public final val idempotentResume: Any?

   @JvmField
   @Nullable
   public final val cancelCause: Throwable?

   init {
      this.result = result
      this.cancelHandler = cancelHandler
      this.onCancellation = onCancellation
      this.idempotentResume = idempotentResume
      this.cancelCause = cancelCause
   }

   public final val cancelled: Boolean
      public final get() {
         return this.cancelCause != null
      }


   public fun invokeHandlers(cont: CancellableContinuationImpl<*>, cause: Throwable) {
      if (this.cancelHandler != null) {
         cont.callCancelHandler(this.cancelHandler, cause)
      }

      if (this.onCancellation != null) {
         cont.callOnCancellation(this.onCancellation, cause)
      }
   }

   public operator fun component1(): Any? {
      return this.result
   }

   public operator fun component2(): CancelHandler? {
      return this.cancelHandler
   }

   public operator fun component3(): ((Throwable) -> Unit)? {
      return this.onCancellation
   }

   public operator fun component4(): Any? {
      return this.idempotentResume
   }

   public operator fun component5(): Throwable? {
      return this.cancelCause
   }

   public fun copy(
      result: Any? = this.result,
      cancelHandler: CancelHandler? = this.cancelHandler,
      onCancellation: ((Throwable) -> Unit)? = this.onCancellation,
      idempotentResume: Any? = this.idempotentResume,
      cancelCause: Throwable? = this.cancelCause
   ): CompletedContinuation {
      return CompletedContinuation(result, cancelHandler, onCancellation, idempotentResume, cancelCause)
   }

   public override fun toString(): String {
      return "CompletedContinuation(result=${this.result}, cancelHandler=${this.cancelHandler}, onCancellation=${this.onCancellation}, idempotentResume=${this.idempotentResume}, cancelCause=${this.cancelCause})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        ((if (this.result == null) 0 else this.result.hashCode()) * 31 + (if (this.cancelHandler == null) 0 else this.cancelHandler.hashCode()))
                              * 31
                           + (if (this.onCancellation == null) 0 else this.onCancellation.hashCode())
                     )
                     * 31
                  + (if (this.idempotentResume == null) 0 else this.idempotentResume.hashCode())
            )
            * 31
         + (if (this.cancelCause == null) 0 else this.cancelCause.hashCode())
      }

   public override operator fun equals(other: Any?): Boolean {
      label46@
      if (this === other) {
         return true
      } else {
         return other is CompletedContinuation
            && this.result == (other as CompletedContinuation).result
            && this.cancelHandler == (other as CompletedContinuation).cancelHandler
            && this.onCancellation == (other as CompletedContinuation).onCancellation
            && this.idempotentResume == (other as CompletedContinuation).idempotentResume
            && this.cancelCause == (other as CompletedContinuation).cancelCause
         }
   }
}
